package org.bubblecloud.ilves.component.formatter;

import com.vaadin.data.util.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Timestamp converter.
 */
public class DatePartConverter implements Converter<String, Date> {
    /** The format for currency values. */
    private SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public Date convertToModel(String value, Class<? extends Date> targetType, Locale locale) throws ConversionException {
        if (value == null || value.length() == 0) {
            return null;
        } else {
            try {
                return format.parse(value);
            } catch (final ParseException e) {
                throw new ConversionException(e);
            }
        }
    }

    @Override
    public String convertToPresentation(Date value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        } else {
            return format.format((Date) value);
        }
    }

    @Override
    public Class<Date> getModelType() {
        return Date.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
