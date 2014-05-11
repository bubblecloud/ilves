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
package org.vaadin.addons.sitekit.module.audit.view;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import org.vaadin.addons.lazyquerycontainer.EntityContainer;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.grid.FieldDescriptor;
import org.vaadin.addons.sitekit.grid.FieldSetDescriptorRegister;
import org.vaadin.addons.sitekit.grid.FilterDescriptor;
import org.vaadin.addons.sitekit.grid.Grid;
import org.vaadin.addons.sitekit.module.audit.model.AuditLogEntry;
import org.vaadin.addons.sitekit.util.ContainerUtil;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * AuditLogEntry list flow.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class AuditLogFlowlet extends AbstractFlowlet {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The entity container. */
    private EntityContainer<AuditLogEntry> entityContainer;
    /** The content grid. */
    private Grid entityGrid;

    @Override
    public String getFlowletKey() {
        return "audit";
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
        // Get entity manager from site context and prepare container.
        final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);
        entityContainer = new EntityContainer<AuditLogEntry>(entityManager, true, false, false, AuditLogEntry.class, 1000,
                new String[] { "page" }, new boolean[] { true }, "auditLogEntryId");

        // Get descriptors and set container properties.
        final List<FilterDescriptor> filterDescriptors = new ArrayList<FilterDescriptor>();
        final List<FieldDescriptor> fieldDescriptors = FieldSetDescriptorRegister.getFieldSetDescriptor(
                AuditLogEntry.class).getFieldDescriptors();
        ContainerUtil.addContainerProperties(entityContainer, fieldDescriptors);

        // Initialize layout
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

        // Initialize grid
        entityGrid = new Grid(new Table(), entityContainer);
        entityGrid.setFields(fieldDescriptors);
        entityGrid.setFilters(filterDescriptors);
        gridLayout.addComponent(entityGrid, 0, 1);

        final Button viewButton = getSite().getButton("view");
        buttonLayout.addComponent(viewButton);
        viewButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                final AuditLogEntry entity = entityContainer.getEntity(entityGrid.getSelectedItemId());
                final AuditLogEntryFlowlet contentView = getFlow().forward(AuditLogEntryFlowlet.class);
                contentView.edit(entity, false);
            }
        });

    }

    @Override
    public void enter() {
        //final Company company = getSite().getSiteContext().getObject(Company.class);
        entityContainer.removeDefaultFilters();
        //entityContainer.addDefaultFilter(new Compare.Equal("owner.companyId", company.getCompanyId()));
        entityGrid.refresh();
    }

}
