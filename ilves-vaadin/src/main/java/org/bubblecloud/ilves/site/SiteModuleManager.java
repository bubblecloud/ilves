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
package org.bubblecloud.ilves.site;

import org.apache.log4j.Logger;
import org.bubblecloud.ilves.exception.SiteException;
import org.bubblecloud.ilves.module.audit.AuditModule;
import org.bubblecloud.ilves.module.content.ContentModule;

import java.util.ArrayList;
import java.util.List;

/**
 * Static class for managing modules.
 */
public class SiteModuleManager {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(SiteModuleManager.class);

    /**
     * Set of initialized site modules.
     */
    private static List<Class<? extends SiteModule>> initializedSiteModules = new ArrayList<Class<? extends SiteModule>>();
    /**
     * The site modules.
     */
    private static List<SiteModule> siteModules = new ArrayList<>();


    /**
     * Initializes site module.
     * @param siteModuleClass the site  module class
     * @return true if initialization succeeded.
     */
    public static synchronized boolean initializeModule(final Class<? extends SiteModule> siteModuleClass) {
        if (initializedSiteModules.contains(siteModuleClass)) {
            throw new SiteException("Module already enabled.");
        }
        if (SiteModule.class.isAssignableFrom(siteModuleClass)) {
            try {
                final SiteModule siteModule = siteModuleClass.newInstance();
                siteModule.initialize();
                siteModules.add(siteModule);
            } catch (final Exception e) {
                LOGGER.error("Error initializing module: " + siteModuleClass, e);
            }
            initializedSiteModules.add(siteModuleClass);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks whether site module has been initialized.
     * @param siteModuleClass the site module class
     * @return true if enabled.
     */
    public static synchronized boolean isModuleInitialized(final Class<? extends SiteModule> siteModuleClass) {
        return initializedSiteModules.contains(siteModuleClass);
    }

    /**
     * Injects dynamic content to dynamic site descriptor.
     * @param dynamicSiteDescriptor the dynamic site descriptor
     */
    public static synchronized void injectDynamicContent(final SiteDescriptor dynamicSiteDescriptor) {
        for (final SiteModule siteModule : siteModules) {
            siteModule.injectDynamicContent(dynamicSiteDescriptor);
        }
    }


}
