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
package org.bubblecloud.ilves.module.customer;

import org.bubblecloud.ilves.component.formatter.TimestampConverter;
import org.bubblecloud.ilves.component.grid.FieldSetDescriptor;
import org.bubblecloud.ilves.component.grid.FieldSetDescriptorRegister;
import org.bubblecloud.ilves.model.AuditLogEntry;
import org.bubblecloud.ilves.module.audit.AuditFlow;
import org.bubblecloud.ilves.security.DefaultRoles;
import org.bubblecloud.ilves.site.*;
import org.bubblecloud.ilves.site.view.valo.DefaultValoView;
import org.bubblecloud.ilves.ui.administrator.customer.CustomerFlowViewlet;

/**
 * Customer module adds support for customers.
 *
 * - New customer is automatically created on user self registration.
 * - Users with customer admin group membership can link other users to customer member and admin groups.
 *
 * @author Tommi S.E. Laukkanen
 */
public class CustomerModule implements SiteModule {

    @Override
    public void initialize() {
        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();

        final NavigationVersion navigationVersion = siteDescriptor.getNavigation().getProductionVersion();
        navigationVersion.addChildPage("configuration", "account", "customers");

        final ViewDescriptor customers = new ViewDescriptor("customers", "Customers", DefaultValoView.class);
        customers.setViewerRoles(DefaultRoles.ADMINISTRATOR);
        customers.setViewletClass("content", CustomerFlowViewlet.class);
        siteDescriptor.getViewDescriptors().add(customers);
    }

    @Override
    public void injectDynamicContent(final SiteDescriptor dynamicSiteDescriptor) {
    }
}
