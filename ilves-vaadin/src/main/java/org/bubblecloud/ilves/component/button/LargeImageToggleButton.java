package org.bubblecloud.ilves.component.button;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;

/**
 * Simple image toggle button.
 */
public class LargeImageToggleButton extends Embedded {
    public LargeImageToggleButton(final String imageThemePath) {
        super(null, new ThemeResource(imageThemePath));
        setState(false);
    }

    public void setState(final boolean state) {
        if (state) {
            setStyleName("image-button-large-on");
        } else {
            setStyleName("image-button-large");
        }
    }
}
