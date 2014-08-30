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

import org.eclipse.jetty.server.*;
import org.vaadin.addons.sitekit.grid.FieldSetDescriptor;
import org.vaadin.addons.sitekit.grid.FieldSetDescriptorRegister;
import org.vaadin.addons.sitekit.model.Feedback;
import org.vaadin.addons.sitekit.jetty.DefaultJettyConfiguration;
import org.vaadin.addons.sitekit.site.*;

/**
 * Example site main class. Please note you can not run this main class directly from vaadin-sitekit
 * project as the database driver nor configuration resides in this project. You can use the example
 * main classes in vaadin-sitekit-example-postgresql and vaadin-sitekit-example-mysql projects.
 *
 *
 * @author Tommi S.E. Laukkanen
 */
public class ExampleSiteMain {
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
        // The default Jetty server configuration.
        final Server server = DefaultJettyConfiguration.configureServer(PERSISTENCE_UNIT, LOCALIZATION_BUNDLE);

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
                "title", "description", "emailAddress", "firstName", "lastName",
                "organizationName", "organizationSize"
        });
        FieldSetDescriptorRegister.registerFieldSetDescriptor("feedback", feedbackFieldSetDescriptor);

        server.start();

        server.join();
    }

}
