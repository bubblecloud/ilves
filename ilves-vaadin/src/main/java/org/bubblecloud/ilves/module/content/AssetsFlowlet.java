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
package org.bubblecloud.ilves.module.content;

import com.vaadin.data.util.filter.Compare;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import org.bubblecloud.ilves.component.flow.AbstractFlowlet;
import org.bubblecloud.ilves.component.grid.FieldDescriptor;
import org.bubblecloud.ilves.component.grid.FieldSetDescriptorRegister;
import org.bubblecloud.ilves.component.grid.FilterDescriptor;
import org.bubblecloud.ilves.component.grid.Grid;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.util.ContainerUtil;
import org.vaadin.addons.lazyquerycontainer.EntityContainer;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Asset list flow.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class AssetsFlowlet extends AbstractFlowlet {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The entity container. */
    private EntityContainer<Asset> entityContainer;
    /** The asset grid. */
    private Grid entityGrid;

    @Override
    public String getFlowletKey() {
        return "assets";
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
        entityContainer = new EntityContainer<Asset>(entityManager, true, false, false, Asset.class, 1000,
                new String[] { "name" }, new boolean[] { true }, "assetId");

        // Get descriptors and set container properties.
        final List<FilterDescriptor> filterDescriptors = new ArrayList<FilterDescriptor>();
        final List<FieldDescriptor> fieldDescriptors = FieldSetDescriptorRegister.getFieldSetDescriptor(
                Asset.class).getFieldDescriptors();
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
                final Asset asset = new Asset();
                asset.setAssetId(UUID.randomUUID().toString());
                asset.setCreated(new Date());
                asset.setModified(asset.getCreated());
                asset.setOwner((Company) getSite().getSiteContext().getObject(Company.class));
                final AssetFlowlet assetView = getFlow().forward(AssetFlowlet.class);
                assetView.edit(asset, true);
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
                final Asset entity = entityContainer.getEntity(entityGrid.getSelectedItemId());
                final AssetFlowlet assetView = getFlow().forward(AssetFlowlet.class);
                assetView.edit(entity, false);
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
