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
import org.vaadin.addons.sitekit.model.Customer;
import org.vaadin.addons.sitekit.model.Group;

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
    private static final Logger LOG = Logger.getLogger(CustomerDao.class);

    /**
     * Adds new customer to database.
     * @param entityManager the entity manager
     * @param customer the group
     */
    public static void saveCustomer(final EntityManager entityManager, final Customer customer) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            if (customer.getCustomerId() != null) {
                customer.setCreated(new Date());
            }
            customer.setModified(new Date());


            if (customer.getMemberGroup() == null) {
                final Group memberGroup = new Group();
                memberGroup.setOwner(customer.getOwner());
                memberGroup.setCreated(new Date());
                customer.setMemberGroup(memberGroup);
            }
            customer.getMemberGroup().setModified(new Date());
            customer.getMemberGroup().setName("customer_members_" +
                    customer.toString().toLowerCase().replace(" ", "_").replace("(", "_").replace(")", "_"));
            customer.getMemberGroup().setDescription("Members / " + customer.toString());

            if (customer.getAdminGroup() == null) {
                final Group adminGroup = new Group();
                adminGroup.setOwner(customer.getOwner());
                adminGroup.setCreated(new Date());
                customer.setAdminGroup(adminGroup);
            }
            customer.getAdminGroup().setModified(new Date());
            customer.getAdminGroup().setName("customer_admins_" +
                    customer.toString().toLowerCase().replace(" ", "_").replace("(", "_").replace(")", "_"));
            customer.getAdminGroup().setDescription("Admins / " + customer.toString());

            entityManager.persist(customer);
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in add customer.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
        UserDao.addGroupPrivilege(entityManager, customer.getMemberGroup(), "member", customer.getCustomerId());
        UserDao.addGroupPrivilege(entityManager, customer.getAdminGroup(), "administrator", customer.getCustomerId());
    }

}
