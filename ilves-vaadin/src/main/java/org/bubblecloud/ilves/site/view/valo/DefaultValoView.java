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
package org.bubblecloud.ilves.site.view.valo;

import com.vaadin.server.*;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.bubblecloud.ilves.exception.SiteException;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.security.LoginService;
import org.bubblecloud.ilves.site.NavigationVersion;
import org.bubblecloud.ilves.site.SecurityProviderSessionImpl;
import org.bubblecloud.ilves.site.Site;
import org.bubblecloud.ilves.site.ViewVersion;
import org.bubblecloud.ilves.util.GravatarUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * Default Valo view implementation.
 *
 * @author Tommi S.E. Laukkanen
 */
public class DefaultValoView extends AbstractValoView {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    private final CssLayout menu = new CssLayout();
    private final CssLayout menuItemsLayout = new CssLayout();

    /**
     * The default portal window.
     * @throws java.io.IOException if template stream can not be read.
     */
    public DefaultValoView() throws IOException {
        setImmediate(true);
        if (!UI.getCurrent().getStyleName().contains(ValoTheme.UI_WITH_MENU)) {
            UI.getCurrent().addStyleName(ValoTheme.UI_WITH_MENU);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initializeComponents() {
        final AbstractComponent contentComponent = getComponent("content");
        final AbstractComponent footerComponent = getComponent("footer");

        if (Page.getCurrent().getWebBrowser().isIE()
                && Page.getCurrent().getWebBrowser().getBrowserMajorVersion() == 9) {
            menu.setWidth("320px");
        }
        Responsive.makeResponsive(UI.getCurrent());
        setWidth("100%");
        addMenu(buildMenu());

        getContentContainer().addComponent(contentComponent);
        if (footerComponent != null) {
            getContentContainer().addComponent(footerComponent);
        }

    }

    private CssLayout buildMenu() {

        final HorizontalLayout topLayout = new HorizontalLayout();
        topLayout.setWidth("100%");
        topLayout.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        topLayout.addStyleName("valo-menu-title");
        menu.addComponent(topLayout);

        final Button showMenu = new Button("Menu", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (menu.getStyleName().contains("valo-menu-visible")) {
                    menu.removeStyleName("valo-menu-visible");
                } else {
                    menu.addStyleName("valo-menu-visible");
                }
            }
        });
        showMenu.addStyleName(ValoTheme.BUTTON_PRIMARY);
        showMenu.addStyleName(ValoTheme.BUTTON_SMALL);
        showMenu.addStyleName("valo-menu-toggle");
        showMenu.setIcon(FontAwesome.LIST);
        menu.addComponent(showMenu);

        final Label title = new Label("<h3>" + Site.getCurrent().localize(getViewVersion().getTitle()) + "</h3>", ContentMode.HTML);
        title.setSizeUndefined();
        topLayout.addComponent(title);
        topLayout.setExpandRatio(title, 1);

        final MenuBar settings = new MenuBar();
        settings.addStyleName("user-menu");

        final String user = Site.getCurrent().getSecurityProvider().getUser();
        final String userMenuCaption;
        final Resource userMenuIcon;
        if (user == null) {
            userMenuIcon = getSite().getIcon("ilves_logo");
            userMenuCaption = "";
        } else {
            final URL gravatarUrl = GravatarUtil.getGravatarUrl(user, 64);
            userMenuIcon = new ExternalResource(gravatarUrl);
            userMenuCaption = ((SecurityProviderSessionImpl) Site.getCurrent().getSecurityProvider()).getUserFromSession().getFirstName();
        }

        if (user != null) {
            final MenuBar.MenuItem settingsItem = settings.addItem(userMenuCaption, userMenuIcon, new MenuBar.Command() {
                @Override
                public void menuSelected(MenuBar.MenuItem selectedItem) {
                    UI.getCurrent().getNavigator().navigateTo("account");
                }
            });
            menu.addComponent(settings);
            /*settingsItem.addItem(Site.getCurrent().localize("page-link-account"), new MenuBar.Command() {
                @Override
                public void menuSelected(MenuBar.MenuItem selectedItem) {
                    UI.getCurrent().getNavigator().navigateTo("account");
                }
            });
            settingsItem.addSeparator();
            settingsItem.addItem(Site.getCurrent().localize("button-logout"), new MenuBar.Command() {
                @Override
                public void menuSelected(MenuBar.MenuItem selectedItem) {
                    final Company company = Site.getCurrent().getSiteContext().getObject(Company.class);
                    LoginService.logout(Site.getCurrent().getSiteContext());
                    getUI().getPage().setLocation(company.getUrl());
                    getSession().getSession().invalidate();
                    getSession().close();
                }
            });*/
        } else {
            final MenuBar.MenuItem settingsItem = settings.addItem(userMenuCaption, userMenuIcon, null);
            menu.addComponent(settings);
        }


        menuItemsLayout.setPrimaryStyleName("valo-menuitems");
        menu.addComponent(menuItemsLayout);

        final Site site = Site.getCurrent();
        final NavigationVersion navigationVersion = site.getCurrentNavigationVersion();

        for (final String pageName : navigationVersion.getRootPages()) {
            final ViewVersion pageVersion = site.getCurrentViewVersion(pageName);
            if (pageVersion == null) {
                throw new SiteException("Unknown page: " + pageName);
            }
            if (pageVersion.getViewerRoles().length > 0) {
                boolean roleMatch = false;
                for (final String role : pageVersion.getViewerRoles()) {
                    if (site.getSecurityProvider().getRoles().contains(role)) {
                        roleMatch = true;
                        break;
                    }
                }
                if (!roleMatch) {
                    continue;
                }
            }

            if (navigationVersion.hasChildPages(pageName)) {
                final List<String> childPages = navigationVersion.getChildPages(pageName);

                final String localizedPageName = pageVersion.isDynamic() ? pageName
                        : Site.getCurrent().localize("page-link-" + pageName);

                addMenuHeader(localizedPageName, childPages.size());

                for (final String childPage : childPages) {
                    addMenuLink(childPage);
                }

            } else {
                addMenuLink(pageName);
            }

        }

        if (user != null) {
            addMenuLogoutLink();
        }

        return menu;
    }

