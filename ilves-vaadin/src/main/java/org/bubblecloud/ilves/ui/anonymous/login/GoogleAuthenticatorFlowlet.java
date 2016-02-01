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
package org.bubblecloud.ilves.ui.anonymous.login;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.bubblecloud.ilves.component.flow.AbstractFlowlet;
import org.bubblecloud.ilves.model.AuthenticationDeviceType;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.security.SiteAuthenticationService;

import java.util.UUID;

/**
 * Flowlet for GoogleAuthenticator authentication step.
 *
 * @author Tommi S.E. Laukkanen
 */
public class GoogleAuthenticatorFlowlet extends AbstractFlowlet {
    @Override
    public String getFlowletKey() {
        return "google-authenticator";
    }

    @Override
    protected void initialize() {
        final Company company = getSite().getSiteContext().getObject(Company.class);

        final Panel loginPanel = new Panel(getSite().localize("header-google-authenticator"));
        setViewContent(loginPanel);

        final VerticalLayout panelLayout = new VerticalLayout();
        loginPanel.setContent(panelLayout);
        panelLayout.setMargin(true);
        panelLayout.setSpacing(true);

        final TextField codeField = new TextField(getSite().localize("label-code"));
        codeField.setId("code");
        codeField.setWidth(100, Unit.PERCENTAGE);
        panelLayout.addComponent(codeField);

        final Button loginButton = new Button(getSite().localize("button-login"));
        loginButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        loginButton.setId("login");
        panelLayout.addComponent(loginButton);
        loginButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                final LoginFlowlet loginFlowlet = getFlow().getFlowlet(LoginFlowlet.class);
                final String emailAddress = loginFlowlet.getUsername().toLowerCase();
                final char[] password = loginFlowlet.getPassword();
                final String code = codeField.getValue();
                final AuthenticationDeviceType authenticationDeviceType = SiteAuthenticationService.getAuthenticationDeviceType(emailAddress);
                SiteAuthenticationService.login(emailAddress, password, code, UUID.randomUUID().toString());
            }
        });
    }

    @Override
    public void enter() {

    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    protected boolean isValid() {
        return false;
    }
}
