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

import org.vaadin.addons.sitekit.model.Customer;
import org.apache.log4j.Logger;

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
    public static void addCustomer(final EntityManager entityManager, final Customer customer) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            customer.setCreated(new Date());
            customer.setModified(new Date());
            entityManager.persist(customer);
            transaction.commit();
        } catch (final Exception e) {
            LOG.error("Error in add customer.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }
}
