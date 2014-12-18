package org.bubblecloud.ilves.component.formatter;

import com.vaadin.data.util.converter.Converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Percentage converter.
 */
public class CurrencyConverter implements Converter<String, Long> {
    /** The format for currency values. */
    private NumberFormat format = new DecimalFormat(",##0.00");

    @Override
    public Long convertToModel(String value, Class<? extends Long> targetType, Locale locale) throws ConversionException {
        if (value == null || value.length() == 0) {
            return null;
        } else {
            try {
                final Number number = format.parse(value);
                final double doubleValue = number.doubleValue();
                return Math.round(doubleValue * 100);
            } catch (final ParseException e) {
                throw new ConversionException(e);
            }
        }
    }

    @Override
    public String convertToPresentation(Long value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        } else {
            return format.format(((Long) value) / 100.0f);
        }
    }

    @Override
    public Class<Long> getModelType() {
        return Long.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
