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
package org.vaadin.addons.sitekit.grid;

import com.vaadin.data.Container;
import com.vaadin.data.Property;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.ui.Table;

import java.util.HashMap;

/**
 * Table implementation which supports formatting of cell values with
 * PropertyFormatters.
 *
 * @author Tommi S.E. Laukkanen
 */
public class FormattingTable extends Table {
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Value formatters. */
    private final HashMap<Object, PropertyFormatter> formatters = new HashMap<Object, PropertyFormatter>();

    /**
     * Default constructor which sets default page length to 50.
     */
    public FormattingTable() {
        super();
        setPageLength(50);
    }

    /**
     * Default constructor which sets default page length to 50.
     * @param caption the caption
     * @param dataSource the data source
     */
    public FormattingTable(final String caption, final Container dataSource) {
        super(caption, dataSource);
        setPageLength(50);
    }

    /**
     * Default constructor which sets default page length to 50.
     * @param caption the caption
     */
    public FormattingTable(final String caption) {
        super(caption);
        setPageLength(50);
    }

    /**
     * Set value formatter for given column.
     * @param columnId the column ID
     * @param propertyFormatter the PropertyFormatter
     */
    public final void setFormatter(final Object columnId, final PropertyFormatter propertyFormatter) {
        formatters.put(columnId, propertyFormatter);
    }

    @Override
    protected final String formatPropertyValue(final Object rowId, final Object colId, final Property property) {
        if (formatters.containsKey(colId)) {
            return formatters.get(colId).format(property.getValue());
        } else {
            return super.formatPropertyValue(rowId, colId, property);
        }
    }

}
