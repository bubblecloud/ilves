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
package org.bubblecloud.ilves.component.field;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;

/**
 * Field for entering dates.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class DatePartField extends DateField {
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor which sets resolution and date format.
     */
    public DatePartField() {
        super();
        setResolution(Resolution.DAY);
        setDateFormat("dd/MM/yyyy");
    }

}
