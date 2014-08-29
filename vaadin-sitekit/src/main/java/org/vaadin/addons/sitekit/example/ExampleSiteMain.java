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
package org.vaadin.addons.sitekit.example;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.webapp.WebAppContext;
import org.vaadin.addons.sitekit.grid.FieldSetDescriptor;
import org.vaadin.addons.sitekit.grid.FieldSetDescriptorRegister;
import org.vaadin.addons.sitekit.model.Feedback;
import org.vaadin.addons.sitekit.module.audit.AuditModule;
import org.vaadin.addons.sitekit.site.SiteModuleManager;
import org.vaadin.addons.sitekit.module.content.ContentModule;
import org.vaadin.addons.sitekit.site.*;
import org.vaadin.addons.sitekit.util.JettySiteUtil;
import org.vaadin.addons.sitekit.util.PersistenceUtil;
import org.vaadin.addons.sitekit.util.PropertiesUtil;

import java.net.BindException;
import java.net.URI;
import java.security.Security;

/**
 * Example site main class.
 *
 * @author Tommi S.E. Laukkanen
 */
public class ExampleSiteMain {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(ExampleSiteMain.class);
    /** The properties category used in instantiating default services. */
    private static final String PROPERTIES_CATEGORY = "site";
    /** The persistence unit to be used. */
    public static final String PERSISTENCE_UNIT = "site";
    /** The localization bundle. */
    public static final String LOCALIZATION_BUNDLE = "site-localization";

    /**
     * Main method for running DefaultSiteUI.
     * @param args the commandline arguments
     * @throws Exception if exception occurs in jetty startup.
     */
    public static void main(final String[] args) throws Exception {

        // Configure security provider.
        Security.addProvider(new BouncyCastleProvider());

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
        final int httpPort;
        if (StringUtils.isNotEmpty(environmentPortString)) {
            httpPort = Integer.parseInt(environmentPortString);
            LOGGER.info("Environment variable defined HTTP port: " + httpPort);
        } else {
            httpPort = Integer.parseInt(PropertiesUtil.getProperty("site", "http-port"));
            LOGGER.info("Configuration defined HTTP port: " + httpPort);
        }
        final int httpsPort = Integer.parseInt(PropertiesUtil.getProperty("site", "https-port"));

        // Configure Java Persistence API.
        // -------------------------------
        DefaultSiteUI.setEntityManagerFactory(PersistenceUtil.getEntityManagerFactory(
                PERSISTENCE_UNIT, PROPERTIES_CATEGORY));

        // Configure providers.
        // --------------------
        // Configure security provider.
        DefaultSiteUI.setSecurityProvider(new SecurityProviderSessionImpl("administrator", "user"));
        // Configure content provider.
        DefaultSiteUI.setContentProvider(new DefaultContentProvider());
        // Configure localization provider.
        DefaultSiteUI.setLocalizationProvider(new LocalizationProviderBundleImpl(LOCALIZATION_BUNDLE));

        // Initialize modules
        SiteModuleManager.initializeModule(AuditModule.class);
        SiteModuleManager.initializeModule(ContentModule.class);

        // Add feedback view and set it default.
        // -----------------------------------
        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();

        // Customize navigation tree.
        final NavigationVersion navigationVersion = siteDescriptor.getNavigation().getProductionVersion();
        navigationVersion.setDefaultPageName("feedback");
        navigationVersion.addRootPage(0, "feedback");

        // Describe feedback view.
        final ViewDescriptor feedback = new ViewDescriptor("feedback", "Feedback", DefaultView.class);
        feedback.setViewletClass("content", FeedbackViewlet.class);
        siteDescriptor.getViewDescriptors().add(feedback);

        // Describe feedback view fields.
        final FieldSetDescriptor feedbackFieldSetDescriptor = new FieldSetDescriptor(Feedback.class);
        feedbackFieldSetDescriptor.setVisibleFieldIds(new String[]{
                "title", "description", "emailAddress", "firstName", "lastName", "organizationName", "organizationSize"
        });
        FieldSetDescriptorRegister.registerFieldSetDescriptor("feedback", feedbackFieldSetDescriptor);

        // Configure Embedded jetty.
        // -------------------------
        final boolean developmentEnvironment = ExampleSiteMain.class.getClassLoader()
                .getResource("webapp/").toExternalForm().startsWith("file:");

        final String webappUrl;
        if (developmentEnvironment) {
            webappUrl = DefaultSiteUI.class.getClassLoader().getResource("webapp/").toExternalForm().replace(
                    "target/classes", "src/main/resources");
        } else {
            webappUrl = DefaultSiteUI.class.getClassLoader().getResource("webapp/").toExternalForm();
        }

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

        final Server server = JettySiteUtil.newServer(
                httpPort,
                httpsPort,
                false);

        server.setHandler(context);

        try {
            server.start();
        } catch (final BindException e) {
            LOGGER.warn("Jetty port (" + httpPort + ") binding failed: " + e.getMessage());
            return;
        }
        server.join();
    }

}
