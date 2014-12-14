package org.vaadin.addons.sitekit.service;

import org.vaadin.addons.sitekit.cache.PrivilegeCache;
import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.model.*;
import org.vaadin.addons.sitekit.module.audit.AuditService;
import org.vaadin.addons.sitekit.site.ProcessingContext;
import org.vaadin.addons.sitekit.site.SiteException;
import org.vaadin.addons.sitekit.site.SitePrivileges;
import org.vaadin.addons.sitekit.site.SiteRoles;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by tlaukkan on 12/14/2014.
 */
public class SecurityService {

    /**
     * Adds user to database.
     * @param context the processing context
     * @param user the user
     * @param defaultGroup the default group
     */
    public static final void addUser(final ProcessingContext context, final User user, final Group defaultGroup) {
        if (user.getOwner().isSelfRegistration()) {
            requireRole(SitePrivileges.ADMINISTER, context, SiteRoles.ADMINISTRATOR, SiteRoles.ANONYMOUS);
        } else {
            requireRole(SitePrivileges.ADMINISTER, context, SiteRoles.ADMINISTRATOR);
        }
        UserDao.addUser(context.getEntityManager(), user, defaultGroup);
    }

    /**
     * Updates user to database.
     * @param context the processing context
     * @param user the user
     */
    public static final void updateUser(final ProcessingContext context, final User user) {
        if (user.getUserId().equals(context.getUserId())) {
            requireRole(SitePrivileges.ADMINISTER, context, SiteRoles.ADMINISTRATOR, SiteRoles.USER);
        } else {
            requireRole(SitePrivileges.ADMINISTER, context, SiteRoles.ADMINISTRATOR);
        }
        UserDao.updateUser(context.getEntityManager(), user);
    }

    /**
     * Removes user from database.
     * @param context the processing context
     * @param user the user
     */
    public static final void removeUser(final ProcessingContext context, final User user) {
        requireRole(SitePrivileges.ADMINISTER, context, SiteRoles.ADMINISTRATOR);
        UserDao.removeUser(context.getEntityManager(), user);
    }

    /**
     * Adds new group member to database.
     * @param context the processing context
     * @param group the group
     * @param user the user
     */
    public static void addGroupMember(final ProcessingContext context, final Group group, final User user) {
        requirePrivilege(SitePrivileges.ADMINISTER, "group", group.getGroupId(), group.getName(), context, SiteRoles.ADMINISTRATOR);
        UserDao.addGroupMember(context.getEntityManager(), group, user);
    }

    /**
     * Removes group member from database.
     * @param context the processing context
     * @param group the group
     * @param user the user
     */
    public static void removeGroupMember(final ProcessingContext context, final Group group, final User user) {
        requirePrivilege(SitePrivileges.ADMINISTER, "group", group.getGroupId(), group.getName(), context, SiteRoles.ADMINISTRATOR);
        UserDao.addGroupMember(context.getEntityManager(), group, user);
    }

    /**
     * Adds new user privilege to database.
     * @param context the processing context
     * @param user the user
     * @param privilegeKey the privilegeKey
     * @param dataType the data type
     * @param dataId the data ID
     * @param dataLabel the data label
     */
    public static void addUserPrivilege(final ProcessingContext context, final User user, final String privilegeKey,
                                        final String dataType, final String dataId, final String dataLabel) {
        requirePrivilege(SitePrivileges.ADMINISTER, dataType, dataId, dataLabel, context, SiteRoles.ADMINISTRATOR);
        UserDao.addUserPrivilege(context.getEntityManager(), user, privilegeKey, dataId);
    }

    /**
     * Adds new group privilege to database.
     * @param context the processing context
     * @param group the group
     * @param privilegeKey the privilegeKey
     * @param dataType the data type
     * @param dataId the data ID
     * @param dataLabel the data label
     */
    public static void addGroupPrivilege(final ProcessingContext context,
                                         final Group group, final String privilegeKey,
                                         final String dataType, final String dataId, final String dataLabel) {
        requirePrivilege(SitePrivileges.ADMINISTER, dataType, dataId, dataLabel, context, SiteRoles.ADMINISTRATOR);
        UserDao.addGroupPrivilege(context.getEntityManager(), group, privilegeKey, dataId);
    }

