/**
 * Copyright 2013 Tommi S.E. Laukkanen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.addons.sitekit.util;

import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.apache.log4j.Logger;
import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.dao.UserDirectoryDao;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.model.UserDirectory;

import javax.persistence.EntityManager;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Password login utility.
 *
 * @author Tommi S.E. Laukkanen
 */
public class PasswordLoginUtil {
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(PasswordLoginUtil.class);

    /**
     * Attempt password login through local password or LDAP directory.
     *
     * @param remoteHost the remote host from which user is attempting to login
     * @param remoteIpAddress the remote IP address from which user is attempting to login
     * @param remotePort the remote port from which user is attempting to login
     * @param entityManager the entity manager for database access
     * @param company the company (local web site) entity under which user belongs to
     * @param user the user entity
     * @param userPassword the user password
     * @return null on success and error localization key on failure.
     */
    public static String login(final String emailAddress,
                               final String remoteHost,
                               final String remoteIpAddress,
                               final int remotePort,
                               final EntityManager entityManager,
                               final Company company,
                               final User user,
                               final String userPassword) {
        if (user == null) {
            LOGGER.warn("User login failed due to not registered email address: " + emailAddress
                    + " (Remote address: " + remoteHost + " (" + remoteIpAddress + "):" + remotePort + ")");
            return "message-login-failed";
        }

        if (user.isLockedOut()) {
            LOGGER.warn("User login failed due to user being locked out: " + user.getEmailAddress()
                    + " (Remote address: " + remoteHost + " (" + remoteIpAddress + "):" + remotePort + ")");
            return "message-login-failed";
        }

        try {
            final List<UserDirectory> userDirectories = UserDirectoryDao.getUserDirectories(entityManager, company);
            for (final UserDirectory userDirectory : userDirectories) {
                if (!userDirectory.isEnabled()) {
                    continue;
                }
                final String[] subnets = userDirectory.getSubNetWhiteList().split(",");
                for (final String subnet : subnets) {
                    final CidrUtil cidrUtils = new CidrUtil(subnet);
                    if (cidrUtils.isInRange(remoteIpAddress)) {
                        return attemptDirectoryLogin(remoteHost, remoteIpAddress, remotePort,
                                entityManager, company, user, userPassword, userDirectory);
                    }
                }
            }

            return attemptLocalLogin(remoteHost, remoteIpAddress, remotePort, entityManager, company, user, userPassword);
        } catch (final Exception e) {
            LOGGER.error("Error logging in user: " + user.getEmailAddress()
                    + " (Remote address: " + remoteHost + " (" + remoteIpAddress + "):" + remotePort + ")", e);
            return "message-login-error";
        }

    }

