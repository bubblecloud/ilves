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
package org.vaadin.addons.sitekit.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Properties loading utility which supports <category>-ext.properties for
 * extending properties defined in <category>.properties.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class PropertiesUtil {

    /** The loaded properties. */
    private static final Map<String, Properties> PROPERTIES_MAP = new HashMap<String, Properties>();
    /** The loaded extension properties. */
    private static final Map<String, Properties> EXTENDED_PROPERTIES_MAP = new HashMap<String, Properties>();

    /**
     * Private default constructor to disable construction of utility class.
     */
    private PropertiesUtil() {

    }

    /**
     * Gets property value String or null if no value is defined.
     * @param categoryKey Category defines the property file prefix.
     * @param propertyKey Property key defines the key in property file.
     * @return property value String or null.
     */
    public static String getProperty(final String categoryKey, final String propertyKey) {

        if (!PROPERTIES_MAP.containsKey(categoryKey)) {
            PROPERTIES_MAP.put(categoryKey, getProperties(categoryKey));
        }

        if (!EXTENDED_PROPERTIES_MAP.containsKey(categoryKey)) {
            final String extendedCategoryKey = categoryKey + "-ext";
            EXTENDED_PROPERTIES_MAP.put(categoryKey, getProperties(extendedCategoryKey));
        }

        if (EXTENDED_PROPERTIES_MAP.get(categoryKey) != null) {
            final String valueString = (String) EXTENDED_PROPERTIES_MAP.get(categoryKey).get(propertyKey);
            if (valueString != null) {
                return valueString;
            }
        }

        if (PROPERTIES_MAP.get(categoryKey) != null) {
            final String valueString = (String) PROPERTIES_MAP.get(categoryKey).get(propertyKey);
            if (valueString != null) {
                return valueString;
            }
        }

        throw new RuntimeException("Property not found: " + categoryKey + " / " + propertyKey);
    }

    /**
     * Loads properties with given category key.
     * @param categoryKey The category key.
     * @return Properties or null.
     */
    private static Properties getProperties(final String categoryKey) {
        final String propertiesFileName = categoryKey + ".properties";
        final Properties properties = new Properties();
        InputStream inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(propertiesFileName);
        if (inputStream == null) {
            try {
                if (!new File(propertiesFileName).exists()) {
                    return null;
                }
                inputStream = new FileInputStream(propertiesFileName);
            } catch (final IOException e) {
                e.printStackTrace();
                return null;
            }
            if (inputStream == null) {
                return null;
            }
        }
        try {
            properties.load(inputStream);
            inputStream.close();
        } catch (final IOException e) {
            e.printStackTrace();
            return null;
        }
        return properties;
    }
}
