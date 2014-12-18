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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;
import java.util.List;

/**
 * Company data access object.
 *
 * @author Tommi S.E. Laukkanen
 */
public class CompanyDao {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(UserDao.class);

    /**
     * Adds new company to database.
     * @param entityManager the entity manager
     * @param company the company
     */
    protected static void addCompany(final EntityManager entityManager, final Company company) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.persist(company);
            transaction.commit();
        } catch (final Exception e) {
            LOGGER.error("Error in add company.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Updates new company to database.
     * @param entityManager the entity manager
     * @param company the company
     */
    protected static void updateCompany(final EntityManager entityManager, final Company company) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            company.setModified(new Date());
            entityManager.persist(company);
            transaction.commit();
        } catch (final Exception e) {
            LOGGER.error("Error in update company.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes company from database.
     * @param entityManager the entity manager
     * @param company the company
     */
    protected static void removeCompany(final EntityManager entityManager, final Company company) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            entityManager.remove(company);
            transaction.commit();
        } catch (final Exception e) {
            LOGGER.error("Error in remove company.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets company.
     * @param entityManager the entity manager
     * @param host the company host name
     * @return company or null
     */
    public static Company getCompany(final EntityManager entityManager, final String host) {
        final CriteriaBuilder queryBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Company> criteriaQuery = queryBuilder.createQuery(Company.class);
        final Root<Company> companyRoot = criteriaQuery.from(Company.class);
        final Predicate condition = queryBuilder.equal(companyRoot.get("host"), host);
        criteriaQuery.where(condition);
        final TypedQuery<Company> typedQuery = entityManager.createQuery(criteriaQuery);
        final List<Company> companies = typedQuery.getResultList();
        if (companies.size() > 0) {
            return companies.get(0);
        } else {
            return null;
        }
    }

    /**
     * Gets companies.
     * @param entityManager the entity manager
     * @return list of companies
     */
    public static List<Company> getCompanies(final EntityManager entityManager) {
        final CriteriaBuilder queryBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Company> criteriaQuery = queryBuilder.createQuery(Company.class);
        final Root<Company> companyRoot = criteriaQuery.from(Company.class);
        final TypedQuery<Company> typedQuery = entityManager.createQuery(criteriaQuery);
        return typedQuery.getResultList();
    }
}
