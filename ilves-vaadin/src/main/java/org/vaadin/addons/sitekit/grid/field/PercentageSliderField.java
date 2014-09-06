package org.vaadin.addons.sitekit.grid.field;

import com.vaadin.data.Property;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.Slider;

/**
 * Color picker form field.
 */
public class PercentageSliderField extends CustomField<Double> {

    final Slider slider;

    public PercentageSliderField() {
        slider = new Slider();
        slider.setMin(0);
        slider.setMax(100);
        slider.setSizeFull();
        slider.addValueChangeListener(new ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                setPercentageValue((Double) event.getProperty().getValue());
            }
        });
    }

    @Override
    protected Component initContent() {
        return slider;
    }

    protected void setPercentageValue(final Double value) {
        super.setValue(value);
    }

    @Override
    public Class<? extends Double> getType() {
        return Double.class;
    }

    @Override
    protected void setInternalValue(Double newValue) {
        if (newValue != null) {
            super.setInternalValue(newValue);
            slider.setValue(newValue);
        }
    }
}
