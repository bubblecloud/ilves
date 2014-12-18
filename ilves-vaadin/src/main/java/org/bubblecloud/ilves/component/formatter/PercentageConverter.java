package org.bubblecloud.ilves.component.formatter;

import com.vaadin.data.util.converter.Converter;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

/**
 * Percentage converter.
 */
public class PercentageConverter implements Converter<String, Integer> {
    /** The format for currency values. */
    private NumberFormat format = new DecimalFormat(",##0.00");

    @Override
    public Integer convertToModel(String value, Class<? extends Integer> targetType, Locale locale) throws ConversionException {
        if (value == null || value.length() == 0) {
            return null;
        } else {
            try {
                return format.parse(value).intValue();
            } catch (final ParseException e) {
                throw new ConversionException(e);
            }
        }
    }

    @Override
    public String convertToPresentation(Integer value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        } else {
            return format.format((Integer) value);
        }
    }

    @Override
    public Class<Integer> getModelType() {
        return Integer.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
