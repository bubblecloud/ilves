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
package sitekit.tutorial;

import org.eclipse.jetty.server.Server;
import org.vaadin.addons.sitekit.jetty.DefaultJettyConfiguration;
import org.vaadin.addons.sitekit.site.DefaultSiteUI;
import org.vaadin.addons.sitekit.site.NavigationVersion;
import org.vaadin.addons.sitekit.site.SiteDescriptor;
import org.vaadin.addons.sitekit.site.ViewDescriptor;

/**
 * Tutorial site main class.
 *
 * @author Tommi S.E. Laukkanen
 */
public class TutorialSiteMain {
    /** The persistence unit to be used. */
    public static final String PERSISTENCE_UNIT = "site";
    /** The localization bundle. */
    public static final String LOCALIZATION_BUNDLE = "custom-localization";

    /**
     * Main method for tutorial site.
     *
     * @param args the commandline arguments
     * @throws Exception if exception occurs in jetty startup.
     */
    public static void main(final String[] args) throws Exception {
        // The default Jetty server configuration.
        final Server server = DefaultJettyConfiguration.configureServer(PERSISTENCE_UNIT, LOCALIZATION_BUNDLE);

        // Get default site descriptor.
        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();

        // Describe custom view.
        final ViewDescriptor customViewDescriptor = new ViewDescriptor("custom", CustomView.class);
        customViewDescriptor.setViewletClass("content", HelloWorldViewlet.class);
        siteDescriptor.getViewDescriptors().add(customViewDescriptor);

        // Add custom view to navigation.
        final NavigationVersion navigationVersion = siteDescriptor.getNavigationVersion();

        navigationVersion.setDefaultPageName("custom");
        navigationVersion.addRootPage(0, "custom");

        // Start server.
        server.start();

        // Join this main thread to the Jetty server thread.
        server.join();
    }
}