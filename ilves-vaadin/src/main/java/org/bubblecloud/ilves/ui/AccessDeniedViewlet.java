package org.bubblecloud.ilves.ui;

import com.vaadin.ui.Label;
import org.bubblecloud.ilves.site.AbstractViewlet;


/**
 * Hello world viewlet.
 */
public class AccessDeniedViewlet extends AbstractViewlet {

    public AccessDeniedViewlet() {
        setCompositionRoot(new Label(getSite().localize("message-access-denied")));
    }

    @Override
    public void enter(String parameters) {

    }
}
