package org.bubblecloud.ilves;

import com.vaadin.navigator.View;
import com.vaadin.ui.Component;
import org.bubblecloud.ilves.security.DefaultRoles;
import org.bubblecloud.ilves.server.jetty.DefaultJettyConfiguration;
import org.bubblecloud.ilves.site.*;
import org.bubblecloud.ilves.site.view.valo.DefaultValoView;
import org.bubblecloud.ilves.util.PropertiesUtil;
import org.eclipse.jetty.server.Server;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Ilves API class.
 *
 * @author Tommi S.E. Laukkanen
 */
public class Ilves {

    /**
     * Configures Ilves and returns embedded Jetty server.
     *
     * Choosing another properties file prefix than default site requires content of site.properties
     * to be copied as defaults to custom properties file.
     *
     * @param propertiesFilePrefix the prefix of the properties file or null if site is used.
     * @param localizationBundlePrefix the localization bundle
     * @param persistentUnit the persistence unit
     * @return the server
     * @throws IOException if IO exception occurs
     * @throws URISyntaxException if URI syntax exception occurs
     */
    public static Server configure(final String propertiesFilePrefix,
                                   final String localizationBundlePrefix,
                                   final String persistentUnit)
            throws IOException, URISyntaxException {
        if (propertiesFilePrefix != null && !propertiesFilePrefix.equals("site")) {
            PropertiesUtil.setCategoryRedirection("site", propertiesFilePrefix);
        }
        return DefaultJettyConfiguration.configureServer(persistentUnit, localizationBundlePrefix);
    }

    /**
     * Initializes site module.
     * @param siteModuleClass the site  module class
     * @return true if initialization succeeded.
     */
    public static synchronized boolean initializeModule(final Class<? extends SiteModule> siteModuleClass) {
        return SiteModuleManager.initializeModule(siteModuleClass);
    }


    /**
     * Adds navigation category page to Ilves site.
     * @param page the page
     * @param roles the roles able to access the page
     */
    public static void addNavigationCategoryPage(final String page, final String... roles) {
        addViewDescriptor(page, DefaultValoView.class, roles);
        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();
        final NavigationVersion navigationVersion = siteDescriptor.getNavigationVersion();
        navigationVersion.addRootPage(page);
    }

    /**
     * Adds navigation category page to Ilves site.
     * @param index the page index
     * @param page the page
     * @param roles the roles able to access the page
     */
    public static void addNavigationCategoryPage(final int index, final String page, final String... roles) {
        addViewDescriptor(page, DefaultValoView.class, roles);
        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();
        final NavigationVersion navigationVersion = siteDescriptor.getNavigationVersion();
        navigationVersion.addRootPage(index, page);
    }

    /**
     * Adds navigation category page to Ilves site.
     * @param previousCategoryPage the previous category page
     * @param page the page
     * @param roles the roles able to access the page
     */
    public static void addNavigationCategoryPage(final String previousCategoryPage, final String page, final String... roles) {
        addViewDescriptor(page, DefaultValoView.class, roles);
        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();
        final NavigationVersion navigationVersion = siteDescriptor.getNavigationVersion();
        navigationVersion.addRootPage(previousCategoryPage, page);
    }

    /**
     * Adds root page to Ilves site.
     * @param page the page
     * @param viewClass the view class
     * @param roles the roles able to access the page
     */
    public static void addRootPage(final String page, final Class<? extends View> viewClass, final String... roles) {
        addViewDescriptor(page, viewClass, roles);

        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();
        final NavigationVersion navigationVersion = siteDescriptor.getNavigationVersion();
        navigationVersion.addRootPage(page);
    }

    /**
     * Adds root page to Ilves site.
     * @param index the page index
     * @param page the page
     * @param viewClass the view class
     * @param roles the roles able to access the page
     */
    public static void addRootPage(final int index, final String page, final Class<? extends View> viewClass, final String... roles) {
        addViewDescriptor(page, viewClass, roles);

        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();
        final NavigationVersion navigationVersion = siteDescriptor.getNavigationVersion();
        navigationVersion.addRootPage(index, page);
    }

    /**
     * Adds root page to Ilves site.
     * @param previousPage an existing root page after which the new root page is added
     * @param page the page
     * @param viewClass the view class
     * @param roles the roles able to access the page
     */
    public static void addRootPage(final String previousPage, final String page, final Class<? extends View> viewClass, final String... roles) {
        addViewDescriptor(page, viewClass, roles);

        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();
        final NavigationVersion navigationVersion = siteDescriptor.getNavigationVersion();
        navigationVersion.addRootPage(previousPage, page);
    }

