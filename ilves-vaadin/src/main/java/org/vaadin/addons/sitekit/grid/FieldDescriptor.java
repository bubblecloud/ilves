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

import com.vaadin.data.Validator;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.ui.Field;
import com.vaadin.ui.UI;
import org.vaadin.addons.sitekit.site.AbstractSiteUI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Value object containing field definition information.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class FieldDescriptor {
    /** ID of the field. */
    private final String id;
    /** Label localization key of the field. */
    private String labelKey;
    /** Width of the field. */
    private int width;
    /** Type of the field value. */
    private Class<?> valueType;
    /** Default value for field. */
    private Object defaultValue;
    /** Field editor component. */
    private Class<? extends Field> fieldClass;
    /** Field converter. */
    private Converter<?,?> converter;
    /** Reflects whether field is readonly. */
    private boolean readOnly;
    /** Reflects whether field is sortable. */
    private boolean sortable;
    /** Reflects whether field is required. */
    private boolean required;
    /** Reflects whether field is collapsed. */
    private boolean collapsed = false;
    /** The value alignment. */
    private HorizontalAlignment valueAlignment = HorizontalAlignment.LEFT;
    /** Validators. */
    private final List<Validator> validators = new ArrayList<Validator>();

    /**
     * Constructor for setting values of the FieldDefinition.
     * @param id ID of the field.
     * @param labelKey Localization key of the field.
     * @param fieldClass Field editor component or null.
     * @param converter Field converter.
     * @param width Width of the field.
     * @param valueAlignment Value vertical alignment.
     * @param valueType Type of the field value.
     * @param defaultValue Default value for field.
     * @param readOnly true if field is readonly.
     * @param sortable true if field is sortable.
     * @param required true if field is required.
     */
    public FieldDescriptor(final String id, final String labelKey, final Class<? extends Field> fieldClass,
            final Converter<?,?> converter, final int width,
            final HorizontalAlignment valueAlignment, final Class<?> valueType,
            final Object defaultValue, final boolean readOnly, final boolean sortable, final boolean required) {
        super();
        this.id = id;
        this.labelKey = labelKey;
        this.width = width;
        this.valueType = valueType;
        this.defaultValue = defaultValue;
        this.fieldClass = fieldClass;
        this.converter = converter;
        this.readOnly = readOnly;
        this.sortable = sortable;
        this.required = required;
        if (valueAlignment != null) {
            this.valueAlignment = valueAlignment;
        }
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        final AbstractSiteUI siteUI = (AbstractSiteUI) UI.getCurrent();
        final Locale locale = siteUI.getLocale();
        return siteUI.getSite().getLocalizationProvider().localize(labelKey, locale);
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
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

    /**
     * @return the field
     */
    public Class<?> getFieldClass() {
        return fieldClass;
    }

    /**
     * @return the field converter
     */
    public Converter<?,?> getConverter() {
        return converter;
    }

    /**
     * @return the readOnly
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * @return the sortable
     */
    public boolean isSortable() {
        return sortable;
    }

    /**
     * @return the required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * @return the collapsed
     */
    public boolean isCollapsed() {
        return collapsed;
    }

    /**
     * Adds validator to field descriptor.
     * @param validator the validator to add.
     * @return self.
     */
    public FieldDescriptor addValidator(final Validator validator) {
        validators.add(validator);
        return this;
    }

    /**
     * Gets validators.
     * @return list of validators.
     */
    public List<Validator> getValidators() {
        return validators;
    }

    /**
     * @return the valueAlignment
     */
    public HorizontalAlignment getValueAlignment() {
        return valueAlignment;
    }

    /**
     * @param labelKey the labelKey
     */
    public void setLabelKey(final String labelKey) {
        this.labelKey = labelKey;
    }

    /**
     * @param width the width
     */
    public void setWidth(final int width) {
        this.width = width;
    }

    /**
     * @param valueType the valueType
     */
    public void setValueType(final Class<?> valueType) {
        this.valueType = valueType;
    }

    /**
     * @param defaultValue the defaultValue
     */
    public void setDefaultValue(final Object defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * @param fieldClass the fieldClass
     */
    public void setFieldClass(final Class<? extends Field> fieldClass) {
        this.fieldClass = fieldClass;
    }

    /**
     * @param converter the converter
     */
    public void setConverter(final Converter<?, ?> converter) {
        this.converter = converter;
    }

    /**
     * @param readOnly the readOnly
     */
    public void setReadOnly(final boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * @param sortable the sortable
     */
    public void setSortable(final boolean sortable) {
        this.sortable = sortable;
    }

    /**
     * @param required the required
     */
    public void setRequired(final boolean required) {
        this.required = required;
    }

    /**
     * @param collapsed the collapsed
     */
    public void setCollapsed(final boolean collapsed) {
        this.collapsed = collapsed;
    }

    /**
     * @param valueAlignment the valueAlignment
     */
    public void setValueAlignment(final HorizontalAlignment valueAlignment) {
        this.valueAlignment = valueAlignment;
    }
}
