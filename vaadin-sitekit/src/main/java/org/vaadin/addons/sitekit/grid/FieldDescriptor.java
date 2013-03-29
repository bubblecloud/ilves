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
import com.vaadin.ui.Field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Value object containing field definition information.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class FieldDescriptor {
    /** ID of the field. */
    private final String id;
    /** Label of the field. */
    private final String label;
    /** Width of the field. */
    private final int width;
    /** Type of the field value. */
    private final Class<?> valueType;
    /** Default value for field. */
    private final Object defaultValue;
    /** Field editor component. */
    private final Class<? extends Field> fieldClass;
    /** Field formatter. */
    private final Class<? extends PropertyFormatter> formatterClass;
    /** Reflects whether field is readonly. */
    private final boolean readOnly;
    /** Reflects whether field is sortable. */
    private final boolean sortable;
    /** Reflects whether field is required. */
    private final boolean required;
    /** The value alignment. */
    private HorizontalAlignment valueAlignment = HorizontalAlignment.LEFT;
    /** Validators. */
    private final List<Validator> validators = new ArrayList<Validator>();

    /**
     * Constructor for setting values of the FieldDefinition.
     * @param id ID of the field.
     * @param label Label of the field.
     * @param fieldClass Field editor component or null.
     * @param formatterClass Field value formatter class.
     * @param width Width of the field.
     * @param valueAlignment Value vertical alignment.
     * @param valueType Type of the field value.
     * @param defaultValue Default value for field.
     * @param readOnly true if field is readonly.
     * @param sortable true if field is sortable.
     * @param required true if field is required.
     */
    public FieldDescriptor(final String id, final String label, final Class<? extends Field> fieldClass,
            final Class<? extends PropertyFormatter> formatterClass, final int width, final HorizontalAlignment valueAlignment, final Class<?> valueType,
            final Object defaultValue, final boolean readOnly, final boolean sortable, final boolean required) {
        super();
        this.id = id;
        this.label = label;
        this.width = width;
        this.valueType = valueType;
        this.defaultValue = defaultValue;
        this.fieldClass = fieldClass;
        this.formatterClass = formatterClass;
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
        return label;
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
     * @return the propertyFormatterClass
     */
    public Class<? extends PropertyFormatter> getFormatterClass() {
        return formatterClass;
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
        return Collections.unmodifiableList(validators);
    }

    /**
     * @return the valueAlignment
     */
    public HorizontalAlignment getValueAlignment() {
        return valueAlignment;
    }

}
