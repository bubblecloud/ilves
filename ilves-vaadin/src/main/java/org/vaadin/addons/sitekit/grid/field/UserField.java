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
package org.vaadin.addons.sitekit.grid.field;

import com.vaadin.ui.ComboBox;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.site.AbstractSiteUI;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;

/**
 * Field for selecting group.
 *
 * @author Tommi S.E. Laukkanen
 */
public class UserField extends ComboBox {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor which populates the select with existing customers.
     */
    public UserField() {
        super();
    }

    @Override
    public final void attach() {
        super.attach();
        final EntityManager entityManager = ((AbstractSiteUI) getUI().getUI()).getSite().getSiteContext().getObject(
                EntityManager.class);
        final Company company = ((AbstractSiteUI) getUI().getUI()).getSite().getSiteContext().getObject(
                Company.class);
        final CriteriaBuilder queryBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteriaQuery = queryBuilder.createQuery(User.class);
        final Root<User> root = criteriaQuery.from(User.class);
        final Expression<Comparable> owner = root.get("owner");
        criteriaQuery.where(queryBuilder.equal(owner, company));
        criteriaQuery.orderBy(queryBuilder.asc(root.get("owner")),
                queryBuilder.asc(root.get("lastName")),
                queryBuilder.asc(root.get("firstName")));
        final TypedQuery<User> companyQuery = entityManager.createQuery(criteriaQuery);
        for (final User user : companyQuery.getResultList()) {
            addItem(user);
        }
    }

}
