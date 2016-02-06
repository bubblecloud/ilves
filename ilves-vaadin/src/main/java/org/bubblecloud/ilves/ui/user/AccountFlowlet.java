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
package org.bubblecloud.ilves.ui.user;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Or;
import com.vaadin.event.MouseEvents;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.bubblecloud.ilves.component.button.LargeImageToggleButton;
import org.bubblecloud.ilves.component.flow.AbstractFlowlet;
import org.bubblecloud.ilves.component.grid.FieldDescriptor;
import org.bubblecloud.ilves.component.grid.FilterDescriptor;
import org.bubblecloud.ilves.component.grid.Grid;
import org.bubblecloud.ilves.model.*;
import org.bubblecloud.ilves.module.customer.CustomerModule;
import org.bubblecloud.ilves.security.*;
import org.bubblecloud.ilves.site.*;
import org.bubblecloud.ilves.ui.administrator.customer.CustomerFlowlet;
import org.bubblecloud.ilves.ui.administrator.group.GroupFlowlet;
import org.bubblecloud.ilves.util.OpenIdUtil;
import org.vaadin.addons.lazyquerycontainer.EntityContainer;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    /** The authentication device container. */
    private EntityContainer<AuthenticationDevice> authenticationDeviceContainer;
    /** The customer grid. */
    private Grid entityGrid;
    private LargeImageToggleButton googleAuthenticatorButton;
    private LargeImageToggleButton u2fRegisterButton;

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
        final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);

        final GridLayout gridLayout = new GridLayout(1, 7);
        gridLayout.setRowExpandRatio(0, 0.0f);
        gridLayout.setRowExpandRatio(1, 0.0f);
        gridLayout.setRowExpandRatio(2, 0.0f);
        gridLayout.setRowExpandRatio(3, 0.0f);
        gridLayout.setRowExpandRatio(4, 0.0f);
        gridLayout.setRowExpandRatio(5, 0.0f);
        gridLayout.setRowExpandRatio(6, 1.0f);

        gridLayout.setSizeFull();
        gridLayout.setMargin(false);
        gridLayout.setSpacing(true);
        gridLayout.setRowExpandRatio(4, 1f);
        setViewContent(gridLayout);

        authenticationDeviceContainer = new EntityContainer<AuthenticationDevice>(entityManager, true, false, false, AuthenticationDevice.class, 1000,
                new String[]{"name"}, new boolean[]{false}, "authenticationDeviceId");
        authenticationDeviceContainer.addContainerProperty("name", String.class, "", true, false);
        authenticationDeviceContainer.addDefaultFilter(new Compare.Equal("user", null));

        final VerticalLayout userAccountLayout = new VerticalLayout();
        userAccountLayout.setMargin(new MarginInfo(false, false, false, false));
        userAccountLayout.setSpacing(true);

            /*final Embedded titleIcon = new Embedded(null, getSite().getIcon("view-icon-customer"));
            titleIcon.setWidth(32, Unit.PIXELS);
            titleIcon.setHeight(32, Unit.PIXELS);
            titleLayout.addComponent(titleIcon);*/

        userAccountLayout.addComponent(new Label("<hr />", ContentMode.HTML));

        /*final Embedded userAccountTitleIcon = new Embedded(null, getSite().getIcon("view-icon-user"));
        userAccountTitleIcon.setWidth(32, Unit.PIXELS);
        userAccountTitleIcon.setHeight(32, Unit.PIXELS);
        userAccountTitle.addComponent(userAccountTitleIcon);*/
        final Label userAccountTitleLabel = new Label("<h2>" +getSite().localize("header-user-account") + "</h2>", ContentMode.HTML);
        userAccountLayout.addComponent(userAccountTitleLabel);

        gridLayout.addComponent(userAccountLayout, 0, 0);

        final VerticalLayout informationLayout = new VerticalLayout();
        informationLayout.setSpacing(true);
        gridLayout.addComponent(informationLayout, 0, 1);

        informationLayout.addComponent(new Label("<h3>" + getSite().localize("header-user-account-information") + "</h3>", ContentMode.HTML));
        final Button editUserButton = new Button(getSite().localize("button-edit-user-account-information"));
        editUserButton.setIcon(getSite().getIcon("button-icon-edit"));
        informationLayout.addComponent(editUserButton);
        editUserButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                final User entity = ((SecurityProviderSessionImpl)
                        getSite().getSecurityProvider()).getUserFromSession();
                final UserAccountFlowlet customerView = getFlow().getFlowlet(UserAccountFlowlet.class);
                customerView.edit(entity, false);
                getFlow().forward(UserAccountFlowlet.class);
            }
        });

        final Company company = getSite().getSiteContext().getObject(Company.class);
        if (company.isOpenIdLogin()) {
            final VerticalLayout openIdLayout = new VerticalLayout();
            openIdLayout.setCaption(getSite().localize("header-choose-open-id-provider"));
            gridLayout.addComponent(openIdLayout, 0, 2);
            final HorizontalLayout openIdProvidersLayout = new HorizontalLayout();
            openIdLayout.addComponent(openIdProvidersLayout);
            openIdProvidersLayout.setMargin(new MarginInfo(false, false, true, false));
            openIdProvidersLayout.setSpacing(true);
            final String returnViewName = "openidlink";
            final Map<String, String> urlIconMap = OpenIdUtil.getOpenIdProviderUrlIconMap();
            for (final String url : urlIconMap.keySet()) {
                openIdProvidersLayout.addComponent(OpenIdUtil.getLoginButton(url, urlIconMap.get(url), returnViewName));
            }
        }

        // Two factor authentication
        {
            final VerticalLayout mfaLayout = new VerticalLayout();
            mfaLayout.setSpacing(true);
            gridLayout.addComponent(mfaLayout, 0, 3);

            mfaLayout.addComponent(new Label("<h3>" + getSite().localize("header-register-mfa-device") + "</h3>", ContentMode.HTML));

            final HorizontalLayout mfaRegisterButtonLayout = new HorizontalLayout();
            mfaRegisterButtonLayout.setMargin(new MarginInfo(false, false, true, false));
            mfaLayout.addComponent(mfaRegisterButtonLayout);
            mfaRegisterButtonLayout.setSpacing(true);

            googleAuthenticatorButton = new LargeImageToggleButton("icons/twofactor/google-authenticator-large.png");
            googleAuthenticatorButton.addClickListener(new MouseEvents.ClickListener() {
                @Override
                public void click(MouseEvents.ClickEvent event) {
                    GoogleAuthenticatorService.startRegistration(new GoogleAuthenticatorRegistrationListener() {
                        @Override
                        public void onDeviceRegistrationSuccess() {
                            authenticationDeviceContainer.refresh();
                        }
                    });
                }
            });
            mfaRegisterButtonLayout.addComponent(googleAuthenticatorButton);

            u2fRegisterButton = new LargeImageToggleButton("icons/twofactor/u2f.png");
            u2fRegisterButton.addClickListener(new MouseEvents.ClickListener() {
                @Override
                public void click(MouseEvents.ClickEvent event) {
                    final U2fConnector u2fConnector = new U2fConnector();
                    u2fConnector.startRegistration(new U2fRegistrationListener() {
                        @Override
                        public void onDeviceRegistrationSuccess() {
                            authenticationDeviceContainer.refresh();
                        }
                    });

                }
            });
            mfaRegisterButtonLayout.addComponent(u2fRegisterButton);

            mfaLayout.addComponent(new Label("<h3>" + getSite().localize("header-registered-mfa-devices") + "</h3>", ContentMode.HTML));

            final com.vaadin.ui.Grid authenticationDeviceGrid = new com.vaadin.ui.Grid(authenticationDeviceContainer);
            authenticationDeviceGrid.setSelectionMode(com.vaadin.ui.Grid.SelectionMode.MULTI);
            authenticationDeviceGrid.setWidth(100, Unit.PERCENTAGE);
            authenticationDeviceGrid.setHeightMode(HeightMode.ROW);
            authenticationDeviceGrid.setHeightByRows(3);

            final HorizontalLayout buttonLayout = new HorizontalLayout();
            mfaLayout.addComponent(buttonLayout);
            final Button removeButton = new Button(getSite().localize("button-remove"), getSite().getIcon("button-icon-remove"));
            buttonLayout.addComponent(removeButton);
            removeButton.addClickListener(new ClickListener() {
                @Override
                public void buttonClick(ClickEvent event) {
                    for (final Object authenticationDeviceId : authenticationDeviceGrid.getSelectedRows()) {
                        AuthenticationDeviceDao.removeAuthenticationDevice(entityManager,
                                AuthenticationDeviceDao.getAuthenticationDevice(entityManager, (String) authenticationDeviceId));
                    }
                    authenticationDeviceContainer.refresh();
                }
            });

            mfaLayout.addComponent(authenticationDeviceGrid);
        }

        if (SiteModuleManager.isModuleInitialized(CustomerModule.class)) {
            final List<FieldDescriptor> fieldDefinitions = SiteFields.getFieldDescriptors(Customer.class);

            final List<FilterDescriptor> filterDefinitions = new ArrayList<FilterDescriptor>();
            filterDefinitions.add(new FilterDescriptor("companyName", "companyName", "Company Name", new TextField(), 101, "=", String.class, ""));
            filterDefinitions.add(new FilterDescriptor("lastName", "lastName", "Last Name", new TextField(), 101, "=", String.class, ""));

            entityContainer = new EntityContainer<Customer>(entityManager, true, false, false, Customer.class, 1000, new String[]{"companyName",
                    "lastName"}, new boolean[]{false, false}, "customerId");

            for (final FieldDescriptor fieldDefinition : fieldDefinitions) {
                entityContainer.addContainerProperty(fieldDefinition.getId(), fieldDefinition.getValueType(), fieldDefinition.getDefaultValue(),
                        fieldDefinition.isReadOnly(), fieldDefinition.isSortable());
            }

            final VerticalLayout titleLayout = new VerticalLayout();
            titleLayout.setMargin(new MarginInfo(true, false, false, false));
            titleLayout.setSpacing(true);
            /*final Embedded titleIcon = new Embedded(null, getSite().getIcon("view-icon-customer"));
            titleIcon.setWidth(32, Unit.PIXELS);
            titleIcon.setHeight(32, Unit.PIXELS);
            titleLayout.addComponent(titleIcon);*/

            titleLayout.addComponent(new Label("<hr />", ContentMode.HTML));

            final Label titleLabel = new Label("<h2>Customer Accounts</h2>", ContentMode.HTML);
            titleLayout.addComponent(titleLabel);
            gridLayout.addComponent(titleLayout, 0, 4);

            final Table table = new Table();
            table.setPageLength(5);
            entityGrid = new Grid(table, entityContainer);
            entityGrid.setFields(fieldDefinitions);
            entityGrid.setFilters(filterDefinitions);
            //entityGrid.setFixedWhereCriteria("e.owner.companyId=:companyId");

            table.setColumnCollapsed("created", true);
            table.setColumnCollapsed("modified", true);
            table.setColumnCollapsed("company", true);
            table.setColumnCollapsed("phoneNumber", true);
            table.setColumnCollapsed("emailAddress", true);

            gridLayout.addComponent(entityGrid, 0, 6);

            final HorizontalLayout customerButtonsLayout = new HorizontalLayout();
            gridLayout.addComponent(customerButtonsLayout, 0, 5);
            customerButtonsLayout.setMargin(false);
            customerButtonsLayout.setSpacing(true);

            final Button editCustomerDetailsButton = new Button("Edit Customer Details");
            customerButtonsLayout.addComponent(editCustomerDetailsButton);
            editCustomerDetailsButton.setEnabled(false);
            editCustomerDetailsButton.setIcon(getSite().getIcon("button-icon-edit"));
            editCustomerDetailsButton.addClickListener(new ClickListener() {
                /**
                 * Serial version UID.
                 */
                private static final long serialVersionUID = 1L;

                @Override
                public void buttonClick(final ClickEvent event) {
                    if (entityGrid.getSelectedItemId() == null) {
                        return;
                    }
                    final Customer entity = entityContainer.getEntity(entityGrid.getSelectedItemId());
                    final CustomerFlowlet customerView = getFlow().forward(CustomerFlowlet.class);
                    customerView.edit(entity, false);
                }
            });

            final Button editCustomerMembersButton = new Button("Edit Customer Members");
            customerButtonsLayout.addComponent(editCustomerMembersButton);
            editCustomerMembersButton.setEnabled(false);
            editCustomerMembersButton.setIcon(getSite().getIcon("button-icon-edit"));
            editCustomerMembersButton.addClickListener(new ClickListener() {
                /**
                 * Serial version UID.
                 */
                private static final long serialVersionUID = 1L;

                @Override
                public void buttonClick(final ClickEvent event) {
                    if (entityGrid.getSelectedItemId() == null) {
                        return;
                    }
                    final Customer entity = entityContainer.getEntity(entityGrid.getSelectedItemId());
                    final GroupFlowlet view = getFlow().forward(GroupFlowlet.class);
                    view.edit(entity.getMemberGroup(), false);
                }
            });

            final Button editCustomerAdminsButton = new Button("Edit Customer Admins");
            customerButtonsLayout.addComponent(editCustomerAdminsButton);
            editCustomerAdminsButton.setEnabled(false);
            editCustomerAdminsButton.setIcon(getSite().getIcon("button-icon-edit"));
            editCustomerAdminsButton.addClickListener(new ClickListener() {
                /**
                 * Serial version UID.
                 */
                private static final long serialVersionUID = 1L;

                @Override
                public void buttonClick(final ClickEvent event) {
                    if (entityGrid.getSelectedItemId() == null) {
                        return;
                    }
                    final Customer entity = entityContainer.getEntity(entityGrid.getSelectedItemId());
                    final GroupFlowlet view = getFlow().forward(GroupFlowlet.class);
                    view.edit(entity.getAdminGroup(), false);
                }
            });

            table.addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(final Property.ValueChangeEvent event) {
                    editCustomerDetailsButton.setEnabled(table.getValue() != null);
                    editCustomerMembersButton.setEnabled(table.getValue() != null);
                    editCustomerAdminsButton.setEnabled(table.getValue() != null);
                }
            });

        }
    }

    @Override
    public void enter() {
        final User user = ((SecurityProviderSessionImpl) getSite().getSecurityProvider()).getUserFromSession();

        if (SiteModuleManager.isModuleInitialized(CustomerModule.class)) {
            entityContainer.removeDefaultFilters();

            final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);

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

        authenticationDeviceContainer.removeDefaultFilters();
        authenticationDeviceContainer.addDefaultFilter(new Compare.Equal("user", user));
        authenticationDeviceContainer.refresh();

    }

}
