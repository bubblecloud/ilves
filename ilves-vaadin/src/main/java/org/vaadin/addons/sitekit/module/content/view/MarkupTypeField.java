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
package org.vaadin.addons.sitekit.module.content.view;

import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Select;
import org.vaadin.addons.sitekit.model.Customer;
import org.vaadin.addons.sitekit.module.content.model.MarkupType;
import org.vaadin.addons.sitekit.site.AbstractSiteUI;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

/**
 * @author Tommi S.E. Laukkanen
 *
 */
public class MarkupTypeField extends ComboBox {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Default constructor which populates the select with existing customers.
     */
    public MarkupTypeField() {
        super();
    }

    @Override
    public final void attach() {
        super.attach();
        for (final MarkupType markup : MarkupType.values()) {
            addItem(markup);
        }
    }

}
