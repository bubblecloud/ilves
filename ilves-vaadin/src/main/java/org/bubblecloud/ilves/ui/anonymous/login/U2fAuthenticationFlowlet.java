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
import org.apache.commons.codec.binary.Hex;
import org.bubblecloud.ilves.component.flow.AbstractFlowlet;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.security.SecurityUtil;
import org.bubblecloud.ilves.security.SiteAuthenticationService;
import org.bubblecloud.ilves.security.U2fAuthenticationListener;
import org.bubblecloud.ilves.security.U2fConnector;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.UUID;

/**
 * Flowlet for Universal Second Factor authentication step.
 *
 * @author Tommi S.E. Laukkanen
 */
public class U2fAuthenticationFlowlet extends AbstractFlowlet {
    @Override
    public String getFlowletKey() {
        return "u2f-authenticate";
    }

    @Override
    protected void initialize() {
        final Panel loginPanel = new Panel(getSite().localize("header-u2f-authenticate"));
        setViewContent(loginPanel);

        final VerticalLayout layout = new VerticalLayout();
        loginPanel.setContent(layout);
        layout.setMargin(true);

        final Label label = new Label(getSite().localize("message-insert-u2f-device"));
        layout.addComponent(label);
    }

    @Override
    public void enter() {
        final LoginFlowlet loginFlowlet = getFlow().getFlowlet(LoginFlowlet.class);
        final String emailAddress = loginFlowlet.getUsername().toLowerCase();
        final char[] password = loginFlowlet.getPassword();
        final U2fConnector u2fConnector = new U2fConnector();
        u2fConnector.startAuthentication(emailAddress, new U2fAuthenticationListener() {
            @Override
            public void onDeviceAuthenticationSuccess(final String authenticatedEmailAddress) {
                final char[] accessToken = SecurityUtil.generateAccessToken();

                if (!SiteAuthenticationService.login(authenticatedEmailAddress, password, accessToken)) {
                    getFlow().back();
                }
            }

            @Override
            public void onDeviceAuthenticationFailure() {
                getFlow().back();
            }
        });
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
