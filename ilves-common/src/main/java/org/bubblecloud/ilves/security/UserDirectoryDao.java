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
package org.bubblecloud.ilves.security;

import org.apache.log4j.Logger;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.model.UserDirectory;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

/**
 * User directory data access object.
 *
 * @author Tommi S.E. Laukkanen
 */
public class UserDirectoryDao {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(UserDirectoryDao.class);

    /**
     * Adds userDirectory to database.
     * @param entityManager the entity manager
     * @param userDirectory the userDirectory
     */
    protected static final void addUserDirectory(final EntityManager entityManager, final UserDirectory userDirectory) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.persist(userDirectory);
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in add userDirectory.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates userDirectory to database.
     * @param entityManager the entity manager
     * @param userDirectory the userDirectory
     */
    protected static final void updateUserDirectory(final EntityManager entityManager, final UserDirectory userDirectory) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            userDirectory.setModified(new Date());
            entityManager.persist(userDirectory);
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in update userDirectory.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes userDirectory from database.
     * @param entityManager the entity manager
     * @param userDirectory the userDirectory
     */
    protected static final void removeUserDirectory(final EntityManager entityManager, final UserDirectory userDirectory) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.remove(userDirectory);
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in remove userDirectory.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets given userDirectory.
     * @param entityManager the entity manager.
     * @param userDirectoryId the userDirectory ID
     * @return the group
     */
    public static final UserDirectory getUserDirectory(final EntityManager entityManager, final String userDirectoryId) {
        try {
            return entityManager.getReference(UserDirectory.class, userDirectoryId);
        } catch (final EntityNotFoundException e) {
            return null;
        }
    }

    /**
     * Gets list of user directories configured for company.
     *
     * @param entityManager the entity manager.
     * @param owner the owning company
     * @return list of user directories
     */
    public static final List<UserDirectory> getUserDirectories(final EntityManager entityManager, final Company owner) {
        final TypedQuery<UserDirectory> query = entityManager.createQuery("select e from UserDirectory as e where e.owner=:owner",
                UserDirectory.class);
        query.setParameter("owner", owner);
        return query.getResultList();
    }
}
