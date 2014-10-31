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
package org.vaadin.addons.sitekit.dao;

import org.apache.log4j.Logger;
import org.vaadin.addons.sitekit.model.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User data access object.
 *
 * @author Tommi S.E. Laukkanen
 */
public class UserDao {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(UserDao.class);

    /**
     * Adds user to database.
     * @param entityManager the entity manager
     * @param user the user
     * @param defaultGroup the default group
     */
    public static final void addUser(final EntityManager entityManager, final User user, final Group defaultGroup) {
        if (!user.getOwner().equals(defaultGroup.getOwner())) {
            throw new RuntimeException("User and group are not owner by same company.");
        }
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.persist(user);
            entityManager.persist(new GroupMember(defaultGroup, user));
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in add user.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates user to database.
     * @param entityManager the entity manager
     * @param user the user
     */
    public static final void updateUser(final EntityManager entityManager, final User user) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            user.setModified(new Date());
            entityManager.persist(user);
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in update user.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes user from database.
     * @param entityManager the entity manager
     * @param user the user
     */
    public static final void removeUser(final EntityManager entityManager, final User user) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.remove(user);
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in remove user.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets given user.
     * @param entityManager the entity manager.
     * @param userId the user ID
     * @return the group
     */
    public static final User getUser(final EntityManager entityManager, final String userId) {
        try {
            return entityManager.getReference(User.class, userId);
        } catch (final EntityNotFoundException e) {
            return null;
        }
    }

    /**
     * Gets given email password reset.
     * @param entityManager the entity manager.
     * @param emailPasswordResetId the email password reset ID
     * @return the email password reset
     */
    public static final EmailPasswordReset getEmailPasswordReset(final EntityManager entityManager,
                                                   final String emailPasswordResetId) {
        try {
            return entityManager.getReference(EmailPasswordReset.class, emailPasswordResetId);
        } catch (final EntityNotFoundException e) {
            return null;
        }
    }

    /**
     * Gets active email password resets for given user.
     * @param entityManager the entity manager.
     * @param user the user
     * @return list of email password reset
     */
    public static final List<EmailPasswordReset> getEmailPasswordResetByEmailAddress(final EntityManager entityManager,
                                                                 final User user) {
        final TypedQuery<EmailPasswordReset> query = entityManager.createQuery(
                "select e from EmailPasswordReset as e where e.user=:user",
                EmailPasswordReset.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    /**
     * Gets given user.
     * @param entityManager the entity manager.
     * @param owner the owning company
     * @param firstName the email address
     * @return the group
     */
    public static final User getUserByFirstName(final EntityManager entityManager, final Company owner, final String firstName) {
        final TypedQuery<User> query = entityManager.createQuery("select e from User as e where e.owner=:owner and e.firstName=:firstName",
                User.class);
        query.setParameter("owner", owner);
        query.setParameter("firstName", firstName);
        final List<User> users = query.getResultList();
        if (users.size() == 1) {
            return users.get(0);
        } else if (users.size() == 0) {
            return null;
        } else {
            throw new RuntimeException("Multiple users with same owner company and first name in database. Constraint is missing.");
        }
    }

    /**
     * Gets given user.
     * @param entityManager the entity manager.
     * @param owner the owning company
     * @param emailAddress the email address
     * @return the user
     */
    public static final User getUser(final EntityManager entityManager, final Company owner, final String emailAddress) {
        final TypedQuery<User> query = entityManager.createQuery("select e from User as e where e.owner=:owner and e.emailAddress=:emailAddress",
                User.class);
        query.setParameter("owner", owner);
        query.setParameter("emailAddress", emailAddress);
        final List<User> users = query.getResultList();
        if (users.size() == 1) {
            return users.get(0);
        } else if (users.size() == 0) {
            return null;
        } else {
            throw new RuntimeException("Multiple users with same owner company and email address in database. Constraint is missing.");
        }
    }

    /**
     * Gets given user.
     * @param entityManager the entity manager.
     * @param owner the owning company
     * @param openIdIdentifier the user open ID identifier
     * @return the user
     */
    public static final User getUserByOpenIdIdentifier(final EntityManager entityManager, final Company owner, final String openIdIdentifier) {
        final TypedQuery<User> query = entityManager.createQuery("select e from User as e where e.owner=:owner and e.openIdIdentifier=:openIdIdentifier",
                User.class);
        query.setParameter("owner", owner);
        query.setParameter("openIdIdentifier", openIdIdentifier);
        final List<User> users = query.getResultList();
        if (users.size() == 1) {
            return users.get(0);
        } else if (users.size() == 0) {
            return null;
        } else {
            throw new RuntimeException("Multiple users with same owner company and open ID identifier in database. Constraint is missing.");
        }
    }

    /**
     * Gets list of users.
     * @param entityManager the entity manager.
     * @param owner the owning company
     * @return list of users
     */
    public static final List<User> getUsers(final EntityManager entityManager, final Company owner) {
        final TypedQuery<User> query = entityManager.createQuery(
                "select e from User as e order by e.lastName, e.firstName",
                User.class);
        return  query.getResultList();
    }

    /**
     * Gets list of groups.
     * @param entityManager the entity manager.
     * @param owner the owning company
     * @return list of groups
     */
    public static final List<Group> getGroups(final EntityManager entityManager, final Company owner) {
        final TypedQuery<Group> query = entityManager.createQuery("select e from Group as e order by e.name",
                Group.class);
        return  query.getResultList();
    }

    /**
     * Gets list of groups for given user.
     * @param entityManager the entity manager.
     * @param owner the owning company
     * @param user the user
     * @return list of groups
     */
    public static final List<Group> getUserGroups(final EntityManager entityManager, final Company owner, final User user) {
        final TypedQuery<GroupMember> query = entityManager.createQuery("select e from GroupMember as e where e.user=:user order by e.group.name",
                GroupMember.class);
        query.setParameter("user", user);
        final List<GroupMember> groupMembers = query.getResultList();
        final List<Group> groups = new ArrayList<Group>();
        for (final GroupMember groupMember : groupMembers) {
            groups.add(groupMember.getGroup());
        }
        return groups;
    }

    /**
     * Gets list of users in a group
     * @param entityManager the entity manager.
     * @param owner the owning company
     * @param group the group
     * @return list of groups
     */
    public static final List<User> getGroupMembers(final EntityManager entityManager, final Company owner, final Group group) {
        final TypedQuery<GroupMember> query = entityManager.createQuery("select e from GroupMember as e where e.group=:group order by e.user.firstName, e.user.lastName",
                GroupMember.class);
        query.setParameter("group", group);
        final List<GroupMember> groupMembers = query.getResultList();
        final List<User> users = new ArrayList<User>();
        for (final GroupMember groupMember : groupMembers) {
            users.add(groupMember.getUser());
        }
        return users;
    }

    /**
     * Adds new group to database.
     * @param entityManager the entity manager
     * @param group the group
     */
    public static void addGroup(final EntityManager entityManager, final Group group) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.persist(group);
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in add group.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates new group to database.
     * @param entityManager the entity manager
     * @param group the group
     */
    public static void updateGroup(final EntityManager entityManager, final Group group) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            group.setModified(new Date());
            entityManager.persist(group);
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in update group.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes group from database.
     * @param entityManager the entity manager
     * @param group the group
     */
    public static void removeGroup(final EntityManager entityManager, final Group group) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.remove(group);
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in remove group.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets given group.
     * @param entityManager the entity manager.
     * @param owner the owning company
     * @param name the name
     * @return the group
     */
    public static final Group getGroup(final EntityManager entityManager, final Company owner, final String name) {
        final TypedQuery<Group> query = entityManager.createQuery("select e from Group as e where e.owner=:owner and e.name=:name", Group.class);
        query.setParameter("owner", owner);
        query.setParameter("name", name);
        final List<Group> groups = query.getResultList();
        if (groups.size() == 1) {
            return groups.get(0);
        } else if (groups.size() == 0) {
            return null;
        } else {
            throw new RuntimeException("Multiple groups with same owner company and name in database. Constraint is missing.");
        }
    }

    /**
     * Allocates new reference number.
     * @param entityManager the entity manager
     * @return the maximum reference number in use
     */
    public static final long allocateReferenceNumber(final EntityManager entityManager) {
        final Long maxReferenceNumber = (Long) entityManager.createQuery("select max(e.referenceNumber) from Invoice as e").getSingleResult();
        if (maxReferenceNumber == null) {
            return 1;
        } else {
            return maxReferenceNumber + 1;
        }
    }

    /**
     * Adds new group member to database.
     * @param entityManager the entity manager
     * @param group the group
     * @param user the user
     */
    public static void addGroupMember(final EntityManager entityManager, final Group group, final User user) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.persist(new GroupMember(group, user));
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in add group member.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes group member from database.
     * @param entityManager the entity manager
     * @param group the group
     * @param user the user
     */
    public static void removeGroupMember(final EntityManager entityManager, final Group group, final User user) {
        final EntityTransaction transaction = entityManager.getTransaction();
        final TypedQuery<GroupMember> query = entityManager.createQuery("select e from GroupMember as e where e.user=:user and e.group=:group",
                GroupMember.class);
        query.setParameter("user", user);
        query.setParameter("group", group);
        final List<GroupMember> groupMembers = query.getResultList();

        if (groupMembers.size() == 1) {
            transaction.begin();
            try {
                entityManager.remove(groupMembers.get(0));
                transaction.commit();
            } catch (final Exception e) {
                LOG.error("Error in remove group member.", e);
                if (transaction.isActive()) {
                    transaction.rollback();
                }
            }
        }
    }

    /**
     * Adds new user privilege to database.
     * @param entityManager the entity manager
     * @param user the user
     * @param privilegeKey the privilegeKey
     * @param dataId the dataId
     */
    public static void addUserPrivilege(final EntityManager entityManager, final User user, final String privilegeKey, final String dataId) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.persist(new Privilege(null, user, privilegeKey, dataId));
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in adding user privilege.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds new user privileges to database.
     * @param entityManager the entity manager
     * @param user the user
     * @param privilegeKey the privilegeKey
     * @param dataIds the dataIds
     */
    public static void addUserPrivileges(final EntityManager entityManager, final User user, final String privilegeKey, final List<String> dataIds) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            for (final String dataId : dataIds) {
                entityManager.persist(new Privilege(null, user, privilegeKey, dataId));
            }
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in adding user privilege.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds new group privilege to database.
     * @param entityManager the entity manager
     * @param group the group
     * @param privilegeKey the privilegeKey
     * @param dataId the dataId
     */
    public static void addGroupPrivilege(final EntityManager entityManager,
            final Group group, final String privilegeKey, final String dataId) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.persist(new Privilege(group, null, privilegeKey, dataId));
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in adding group privilege.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds new group privilege to database.
     * @param entityManager the entity manager
     * @param group the group
     * @param privilegeKey the privilegeKey
     * @param dataIds the dataIds
     */
    public static void addGroupPrivileges(final EntityManager entityManager,
                                         final Group group, final String privilegeKey, final List<String> dataIds) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            for (final String dataId : dataIds) {
                entityManager.persist(new Privilege(group, null, privilegeKey, dataId));
            }
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in adding group privilege.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes user privilege from database.
     * @param entityManager the entity manager
     * @param user the user
     * @param privilegeKey the privilegeKey
     * @param dataId the dataId
     */
    public static void removeUserPrivilege(final EntityManager entityManager, final User user, final String privilegeKey, final String dataId) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            final TypedQuery<Privilege> query = entityManager.createQuery(
                    "select e from Privilege as e where e.user=:user and e.key=:key and e.dataId=:dataId",
                    Privilege.class);
            query.setParameter("user", user);
            query.setParameter("key", privilegeKey);
            query.setParameter("dataId", dataId);
            final List<Privilege> privileges = query.getResultList();
            if (privileges.size() > 0) {
                entityManager.remove(privileges.get(0));
            }
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in removing user privilege.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes user privileges from database.
     * @param entityManager the entity manager
     * @param user the user
     * @param privilegeKey the privilegeKey
     * @param dataIds the dataIds
     */
    public static void removeUserPrivileges(final EntityManager entityManager, final User user, final String privilegeKey, final List<String> dataIds) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            for (final String dataId : dataIds) {
                final TypedQuery<Privilege> query = entityManager.createQuery(
                        "select e from Privilege as e where e.user=:user and e.key=:key and e.dataId=:dataId",
                        Privilege.class);
                query.setParameter("user", user);
                query.setParameter("key", privilegeKey);
                query.setParameter("dataId", dataId);
                final List<Privilege> privileges = query.getResultList();
                if (privileges.size() > 0) {
                    entityManager.remove(privileges.get(0));
                }
            }
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in removing user privilege.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes group privilege from database.
     * @param entityManager the entity manager
     * @param group the group
     * @param privilegeKey the privilegeKey
     * @param dataId the dataId
     */
    public static void removeGroupPrivilege(final EntityManager entityManager, final Group group, final String privilegeKey, final String dataId) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            final TypedQuery<Privilege> query = entityManager.createQuery(
                    "select e from Privilege as e where e.group=:group and e.key=:key and e.dataId=:dataId",
                    Privilege.class);
            query.setParameter("group", group);
            query.setParameter("key", privilegeKey);
            query.setParameter("dataId", dataId);
            final List<Privilege> privileges = query.getResultList();
            if (privileges.size() > 0) {
                entityManager.remove(privileges.get(0));
            }
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in removing group privilege.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes group privileges from database.
     * @param entityManager the entity manager
     * @param group the group
     * @param privilegeKey the privilegeKey
     * @param dataIds the dataIds
     */
    public static void removeGroupPrivilege(final EntityManager entityManager, final Group group, final String privilegeKey, final List<String> dataIds) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            for (final String dataId : dataIds) {
                final TypedQuery<Privilege> query = entityManager.createQuery(
                        "select e from Privilege as e where e.group=:group and e.key=:key and e.dataId=:dataId",
                        Privilege.class);
                query.setParameter("group", group);
                query.setParameter("key", privilegeKey);
                query.setParameter("dataId", dataId);
                final List<Privilege> privileges = query.getResultList();
                if (privileges.size() > 0) {
                    entityManager.remove(privileges.get(0));
                }
            }
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in removing group privilege.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }
    /**
     * Check if user has given privilege.
     * @param entityManager the entity manager
     * @param user the user
     * @param privilegeKey the privilegeKey
     * @param dataId the dataId
     * @return true if user has privilege.
     */
    public static boolean hasUserPrivilege(final EntityManager entityManager, final User user,
            final String privilegeKey, final String dataId) {
        final TypedQuery<Long> query = entityManager.createQuery(
                    "select count(e) from Privilege as e where e.user=:user and e.key=:key and e.dataId=:dataId",
                    Long.class);
        query.setParameter("user", user);
        query.setParameter("key", privilegeKey);
        query.setParameter("dataId", dataId);
        return query.getSingleResult().longValue() > 0;
    }

    /**
     * Get user privileges.
     * @param entityManager the entity manager
     * @param user the user
     * @return list of user privileges.
     */
    public static List<Privilege> getUserPrivileges(final EntityManager entityManager, final User user) {
        final TypedQuery<Privilege> query = entityManager.createQuery(
                "select e from Privilege as e where e.user=:user",
                Privilege.class);
        query.setParameter("user", user);
        return query.getResultList();
    }

    /**
     * Get group privileges.
     * @param entityManager the entity manager
     * @param group the group
     * @return Llist of group privileges.
     */
    public static List<Privilege> getGroupPrivileges(final EntityManager entityManager, final Group group) {
        final TypedQuery<Privilege> query = entityManager.createQuery(
                "select e from Privilege as e where e.group=:group",
                Privilege.class);
        query.setParameter("group", group);
        return query.getResultList();
    }

    /**
     * Check if group has given privilege.
     * @param entityManager the entity manager
     * @param group the group
     * @param privilegeKey the privilegeKey
     * @param dataId the dataId
     * @return true if user has privilege.
     */
    public static boolean hasGroupPrivilege(final EntityManager entityManager, final Group group,
            final String privilegeKey, final String dataId) {
        final TypedQuery<Long> query = entityManager.createQuery(
                   "select count(e) from Privilege as e where e.group=:group and e.key=:key and e.dataId=:dataId",
                   Long.class);
        query.setParameter("group", group);
        query.setParameter("key", privilegeKey);
        query.setParameter("dataId", dataId);
        return query.getSingleResult().longValue() > 0;
    }

    /**
     * List privileges for given data.
     * @param entityManager the entity manager
     * @param privilegeKey the privilegeKey
     * @param dataId the dataId
     * @return true if user has privilege.
     */
    public static List<Privilege> listPrivileges(final EntityManager entityManager,
            final String privilegeKey, final String dataId) {
        final TypedQuery<Privilege> query = entityManager.createQuery(
                   "select e from Privilege as e where e.key=:key and e.dataId=:dataId",
                   Privilege.class);
        query.setParameter("key", privilegeKey);
        query.setParameter("dataId", dataId);
        return query.getResultList();
    }
}
