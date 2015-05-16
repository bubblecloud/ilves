package org.bubblecloud.ilves.component.button;

import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Embedded;
import org.bubblecloud.ilves.site.Site;

/**
 * Simple image toggle button.
 */
public class ImageToggleButton extends Embedded {
    public ImageToggleButton(final String imageThemePath) {
        super(null, new ThemeResource(imageThemePath));
        setState(false);
    }

    public void setState(final boolean state) {
        if (state) {
            setStyleName("image-button-on");
        } else {
            setStyleName("image-button");
        }
    }
}
