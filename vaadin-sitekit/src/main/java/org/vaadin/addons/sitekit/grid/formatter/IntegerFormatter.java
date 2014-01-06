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
package org.vaadin.addons.sitekit.grid.formatter;

import com.vaadin.data.Property;
import com.vaadin.data.util.PropertyFormatter;


/**
 * Formatter for big decimals.
 *
 * @author Tommi S.E. Laukkanen
 */
@SuppressWarnings("unchecked")
public final class IntegerFormatter extends PropertyFormatter {

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    public IntegerFormatter() {
        super();
    }

    /**
     * @param propertyDataSource the property data source
     */
    public IntegerFormatter(final Property propertyDataSource) {
        super(propertyDataSource);
    }

    /**
     * On demand initialization.
     */
    private void initialize() {
    }

    @Override
    public String format(final Object value) {
        if (value == null) {
            return null;
        } else {
            initialize();
            return ((Integer) value).toString();
        }

    }

    @Override
    public Object parse(final String formattedValue) throws Exception {
        if (formattedValue == null || formattedValue.length() == 0) {
            return null;
        } else {
            initialize();
            return new Integer(formattedValue);
        }
    }

}
