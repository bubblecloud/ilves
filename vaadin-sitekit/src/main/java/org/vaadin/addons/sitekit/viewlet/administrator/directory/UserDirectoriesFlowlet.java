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
package org.vaadin.addons.sitekit.viewlet.administrator.directory;

import com.vaadin.data.util.filter.Compare;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.vaadin.addons.lazyquerycontainer.EntityContainer;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.grid.FieldDescriptor;
import org.vaadin.addons.sitekit.grid.FilterDescriptor;
import org.vaadin.addons.sitekit.grid.Grid;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.UserDirectory;
import org.vaadin.addons.sitekit.site.SiteFields;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * UserDirectory list flow.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class UserDirectoriesFlowlet extends AbstractFlowlet {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The entity container. */
    private EntityContainer<UserDirectory> entityContainer;
    /** The userDirectory grid. */
    private Grid entityGrid;

    @Override
    public String getFlowletKey() {
        return "directories";
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
        final List<FieldDescriptor> fieldDefinitions = new ArrayList<FieldDescriptor>(
                SiteFields.getFieldDescriptors(UserDirectory.class));

        for (final FieldDescriptor fieldDescriptor : fieldDefinitions) {
            if (fieldDescriptor.getId().equals("loginPassword")) {
                fieldDefinitions.remove(fieldDescriptor);
                break;
            }
        }

        final List<FilterDescriptor> filterDefinitions = new ArrayList<FilterDescriptor>();

        final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);
        entityContainer = new EntityContainer<UserDirectory>(entityManager, true, false, false, UserDirectory.class, 1000, new String[] { "address", "port" }, new boolean[] { true, true }, "userDirectoryId");

        for (final FieldDescriptor fieldDefinition : fieldDefinitions) {
            entityContainer.addContainerProperty(fieldDefinition.getId(), fieldDefinition.getValueType(), fieldDefinition.getDefaultValue(),
                    fieldDefinition.isReadOnly(), fieldDefinition.isSortable());
        }

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

        final Table table = new Table();
        entityGrid = new Grid(table, entityContainer);
        entityGrid.setFields(fieldDefinitions);
        entityGrid.setFilters(filterDefinitions);
        //entityGrid.setFixedWhereCriteria("e.owner.companyId=:companyId");

        table.setColumnCollapsed("loginDn", true);
        table.setColumnCollapsed("userEmailAttribute", true);
        table.setColumnCollapsed("userSearchBaseDn", true);
        table.setColumnCollapsed("groupSearchBaseDn", true);
        table.setColumnCollapsed("remoteLocalGroupMapping", true);
        table.setColumnCollapsed("created", true);
        table.setColumnCollapsed("modified", true);
        table.setColumnCollapsed("company", true);
        gridLayout.addComponent(entityGrid, 0, 1);

        final Button addButton = new Button("Add");
        addButton.setIcon(getSite().getIcon("button-icon-add"));
        addButton.setWidth(100, UNITS_PIXELS);
        buttonLayout.addComponent(addButton);

        addButton.addListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                final UserDirectory userDirectory = new UserDirectory();
                userDirectory.setCreated(new Date());
                userDirectory.setModified(userDirectory.getCreated());
                userDirectory.setOwner((Company) getSite().getSiteContext().getObject(Company.class));
                final UserDirectoryFlowlet userDirectoryView = getFlow().forward(UserDirectoryFlowlet.class);
                userDirectoryView.edit(userDirectory, true);
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
                final UserDirectory entity = entityContainer.getEntity(entityGrid.getSelectedItemId());
                final UserDirectoryFlowlet userDirectoryView = getFlow().forward(UserDirectoryFlowlet.class);
                userDirectoryView.edit(entity, false);
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

        final Company company = getSite().getSiteContext().getObject(Company.class);
        //entityGrid.getFixedWhereParameters().put("companyId", company.getCompanyId());
        entityContainer.removeDefaultFilters();
        entityContainer.addDefaultFilter(new Compare.Equal("owner.companyId", company.getCompanyId()));

        entityGrid.refresh();
    }

}
