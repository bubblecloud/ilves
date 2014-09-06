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
package org.vaadin.addons.sitekit.module.content.view;

import com.vaadin.data.util.filter.Compare;
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
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.module.content.model.Content;
import org.vaadin.addons.sitekit.util.ContainerUtil;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Content list flow.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class ContentsFlowlet extends AbstractFlowlet {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The entity container. */
    private EntityContainer<Content> entityContainer;
    /** The content grid. */
    private Grid entityGrid;

    @Override
    public String getFlowletKey() {
        return "contents";
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
        entityContainer = new EntityContainer<Content>(entityManager, true, false, false, Content.class, 1000,
                new String[] { "page" }, new boolean[] { true }, "contentId");

        // Get descriptors and set container properties.
        final List<FilterDescriptor> filterDescriptors = new ArrayList<FilterDescriptor>();
        final List<FieldDescriptor> fieldDescriptors = FieldSetDescriptorRegister.getFieldSetDescriptor(
                Content.class).getFieldDescriptors();
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

        final Button addButton = getSite().getButton("add");
        buttonLayout.addComponent(addButton);
        addButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                final Content content = new Content();
                content.setCreated(new Date());
                content.setModified(content.getCreated());
                content.setOwner((Company) getSite().getSiteContext().getObject(Company.class));
                final ContentFlowlet contentView = getFlow().forward(ContentFlowlet.class);
                contentView.edit(content, true);
            }
        });

        final Button editButton = getSite().getButton("edit");
        buttonLayout.addComponent(editButton);
        editButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                if (entityGrid.getSelectedItemId() == null) {
                    return;
                }
                final Content entity = entityContainer.getEntity(entityGrid.getSelectedItemId());
                final ContentFlowlet contentView = getFlow().forward(ContentFlowlet.class);
                contentView.edit(entity, false);
            }
        });

        final Button removeButton = getSite().getButton("remove");
        buttonLayout.addComponent(removeButton);
        removeButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                if (entityGrid.getSelectedItemId() == null) {
                    return;
                }
                entityContainer.removeItem(entityGrid.getSelectedItemId());
                entityContainer.commit();
            }
        });
    }

    @Override
    public void enter() {
        final Company company = getSite().getSiteContext().getObject(Company.class);
        entityContainer.removeDefaultFilters();
        entityContainer.addDefaultFilter(new Compare.Equal("owner.companyId", company.getCompanyId()));
        entityGrid.refresh();
    }

}
