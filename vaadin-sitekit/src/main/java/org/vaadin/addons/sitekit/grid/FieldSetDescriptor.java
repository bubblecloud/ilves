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
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;
import org.vaadin.addons.sitekit.grid.field.TimestampField;
import org.vaadin.addons.sitekit.grid.formatter.ObjectToStringFormatter;
import org.vaadin.addons.sitekit.grid.formatter.TimestampFormatter;
import org.vaadin.addons.sitekit.util.StringUtil;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Value object containing set of field descriptors.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class FieldSetDescriptor {

    /**
     * The field descriptors.
     */
    private List<FieldDescriptor> fieldDescriptors = new ArrayList<FieldDescriptor>();

    /**
     * Constructs field set descriptor from bean class.
     *
     * @param beanClass the bean class
     */
    public FieldSetDescriptor(final Class<?> beanClass) {
        // Try to introspect, if it fails, we just have an empty Item
        try {
            List<PropertyDescriptor> propertyDescriptors = getBeanPropertyDescriptor(beanClass);

            // Add all the bean properties as MethodProperties to this Item
            // later entries on the list overwrite earlier ones
            for (PropertyDescriptor pd : propertyDescriptors) {
                final Method getMethod = pd.getReadMethod();
                if ((getMethod != null)
                        && getMethod.getDeclaringClass() != Object.class) {
                    final String fieldId = pd.getName();
                    final String labelKey = StringUtil.fromCamelCaseToLocalizationKeyConvetion(fieldId);
                    final Class<?> valueType = pd.getPropertyType();

                    final Class<? extends Field> fieldClass;
                    final Class<? extends PropertyFormatter> formatterClass;
                    final int width;
                    final HorizontalAlignment valueAlignment;
                    final Object defaultValue;
                    final boolean readOnly;
                    final List<Validator> validators = new ArrayList<Validator>();
                    if (valueType.equals(String.class)) {
                        fieldClass = TextField.class;
                        formatterClass = null;
                        width = 150;
                        valueAlignment = HorizontalAlignment.LEFT;
                        defaultValue = null;
                        readOnly = false;
                        validators.add(
                                new StringLengthValidator("Invalid length.", 0, 255, true));
                    } else if (valueType.equals(Integer.class)) {
                        fieldClass = TextField.class;
                        formatterClass = null;
                        width = 50;
                        valueAlignment = HorizontalAlignment.LEFT;
                        defaultValue = null;
                        readOnly = false;
                        validators.add(
                                new IntegerRangeValidator("Not integer.", Integer.MIN_VALUE, Integer.MIN_VALUE));
                    } else if (valueType.equals(Long.class)) {
                        fieldClass = TextField.class;
                        formatterClass = null;
                        width = 50;
                        valueAlignment = HorizontalAlignment.LEFT;
                        defaultValue = null;
                        readOnly = false;
                        validators.add(
                                new IntegerRangeValidator("Not integer.", Integer.MIN_VALUE, Integer.MIN_VALUE));
                    }  else if (valueType.equals(Boolean.class)) {
                        fieldClass = CheckBox.class;
                        formatterClass = null;
                        width = 50;
                        valueAlignment = HorizontalAlignment.LEFT;
                        defaultValue = null;
                        readOnly = false;
                    } else if (valueType.equals(Date.class)) {
                        fieldClass = TimestampField.class;
                        formatterClass = TimestampFormatter.class;
                        width = 150;
                        valueAlignment = HorizontalAlignment.LEFT;
                        defaultValue = null;
                        readOnly = false;
                    } else {
                        fieldClass = TextField.class;
                        formatterClass = ObjectToStringFormatter.class;
                        width = 100;
                        valueAlignment = HorizontalAlignment.LEFT;
                        defaultValue = null;
                        readOnly = true;
                    }

                    final boolean sortable = true;
                    final boolean required = false;

                    final FieldDescriptor fieldDescriptor = new FieldDescriptor(
                            fieldId,
                            labelKey,
                            fieldClass,
                            formatterClass,
                            width,
                            valueAlignment,
                            valueType,
                            defaultValue,
                            readOnly,
                            sortable,
                            required
                    );

                    for (final Validator validator : validators) {
                        fieldDescriptor.addValidator(validator);
                    }

                    fieldDescriptors.add(fieldDescriptor);
                }
            }
        } catch (final java.beans.IntrospectionException ignored) {
        }

    }

    /**
     * @return fieldDescriptors
     */
    public List<FieldDescriptor> getFieldDescriptors() {
        return fieldDescriptors;
    }

    /**
     * Gets field descriptor based on fieldId.
     * @param fieldId the fieldId
     * @return the field descriptor.
     */
    public FieldDescriptor getFieldDescriptor(final String fieldId) {
        for (final FieldDescriptor fieldDescriptor : fieldDescriptors) {
            if (fieldDescriptor.getId().equals(fieldId)) {
                return fieldDescriptor;
            }
        }
        throw new IllegalArgumentException("No such field id: " + fieldId);
    }

    /**
     * Returns the property descriptors of a class or an interface.
     *
     * For an interface, superinterfaces are also iterated as Introspector does
     * not take them into account (Oracle Java bug 4275879), but in that case,
     * both the setter and the getter for a property must be in the same
     * interface and should not be overridden in subinterfaces for the discovery
     * to work correctly.
     *
     * For interfaces, the iteration is depth first and the properties of
     * superinterfaces are returned before those of their subinterfaces.
     *
     * @param beanClass the bean class
     * @return list of property descriptors
     * @throws java.beans.IntrospectionException if exception occurs in introspection
     */
    private static List<PropertyDescriptor> getBeanPropertyDescriptor(
            final Class<?> beanClass) throws IntrospectionException {
        if (beanClass.isInterface()) {
            List<PropertyDescriptor> propertyDescriptors = new ArrayList<PropertyDescriptor>();

            for (Class<?> cls : beanClass.getInterfaces()) {
                propertyDescriptors.addAll(getBeanPropertyDescriptor(cls));
            }

            BeanInfo info = Introspector.getBeanInfo(beanClass);
            propertyDescriptors.addAll(Arrays.asList(info
                    .getPropertyDescriptors()));

            return propertyDescriptors;
        } else {
            BeanInfo info = Introspector.getBeanInfo(beanClass);
            return Arrays.asList(info.getPropertyDescriptors());
        }
    }
}
