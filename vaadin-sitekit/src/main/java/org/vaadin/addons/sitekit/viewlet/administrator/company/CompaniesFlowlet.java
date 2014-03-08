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
package org.vaadin.addons.sitekit.viewlet.administrator.company;

import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.grid.FieldDescriptor;
import org.vaadin.addons.sitekit.grid.FilterDescriptor;
import org.vaadin.addons.sitekit.grid.Grid;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.PostalAddress;
import org.vaadin.addons.sitekit.site.SiteFields;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import org.vaadin.addons.lazyquerycontainer.EntityContainer;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Company list flow.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class CompaniesFlowlet extends AbstractFlowlet {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The entity container. */
    private EntityContainer<Company> entityContainer;
    /** The grid layout. */
    private GridLayout gridLayout;
    /** The entity manager. */
    private EntityManager entityManager;
    /** The entity grid. */
    private Grid entityGrid;

    @Override
    public String getFlowletKey() {
        return "companies";
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void initialize() {
        entityManager = getSite().getSiteContext().getObject(EntityManager.class);

        gridLayout = new GridLayout(1, 2);
        gridLayout.setSizeFull();
        gridLayout.setMargin(false);
        gridLayout.setSpacing(true);
        gridLayout.setRowExpandRatio(1, 1f);
        setViewContent(gridLayout);

        final List<FieldDescriptor> fieldDefinitions = SiteFields.getFieldDescriptors(Company.class);

        final List<FilterDescriptor> filterDefinitions = new ArrayList<FilterDescriptor>();
        filterDefinitions.add(new FilterDescriptor("companyName", "companyName", "Company Name", new TextField(), 101, "=", String.class, ""));

        entityContainer = new EntityContainer<Company>(entityManager, true, false, false, Company.class, 1000,
                new String[] { "companyName" },
                new boolean[] { false }, "companyId");
        for (final FieldDescriptor fieldDefinition : fieldDefinitions) {
            entityContainer.addContainerProperty(fieldDefinition.getId(), fieldDefinition.getValueType(), fieldDefinition.getDefaultValue(),
                    fieldDefinition.isReadOnly(), fieldDefinition.isSortable());
        }

        final Table table = new Table();
        entityGrid = new Grid(table, entityContainer);
        entityGrid.setFields(fieldDefinitions);
        entityGrid.setFilters(filterDefinitions);

        table.setColumnCollapsed("created", true);
        table.setColumnCollapsed("modified", true);
        table.setColumnCollapsed("company", true);
        table.setColumnCollapsed("emailPasswordReset", true);
        table.setColumnCollapsed("openIdLogin", true);
        table.setColumnCollapsed("maxFailedLoginCount", true);
        table.setColumnCollapsed("salesEmailAddress", true);
        table.setColumnCollapsed("supportEmailAddress", true);
        table.setColumnCollapsed("invoicingEmailAddress", true);
        gridLayout.addComponent(entityGrid, 0, 1);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setSizeUndefined();
        gridLayout.addComponent(buttonLayout, 0, 0);

        final Button addButton = new Button("Add");
        addButton.setIcon(getSite().getIcon("button-icon-add"));
        addButton.setWidth(100, UNITS_PIXELS);
        buttonLayout.addComponent(addButton);

        addButton.addListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                final Company company = new Company();
                company.setCreated(new Date());
                company.setModified(company.getCreated());
                company.setInvoicingAddress(new PostalAddress());
                company.setDeliveryAddress(new PostalAddress());
                final CompanyFlowlet companyView = getFlow().forward(CompanyFlowlet.class);
                companyView.edit(company, true);
            }
        });

        final Button editButton = new Button("Edit");
        editButton.setIcon(getSite().getIcon("button-icon-edit"));
        editButton.setWidth(100, UNITS_PIXELS);
        buttonLayout.addComponent(editButton);
        editButton.addListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                final Company entity = entityContainer.getEntity(entityGrid.getSelectedItemId());
                final CompanyFlowlet companyView = getFlow().forward(CompanyFlowlet.class);
                companyView.edit(entity, false);
            }
        });

        final Button removeButton = new Button("Remove");
        removeButton.setIcon(getSite().getIcon("button-icon-remove"));
        removeButton.setWidth(100, UNITS_PIXELS);
        buttonLayout.addComponent(removeButton);
        removeButton.addListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                entityContainer.removeItem(entityGrid.getSelectedItemId());
                entityContainer.commit();
            }
        });

    }

    @Override
    public void enter() {
        entityGrid.refresh();
    }

}
