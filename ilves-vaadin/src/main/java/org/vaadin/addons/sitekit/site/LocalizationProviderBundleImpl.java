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
package org.vaadin.addons.sitekit.site;

import org.apache.log4j.Logger;

import java.util.*;

/**
 * LocalizationProvider implementation which loads localized values from
 * resource bundle in classpath.
 * @author Tommi S.E. Laukkanen
 */
public final class LocalizationProviderBundleImpl implements LocalizationProvider {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(LocalizationProviderBundleImpl.class);
    /** The bundle base names. */
    private final String[] bundleBaseNames;
    /** The loaded resource bundles. */
    private final Map<Locale, List<ResourceBundle>> resourceBundles = new HashMap<Locale, List<ResourceBundle>>();

    /**
     * Constructor which allows setting bundle base names.
     * @param bundleBaseNames base names of the bundles.
     */
    public LocalizationProviderBundleImpl(final String... bundleBaseNames) {
        this.bundleBaseNames = bundleBaseNames;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String localize(final String key, final Locale locale) {
        if (!resourceBundles.containsKey(locale)) {
            resourceBundles.put(locale, new ArrayList<ResourceBundle>());
            final List<ResourceBundle> bundles = resourceBundles.get(locale);
            for (final String bundleBaseName : bundleBaseNames) {
                bundles.add(ResourceBundle.getBundle(bundleBaseName, locale));
            }
        }

        for (final ResourceBundle resourceBundle : resourceBundles.get(locale)) {
            if (resourceBundle.containsKey(key)) {
                return resourceBundle.getString(key);
            }
        }

        //LOGGER.warn("No localization found for: '" + key + "' in locale: " + UI.getCurrent().getLocale());
        return key;
    }

}
