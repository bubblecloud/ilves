package org.vaadin.addons.sitekit.jetty;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.vaadin.addons.sitekit.module.audit.AuditModule;
import org.vaadin.addons.sitekit.module.content.ContentModule;
import org.vaadin.addons.sitekit.site.*;
import org.vaadin.addons.sitekit.util.PersistenceUtil;
import org.vaadin.addons.sitekit.util.PropertiesUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.Security;

/**
 * Created by tlaukkan on 8/29/2014.
 */
public class DefaultJettyConfiguration {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(DefaultJettyConfiguration.class);

    /**
     * Starts Jetty server with DefaultSiteUI.
     *
     * @param persistenceUnit the persistence unit
     * @param localizationBundle the localization bundle
     * @return the server.
     *
     * @throws Exception if exception occurs in server construction
     */
    public static Server configureServer(final String persistenceUnit,
                                         final String localizationBundle) throws IOException, URISyntaxException {
        final String propertiesCategory = "site";

        // Configure security provider.
        Security.addProvider(new BouncyCastleProvider());

        // Configuration loading with HEROKU support.
        final String environmentDatabaseString = System.getenv("DATABASE_URL");
        if (StringUtils.isNotEmpty(environmentDatabaseString)) {
            final URI dbUri = new URI(environmentDatabaseString);

            final String dbUser = dbUri.getUserInfo().split(":")[0];
            final String dbPassword = dbUri.getUserInfo().split(":")[1];
            final String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

            PropertiesUtil.setProperty(propertiesCategory, "javax.persistence.jdbc.url", dbUrl);
            PropertiesUtil.setProperty(propertiesCategory, "javax.persistence.jdbc.user", dbUser);
            PropertiesUtil.setProperty(propertiesCategory, "javax.persistence.jdbc.password", dbPassword);
            LOGGER.info("HEROKU environment variable defined database URL: " + environmentDatabaseString);
        }

        final String environmentPortString = System.getenv().get("PORT");
        final int httpPort;
        if (StringUtils.isNotEmpty(environmentPortString)) {
            httpPort = Integer.parseInt(environmentPortString);
            LOGGER.info("HEROKU environment variable defined HTTP port: " + httpPort);
            if (!"true".equals(PropertiesUtil.getProperty("site", "production-mode"))) {
                LOGGER.warn("Ilves production mode enforced in HEROKU.");
                PropertiesUtil.setProperty("site", "production-mode", "true");
            }
        } else {
            httpPort = Integer.parseInt(PropertiesUtil.getProperty("site", "http-port"));
            LOGGER.info("Configuration defined HTTP port: " + httpPort);
        }
        final int httpsPort = Integer.parseInt(PropertiesUtil.getProperty("site", "https-port"));

        // Configure Java Persistence API.
        // -------------------------------
        DefaultSiteUI.setEntityManagerFactory(PersistenceUtil.getEntityManagerFactory(
                persistenceUnit, propertiesCategory));

        // Configure providers.
        // --------------------
        // Configure security provider.
        DefaultSiteUI.setSecurityProvider(new SecurityProviderSessionImpl("administrator", "user"));
        // Configure content provider.
        DefaultSiteUI.setContentProvider(new DefaultContentProvider());
        // Configure localization provider.
        if ("site-localization".equals(localizationBundle) || localizationBundle == null) {
            DefaultSiteUI.setLocalizationProvider(new LocalizationProviderBundleImpl("site-localization"));
        } else {
            DefaultSiteUI.setLocalizationProvider(new LocalizationProviderBundleImpl("site-localization",
                    localizationBundle));
        }
        // Initialize modules
        SiteModuleManager.initializeModule(AuditModule.class);
        SiteModuleManager.initializeModule(ContentModule.class);

        // Configure Embedded jetty.
        // -------------------------
        final boolean developmentEnvironment = DefaultJettyConfiguration.class.getClassLoader()
                .getResource("webapp/").toExternalForm().startsWith("file:");

        final String webappUrl;
        if (developmentEnvironment) {
            webappUrl = DefaultSiteUI.class.getClassLoader().getResource("webapp/").toExternalForm().replace(
                    "target/classes", "src/main/resources");
            LOGGER.info("Jetty is loading static resources from src/main/resources.");
        } else {
            webappUrl = DefaultSiteUI.class.getClassLoader().getResource("webapp/").toExternalForm();
        }

        final WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setDescriptor(webappUrl + "/WEB-INF/web.xml");
        context.setResourceBase(webappUrl);
        context.setParentLoaderPriority(true);
        if ("true".equals(PropertiesUtil.getProperty("site", "production-mode"))) {
            context.setInitParameter("productionMode", "true");
            LOGGER.info("Ilves is in production mode.");
        } else {
            context.setInitParameter("productionMode", "false");
            LOGGER.info("Ilves is in development mode.");
        }
        if (developmentEnvironment) {
            context.setInitParameter("cacheControl","no-cache");
            context.setInitParameter("useFileMappedBuffer", "false");
            context.setInitParameter("maxCachedFiles", "0");
        }

        final boolean clientCertificateRequired = "true".equals(
                PropertiesUtil.getProperty("site", "client-certificate-required"));
        final Server server = JettyUtil.newServer(
                httpPort,
                httpsPort,
                clientCertificateRequired);

        server.setHandler(context);

        return server;
    }

}
