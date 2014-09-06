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

import org.vaadin.addons.lazyquerycontainer.LazyQueryContainer;
import org.vaadin.addons.sitekit.grid.FieldDescriptor;

import java.util.List;

/**
 * Utility for manipulating Vaadin containers.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class ContainerUtil {

    /**
     * Private default constructor for disabling construction of utility class.
     */
    private ContainerUtil() {
    }

    /**
     * Utility methods for setting container properties based on field descriptors.
     *
     * @param container the container
     * @param fieldDescriptors the field descriptors
     */
    public static void addContainerProperties(final LazyQueryContainer container,
                                              final List<FieldDescriptor> fieldDescriptors) {
        for (final FieldDescriptor fieldDescriptor : fieldDescriptors) {
            container.addContainerProperty(
                    fieldDescriptor.getId(),
                    fieldDescriptor.getValueType(),
                    fieldDescriptor.getDefaultValue(),
                    fieldDescriptor.isReadOnly(),
                    fieldDescriptor.isSortable());
        }
    }

}
