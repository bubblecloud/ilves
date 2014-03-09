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

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Field;
import com.vaadin.ui.Form;
import com.vaadin.ui.FormFieldFactory;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data edit form implementation.
 *
 * @author Tommi S.E. Laukkanen
 */
public class ValidatingEditor extends CustomComponent implements
        FormFieldFactory, ValueChangeListener, TextChangeListener {

    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;
    /** Form component. */
    private final Form form;
    /** Field definitions defining columns for the grid. */
    private final List<FieldDescriptor> fieldDescriptors;
    /** Visible properties of the form. */
    private final Object[] fieldIds;
    /** Visible labels of the fields. */
    private final Label[] fieldLabels;
    /** Visible labels of the fields. */
    private final Field[] fields;
    /** Visible status icons of the fields. */
    private final Embedded[] fieldIcons;
    /** The layout of the form. */
    private final GridLayout formLayout;
    /** The property field index map. */
    private final Map<Field, Integer> fieldIndexes = new HashMap<Field, Integer>();
    /** The none getIcon. */
    private final ThemeResource noneIcon = new ThemeResource("icons/field-status-icon-none.png");
    /** The none getIcon. */
    private final ThemeResource newIcon = new ThemeResource("icons/field-status-icon-new.png");
    /** The ok getIcon. */
    private final ThemeResource validIcon = new ThemeResource("icons/field-status-icon-ok.png");
    /** The invalid getIcon. */
    private final ThemeResource invalidIcon = new ThemeResource("icons/field-status-icon-invalid.png");
    /** The db getIcon. */
    private final ThemeResource dbIcon = new ThemeResource("icons/field-status-icon-db.png");
    /** True if item edited is new. */
    private boolean newItem = false;
    /** Validation state change listeners. */
    private final List<ValidatingEditorStateListener> listeners = new ArrayList<ValidatingEditorStateListener>();
    /** True if value change listener is disabled. */
    private boolean disableValueChangeListener = false;

    /**
     * Constructor which initializes the form.
     * @param fieldDescriptors The field definitions.
     */
    public ValidatingEditor(final List<FieldDescriptor> fieldDescriptors) {
        this.fieldDescriptors = fieldDescriptors;

        form = new Form();
        form.setBuffered(true);
        form.setFormFieldFactory(this);
        form.setImmediate(true);
        setCompositionRoot(form);

        formLayout = new GridLayout(3, fieldDescriptors.size());
        formLayout.setSpacing(true);
        formLayout.setMargin(new MarginInfo(true, false, true, false));
        form.setLayout(formLayout);

        fieldIds = new Object[fieldDescriptors.size()];
        fields = new Field[fieldDescriptors.size()];
        fieldLabels = new Label[fieldDescriptors.size()];
        fieldIcons = new Embedded[fieldDescriptors.size()];
        for (int i = 0; i < fieldDescriptors.size(); i++) {
            final FieldDescriptor fieldDescriptor = fieldDescriptors.get(i);
            fieldIds[i] = fieldDescriptor.getId();
            fieldLabels[i] = new Label(fieldDescriptor.getLabel());
            fieldIcons[i] = new Embedded(null, noneIcon);
            fieldIcons[i].setWidth(20, UNITS_PIXELS);
            fieldIcons[i].setHeight(20, UNITS_PIXELS);

            formLayout.addComponent(fieldLabels[i], 0, i);
            formLayout.addComponent(fieldIcons[i], 2, i);
            formLayout.setComponentAlignment(fieldLabels[i], Alignment.MIDDLE_RIGHT);
        }
    }

    /**
     * @return the form
     */
    public final Form getForm() {
        return form;
    }

    /**
     * Sets the Item to be edited.
     * @param item the Item to be edited.
     * @param newItem true if item being edited is new.
     */
    public final void setItem(final Item item, final boolean newItem) {
        disableValueChangeListener = true;
        if (item != null) {
            form.setItemDataSource(item);
            form.setVisibleItemProperties(fieldIds);
        } else {
            form.setItemDataSource(null);
        }
        disableValueChangeListener = false;
        this.newItem = newItem;
        refreshFieldState(newItem);
        notifyStateChange();
    }

    /**
     * Commits changes to edited item.
     */
    public final void commit() {
        disableValueChangeListener = true;
        form.commit();
        disableValueChangeListener = false;
        newItem = false;
        if (refreshFieldState(false)) {
            notifyStateChange();
        }
    }

    /**
     * Discards changes to edited item.
     */
    public final void discard() {
        disableValueChangeListener = true;
        form.discard();
        disableValueChangeListener = false;
        if (refreshFieldState(newItem)) {
            notifyStateChange();
        }
    }

    /**
     * Gets the Item being edited.
     * @return the Item being edited.
     */
    public final Item getItem() {
        return form.getItemDataSource();
    }

    /**
     * Creates a field based on the item, property id and the component (most
     * commonly {@link com.vaadin.ui.Form}) where the Field will be presented.
     *
     * @param item the item where the property belongs to.
     * @param propertyId the Id of the property.
     * @param uiContext the component where the field is presented, most
     *            commonly this is {@link com.vaadin.ui.Form}. uiContext will not necessary be
     *            the parent component of the field, but the one that is
     *            responsible for creating it.
     * @return Field the field suitable for editing the specified data.
     */
    @Override
    public final Field createField(final Item item, final Object propertyId, final Component uiContext) {
        for (int i = 0; i < fieldDescriptors.size(); i++) {
            final FieldDescriptor fieldDefinition = fieldDescriptors.get(i);
            if (propertyId.equals(fieldDefinition.getId()) && fieldDefinition.getFieldClass() != null) {
                try {
                    final Field field = (Field) fieldDefinition.getFieldClass().newInstance();
                    if (field instanceof TextField) {
                        ((TextField) field).setNullRepresentation("");
                        ((TextField) field).setTextChangeTimeout(200);
                        ((TextField) field).addListener((TextChangeListener) this);
                    }
                    if (field instanceof PasswordField) {
                        ((PasswordField) field).setNullRepresentation("");
                        ((PasswordField) field).setTextChangeTimeout(200);
                        ((PasswordField) field).addListener((TextChangeListener) this);
                    }
                    ((AbstractField) field).setValidationVisible(false);
                    for (final Validator validator : fieldDefinition.getValidators()) {
                        field.addValidator(validator);
                    }
                    field.setRequired(fieldDefinition.isRequired());
                    ((AbstractField) field).setConverter(fieldDefinition.getConverter());
                    field.setPropertyDataSource(null);
                    field.setWidth(fieldDefinition.getWidth(), UNITS_PIXELS);
                    field.setReadOnly(isReadOnly() || fieldDefinition.isReadOnly());
                    if (!fieldDefinition.isReadOnly()) {
                        field.setValue(fieldDefinition.getDefaultValue());
                    }
                    field.addListener(this);
                    ((AbstractComponent) field).setImmediate(true);
                    formLayout.setCursorX(1);
                    formLayout.setCursorY(i);
                    fieldIndexes.put(field, i);
                    fields[i] = field;
                    return field;
                } catch (final Throwable t) {
                    throw new RuntimeException("Error instantiating field: " + propertyId, t);
                }
            }
        }
        return null;
    }

    /**
     * Can be used to validate the contents of a field. This can be used to
     * invoke validation of dependent field from another validator.
     *
     * @param propertyId the property ID
     */
    public final void validateField(final Object propertyId) {
        for (int i = 0; i < fieldDescriptors.size(); i++) {
            if (fieldDescriptors.get(i).getId().equals(propertyId)) {
                refreshFieldState(i, newItem);
                notifyStateChange();
            }
        }
    }

    /**
     * Refreshes field icons.
     * @param newItem true if item being edited is new.
     * @return true if field state changed.
     */
    private boolean refreshFieldState(final boolean newItem) {
        boolean stateChange = false;
        for (int i = 0; i < fieldDescriptors.size(); i++) {
            if (refreshFieldState(i, newItem)) {
                stateChange = true;
            }
        }
        return stateChange;
    }

    /**
     * Notify state change to listeners.
     */
    public final void notifyStateChange() {
        for (final ValidatingEditorStateListener listener : listeners) {
            listener.editorStateChanged(this);
        }
    }

    /**
     * Updates field state getIcon of given field index.
     * @param fieldIndex index of the field.
     * @param newItem true if item being edited is new.
     * @return true if field state changed.
     */
    public final boolean refreshFieldState(final int fieldIndex, final boolean newItem) {
        final Resource currentIcon = fieldIcons[fieldIndex].getSource();
        if (fields[fieldIndex].isReadOnly()) {
            fieldIcons[fieldIndex].setSource(noneIcon);
        } else if (newItem) {
            if (fields[fieldIndex] instanceof CheckBox) {
                fieldIcons[fieldIndex].setSource(dbIcon);
            } else {
                if (fields[fieldIndex].isValid()) {
                    fieldIcons[fieldIndex].setSource(dbIcon);
                    fieldIcons[fieldIndex].setDescription("");
                } else {
                    fieldIcons[fieldIndex].setSource(newIcon);
                }
            }
        } else if (fields[fieldIndex].isModified()) {
            if (fields[fieldIndex].isValid()) {
                fieldIcons[fieldIndex].setSource(validIcon);
                fieldIcons[fieldIndex].setDescription("");
            } else {
                fieldIcons[fieldIndex].setSource(invalidIcon);
                try {
                    fields[fieldIndex].validate();
                } catch (final InvalidValueException e) {
                    fieldIcons[fieldIndex].setDescription(e.getMessage());
                }
            }
        } else {
            fieldIcons[fieldIndex].setSource(dbIcon);
        }
        return fieldIcons[fieldIndex].getSource() != currentIcon;
    }

    /**
     * Adds state change listener.
     * @param listener the listener to add.
     */
    public final void addListener(final ValidatingEditorStateListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes state change listener.
     * @param listener the listener to remove.
     */
    public final void removeListener(final ValidatingEditorStateListener listener) {
        listeners.remove(listener);
    }

    /**
     * Does form contain modified field values.
     * @return true if modifications exist.
     */
    public final boolean isModified() {
        for (int i = 0; i < fieldIcons.length; i++) {
            final Resource icon = fieldIcons[i].getSource();
            if (icon == validIcon || icon == invalidIcon) {
                return true;
            }
        }
        return false;
    }

    /**
     * Are all form field values valid.
     * @return true if all field values are valid.
     */
    public final boolean isValid() {
        for (int i = 0; i < fieldIcons.length; i++) {
            final Resource icon = fieldIcons[i].getSource();
            if (icon != validIcon && icon != noneIcon && icon != dbIcon) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return true if the item edited is new.
     */
    public final boolean isNewItem() {
        return newItem;
    }

    @Override
    public final void valueChange(final ValueChangeEvent event) {
        if (!disableValueChangeListener) {
            if (refreshFieldState(fieldIndexes.get(event.getProperty()), false)) {
                notifyStateChange();
            }
        }
    }

    @Override
    public final void textChange(final TextChangeEvent event) {
        boolean valid = true;
        final int fieldIndex = fieldIndexes.get((Field) event.getComponent());
        if (fields[fieldIndex].getValidators() != null) {
            if ((event.getText() == null || event.getText().length() == 0) &&
                    fields[fieldIndex].isRequired()) {
                fieldIcons[fieldIndex].setDescription("");
                valid = false;
            } else {
                for (final Validator validator : fields[fieldIndex].getValidators()) {
                    try {
                        final Converter converter = ((AbstractField) fields[fieldIndex]).getConverter();
                        final Object value;
                        if (converter != null) {
                            value = converter.convertToModel(event.getText(), converter.getModelType(),
                                ((AbstractField) fields[fieldIndex]).getLocale());
                        } else {
                            value = event.getText();
                        }
                        validator.validate(value);
                        fieldIcons[fieldIndex].setDescription("");
                    } catch (final InvalidValueException e) {
                        fieldIcons[fieldIndex].setDescription(e.getMessage());
                        valid = false;
                    } catch (final Exception e) {
                        fieldIcons[fieldIndex].setDescription("");
                        valid = false;
                    }
                }
            }
        }
        final Resource originalSource = fieldIcons[fieldIndex].getSource();
        if (valid) {
            fieldIcons[fieldIndex].setSource(validIcon);
        } else {
            fieldIcons[fieldIndex].setSource(invalidIcon);
        }
        if (originalSource != fieldIcons[fieldIndex].getSource()) {
            notifyStateChange();
        }
    }

    @Override
    public final void setCaption(final String caption) {
        form.setCaption(caption);
    }

}
