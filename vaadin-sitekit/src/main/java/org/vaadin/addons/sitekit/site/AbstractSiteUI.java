/**
 * Copyright 2013 Tommi S.E. Laukkanen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.addons.sitekit.site;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.vaadin.addons.sitekit.model.Company;

import java.util.Map;

/**
 * Abstract base class for UI which need to track page requests
 * and be able to get currently active window.
 *
 * @author Tommi S.E. Laukkanen
 */
public abstract class AbstractSiteUI extends UI {
    /** The default serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The site object. */
    private Site site;
    /** The navigator. */
    private Navigator navigator;

    @Override
    protected final void init(final VaadinRequest request) {
        final VaadinServletRequest servletRequest = (VaadinServletRequest) VaadinService.getCurrentRequest();
        final StringBuffer urlBuilder = servletRequest.getHttpServletRequest().getRequestURL();
        final String queryString = servletRequest.getHttpServletRequest().getQueryString();
        if (queryString != null) {
            urlBuilder.append('?');
            urlBuilder.append(queryString);
        }

        navigator = new SiteNavigator(this, this);
        site = constructSite(request);
        navigator.addViewChangeListener(site);
        navigator.addProvider(site);
        site.initialize();

        // Show redirect notification
        if (getSession().getAttribute("redirectNotification") != null) {
            Notification.show((String) getSession().getAttribute("redirectNotification"),
                    (Notification.Type) getSession().getAttribute("redirectNotificationType"));
        }
    }

    /**
     * Get the site.
     * @return the site
     */
    public final Site getSite() {
        return site;
    }

    /**
     * Application constructSite phase based on the VaadinRequest.
     * This phase need to construct the site and all composites it needs
     * but not initialize the site.
     *
     * @param request the VaadinRequest
     * @return the constructed site
     */
    protected abstract Site constructSite(final VaadinRequest request);

    /**
     * Redirects user to given view and shows notification.
     *
     * @param siteUrl the site URL
     * @param viewName the view name
     * @param notification the notification message
     * @param notificationType the notification type
     */
    public void redirectTo(final String siteUrl,
                           final String viewName, final String notification, final Notification.Type notificationType) {
        getUI().getPage().setLocation(siteUrl + "#!" + viewName);
        getSession().setAttribute("redirectNotification", notification);
        getSession().setAttribute("redirectNotificationType", notificationType);
    }
}
