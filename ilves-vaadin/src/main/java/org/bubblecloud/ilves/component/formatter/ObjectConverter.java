package org.bubblecloud.ilves.component.formatter;

import com.vaadin.data.util.converter.Converter;

import java.util.Locale;

/**
 * Percentage converter.
 */
public class ObjectConverter implements Converter<String, Object> {

    @Override
    public Object convertToModel(String value, Class<? extends Object> targetType, Locale locale) throws ConversionException {
        if (value == null || value.length() == 0) {
            return null;
        } else {
            throw new ConversionException("ObjectConverter does not support back to model conversion.");
        }
    }

    @Override
    public String convertToPresentation(Object value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        } else {
            return value.toString();
        }
    }

    @Override
    public Class<Object> getModelType() {
        return Object.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
