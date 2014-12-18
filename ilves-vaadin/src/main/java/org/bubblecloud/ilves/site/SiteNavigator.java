package org.bubblecloud.ilves.site;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.Page;
import com.vaadin.ui.Notification;
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
        // Show redirect notification
        if (UI.getCurrent().getSession().getAttribute("redirectNotification") != null) {
            /*Notification.show((String) UI.getCurrent().getSession().getAttribute("redirectNotification"),
                    (Notification.Type) UI.getCurrent().getSession().getAttribute("redirectNotificationType"));*/
            final Notification notification = new Notification(
                    (String) UI.getCurrent().getSession().getAttribute("redirectNotification"),
                    (Notification.Type) UI.getCurrent().getSession().getAttribute("redirectNotificationType"));
            //notification.setPosition(Position.TOP_RIGHT);
            //notification.setDelayMsec(3000);
            notification.show(Page.getCurrent());
            UI.getCurrent().getSession().setAttribute("redirectNotification", null);
            UI.getCurrent().getSession().setAttribute("redirectNotificationType", null);
        }

        if (Page.getCurrent().getLocation().toString().contains("openidlink")) {
            super.navigateTo("openidlink");
        } else if (Page.getCurrent().getLocation().toString().contains("openidlogin")) {
            super.navigateTo("openidlogin");
        } else {
            super.navigateTo(navigationState);
        }
    }
}
