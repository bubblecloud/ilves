package sitekit.tutorial;

import com.vaadin.ui.Label;
import org.vaadin.addons.sitekit.site.AbstractViewlet;

/**
 * Hello world viewlet.
 */
public class HelloWorldViewlet extends AbstractViewlet {

    public HelloWorldViewlet() {
        setCompositionRoot(new Label("Hello World!"));
    }

    @Override
    public void enter(String parameters) {

    }
}
