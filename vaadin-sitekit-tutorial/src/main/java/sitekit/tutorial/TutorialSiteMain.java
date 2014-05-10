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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.vaadin.addons.sitekit.example.FeedbackViewlet;
import org.vaadin.addons.sitekit.grid.FieldSetDescriptor;
import org.vaadin.addons.sitekit.grid.FieldSetDescriptorRegister;
import org.vaadin.addons.sitekit.model.Feedback;
import org.vaadin.addons.sitekit.site.*;
import org.vaadin.addons.sitekit.util.PersistenceUtil;
import org.vaadin.addons.sitekit.util.PropertiesUtil;

import java.net.BindException;
import java.net.URI;

/**
 * Example site main class.
 *
 * @author Tommi S.E. Laukkanen
 */
public class TutorialSiteMain {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(TutorialSiteMain.class);
    /** The properties category used in instantiating default services. */
    private static final String PROPERTIES_CATEGORY = "site";
    /** The persistence unit to be used. */
    public static final String PERSISTENCE_UNIT = "site";
    /** The localization bundle. */
    public static final String LOCALIZATION_BUNDLE = "site-localization";

    /**
     * Main method for running DefaultSiteUI.
     *
     * @param args the commandline arguments
     * @throws Exception if exception occurs in jetty startup.
     */
    public static void main(final String[] args) throws Exception {
        // Configure logging.
        DOMConfigurator.configure("./log4j.xml");

        // Configuration loading with HEROKU support.
        final String environmentDatabaseString = System.getenv("DATABASE_URL");
        if (StringUtils.isNotEmpty(environmentDatabaseString)) {
            final URI dbUri = new URI(environmentDatabaseString);

            final String dbUser = dbUri.getUserInfo().split(":")[0];
            final String dbPassword = dbUri.getUserInfo().split(":")[1];
            final String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

            PropertiesUtil.setProperty(PROPERTIES_CATEGORY, "javax.persistence.jdbc.url", dbUrl);
            PropertiesUtil.setProperty(PROPERTIES_CATEGORY, "javax.persistence.jdbc.user", dbUser);
            PropertiesUtil.setProperty(PROPERTIES_CATEGORY, "javax.persistence.jdbc.password", dbPassword);
            LOGGER.info("Environment variable defined database URL: " + environmentDatabaseString);
        }

        final String environmentPortString = System.getenv().get("PORT");
        final int port;
        if (StringUtils.isNotEmpty(environmentPortString)) {
            port = Integer.parseInt(environmentPortString);
            LOGGER.info("Environment variable defined HTTP port: " + port);
        } else {
            port = Integer.parseInt(PropertiesUtil.getProperty("site", "http-port"));
            LOGGER.info("Configuration defined HTTP port: " + port);
        }

        // Configure Java Persistence API.
        DefaultSiteUI.setEntityManagerFactory(PersistenceUtil.getEntityManagerFactory(PERSISTENCE_UNIT, PROPERTIES_CATEGORY));
        // Configure security provider.
        DefaultSiteUI.setSecurityProvider(new SecurityProviderSessionImpl("administrator", "user"));
        // Configure content provider.
        DefaultSiteUI.setContentProvider(new DefaultContentProvider());
        // Configure localization provider.
        DefaultSiteUI.setLocalizationProvider(new LocalizationProviderBundleImpl(LOCALIZATION_BUNDLE,
                "custom-localization"));

        // Get default site descriptor.
        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();
        // Describe custom view.
        final ViewDescriptor customDescriptor = new ViewDescriptor("custom", "Custom Title", DefaultView.class);
        customDescriptor.setViewletClass("content", HelloWorldViewlet.class);
        siteDescriptor.getViewDescriptors().add(customDescriptor);
        // Add custom view to navigation.
        final NavigationVersion navigationVersion = siteDescriptor.getNavigation().getProductionVersion();
        navigationVersion.setDefaultPageName("custom");
        navigationVersion.addRootPage(0, "custom");

        // Configure Embedded jetty.
        // -------------------------
        final boolean developmentEnvironment = TutorialSiteMain.class.getClassLoader()
                .getResource("webapp/").toExternalForm().startsWith("file:");

        final String webappUrl;
        if (developmentEnvironment) {
            webappUrl = DefaultSiteUI.class.getClassLoader().getResource("webapp/").toExternalForm().replace(
                    "target/classes", "src/main/resources");
        } else {
            webappUrl = DefaultSiteUI.class.getClassLoader().getResource("webapp/").toExternalForm();
        }

        final Server server = new Server(port);

        final WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setDescriptor(webappUrl + "/WEB-INF/web.xml");
        context.setResourceBase(webappUrl);
        context.setParentLoaderPriority(true);
        if (developmentEnvironment) {
            context.setInitParameter("cacheControl","no-cache");
            context.setInitParameter("useFileMappedBuffer", "false");
            context.setInitParameter("maxCachedFiles", "0");
        }
        server.setHandler(context);
        try {
            server.start();
        } catch (final BindException e) {
            LOGGER.warn("Jetty port (" + port + ") binding failed: " + e.getMessage());
            return;
        }
        server.join();
    }
}