    /**
     * Adds child page to Ilves site.
     * @param parentPage the parent page
     * @param page the new child page
     * @param viewClass the view class
     * @param roles the roles able to access the page
     */
    public static void addChildPage(final String parentPage, final String page, final Class<? extends View> viewClass, final String... roles) {
        addViewDescriptor(page, viewClass, roles);

        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();
        final NavigationVersion navigationVersion = siteDescriptor.getNavigationVersion();
        navigationVersion.addChildPage(parentPage, page);
    }

    /**
     * Adds child page to Ilves site.
     * @param parentPage the parent page
     * @param index the page index
     * @param page the new child page
     * @param viewClass the view class
     * @param roles the roles able to access the page
     */
    public static void addChildPage(final String parentPage, final int index, final String page, final Class<? extends View> viewClass, final String... roles) {
        addViewDescriptor(page, viewClass, roles);

        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();
        final NavigationVersion navigationVersion = siteDescriptor.getNavigationVersion();
        navigationVersion.addChildPage(parentPage, index, page);
    }

    /**
     * Adds child page to Ilves site.
     * @param parentPage the parent page
     * @param previousPage the existing child page after which the new child page is added
     * @param page the new child page
     * @param viewClass the view class
     * @param roles the roles able to access the page
     */
    public static void addChildPage(final String parentPage, final String previousPage, final String page, final Class<? extends View> viewClass, final String... roles) {
        addViewDescriptor(page, viewClass, roles);

        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();
        final NavigationVersion navigationVersion = siteDescriptor.getNavigationVersion();
        navigationVersion.addChildPage(parentPage, previousPage, page);
    }

    /**
     * Internal helper method for adding view descriptor.
     * @param viewName the view name
     * @param viewClass the view class
     * @param roles the roles able to access the view
     */
    private static void addViewDescriptor(final String viewName, final Class<? extends View> viewClass, final String... roles) {
        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();
        final ViewDescriptor viewDescriptor = new ViewDescriptor(viewName, viewClass);
        if (roles.length != 0) {
            viewDescriptor.setViewerRoles(roles);
        }
        siteDescriptor.getViewDescriptors().add(viewDescriptor);

    }

    /**
     * Sets the default page in Ilves site.
     * @param page the page.
     */
    public static void setDefaultPage(final String page) {
        // Get default site descriptor.
        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();

        // Add custom view to navigation.
        final NavigationVersion navigationVersion = siteDescriptor.getNavigationVersion();
        navigationVersion.setDefaultPageName(page);
    }

    /**
     * Sets Vaadin component to Ilves site page.
     * @param page the page
     * @param slot the slot
     * @param componentClass the component class
     */
    public static void setPageComponent(final String page, final String slot,
                                        final Class<? extends Component> componentClass) {
        // Get default site descriptor.
        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();

        ViewDescriptor viewDescriptor = null;
        for (final ViewDescriptor candidateDescriptor : siteDescriptor.getViewDescriptors()) {
            if (candidateDescriptor.getName().equals(page)) {
                viewDescriptor = candidateDescriptor;
            }
        }

        if (viewDescriptor == null) {
            // Describe custom view.
            viewDescriptor = new ViewDescriptor(page, DefaultValoView.class);
            siteDescriptor.getViewDescriptors().add(viewDescriptor);
        }

        // Place example Vaadin component to content slot in the view.
        viewDescriptor.setComponentClass(slot, componentClass);
    }

    /**
     * Sets Viewlet to Ilves site page.
     * @param page the page
     * @param slot the slot
     * @param viewletClass the viewlet class
     */
    public static void setPageViewlet(final String page, final String slot,
                                        final Class<? extends Viewlet> viewletClass) {
        // Get default site descriptor.
        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();

        ViewDescriptor viewDescriptor = null;
        for (final ViewDescriptor candidateDescriptor : siteDescriptor.getViewDescriptors()) {
            if (candidateDescriptor.getName().equals(page)) {
                viewDescriptor = candidateDescriptor;
            }
        }

        if (viewDescriptor == null) {
            // Describe custom view.
            viewDescriptor = new ViewDescriptor(page, DefaultValoView.class);
            siteDescriptor.getViewDescriptors().add(viewDescriptor);
        }

        // Place example Vaadin component to content slot in the view.
        viewDescriptor.setViewletClass(slot, viewletClass);
    }
}
