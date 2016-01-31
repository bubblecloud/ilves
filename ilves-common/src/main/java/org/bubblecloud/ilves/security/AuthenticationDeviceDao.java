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
import org.bubblecloud.ilves.model.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * AuthenticationDevice data access object.
 *
 * @author Tommi S.E. Laukkanen
 */
public class AuthenticationDeviceDao {

    /**
     * The logger.
     */
    private static final Logger LOGGER = Logger.getLogger(AuthenticationDeviceDao.class);

    /**
     * Adds authenticationDevice to database.
     *
     * @param entityManager the entity manager
     * @param authenticationDevice the authenticationDevice
     */
    protected static final void addAuthenticationDevice(final EntityManager entityManager,
                                                        final AuthenticationDevice authenticationDevice) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            authenticationDevice.setCreated(new Date());
            authenticationDevice.setModified(new Date());
            entityManager.persist(authenticationDevice);
            transaction.commit();
        } catch (final Exception e) {
            LOGGER.error("Error in add authentication device.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates authenticationDevice to database.
     *
     * @param entityManager the entity manager
     * @param authenticationDevice the authenticationDevice
     */
    protected static final void updateAuthenticationDevice(final EntityManager entityManager,
                                                           final AuthenticationDevice authenticationDevice) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            authenticationDevice.setModified(new Date());
            entityManager.persist(authenticationDevice);
            transaction.commit();
        } catch (final Exception e) {
            LOGGER.error("Error in update authenticationDevice.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes authenticationDevice from database.
     *
     * @param entityManager the entity manager
     * @param authenticationDevice the authenticationDevice
     */
    protected static final void removeAuthenticationDevice(final EntityManager entityManager,
                                                           final AuthenticationDevice authenticationDevice) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.remove(authenticationDevice);
            transaction.commit();
        } catch (final Exception e) {
            LOGGER.error("Error in remove authenticationDevice.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets given authenticationDevice.
     *
     * @param entityManager the entity manager.
     * @param authenticationDeviceId the authenticationDevice ID
     * @return the authenticationDevice
     */
    public static final AuthenticationDevice getAuthenticationDevice(final EntityManager entityManager,
                                                                     final String authenticationDeviceId) {
        try {
            return entityManager.getReference(AuthenticationDevice.class, authenticationDeviceId);
        } catch (final EntityNotFoundException e) {
            return null;
        }
    }

    /**
     * Gets list of authentication devices of given user.
     *
     * @param entityManager the entity manager.
     * @param user          the user
     * @return list of authentication devices
     */
    public static final List<AuthenticationDevice> getAuthenticationDevices(final EntityManager entityManager,
                                                                            final User user) {
        final TypedQuery<AuthenticationDevice> query = entityManager.createQuery(
                "select e from AuthenticationDevice as e where e.user=:user order by e.name",
                AuthenticationDevice.class);
        query.setParameter("user", user);
        return query.getResultList();
    }
}