    /**
     * Attempt directory login.
     *
     * @param remoteHost
     * @param remoteIpAddress
     * @param remotePort
     * @param entityManager
     * @param company
     * @param user
     * @param userPassword
     * @param userDirectory
     * @return
     * @throws Exception
     */
    private static String attemptDirectoryLogin(
                                          final String remoteHost,
                                          final String remoteIpAddress,
                                          final int remotePort,
                                          final EntityManager entityManager,
                                          final Company company,
                                          final User user,
                                          final String userPassword,
                                          final UserDirectory userDirectory)
            throws Exception {
        LOGGER.info("Attempting LDAP login: address: " + userDirectory.getAddress() + ":" + userDirectory.getPort()
                + ") email: " + user.getEmailAddress()
                + " (Remote address: " + remoteHost + " (" + remoteIpAddress + "):" + remotePort + ")");

        final LdapConnection connection = new LdapNetworkConnection(userDirectory.getAddress(),
                userDirectory.getPort());

        boolean passwordMatch = false;
        try {
            final String ldapLoginDn = userDirectory.getLoginDn();
            final String ldapLoginPassword = userDirectory.getLoginPassword();
            final String userEmailAttribute = userDirectory.getUserEmailAttribute();
            final String userSearchBaseDn = userDirectory.getUserSearchBaseDn();
            final String groupSearchBaseDn = userDirectory.getGroupSearchBaseDn();

            final String userFilter = "(" + userEmailAttribute + "=" + user.getEmailAddress() + ")";

            connection.bind(ldapLoginDn, ldapLoginPassword);

            final EntryCursor userCursor = connection.search(userSearchBaseDn, userFilter, SearchScope.ONELEVEL);
            if (!userCursor.next()) {
                LOGGER.warn("User not found from LDAP address: "
                        + userDirectory.getAddress() + ":" + userDirectory.getPort()
                        + ") email: " + user.getEmailAddress()
                        + " (Remote address: " + remoteHost + " (" + remoteIpAddress + "):" + remotePort + ")");
                userCursor.close();
                connection.unBind();
                return "message-directory-user-not-found";
            } else {
                final Entry userEntry = userCursor.get();
                userCursor.close();
                connection.unBind();
                connection.bind(userEntry.getDn(), userPassword);

                if (!isInRemoteGroup(connection, groupSearchBaseDn,
                        userEntry, userDirectory.getRequiredRemoteGroup())) {
                    LOGGER.warn("User not in required remote group '" + userDirectory.getRequiredRemoteGroup()
                            + "', LDAP address: " + userDirectory.getAddress() + ":" + userDirectory.getPort()
                            + ") email: " + user.getEmailAddress()
                            + " (Remote address: " + remoteHost + " (" + remoteIpAddress + "):" + remotePort + ")");
                    return "message-login-failed";
                }

                final List<Group> groups = UserDao.getUserGroups(entityManager, company, user);
                final Map<String, Group> localGroups = new HashMap<String, Group>();
                for (final Group group : groups) {
                    localGroups.put(group.getName(), group);
                }

                for (final String remoteLocalGroupPair :  userDirectory.getRemoteLocalGroupMapping().split(",")) {
                    final String[] parts = remoteLocalGroupPair.split("=");
                    if (parts.length != 2) {
                        continue;
                    }
                    final String remoteGroupName = parts[0].trim();
                    final String localGroupName = parts[1].trim();

                    final boolean remoteGroupMember = isInRemoteGroup(connection, groupSearchBaseDn,
                            userEntry, remoteGroupName);

                    final boolean localGroupMember = localGroups.containsKey(localGroupName);
                    final Group localGroup = UserDao.getGroup(entityManager, company, localGroupName);
                    if (localGroup == null) {
                        LOGGER.warn("No local group '" + localGroupName
                                + "'. Skipping group membership synchronization.");
                        continue;
                    }
                    if (remoteGroupMember && !localGroupMember) {
                        UserDao.addGroupMember(entityManager, localGroup, user);
                        LOGGER.info("Added user '" + user.getEmailAddress()
                                + "' to group '" + localGroupName
                                + "' (Remote address: " + remoteHost + " (" + remoteIpAddress + "):" + remotePort + ")");
                    } else if (!remoteGroupMember && localGroupMember) {
                        UserDao.removeGroupMember(entityManager, localGroup, user);
                        LOGGER.info("Removed user '" + user.getEmailAddress()
                                + "' from group '" + localGroupName
                                + "' (Remote address: " + remoteHost + " (" + remoteIpAddress + "):" + remotePort + ")");
                    }

                }

                passwordMatch = true;
                connection.unBind();
            }
        } catch (final LdapException exception) {
            LOGGER.error("LDAP error: " + user.getEmailAddress()
                    + " (Remote address: " + remoteHost + " (" + remoteIpAddress + "):" + remotePort + ")", exception);
        }

        if (passwordMatch) {
            LOGGER.info("User login: " + user.getEmailAddress()
                    + " (Remote address: " + remoteHost + " (" + remoteIpAddress + "):" + remotePort + ")");

            user.setFailedLoginCount(0);
            UserDao.updateUser(entityManager, user);

            return null;
        } else {
            LOGGER.warn("User login, password mismatch: " + user.getEmailAddress()
                    + " (Remote address: " + remoteHost + " (" + remoteIpAddress + "):" + remotePort + ")");
            user.setFailedLoginCount(user.getFailedLoginCount() + 1);
            if (user.getFailedLoginCount() > company.getMaxFailedLoginCount()) {
                user.setLockedOut(true);
                LOGGER.warn("User locked out due to too many failed login attempts: " + user.getEmailAddress()
                        + " (Remote address: " + remoteHost + " (" + remoteIpAddress + "):" + remotePort + ")");
            }
            UserDao.updateUser(entityManager, user);
            return "message-login-failed";
        }
    }

