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

import java.util.HashMap;
import java.util.Map;

import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.server.Resource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import org.apache.log4j.Logger;
import com.vaadin.navigator.View;

/**
 * Vaadin portal implementation.
 * @author Tommi S.E. Laukkanen
 */
public final class Site implements ViewProvider, ViewChangeListener {

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(Site.class);
    /** The site. */
    private final SiteDescriptor siteDescriptor;
    /** The portal mode of operation. */
    private final SiteMode siteMode;
    /** The portal view windows. */
    private final Map<String, Window> windows = new HashMap<String, Window>();
    /** The localization provider. */
    private final LocalizationProvider localizationProvider;
    /** The security provider. */
    private final SecurityProvider securityProvider;
    /** The site context. */
    private final SiteContext siteContext;
    /** The views. */
    private final Map<String, View> views = new HashMap<String, View>();

    /**
     * Construct site with help of the providers.
     * @param siteMode the portal mode of operation
     * @param contentProvider the content provider
     * @param localizationProvider the localization provider
     * @param securityProvider the security provider
     * @param siteContext the site context
     */
    public Site(final SiteMode siteMode, final ContentProvider contentProvider,
                final LocalizationProvider localizationProvider,
            final SecurityProvider securityProvider, final SiteContext siteContext) {
        super();
        this.siteDescriptor = contentProvider.getSiteDescriptor();
        this.siteMode = siteMode;
        this.localizationProvider = localizationProvider;
        this.securityProvider = securityProvider;
        this.siteContext = siteContext;
    }

    /**
     * Initialize the site.
     */
    public void initialize() {
        for (final ViewDescriptor view : this.siteDescriptor.getPageDescriptors()) {
            setPage(view);
        }
    }

    /**
     * @return the localizationProvider
     */
    public LocalizationProvider getLocalizationProvider() {
        return localizationProvider;
    }

    /**
     * @return the siteContext
     */
    public SiteContext getSiteContext() {
        return siteContext;
    }

    /**
     * @return the securityProvider
     */
    public SecurityProvider getSecurityProvider() {
        return securityProvider;
    }

    /**
     * @return the mode
     */
    public SiteMode getSiteMode() {
        return siteMode;
    }

    /**
     * @return the site
     */
    public SiteDescriptor getSiteDescriptor() {
        return siteDescriptor;
    }

    /**
     * Gets localized value corresponding to given localization key.
     * @param key The localization key.
     * @return The localized value.
     */
    public String localize(final String key) {
        if (localizationProvider != null) {
            String value = localizationProvider.localize(key, UI.getCurrent().getLocale());
            if (value == null) {
                value = UI.getCurrent().getLocale() + "." + key;
            }
            return value;
        } else {
            return null;
        }
    }

    /**
     * Gets getIcon corresponding to given localization key.
     * @param key The localization key.
     * @return The localized getIcon.
     */
    public Resource getIcon(final String key) {
        return new ThemeResource("icons/" + key + ".png");
    }

    /**
     * Gets button.
     * @param key the button key
     * @return the key
     */
    public Button getButton(final String key) {
        final Button button = new Button(localize("button-" + key));
        button.setIcon(getIcon("button-icon-" + key));
        return button;
    }

    /**
     * Gets the current navigation version according to portal mode.
     * @return The navigation version in use.
     */
    public NavigationVersion getCurrentNavigationVersion() {
        if (siteMode == SiteMode.DEVELOPMENT) {
            return siteDescriptor.getNavigation().getDevelopmentVersion();
        }
        if (siteMode == SiteMode.TEST) {
            return siteDescriptor.getNavigation().getTestVersion();
        }
        if (siteMode == SiteMode.PRODUCTION) {
            return siteDescriptor.getNavigation().getProductionVersion();
        }
        throw new SiteException("Unable to deduce navigation version due to invalid portal mode: " + siteMode);
    }

    /**
     * Gets current view version for given view name.
     * @param viewName The name of the view.
     * @return The current view version.
     */
    public ViewVersion getCurrentViewVersion(final String viewName) {
        for (final ViewDescriptor view : siteDescriptor.getPageDescriptors()) {
            if (viewName.equals(view.getName())) {
                ViewVersion viewVersion = null;
                if (siteMode == SiteMode.DEVELOPMENT) {
                    viewVersion = view.getDevelopmentVersion();
                }
                if (siteMode == SiteMode.TEST) {
                    viewVersion = view.getTestVersion();
                }
                if (siteMode == SiteMode.PRODUCTION) {
                    viewVersion = view.getProductionVersion();
                }
                return viewVersion;
            }
        }
        return null;
    }

    /**
     * Sets view to the portal.
     * @param viewDescriptor The view descriptor of the view to be added.
     */
    private void setPage(final ViewDescriptor viewDescriptor) {
        views.remove(viewDescriptor.getName());

        ViewVersion viewVersion = null;
        if (siteMode == SiteMode.DEVELOPMENT) {
            viewVersion = viewDescriptor.getDevelopmentVersion();
        }
        if (siteMode == SiteMode.TEST) {
            viewVersion = viewDescriptor.getTestVersion();
        }
        if (siteMode == SiteMode.PRODUCTION) {
            viewVersion = viewDescriptor.getProductionVersion();
        }

        try {
            final Class<?> windowClass = Class.forName(viewVersion.getWindowClass());
            final View view = (View) windowClass.newInstance();

            if (view instanceof SiteView) {
                ((SiteView) view).setViewDescriptor(viewDescriptor);
                ((SiteView) view).setViewVersion(viewVersion);
                ((SiteView) view).initialize();
            }

            views.put(viewDescriptor.getName(), view);

        } catch (final Exception e) {
            LOGGER.error("Error instantiating view window: " + viewDescriptor.getName(), e);
            throw new SiteException("Error instantiating view window: " + viewDescriptor.getName(), e);
        }
    }

    @Override
    public String getViewName(final String viewAndParameters) {
        if (viewAndParameters.length() == 0) {
            return getCurrentNavigationVersion().getDefaultPageName();
        }
        return viewAndParameters;
    }

    @Override
    public View getView(final String viewName) {
        return views.get(viewName);
    }


    @Override
    public boolean beforeViewChange(final ViewChangeEvent event) {
        return true;
    }

    @Override
    public void afterViewChange(final ViewChangeEvent event) {
    }
}
