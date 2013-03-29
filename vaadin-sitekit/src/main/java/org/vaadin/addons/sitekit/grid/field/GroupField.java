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

import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.site.AbstractSiteUI;
import com.vaadin.ui.Select;

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
public class GroupField extends Select {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor which populates the select with existing customers.
     */
    public GroupField() {
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
        final CriteriaQuery<Group> criteriaQuery = queryBuilder.createQuery(Group.class);
        final Root<Group> root = criteriaQuery.from(Group.class);
        final Expression<Comparable> owner = root.get("owner");
        criteriaQuery.where(queryBuilder.equal(owner, company));
        final TypedQuery<Group> companyQuery = entityManager.createQuery(criteriaQuery);
        for (final Group group : companyQuery.getResultList()) {
            addItem(group);
        }
    }

}
