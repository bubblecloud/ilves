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

import java.util.HashMap;

/**
 * Stores field set centrally.
 * @author Tommi S.E. Laukkanen
 */
public final class FieldSetDescriptorRegister {
    /**
     * The field set descriptors.
     */
    private static HashMap<Object, FieldSetDescriptor> fieldSetDescriptors = new HashMap<Object, FieldSetDescriptor>();

    /**
     * Private default constructor to disable construction of utility class.
     */
    private FieldSetDescriptorRegister() {
    }

    /**
     * Register field set descriptor.
     * @param id the id
     * @param fieldSetDescriptor the field set descriptor
     */
    public static synchronized void registerFieldSetDescriptor(final Object id,
                                                                final FieldSetDescriptor fieldSetDescriptor) {
        fieldSetDescriptors.put(id, fieldSetDescriptor);
    }

    /**
     * Gets earlier registered field set descriptor.
     * @param id the field set descriptor ID
     * @return the field set descriptor
     */
    public static synchronized FieldSetDescriptor getFieldSetDescriptor(final Object id) {
        if (fieldSetDescriptors.containsKey(id)) {
            return fieldSetDescriptors.get(id);
        } else {
            throw new IllegalArgumentException("No such field set descriptor id: " + id);
        }
    }

}
