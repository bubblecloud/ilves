package sitekit.tutorial;

import org.vaadin.addons.sitekit.site.DefaultView;

import java.io.IOException;

/**
 * Tutorial site custom view class.
 *
 * @author Tommi S.E. Laukkanen
 */
public class CustomView extends DefaultView {
    /**
     * Default constructor which sets template path.
     *
     * @throws IOException if exception occurs in construction.
     */
    public CustomView() throws IOException {
        super("/VAADIN/themes/ilves/layouts/custom.jade");
    }
}
