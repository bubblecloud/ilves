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
package org.vaadin.addons.sitekit.viewlet.anonymous.login;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.apache.directory.api.ldap.model.cursor.EntryCursor;
import org.apache.directory.api.ldap.model.entry.Entry;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.message.SearchScope;
import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;
import org.vaadin.addons.sitekit.dao.UserDirectoryDao;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.model.UserDirectory;
import org.vaadin.addons.sitekit.site.SecurityProviderSessionImpl;
import org.vaadin.addons.sitekit.util.CidrUtil;
import org.vaadin.addons.sitekit.util.OpenIdUtil;
import org.vaadin.addons.sitekit.util.StringUtil;
import org.apache.log4j.Logger;

import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.User;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.LoginForm.LoginEvent;

/**
 * Login Flowlet.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class LoginFlowlet extends AbstractFlowlet implements LoginForm.LoginListener {
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(LoginFlowlet.class);

    /** The login form. */
    private LoginForm loginForm;;

    @Override
    public String getFlowletKey() {
        return "login";
    }

    @SuppressWarnings("serial")
    @Override
    public void initialize() {

        final VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);

        final Company company = getSite().getSiteContext().getObject(Company.class);
        if (company.isOpenIdLogin()) {
            final Panel openIdPanel = new Panel();
            openIdPanel.setStyleName(Reindeer.PANEL_LIGHT);
            openIdPanel.setCaption("OpenID Login");
            layout.addComponent(openIdPanel);
            final HorizontalLayout openIdLayout = new HorizontalLayout();
            openIdPanel.setContent(openIdLayout);
            openIdLayout.setMargin(new MarginInfo(false, false, true, false));
            openIdLayout.setSpacing(true);
            final String returnViewName = "openidlogin";
            final Map<String, String> urlIconMap = OpenIdUtil.getOpenIdProviderUrlIconMap();
            for (final String url : urlIconMap.keySet()) {
                openIdLayout.addComponent(OpenIdUtil.getLoginButton(url,urlIconMap.get(url), returnViewName));
            }
        }

        loginForm = new LoginForm() {
            @Override
            public String getLoginHTML() {
                return super.getLoginHTML().replace(
                        "<input class='v-textfield v-widget' style='display:block;'",
                        "<input class='v-textfield v-widget' style='margin-bottom:10px; display:block;'");
            }
        };

        loginForm.setLoginButtonCaption(getSite().localize("button-login"));
        loginForm.setUsernameCaption(getSite().localize("input-user-name"));
        loginForm.setPasswordCaption(getSite().localize("input-user-password"));
        loginForm.addListener(this);

        layout.addComponent(loginForm);

        final Button registerButton = new Button(getSite().localize("button-register") + " >>");
        registerButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                getFlow().forward(RegisterFlowlet.class);
            }
        });
        layout.addComponent(registerButton);

        if (company.isEmailPasswordReset()) {
            final Button forgotPasswordButton = new Button(getSite().localize("button-forgot-password") + " >>");
            forgotPasswordButton.addListener(new ClickListener() {
                @Override
                public void buttonClick(final ClickEvent event) {
                    getFlow().forward(ForgotPasswordFlowlet.class);
                }
            });
            layout.addComponent(forgotPasswordButton);
        }

        setViewContent(layout);

    }


    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void enter() {
    }

    @Override
    public void onLogin(final LoginEvent event) {
        if (event.getLoginParameter("username") == null) {
            Notification.show(getSite().localize("message-login-failed"), Notification.TYPE_WARNING_MESSAGE);
            return;
        }
        if (event.getLoginParameter("password") == null) {
            Notification.show(getSite().localize("message-login-failed"), Notification.TYPE_WARNING_MESSAGE);
            return;
        }

        final HttpServletRequest request = ((VaadinServletRequest) VaadinService.getCurrentRequest())
                .getHttpServletRequest();
        final String userEmailAddress = event.getLoginParameter("username");
        try {

            final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);
            final Company company = getSite().getSiteContext().getObject(Company.class);
            final User user = UserDao.getUser(entityManager, company, userEmailAddress);

            if (user == null) {
                LOGGER.warn("User login failed due to not registered email address: " + userEmailAddress
                        + " (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")");
                Notification.show(getSite().localize("message-login-failed"), Notification.TYPE_WARNING_MESSAGE);
                return;
            }

            if (user.isLockedOut()) {
                LOGGER.warn("User login failed due to user being locked out: " + userEmailAddress
                        + " (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")");
                Notification.show(getSite().localize("message-login-failed"), Notification.TYPE_WARNING_MESSAGE);
                return;
            }

            final List<UserDirectory> userDirectories = UserDirectoryDao.getUserDirectories(entityManager, company);
            final String remoteIpAddress = request.getRemoteAddr();
            boolean directoryLoginAttempted = false;
            for (final UserDirectory userDirectory : userDirectories) {
                if (!userDirectory.isEnabled()) {
                    continue;
                }
                final String[] subnets = userDirectory.getSubNetWhiteList().split(",");
                for (final String subnet : subnets) {
                    final CidrUtil cidrUtils = new CidrUtil(subnet);
                    if (cidrUtils.isInRange(remoteIpAddress)) {
                        directoryLoginAttempted = attemptDirectoryLogin(event, request, entityManager, company, user, userDirectory);
                        break;
                    }
                }
                if (directoryLoginAttempted) {
                    break;
                }
            }

            if (!directoryLoginAttempted) {
                attemptLocalLogin(event, request, entityManager, company, user);
            }
        } catch (final Exception e) {
            LOGGER.error("Error logging in user: " + userEmailAddress
                    + " (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")", e);
            Notification.show(getSite().localize("message-login-error"), Notification.TYPE_ERROR_MESSAGE);
        }

    }

    private boolean attemptDirectoryLogin(final LoginEvent event, final HttpServletRequest request,
                                      final EntityManager entityManager, final Company company,
                                      final User user, final UserDirectory userDirectory)
            throws IOException, NoSuchAlgorithmException, Exception {
        LOGGER.info("Attempting LDAP login: address: " + userDirectory.getAddress() + ":" + userDirectory.getPort()
                + ") email: " + user.getEmailAddress()
                + " (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")");

        final byte[] passwordAndSaltBytes = (user.getEmailAddress()
                + ":" + ((String) event.getLoginParameter("password")))
                .getBytes("UTF-8");

        final String password = ((String) event.getLoginParameter("password"));
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
                LOGGER.warn("User not found from LDAP address: " + userDirectory.getAddress() + ":" + userDirectory.getPort()
                        + ") email: " + user.getEmailAddress()
                        + " (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")");
                userCursor.close();
                connection.unBind();
                return false;
            } else {
                final Entry userEntry = userCursor.get();
                userCursor.close();
                connection.unBind();
                connection.bind(userEntry.getDn(), password);

                if (!isInRemoteGroup(connection, groupSearchBaseDn,
                        userEntry, userDirectory.getRequiredRemoteGroup())) {
                    LOGGER.warn("User not in required remote group '" + userDirectory.getRequiredRemoteGroup()
                            + "', LDAP address: " + userDirectory.getAddress() + ":" + userDirectory.getPort()
                            + ") email: " + user.getEmailAddress()
                            + " (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")");
                    Notification.show(getSite().localize("message-login-failed"), Notification.TYPE_WARNING_MESSAGE);
                    return true;
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
                                + "' (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")");
                    } else if (!remoteGroupMember && localGroupMember) {
                        UserDao.removeGroupMember(entityManager, localGroup, user);
                        LOGGER.info("Removed user '" + user.getEmailAddress()
                                + "' from group '" + localGroupName
                                + "' (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")");
                    }

                }

                passwordMatch = true;
                connection.unBind();
            }
        } catch (final LdapException exception) {
            LOGGER.error("LDAP error: " + user.getEmailAddress()
                    + " (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")", exception);
        }

        if (passwordMatch) {
            LOGGER.info("User login: " + user.getEmailAddress()
                    + " (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")");

            final List<Group> groups = UserDao.getUserGroups(entityManager, company, user);

            user.setFailedLoginCount(0);
            UserDao.updateUser(entityManager, user);

            ((SecurityProviderSessionImpl) getSite().getSecurityProvider()).setUser(user, groups);
            UI.getCurrent().getNavigator().navigateTo(getSite().getCurrentNavigationVersion().getDefaultPageName());
        } else {
            LOGGER.warn("User login, password mismatch: " + user.getEmailAddress()
                    + " (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")");
            user.setFailedLoginCount(user.getFailedLoginCount() + 1);
            if (user.getFailedLoginCount() > company.getMaxFailedLoginCount()) {
                user.setLockedOut(true);
                LOGGER.warn("User locked out due to too many failed login attempts: " + user.getEmailAddress()
                        + " (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")");
            }
            UserDao.updateUser(entityManager, user);
            Notification.show(getSite().localize("message-login-failed"), Notification.TYPE_WARNING_MESSAGE);
        }
        return true;
    }

    private boolean isInRemoteGroup(LdapConnection connection, String groupSearchBaseDn, Entry userEntry, String remoteGroupName) throws Exception {
        final String groupFilter = "(&(uniqueMember="+ userEntry.getDn() +")(cn=" + remoteGroupName + "))";
        final EntryCursor groupCursor = connection.search(groupSearchBaseDn, groupFilter, SearchScope.ONELEVEL );
        final boolean remoteGroupMember = groupCursor.next();
        groupCursor.close();
        return remoteGroupMember;
    }


    private void attemptLocalLogin(final LoginEvent event, final HttpServletRequest request,
                                   final EntityManager entityManager, final Company company,
                                   final User user) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        final byte[] passwordAndSaltBytes = (user.getEmailAddress()
                + ":" + ((String) event.getLoginParameter("password")))
                .getBytes("UTF-8");
        final MessageDigest md = MessageDigest.getInstance("SHA-256");
        final String passwordAndSaltDigest = StringUtil.toHexString(md.digest(passwordAndSaltBytes));

        final boolean passwordMatch = passwordAndSaltDigest.equals(user.getPasswordHash());
        if (passwordMatch) {
            LOGGER.info("User login: " + user.getEmailAddress()
                    + " (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")");

            final List<Group> groups = UserDao.getUserGroups(entityManager, company, user);

            user.setFailedLoginCount(0);
            UserDao.updateUser(entityManager, user);

            ((SecurityProviderSessionImpl) getSite().getSecurityProvider()).setUser(user, groups);
            UI.getCurrent().getNavigator().navigateTo(getSite().getCurrentNavigationVersion().getDefaultPageName());
        } else {
            LOGGER.warn("User login, password mismatch: " + user.getEmailAddress()
                    + " (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")");
            user.setFailedLoginCount(user.getFailedLoginCount() + 1);
            if (user.getFailedLoginCount() > company.getMaxFailedLoginCount()) {
                user.setLockedOut(true);
                LOGGER.warn("User locked out due to too many failed login attempts: " + user.getEmailAddress()
                        + " (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")");
            }
            UserDao.updateUser(entityManager, user);
            Notification.show(getSite().localize("message-login-failed"), Notification.TYPE_WARNING_MESSAGE);
        }
    }

}
