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
package org.bubblecloud.ilves.component.grid;

import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Like;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;
import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.lazyquerycontainer.LazyQueryView;
import org.vaadin.addons.lazyquerycontainer.QueryItemStatusColumnGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data grid component which supports filtering in addition to standard Table
 * features.
 *
 * @author Tommi S.E. Laukkanen
 */
public class Grid extends CustomComponent {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(Grid.class);
    /** Width of the status column. */
    private static final int STATUS_COLUMN_WIDTH = 18;
    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;
    /** Field definitions defining columns for the grid. */
    private List<FieldDescriptor> fields;
    /** Filter definitions defining filters for the grid. */
    private List<FilterDescriptor> filters;
    /** Table which represents the grid visually. */
    private Table table;
    /** The layout used for filter components. */
    private Layout filterLayout;
    private ArrayList<Object> visibleColumnIds;

    /**
     * Constructor for setting the internal Container to be used as data source.
     * @param table the Table to be used.
     * @param container the Container to be used.
     */
    public Grid(final Table table, final LazyQueryContainer container) {
        super();
        construct(table, container, true);
    }

    /**
     * Constructor for setting the internal Container to be used as data source.
     * @param table the Table to be used.
     * @param container the Container to be used.
     * @param showFilters true if filters should be shown
     */
    public Grid(final Table table, final LazyQueryContainer container, final boolean showFilters) {
        super();
        construct(table, container, showFilters);
    }

    /**
     * Constructs the grid layout.
     * @param table the table
     * @param container the container
     * @param showFilters true if filters should be shown.
     */
    private void construct(final Table table, final LazyQueryContainer container, final boolean showFilters) {
        this.table = table;

        table.setImmediate(true);
        table.setSelectable(true);
        table.setBuffered(false);
        table.setColumnCollapsingAllowed(true);
        table.setContainerDataSource(container);
        table.setSizeFull();

        if (showFilters) {
            final GridLayout layout = new GridLayout(1, 2);
            layout.setSpacing(true);
            layout.setRowExpandRatio(0, 0f);
            layout.setRowExpandRatio(1, 1f);

            filterLayout = new HorizontalLayout();
            ((HorizontalLayout) filterLayout).setSpacing(true);

            layout.addComponent(filterLayout, 0, 0);
            layout.addComponent(table, 0, 1);

            setCompositionRoot(layout);
            layout.setSizeFull();
            setSizeFull();
        } else {
            setCompositionRoot(table);
            setSizeFull();
        }
    }

    /**
     * Gets the Container acting as data source of this Grid.
     * @return the Container
     */
    protected final Container getContainer() {
        return table.getContainerDataSource();
    }

    /**
     * Gets the table which represents this Grid visually.
     * @return the Table
     */
    public final Table getTable() {
        return table;
    }

    /**
     * Sets the field definitions.
     * @param fieldDefinitions the fields to set
     */
    public final void setFields(final List<FieldDescriptor> fieldDefinitions) {
        this.fields = fieldDefinitions;
        visibleColumnIds = new ArrayList<Object>();
        final ArrayList<String> visibleColumnLabels = new ArrayList<String>();

        if (getContainer().getContainerPropertyIds().contains(LazyQueryView.PROPERTY_ID_ITEM_STATUS)) {
            visibleColumnIds.add(LazyQueryView.PROPERTY_ID_ITEM_STATUS);
            visibleColumnLabels.add("");
        }

        for (final FieldDescriptor fieldDefinition : fieldDefinitions) {
            visibleColumnIds.add(fieldDefinition.getId());
            visibleColumnLabels.add(fieldDefinition.getLabel());
        }
        table.setVisibleColumns(visibleColumnIds.toArray());
        table.setColumnHeaders(visibleColumnLabels.toArray(new String[0]));
        for (final FieldDescriptor fieldDefinition : fieldDefinitions) {
            if (fieldDefinition.getWidth() != -1) {
                table.setColumnWidth(fieldDefinition.getId(), fieldDefinition.getWidth());
            }
            if (fieldDefinition.isCollapsed()) {
                table.setColumnCollapsed(fieldDefinition.getId(), true);
            }
            if (fieldDefinition.getConverter() != null) {
                table.setConverter(fieldDefinition.getId(), (Converter<String, ?>) fieldDefinition.getConverter());
            }
            if (fieldDefinition.getValueAlignment() == HorizontalAlignment.CENTER) {
                table.setColumnAlignment(fieldDefinition.getId(), Table.Align.CENTER);
            } else if (fieldDefinition.getValueAlignment() == HorizontalAlignment.RIGHT) {
                table.setColumnAlignment(fieldDefinition.getId(), Table.Align.RIGHT);
            }
        }
        if (getContainer().getContainerPropertyIds().contains(LazyQueryView.PROPERTY_ID_ITEM_STATUS)) {
            table.setColumnWidth(LazyQueryView.PROPERTY_ID_ITEM_STATUS, STATUS_COLUMN_WIDTH);
            table.addGeneratedColumn(LazyQueryView.PROPERTY_ID_ITEM_STATUS, new QueryItemStatusColumnGenerator());
        }
    }