    /**
     * Removes user privilege from database.
     * @param context the processing context
     * @param user the user
     * @param privilegeKey the privilegeKey
     * @param dataType the data type
     * @param dataId the data ID
     * @param dataLabel the data label
     */
    public static void removeUserPrivilege(final ProcessingContext context,
                                           final User user, final String privilegeKey,
                                           final String dataType, final String dataId, final String dataLabel) {
        requirePrivilege(SitePrivileges.ADMINISTER, dataType, dataId, dataLabel, context, SiteRoles.ADMINISTRATOR);
        UserDao.removeUserPrivilege(context.getEntityManager(), user, privilegeKey, dataId);
    }

    /**
     * Removes group privilege from database.
     * @param context the processing context
     * @param group the group
     * @param privilegeKey the privilegeKey
     * @param dataType the data type
     * @param dataId the data ID
     * @param dataLabel the data label
     */
    public static void removeGroupPrivilege(final ProcessingContext context,
                                            final Group group, final String privilegeKey,
                                            final String dataType, final String dataId, final String dataLabel) {
        requirePrivilege(SitePrivileges.ADMINISTER, dataType, dataId, dataLabel, context, SiteRoles.ADMINISTRATOR);
        UserDao.removeGroupPrivilege(context.getEntityManager(), group, privilegeKey, dataId);
    }

    /**
     * Require privilege to given data or one of the listed roles.
     * @param key the privilege key
     * @param dataType the data type
     * @param dataId the data ID
     * @param dataLabel the data label
     * @param context the processing context
     * @param roles the privileged roles
     */
    private static synchronized void requirePrivilege(final String key,
                                                      final String dataType, final String dataId, final String dataLabel,
                                                      final ProcessingContext context, final String... roles) {
        for (final String role : roles) {
            if (context.getRoles().contains(role)) {
                AuditService.log(context, key + " access granted");
                return;
            }
        }
        if (!hasPrivilege(key, dataId, context)) {
            AuditService.log(context, key + " access denied", dataType, dataId, dataLabel);
            throw new SiteException("Access denied.");
        }
    }

    /**
     * Require one of the following roles for privilege identified by privilege key.
     * @param key the privilege key
     * @param context the processing context
     * @param roles the roles
     */
    public static final void requireRole(final String key,
                                         final ProcessingContext context, final String... roles) {
        for (final String role : roles) {
            if (context.getRoles().contains(role)) {
                AuditService.log(context, key + " access granted");
                return;
            }
        }
        AuditService.log(context, key + " access denied");
        throw new SiteException("Access denied.");
    }

    /**
     * Check if processing context has privilege to access given data.
     * @param key the privilege key
     * @param context the processing context
     * @param dataId the data ID
     * @return true if privilege exists on given data.
     */
    private static synchronized boolean hasPrivilege(final String key, final String dataId, final ProcessingContext context) {
        final EntityManager entityManager = context.getEntityManager();
        final Company company = context.getObject(Company.class);

        if (context.getUserId() != null && context.getObject(PrivilegeCache.USER_FOR_PRIVILEGE_CHECK) == null) {
            context.putObject(PrivilegeCache.USER_FOR_PRIVILEGE_CHECK, entityManager.getReference(User.class, context.getUserId()));
        }
        final User user = context.getObject(PrivilegeCache.USER_FOR_PRIVILEGE_CHECK);

        if (context.getObject(PrivilegeCache.USER_GROUPS_FOR_PRIVILEGE_CHECK) == null) {
            context.putObject(PrivilegeCache.USER_GROUPS_FOR_PRIVILEGE_CHECK, UserDao.getUserGroups(entityManager, company, user));;
        }
        final List<Group> groups = context.getObject(PrivilegeCache.USER_GROUPS_FOR_PRIVILEGE_CHECK);

        if (!PrivilegeCache.hasPrivilege(entityManager, company, user, groups, key, dataId)) {
            return false;
        } else {
            return true;
        }
    }
}
