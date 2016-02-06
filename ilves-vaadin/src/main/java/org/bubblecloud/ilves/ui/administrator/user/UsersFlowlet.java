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
package org.bubblecloud.ilves.ui.administrator.user;

import com.vaadin.data.util.filter.Compare;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.bubblecloud.ilves.component.flow.AbstractFlowlet;
import org.bubblecloud.ilves.component.grid.FieldDescriptor;
import org.bubblecloud.ilves.component.grid.FilterDescriptor;
import org.bubblecloud.ilves.component.grid.FormattingTable;
import org.bubblecloud.ilves.component.grid.Grid;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.model.Group;
import org.bubblecloud.ilves.model.Privilege;
import org.bubblecloud.ilves.model.User;
import org.bubblecloud.ilves.security.SecurityService;
import org.bubblecloud.ilves.security.U2fService;
import org.bubblecloud.ilves.security.UserDao;
import org.bubblecloud.ilves.site.SiteFields;
import org.bubblecloud.ilves.util.ContainerUtil;
import org.vaadin.addons.lazyquerycontainer.EntityContainer;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User list Flowlet.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class UsersFlowlet extends AbstractFlowlet {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The container. */
    private EntityContainer<User> container;
    /** The grid. */
    private Grid grid;

    @Override
    public String getFlowletKey() {
        return "users";
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
        final List<FieldDescriptor> fieldDescriptors = SiteFields.getFieldDescriptors(User.class);

        final List<FilterDescriptor> filterDefinitions = new ArrayList<FilterDescriptor>();

        final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);
        container = new EntityContainer<User>(entityManager, true, false, false, User.class, 1000,
                new String[] {"lastName", "firstName"},
                new boolean[] {true, true}, "userId");

        ContainerUtil.addContainerProperties(container, fieldDescriptors);

        final GridLayout gridLayout = new GridLayout(1, 2);
        gridLayout.setSizeFull();
        gridLayout.setMargin(false);
        gridLayout.setSpacing(true);
        gridLayout.setRowExpandRatio(1, 1f);
        setViewContent(gridLayout);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setSizeUndefined();
        gridLayout.addComponent(buttonLayout, 0, 0);

        final Table table = new FormattingTable();
        grid = new Grid(table, container);
        grid.setFields(fieldDescriptors);
        grid.setFilters(filterDefinitions);

        table.setColumnCollapsed("created", true);
        table.setColumnCollapsed("modified", true);
        table.setColumnCollapsed("passwordHash", true);
        table.setColumnCollapsed("openIdIdentifier", true);
        table.setColumnCollapsed("certificate", true);
        table.setColumnCollapsed("failedLoginCount", true);
        gridLayout.addComponent(grid, 0, 1);

        final Button addButton = getSite().getButton("add");
        buttonLayout.addComponent(addButton);
        addButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                final User user = new User();
                user.setCreated(new Date());
                user.setModified(user.getCreated());
                user.setOwner((Company) getSite().getSiteContext().getObject(Company.class));

                final UserFlowlet userView = getFlow().getFlowlet(UserFlowlet.class);
                userView.edit(user, true);
                getFlow().forward(UserFlowlet.class);
            }
        });

        final Button editButton = getSite().getButton("edit");
        buttonLayout.addComponent(editButton);
        editButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                if (grid.getSelectedItemId() == null) {
                    return;
                }
                final User entity = container.getEntity(grid.getSelectedItemId());
                final UserFlowlet userView = getFlow().getFlowlet(UserFlowlet.class);
                userView.edit(entity, false);
                getFlow().forward(UserFlowlet.class);
            }
        });

        final Button removeButton = getSite().getButton("remove");
        buttonLayout.addComponent(removeButton);
        removeButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                if (grid.getSelectedItemId() == null) {
                    return;
                }
                final User entity = container.getEntity(grid.getSelectedItemId());

                final List<Group> groups = UserDao.getUserGroups(entityManager,
                        (Company) getSite().getSiteContext().getObject(Company.class), entity);

                for (final Group group : groups) {
                    SecurityService.removeGroupMember(getSite().getSiteContext(), group, entity);
                }

                final List<Privilege> privileges = UserDao.getUserPrivileges(entityManager, entity);
                for (final Privilege privilege : privileges) {
                    SecurityService.removeUserPrivilege(getSite().getSiteContext(), entity, privilege.getKey(), null, privilege.getDataId(), null);
                }

                SecurityService.removeUser(getSite().getSiteContext(), entity);
                container.refresh();
            }
        });

        final Button  lockButton = getSite().getButton("lock");
        lockButton.setImmediate(true);
        buttonLayout.addComponent(lockButton);
        lockButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                if (grid.getSelectedItemId() == null) {
                    return;
                }
                final User user = container.getEntity(grid.getSelectedItemId());
                user.setLockedOut(true);
                SecurityService.updateUser(getSite().getSiteContext(), user);
                container.refresh();
            }
        });

        final Button  unlockButton = getSite().getButton("unlock");
        unlockButton.setImmediate(true);
        buttonLayout.addComponent(unlockButton);
        unlockButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                if (grid.getSelectedItemId() == null) {
                    return;
                }
                final User user = container.getEntity(grid.getSelectedItemId());
                user.setLockedOut(false);
                user.setFailedLoginCount(0);
                SecurityService.updateUser(getSite().getSiteContext(), user);
                container.refresh();
            }
        });

        final Button disableTwoFactorAuthentication = getSite().getButton("disable-two-factor-authentication");
        disableTwoFactorAuthentication.setImmediate(true);
        buttonLayout.addComponent(disableTwoFactorAuthentication);
        disableTwoFactorAuthentication.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                if (grid.getSelectedItemId() == null) {
                    return;
                }
                final User user = container.getEntity(grid.getSelectedItemId());
                if (U2fService.hasDeviceRegistrations(getSite().getSiteContext(), user.getEmailAddress())) {
                    U2fService.removeDeviceRegistrations(getSite().getSiteContext(), user.getEmailAddress());
                }
                container.refresh();
                Notification.show(getSite().localize("message-disabled-two-factor-authentication-for-user"),
                        Notification.Type.HUMANIZED_MESSAGE);
            }
        });

        final Company company = getSite().getSiteContext().getObject(Company.class);
        container.removeDefaultFilters();
        container.addDefaultFilter(
                new Compare.Equal("owner.companyId", company.getCompanyId()));
        grid.refresh();
    }

    @Override
    public void enter() {
        container.refresh();
    }

}
