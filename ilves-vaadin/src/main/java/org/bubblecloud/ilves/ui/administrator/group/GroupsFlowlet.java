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
package org.bubblecloud.ilves.ui.administrator.group;

import com.vaadin.data.util.filter.Compare;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
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
import org.bubblecloud.ilves.security.UserDao;
import org.bubblecloud.ilves.site.SiteFields;
import org.bubblecloud.ilves.util.ContainerUtil;
import org.vaadin.addons.lazyquerycontainer.EntityContainer;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Group list Flowlet.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class GroupsFlowlet extends AbstractFlowlet {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The container. */
    private EntityContainer<Group> container;
    /** The grid. */
    private Grid grid;

    @Override
    public String getFlowletKey() {
        return "groups";
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
        final List<FieldDescriptor> fieldDescriptors = SiteFields.getFieldDescriptors(Group.class);

        final List<FilterDescriptor> filterDefinitions = new ArrayList<FilterDescriptor>();

        final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);
        container = new EntityContainer<Group>(entityManager, true, false, false, Group.class, 1000,
                new String[] {"name"},
                new boolean[] {true}, "groupId");

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
        gridLayout.addComponent(grid, 0, 1);

        final Button addButton = getSite().getButton("add");
        buttonLayout.addComponent(addButton);
        addButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                final Group group = new Group();
                group.setCreated(new Date());
                group.setModified(group.getCreated());
                group.setOwner((Company) getSite().getSiteContext().getObject(Company.class));
                final GroupFlowlet groupView = getFlow().forward(GroupFlowlet.class);
                groupView.edit(group, true);
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
                final Group entity = container.getEntity(grid.getSelectedItemId());
                final GroupFlowlet groupView = getFlow().forward(GroupFlowlet.class);
                groupView.edit(entity, false);
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
                final Group entity = container.getEntity(grid.getSelectedItemId());
                final List<User> users = UserDao.getGroupMembers(entityManager,
                        (Company) getSite().getSiteContext().getObject(Company.class), entity);

                for (final User user : users) {
                    SecurityService.removeGroupMember(getSite().getSiteContext(), entity, user);
                }

                final List<Privilege> privileges = UserDao.getGroupPrivileges(entityManager, entity);
                for (final Privilege privilege : privileges) {
                    SecurityService.removeGroupPrivilege(getSite().getSiteContext(), entity, privilege.getKey(), null, privilege.getDataId(), null);
                }

                SecurityService.removeGroup(getSite().getSiteContext(), entity);
                container.refresh();
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
