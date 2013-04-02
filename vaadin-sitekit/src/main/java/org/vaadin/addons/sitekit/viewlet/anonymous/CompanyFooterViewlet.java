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
package org.vaadin.addons.sitekit.viewlet.anonymous;

import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.site.AbstractViewlet;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Viewlet which renders company information.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class CompanyFooterViewlet extends AbstractViewlet {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The label viewing company information. */
    private Label companyLabel;

    /**
     * Default constructor which sets up widget content.
     */
    public CompanyFooterViewlet() {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(false);
        layout.addComponent(new Label("<hr />", Label.CONTENT_XHTML));
        companyLabel = new Label();
        layout.addComponent(companyLabel);
        layout.setComponentAlignment(companyLabel, Alignment.BOTTOM_CENTER);
        this.setCompositionRoot(layout);

    }

    @Override
    public void enter(final String parameters) {
        final Company company = getSite().getSiteContext().getObject(Company.class);
        if (company != null) {
            companyLabel.setCaption(company.getCompanyName() + " (" + company.getCompanyCode() + ")");
        }
    }

}
