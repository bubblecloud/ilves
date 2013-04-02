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

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * Viewlet which renders company information.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class CompanyHeaderViewlet extends AbstractViewlet {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The label viewing company information. */
    private Label companyLabel;
    /** The label viewing user information. */
    private Label userLabel;

    /**
     * Default constructor which sets up widget content.
     */
    public CompanyHeaderViewlet() {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(new MarginInfo(true, false, false, false));
        companyLabel = new Label();
        companyLabel.setCaption(getSite().localize("label-company"));
        layout.addComponent(companyLabel);
        userLabel = new Label();
        userLabel.setCaption(getSite().localize("label-user"));
        layout.addComponent(userLabel);
        this.setCompositionRoot(layout);
    }

    @Override
    public void enter(final String parameters) {
        final Company company = getSite().getSiteContext().getObject(Company.class);
        if (company != null) {
            companyLabel.setValue(company.getCompanyName() + " (" + company.getCompanyCode() + ")");
        }
        if (getSite().getSecurityProvider().getUser() != null) {
            userLabel.setValue(getSite().getSecurityProvider().getUser() + " " + getSite().getSecurityProvider().getRoles());
            userLabel.setVisible(true);
        } else {
            userLabel.setVisible(false);
        }
    }

}