    private void addMenuHeader(final String localizedPageName, int childPageCount) {
        final Label label = new Label(localizedPageName, ContentMode.HTML);
        label.setPrimaryStyleName("valo-menu-subtitle");
        label.addStyleName("h4");
        label.setSizeUndefined();
        menuItemsLayout.addComponent(label);
        label.setValue(label.getValue() + " <span class=\"valo-menu-badge\">"
                + childPageCount + "</span>");

    }

    private void addMenuLogoutLink() {
        final Site site = Site.getCurrent();
        final NavigationVersion navigationVersion = site.getCurrentNavigationVersion();
        final Resource iconResource = site.getIcon("page-icon-logout");

        Button b = new Button(Site.getCurrent().localize("button-logout"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                final Company company = Site.getCurrent().getSiteContext().getObject(Company.class);
                LoginService.logout(Site.getCurrent().getSiteContext());
                getUI().getPage().setLocation(company.getUrl());
                getSession().getSession().invalidate();
                getSession().close();
            }
        });

        b.setHtmlContentAllowed(true);
        b.setPrimaryStyleName("valo-menu-item");
        b.setIcon(iconResource);
        menuItemsLayout.addComponent(b);
    }

    private void addMenuLink(final String pageName) {
        final Site site = Site.getCurrent();
        final NavigationVersion navigationVersion = site.getCurrentNavigationVersion();
        final ViewVersion pageVersion = site.getCurrentViewVersion(pageName);
        if (pageVersion == null) {
            throw new SiteException("Unknown page: " + pageName);
        }
        if (pageVersion.getViewerRoles().length > 0) {
            boolean roleMatch = false;
            for (final String role : pageVersion.getViewerRoles()) {
                if (site.getSecurityProvider().getRoles().contains(role)) {
                    roleMatch = true;
                    break;
                }
            }
            if (!roleMatch) {
                return;
            }
        }

        final String localizedPageName = pageVersion.isDynamic() ? pageName : site.localize("page-link-" + pageName);
        final Resource iconResource = pageVersion.isDynamic() ? navigationVersion.hasChildPages(pageName) ?
                site.getIcon("page-icon-folder") : site.getIcon("page-icon-page") : site.getIcon("page-icon-" + pageName);

        Button b = new Button(localizedPageName, new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                UI.getCurrent().getNavigator().navigateTo(pageName);
            }
        });

        b.setHtmlContentAllowed(true);
        b.setPrimaryStyleName("valo-menu-item");
        b.setIcon(iconResource);
        menuItemsLayout.addComponent(b);
    }
}