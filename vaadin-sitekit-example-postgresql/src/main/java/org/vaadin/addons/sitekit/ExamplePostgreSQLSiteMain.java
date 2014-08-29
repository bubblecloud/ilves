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
package org.vaadin.addons.sitekit;

import org.eclipse.jetty.server.Server;
import org.vaadin.addons.sitekit.example.ExampleSiteMain;
import org.vaadin.addons.sitekit.example.FeedbackViewlet;
import org.vaadin.addons.sitekit.grid.FieldSetDescriptor;
import org.vaadin.addons.sitekit.grid.FieldSetDescriptorRegister;
import org.vaadin.addons.sitekit.model.Feedback;
import org.vaadin.addons.sitekit.server.DefaultJettyServer;
import org.vaadin.addons.sitekit.site.*;

/**
 * Example site main class with PostgreSQL configuration.
 *
 * @author Tommi S.E. Laukkanen
 */
public class ExamplePostgreSQLSiteMain {

    /**
     * Main method for running DefaultSiteUI.
     * @param args the commandline arguments
     * @throws Exception if exception occurs in jetty startup.
     */
    public static void main(final String[] args) throws Exception {
        ExampleSiteMain.main(args);
    }

}
