package org.vaadin.addons.sitekit.site;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.ui.SingleComponentContainer;
import com.vaadin.ui.UI;

/**
 * Created by tlaukkan on 2/22/14.
 */
public class SiteNavigator extends Navigator {

    public SiteNavigator(UI ui, SingleComponentContainer container) {
        super(ui, container);
    }

    @Override
    public void navigateTo(String navigationState) {
        if (Page.getCurrent().getLocation().toString().contains("openidlink")) {
            super.navigateTo("openidlink");
        } else if (Page.getCurrent().getLocation().toString().contains("openidlogin")) {
            super.navigateTo("openidlogin");
        } else {
            super.navigateTo(navigationState);
        }
    }
}
