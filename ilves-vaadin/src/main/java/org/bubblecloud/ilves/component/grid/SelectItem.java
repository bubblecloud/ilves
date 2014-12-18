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
package org.bubblecloud.ilves.component.grid;

import java.io.Serializable;

/**
 * Select item which can be used with vaadin Select field.
 *
 * @param <T> the value type.
 *
 * @author Tommi S.E. Laukkanen
 */
public class SelectItem<T> implements Serializable {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The select item label. */
    private final String label;
    /** The select item value. */
    private final T value;

    /**
     * Constructor which sets the select item label and value.
     * @param label the select item label.
     * @param value the select item value.
     */
    public SelectItem(final String label, final T value) {
        super();
        this.label = label;
        this.value = value;
    }

    /**
     * @return the label
     */
    public final String getLabel() {
        return label;
    }

    /**
     * @return the value
     */
    public final T getValue() {
        return value;
    }

    @Override
    public final String toString() {
        return label;
    }

}
