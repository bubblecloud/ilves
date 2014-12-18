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
import org.bubblecloud.ilves.model.Customer;
import org.bubblecloud.ilves.model.Group;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.Date;

/**
 * Customer data access object.
 *
 * @author Tommi S.E. Laukkanen
 *
 */
public class CustomerDao {

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(CustomerDao.class);

    /**
     * Adds new customer to database.
     * @param entityManager the entity manager
     * @param customer the group
     */
    protected static void addCustomer(final EntityManager entityManager, final Customer customer) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            customer.setCreated(new Date());
            customer.setModified(new Date());

            final Group memberGroup = new Group();
            memberGroup.setOwner(customer.getOwner());
            memberGroup.setCreated(new Date());
            memberGroup.setModified(new Date());
            memberGroup.setName("members_" + customer.toString().toLowerCase().replace(" ", "_").replace("(", "_").replace(")", "_"));
            memberGroup.setDescription("Members of " + customer.toString());
            customer.setMemberGroup(memberGroup);

            final Group adminGroup = new Group();
            adminGroup.setOwner(customer.getOwner());
            adminGroup.setCreated(new Date());
            adminGroup.setModified(new Date());
            adminGroup.setName("admins_" + customer.toString().toLowerCase().replace(" ", "_").replace("(", "_").replace(")", "_"));
            adminGroup.setDescription("Administrators of " + customer.toString());
            customer.setAdminGroup(adminGroup);

            entityManager.persist(customer);
            transaction.commit();
        } catch (final Exception e) {
            LOGGER.error("Error adding customer.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
        UserDao.addGroupPrivilege(entityManager, customer.getMemberGroup(), "member", customer.getCustomerId());
        UserDao.addGroupPrivilege(entityManager, customer.getAdminGroup(), DefaultPrivileges.ADMINISTER, customer.getCustomerId());
        UserDao.addGroupPrivilege(entityManager, customer.getAdminGroup(), DefaultPrivileges.ADMINISTER, customer.getAdminGroup().getGroupId());
        UserDao.addGroupPrivilege(entityManager, customer.getAdminGroup(), DefaultPrivileges.ADMINISTER, customer.getMemberGroup().getGroupId());
    }

    /**
     * Updates customer to database.
     * @param entityManager the entity manager
     * @param customer the group
     */
    protected static void updateCustomer(final EntityManager entityManager, final Customer customer) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            customer.setModified(new Date());

            customer.getMemberGroup().setModified(new Date());
            customer.getMemberGroup().setName("members_" +
                    customer.toString().toLowerCase().replace(" ", "_").replace("(", "_").replace(")", "_"));
            customer.getMemberGroup().setDescription("Members of " + customer.toString());

            customer.getAdminGroup().setModified(new Date());
            customer.getAdminGroup().setName("admins_" +
                    customer.toString().toLowerCase().replace(" ", "_").replace("(", "_").replace(")", "_"));
            customer.getAdminGroup().setDescription("Admins of " + customer.toString());

            entityManager.persist(customer);
            transaction.commit();
        } catch (final Exception e) {
            LOGGER.error("Error updating customer.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes customer from database.
     * @param entityManager the entity manager
     * @param customer the customer
     */
    protected static final void removeCustomer(final EntityManager entityManager, final Customer customer) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.remove(customer);
            transaction.commit();
        } catch (final Exception e) {
            LOGGER.error("Error removing customer.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

}
