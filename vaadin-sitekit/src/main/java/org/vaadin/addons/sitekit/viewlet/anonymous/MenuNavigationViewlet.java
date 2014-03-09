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
package org.vaadin.addons.sitekit.viewlet.anonymous;

import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.BaseTheme;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.site.AbstractViewlet;
import org.vaadin.addons.sitekit.site.NavigationVersion;
import org.vaadin.addons.sitekit.site.SiteException;
import org.vaadin.addons.sitekit.site.ViewVersion;

import java.util.List;

/**
 * Default navigation Viewlet.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class MenuNavigationViewlet extends AbstractViewlet {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    private List<String> lastRoles;

    @Override
    public void attach() {
        super.attach();
    }

    public void refresh() {
        final MenuBar menuBar = new MenuBar();
        menuBar.setStyleName("menu-bar-navigation");
        menuBar.setSizeFull();
        //menuBar.setHeight(32, Unit.PIXELS);
        setCompositionRoot(menuBar);

        final NavigationVersion navigationVersion = getSite().getCurrentNavigationVersion();

        for (final String pageName : navigationVersion.getRootPages()) {
            processRootPage(navigationVersion, menuBar, pageName);
        }

        if (getSite().getSecurityProvider().getUser() != null) {
            final String localizedPageName = getSite().localize("button-logout");
            final Resource iconResource = getSite().getIcon("page-icon-logout");

            menuBar.addItem(localizedPageName, iconResource, new MenuBar.Command() {
                @Override
                public void menuSelected(MenuBar.MenuItem selectedItem) {
                    final Company company = getSite().getSiteContext().getObject(Company.class);
                    getUI().getPage().setLocation(company.getUrl());
                    getSession().close();
                }
            }).setStyleName("navigation-logout");
        }
    }

    private void processRootPage(final NavigationVersion navigationVersion,
                                 final MenuBar menuBar, final String pageName) {
        final ViewVersion pageVersion = getSite().getCurrentViewVersion(pageName);
        if (pageVersion == null) {
            throw new SiteException("Unknown page: " + pageName);
        }
        if (pageVersion.getViewerRoles().length > 0) {
            boolean roleMatch = false;
            for (final String role : pageVersion.getViewerRoles()) {
                if (getSite().getSecurityProvider().getRoles().contains(role)) {
                    roleMatch = true;
                    break;
                }
            }
            if (!roleMatch) {
                return;
            }
        }

        final String localizedPageName = pageVersion.isDynamic() ? pageName :
                getSite().localize("page-link-" + pageName);
        final Resource iconResource = pageVersion.isDynamic() ?
                navigationVersion.hasChildPages(pageName) ?
                        getSite().getIcon("page-icon-folder") : getSite().getIcon("page-icon-page") :
                getSite().getIcon("page-icon-" + pageName);

        final MenuBar.MenuItem menuItem = menuBar.addItem(localizedPageName, iconResource,
                navigationVersion.hasChildPages(pageName) ? null : new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                UI.getCurrent().getNavigator().navigateTo(pageName);
            }
        });
        menuItem.setStyleName("navigation-" + pageName);

        if (navigationVersion.hasChildPages(pageName)) {
            for (final String childPage : navigationVersion.getChildPages(pageName)) {
                processChildPage(navigationVersion, menuItem, childPage);
            }
        }
    }

    private void processChildPage(final NavigationVersion navigationVersion,
                                 final MenuBar.MenuItem parentItem, final String pageName) {
        final ViewVersion pageVersion = getSite().getCurrentViewVersion(pageName);
        if (pageVersion == null) {
            throw new SiteException("Unknown page: " + pageName);
        }
        if (pageVersion.getViewerRoles().length > 0) {
            boolean roleMatch = false;
            for (final String role : pageVersion.getViewerRoles()) {
                if (getSite().getSecurityProvider().getRoles().contains(role)) {
                    roleMatch = true;
                    break;
                }
            }
            if (!roleMatch) {
                return;
            }
        }

        final String localizedPageName = pageVersion.isDynamic() ? pageName :
                getSite().localize("page-link-" + pageName);
        final Resource iconResource = pageVersion.isDynamic() ?
                navigationVersion.hasChildPages(pageName) ?
                getSite().getIcon("page-icon-folder") : getSite().getIcon("page-icon-page") :
                getSite().getIcon("page-icon-" + pageName);

        final MenuBar.MenuItem menuItem = parentItem.addItem(localizedPageName, iconResource,
                navigationVersion.hasChildPages(pageName) ? null : new MenuBar.Command() {
            @Override
            public void menuSelected(MenuBar.MenuItem selectedItem) {
                UI.getCurrent().getNavigator().navigateTo(pageName);
            }
        });
        menuItem.setStyleName("navigation-" + pageName);
        menuItem.setEnabled(true);

        if (navigationVersion.hasChildPages(pageName)) {
            for (final String childPage : navigationVersion.getChildPages(pageName)) {
                processChildPage(navigationVersion, menuItem, childPage);
            }
        }
    }

    /**
     * SiteView constructSite occurred.
     */
    @Override
    public void enter(final String parameters) {
        final List<String> currentRoles = getSite().getSecurityProvider().getRoles();
        if (!currentRoles.equals(lastRoles)) {
            refresh();
            lastRoles = currentRoles;
        }
    }
}
