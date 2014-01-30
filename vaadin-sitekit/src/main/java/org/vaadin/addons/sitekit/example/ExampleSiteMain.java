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

import org.apache.log4j.xml.DOMConfigurator;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.vaadin.addons.sitekit.site.*;
import org.vaadin.addons.sitekit.util.PersistenceUtil;
import org.vaadin.addons.sitekit.viewlet.anonymous.FeedbackViewlet;

/**
 * Example bare site embedded jetty main class.
 *
 * @author Tommi S.E. Laukkanen
 */
public class ExampleSiteMain {
    /** The properties category used in instantiating default services. */
    private static final String PROPERTIES_CATEGORY = "bare-site";
    /** The persistence unit to be used. */
    public static final String PERSISTENCE_UNIT = "bare-site";

    /**
     * Main method for running DefaultSiteUI.
     * @param args the commandline arguments
     * @throws Exception if exception occurs in jetty startup.
     */
    public static void main(final String[] args) throws Exception {
        // Configure logging.
        DOMConfigurator.configure("./log4j.xml");

        // Configure Java Persistence API.
        DefaultSiteUI.setEntityManagerFactory(PersistenceUtil.getEntityManagerFactory(
                PERSISTENCE_UNIT, PROPERTIES_CATEGORY));

        // Configure security provider.
        DefaultSiteUI.setSecurityProvider(new SecurityProviderSessionImpl("administrator", "user"));

        // Configure content provider.
        DefaultSiteUI.setContentProvider(new DefaultContentProvider());

        // Configure localization provider.
        DefaultSiteUI.setLocalizationProvider(new LocalizationProviderBundleImpl("bare-site-localization"));

        // Configure fields.
        SiteFields.initialize(DefaultSiteUI.getLocalizationProvider());

        // Add custom view and set it default.
        // -----------------------------------
        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();
        final ViewDescriptor feedback = new ViewDescriptor("feedback", "Feedback", DefaultView.class);
        feedback.setViewletClass("content", FeedbackViewlet.class);
        siteDescriptor.getViewDescriptors().add(feedback);

        final NavigationVersion navigationVersion = siteDescriptor.getNavigation().getProductionVersion();
        navigationVersion.setDefaultPageName("feedback");
        navigationVersion.setTree("feedback;" + navigationVersion.getTree());

        // Configure Embedded jetty.
        // -------------------------
        final boolean test = ExampleSiteMain.class.getClassLoader()
                .getResource("webapp/").toExternalForm().startsWith("file:");

        final String webappUrl;
        if (test) {
            webappUrl = "src/main/resources/webapp/";
        } else {
            webappUrl = DefaultSiteUI.class.getClassLoader().getResource("webapp/").toExternalForm();
        }

        final Server server = new Server(8081);

        final WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setDescriptor(webappUrl + "/WEB-INF/web.xml");
        context.setResourceBase(webappUrl);
        context.setParentLoaderPriority(true);
        if (test) {
            context.setInitParameter("cacheControl","no-cache");
            context.setInitParameter("useFileMappedBuffer", "false");
            context.setInitParameter("maxCachedFiles", "0");
        }
        server.setHandler(context);
        server.start();
        server.join();
    }
}
