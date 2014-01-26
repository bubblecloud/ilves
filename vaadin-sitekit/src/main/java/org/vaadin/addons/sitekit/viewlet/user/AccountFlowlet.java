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
package org.vaadin.addons.sitekit.viewlet.user;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Or;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import org.vaadin.addons.lazyquerycontainer.EntityContainer;
import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.grid.FieldDescriptor;
import org.vaadin.addons.sitekit.grid.FilterDescriptor;
import org.vaadin.addons.sitekit.grid.Grid;
import org.vaadin.addons.sitekit.model.Customer;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.site.SecurityProviderSessionImpl;
import org.vaadin.addons.sitekit.viewlet.administrator.customer.CustomerFlowlet;
import org.vaadin.addons.sitekit.site.SiteFields;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Customer list flow.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class AccountFlowlet extends AbstractFlowlet {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The entity container. */
    private EntityContainer<Customer> entityContainer;
    /** The customer grid. */
    private Grid entityGrid;

    @Override
    public String getFlowletKey() {
        return "account";
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
        final List<FieldDescriptor> fieldDefinitions = SiteFields.getFieldDescriptors(Customer.class);

        final List<FilterDescriptor> filterDefinitions = new ArrayList<FilterDescriptor>();
        filterDefinitions.add(new FilterDescriptor("companyName", "companyName", "Company Name", new TextField(), 101, "=", String.class, ""));
        filterDefinitions.add(new FilterDescriptor("lastName", "lastName", "Last Name", new TextField(), 101, "=", String.class, ""));

        final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);
        entityContainer = new EntityContainer<Customer>(entityManager, true, false, false, Customer.class, 1000, new String[] { "companyName",
                "lastName" }, new boolean[] { false, false }, "customerId");

        for (final FieldDescriptor fieldDefinition : fieldDefinitions) {
            entityContainer.addContainerProperty(fieldDefinition.getId(), fieldDefinition.getValueType(), fieldDefinition.getDefaultValue(),
                    fieldDefinition.isReadOnly(), fieldDefinition.isSortable());
        }

        final GridLayout gridLayout = new GridLayout(1, 5);
        gridLayout.setSizeFull();
        gridLayout.setMargin(false);
        gridLayout.setSpacing(true);
        gridLayout.setRowExpandRatio(4, 1f);
        setViewContent(gridLayout);

        final HorizontalLayout userAccountTitle = new HorizontalLayout();
        userAccountTitle.setMargin(new MarginInfo(true, false, true, false));
        userAccountTitle.setSpacing(true);
        final Embedded userAccountTitleIcon = new Embedded(null, getSite().getIcon("view-icon-user"));
        userAccountTitleIcon.setWidth(32, UNITS_PIXELS);
        userAccountTitleIcon.setHeight(32, UNITS_PIXELS);
        userAccountTitle.addComponent(userAccountTitleIcon);
        final Label userAccountTitleLabel = new Label("<h2>User Account</h2>", Label.CONTENT_XHTML);
        userAccountTitle.addComponent(userAccountTitleLabel);
        gridLayout.addComponent(userAccountTitle, 0, 0);

        final HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setMargin(new MarginInfo(true, false, true, false));
        titleLayout.setSpacing(true);
        final Embedded titleIcon = new Embedded(null, getSite().getIcon("view-icon-customer"));
        titleIcon.setWidth(32, UNITS_PIXELS);
        titleIcon.setHeight(32, UNITS_PIXELS);
        titleLayout.addComponent(titleIcon);
        final Label titleLabel = new Label("<h2>Customer Accounts</h2>", Label.CONTENT_XHTML);
        titleLayout.addComponent(titleLabel);
        gridLayout.addComponent(titleLayout, 0, 2);

        final Table table = new Table();
        entityGrid = new Grid(table, entityContainer);
        entityGrid.setFields(fieldDefinitions);
        entityGrid.setFilters(filterDefinitions);
        //entityGrid.setFixedWhereCriteria("e.owner.companyId=:companyId");

        table.setColumnCollapsed("created", true);
        table.setColumnCollapsed("modified", true);
        table.setColumnCollapsed("company", true);
        gridLayout.addComponent(entityGrid, 0, 4);

        final Button editUserButton = new Button("Edit User Account");
        editUserButton.setIcon(getSite().getIcon("button-icon-edit"));
        editUserButton.setWidth(200, UNITS_PIXELS);
        gridLayout.addComponent(editUserButton, 0, 1);
        editUserButton.addListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                final User entity = ((SecurityProviderSessionImpl)
                        getSite().getSecurityProvider()).getUserFromSession();
                final UserAccountFlowlet customerView = getViewSheet().getFlowlet(UserAccountFlowlet.class);
                customerView.edit(entity, false);
                getViewSheet().forward(UserAccountFlowlet.class);
            }
        });


        final Button editContactDetailsButton = new Button("Edit Customer Account");
        editContactDetailsButton.setEnabled(false);
        editContactDetailsButton.setIcon(getSite().getIcon("button-icon-edit"));
        editContactDetailsButton.setWidth(200, UNITS_PIXELS);
        gridLayout.addComponent(editContactDetailsButton, 0, 3);

        editContactDetailsButton.addListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                final Customer entity = entityContainer.getEntity(entityGrid.getSelectedItemId());
                final CustomerFlowlet customerView = getViewSheet().forward(CustomerFlowlet.class);
                customerView.edit(entity, false);
            }
        });

        table.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(final Property.ValueChangeEvent event) {
                editContactDetailsButton.setEnabled(table.getValue() != null);
            }
        });

    }

    @Override
    public void enter() {

        entityContainer.removeDefaultFilters();

        final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);
        final User user = ((SecurityProviderSessionImpl) getSite().getSecurityProvider()).getUserFromSession();

        if (user != null) {
            final List<Group> groups = UserDao.getUserGroups(entityManager, user.getOwner(), user);
            Container.Filter filter = null;
            for (final Group group : groups) {
                if (filter == null) {
                    filter = new Compare.Equal("adminGroup", group);
                } else {
                    filter = new Or(filter, new Compare.Equal("adminGroup", group));
                }
            }
            if (filter != null) {
                entityContainer.addDefaultFilter(filter);
            }
        }

        entityGrid.refresh();

    }

}
