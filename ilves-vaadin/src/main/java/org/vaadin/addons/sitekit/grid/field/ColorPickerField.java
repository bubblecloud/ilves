package org.vaadin.addons.sitekit.grid.field;

import com.vaadin.data.Buffered;
import com.vaadin.data.Validator;
import com.vaadin.shared.ui.colorpicker.Color;
import com.vaadin.ui.AbstractColorPicker;
import com.vaadin.ui.ColorPicker;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.components.colorpicker.ColorChangeEvent;
import com.vaadin.ui.components.colorpicker.ColorChangeListener;

/**
 * Color picker form field.
 */
public class ColorPickerField extends CustomField<Integer> {

    final ColorPicker colorPicker;

    public ColorPickerField() {
        colorPicker = new ColorPicker();
        colorPicker.addColorChangeListener(new ColorChangeListener() {
            @Override
            public void colorChanged(ColorChangeEvent event) {
                setColorToField(event.getColor().getRGB());
            }
        });
        colorPicker.setHistoryVisibility(false);
    }

    @Override
    protected Component initContent() {
        return colorPicker;
    }

    protected void setColorToField(final Integer color) {
        super.setValue(color);
    }

    @Override
    public Class<? extends Integer> getType() {
        return Integer.class;
    }

    @Override
    protected void setInternalValue(Integer newValue) {
        if (newValue != null) {
            super.setInternalValue(newValue);
            colorPicker.setColor(new Color(newValue));
        }
    }
}
