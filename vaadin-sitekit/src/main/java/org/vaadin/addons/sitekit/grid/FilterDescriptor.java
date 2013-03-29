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
package org.vaadin.addons.sitekit.grid;

import com.vaadin.ui.Field;

/**
 * Value object containing filter definition information.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class FilterDescriptor {
    /** ID of the filter. */
    private String id;
    /** ID of the property. */
    private String propertyId;
    /** Label of the filter. */
    private String label;
    /** Width of the filter field. */
    private int width;
    /** Filter field component. */
    private Field field;
    /** Criteria operator for example: =,<,> etc. This is contract between container and grid. */
    private String criteriaOperator;
    /** Type of the filter value. */
    private Class<?> valueType;
    /** Default value for the filter or null. */
    private Object defaultValue;

    /**
     * Constructor for setting values of the FilterDefinition.
     * @param id ID of the filter.
     * @param propertyId ID of the property.
     * @param label Label of the filter.
     * @param field Filter field component.
     * @param width Width of the filter field.
     * @param criteriaOperator Criteria operator for example: =,<,> etc. This is contract between container and grid.
     * @param valueType Type of the filter value.
     * @param defaultValue Default value for the filter or null.
     */
    public FilterDescriptor(final String id, final String propertyId, final String label, final Field field,
            final int width, final String criteriaOperator, final Class<?> valueType, final Object defaultValue) {
        super();
        this.id = id;
        this.propertyId = propertyId;
        this.label = label;
        this.width = width;
        this.field = field;
        this.criteriaOperator = criteriaOperator;
        this.valueType = valueType;
        this.defaultValue = defaultValue;
    }
    /**
     * @return the id
     */
    public String getId() {
        return id;
    }
    /**
     * @return the propertyId
     */
    public String getPropertyId() {
        return propertyId;
    }
    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }
    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }
    /**
     * @return the field
     */
    public Field getField() {
        return field;
    }
    /**
     * @return the criteriaOperator
     */
    public String getCriteriaOperator() {
        return criteriaOperator;
    }
    /**
     * @return the valueType
     */
    public Class<?> getValueType() {
        return valueType;
    }
    /**
     * @return the defaultValue
     */
    public Object getDefaultValue() {
        return defaultValue;
    }

}
