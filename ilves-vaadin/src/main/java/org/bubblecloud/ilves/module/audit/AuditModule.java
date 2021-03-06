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
package org.bubblecloud.ilves.module.audit;

import org.bubblecloud.ilves.component.formatter.TimestampConverter;
import org.bubblecloud.ilves.component.grid.FieldSetDescriptor;
import org.bubblecloud.ilves.component.grid.FieldSetDescriptorRegister;
import org.bubblecloud.ilves.model.AuditLogEntry;
import org.bubblecloud.ilves.security.DefaultRoles;
import org.bubblecloud.ilves.site.*;
import org.bubblecloud.ilves.site.view.valo.DefaultValoView;

/**
 * Audit module adds support for viewing audit logs.
 *
 * @author Tommi S.E. Laukkanen
 */
public class AuditModule implements SiteModule {

    @Override
    public void initialize() {
        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();

        final NavigationVersion navigationVersion = siteDescriptor.getNavigation().getProductionVersion();
        navigationVersion.addChildPage("configuration", "companies", "audit");

        // Describe content view.
        final ViewDescriptor viewDescriptor = new ViewDescriptor("audit", "Audit", DefaultValoView.class);
        viewDescriptor.setViewerRoles(DefaultRoles.ADMINISTRATOR);
        viewDescriptor.setViewletClass("content", AuditFlow.class);
        siteDescriptor.getViewDescriptors().add(viewDescriptor);

        // Describe feedback view fields.
        final FieldSetDescriptor fieldSetDescriptor = new FieldSetDescriptor(AuditLogEntry.class);

        fieldSetDescriptor.setVisibleFieldIds(new String[]{
                "created", "componentAddress", "componentType", "userAddress", "userId", "userName", "event",
                "dataId", "dataLabel", "dataNewVersionId", "dataOldVersionId"
        });
        fieldSetDescriptor.getFieldDescriptor("created").setConverter(new TimestampConverter());
        fieldSetDescriptor.getFieldDescriptor("componentAddress").setCollapsed(true);
        fieldSetDescriptor.getFieldDescriptor("userAddress").setCollapsed(true);
        fieldSetDescriptor.getFieldDescriptor("userId").setCollapsed(true);
        fieldSetDescriptor.getFieldDescriptor("dataId").setCollapsed(true);
        fieldSetDescriptor.getFieldDescriptor("dataNewVersionId").setCollapsed(true);
        fieldSetDescriptor.getFieldDescriptor("dataOldVersionId").setCollapsed(true);
        fieldSetDescriptor.getFieldDescriptor("dataLabel").setWidth(-1);

        FieldSetDescriptorRegister.registerFieldSetDescriptor(AuditLogEntry.class, fieldSetDescriptor);

    }

    @Override
    public void injectDynamicContent(final SiteDescriptor dynamicSiteDescriptor) {
    }
}