    /**
     * Sets the filter definitions.
     * @param filterDefinitions the filterDefinitions to set
     */
    public final void setFilters(final List<FilterDescriptor> filterDefinitions) {
        this.filters = filterDefinitions;
        if (filterLayout != null) {
            filterLayout.removeAllComponents();
            for (final FilterDescriptor filterDefinition : filterDefinitions) {
                filterDefinition.getField().setWidth(filterDefinition.getWidth() + "px");
                filterDefinition.getField().setCaption(filterDefinition.getLabel());
                filterDefinition.getField().setValue(filterDefinition.getDefaultValue());
                ((AbstractComponent) filterDefinition.getField()).setImmediate(true);
                filterDefinition.getField().addValueChangeListener(new Property.ValueChangeListener() {
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void valueChange(final ValueChangeEvent event) {
                        refresh();
                    }
                });
                filterLayout.addComponent(filterDefinition.getField());
            }
        }
    }

    /**
     * Gets the field definitions.
     * @return the fieldDefinitions
     */
    public final List<FieldDescriptor> getFields() {
        return fields;
    }

    /**
     * Gets the filter definitions.
     * @return the filterDefinitions
     */
    public final List<FilterDescriptor> getFilters() {
        return filters;
    }

    /**
     * Get visible column IDs.
     * @return the visible column IDs
     */
    public ArrayList<Object> getVisibleColumnIds() {
        return visibleColumnIds;
    }

    /**
     * Refreshes the Grid.
     */
    public final void refresh() {
        @SuppressWarnings("unchecked")
        final Container.Filterable container = (Container.Filterable) getContainer();
        final StringBuilder whereCriteria = new StringBuilder();
        final Map<String, Object> whereParameters = new HashMap<String, Object>();

        if (getFilters() != null) {
            container.removeAllContainerFilters();
            for (final FilterDescriptor filterDefinition : getFilters()) {
                Object value = filterDefinition.getField().getValue();
                if (value instanceof String && ((String) value).length() == 0) {
                    value = null;
                }
                if (value == null) {
                    continue;
                }
                if (!value.getClass().equals(filterDefinition.getValueType())) {
                    if (value instanceof String) {
                        if (filterDefinition.getValueType().equals(Long.class)) {
                            value = Long.parseLong((String) value);
                        }
                        if (filterDefinition.getValueType().equals(Integer.class)) {
                            value = Integer.parseInt((String) value);
                        }
                    }
                }

                final String operation = filterDefinition.getCriteriaOperator();
                final Object propertyId = filterDefinition.getPropertyId();

                if (operation.equals(">")) {
                    container.addContainerFilter(new Compare.Greater(propertyId, value));
                } else if (operation.equals(">=")) {
                    container.addContainerFilter(new Compare.GreaterOrEqual(propertyId, value));
                } else if (operation.equals("<")) {
                    container.addContainerFilter(new Compare.Less(propertyId, value));
                } else if (operation.equals("<=")) {
                    container.addContainerFilter(new Compare.LessOrEqual(propertyId, value));
                } else if (operation.equals("=")) {
                    container.addContainerFilter(new Compare.Equal(propertyId, value));
                } else if (operation.equals("like")) {
                    container.addContainerFilter(new Like((String) propertyId, (String) value));
                }

            }
        }

        ((LazyQueryContainer) getContainer()).refresh();
    }

    /**
     * Gets ID of the selected item or null.
     * @return ID of the selected item or null.
     */
    public final Object getSelectedItemId() {
        return table.getValue();
    }

    /**
     * Gets selected item or null.
     * @return the selected item or null.
     */
    public final Item getSelectedItem() {
        final Object itemId = table.getValue();
        return getContainer().getItem(itemId);
    }

    /**
     * @return the filterLayout
     */
    public final Layout getFilterLayout() {
        return filterLayout;
    }

}