    /**
     * Checks whether user is in LDAP group
     *
     * @param connection
     * @param groupSearchBaseDn
     * @param userEntry
     * @param remoteGroupName
     * @return
     * @throws Exception
     */
    private static boolean isInRemoteGroup(LdapConnection connection,
                                           String groupSearchBaseDn, Entry userEntry,
                                           String remoteGroupName) throws Exception {
        final String groupFilter = "(&(uniqueMember="+ userEntry.getDn() +")(cn=" + remoteGroupName + "))";
        final EntryCursor groupCursor = connection.search(groupSearchBaseDn, groupFilter, SearchScope.ONELEVEL );
        final boolean remoteGroupMember = groupCursor.next();
        groupCursor.close();
        return remoteGroupMember;
    }

    /**
     * Attempt local login.
     *
     * @param remoteHost
     * @param remoteIpAddress
     * @param remotePort
     * @param entityManager
     * @param company
     * @param user
     * @param userPassword
     * @return
     * @throws UnsupportedEncodingException
     * @throws NoSuchAlgorithmException
     */
    private static String attemptLocalLogin(final String remoteHost,
                                   final String remoteIpAddress,
                                   final int remotePort,
                                   final EntityManager entityManager,
                                   final Company company,
                                   final User user,
                                   final String userPassword)
            throws UnsupportedEncodingException, NoSuchAlgorithmException {

        boolean passwordMatch = checkPasswordMatchWithUserIdAsSalt(user, userPassword);

        if (!passwordMatch) {
            passwordMatch = checkPasswordMatchWithEmailAsSalt(user, userPassword);
        }

        if (passwordMatch) {
            LOGGER.info("User login: " + user.getEmailAddress()
                    + " (Remote address: " + remoteHost + ":" + remotePort + ")");
            user.setFailedLoginCount(0);
            UserDao.updateUser(entityManager, user);

            return null;
        } else {
            LOGGER.warn("User login, password mismatch: " + user.getEmailAddress()
                    + " (Remote address: " + remoteHost + " (" + remoteIpAddress + "):" + remotePort + ")");
            user.setFailedLoginCount(user.getFailedLoginCount() + 1);

            if (user.getFailedLoginCount() > company.getMaxFailedLoginCount()) {
                user.setLockedOut(true);
                LOGGER.warn("User locked out due to too many failed login attempts: " + user.getEmailAddress()
                        + " (Remote address: " + remoteHost + " (" + remoteIpAddress + "):" + remotePort + ")");
            }

            UserDao.updateUser(entityManager, user);

            return "message-login-failed";
        }
    }

    private static boolean checkPasswordMatchWithUserIdAsSalt(User user, String userPassword) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        final byte[] passwordAndSaltBytes = (user.getUserId() + ":" + userPassword).getBytes("UTF-8");
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        final String passwordAndSaltDigest = StringUtil.toHexString(md.digest(passwordAndSaltBytes));
        return passwordAndSaltDigest.equals(user.getPasswordHash());
    }

    private static boolean checkPasswordMatchWithEmailAsSalt(User user, String userPassword) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        final byte[] passwordAndSaltBytes = (user.getEmailAddress() + ":" + userPassword).getBytes("UTF-8");
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        final String passwordAndSaltDigest = StringUtil.toHexString(md.digest(passwordAndSaltBytes));
        return passwordAndSaltDigest.equals(user.getPasswordHash());
    }
}
