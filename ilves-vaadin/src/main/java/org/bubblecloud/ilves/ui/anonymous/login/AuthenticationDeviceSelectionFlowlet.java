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

import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.bubblecloud.ilves.component.flow.AbstractFlowlet;
import org.bubblecloud.ilves.model.AuthenticationDevice;
import org.bubblecloud.ilves.model.AuthenticationDeviceType;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.model.User;
import org.bubblecloud.ilves.security.*;
import org.vaadin.addons.lazyquerycontainer.EntityContainer;

import javax.persistence.EntityManager;
import java.util.UUID;

/**
 * Flowlet for authentication device selection.
 *
 * @author Tommi S.E. Laukkanen
 */
public class AuthenticationDeviceSelectionFlowlet extends AbstractFlowlet {

    @Override
    public String getFlowletKey() {
        return "select-authentication-device-type";
    }

    @Override
    protected void initialize() {
        final VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.setMargin(false);
        setViewContent(layout);

        final Label label = new Label(getSite().localize("message-select-authentication-device-type"));
        layout.addComponent(label);

        final Button u2fButton = new Button(getSite().localize("button-universal-second-factor"));
        layout.addComponent(u2fButton);
        u2fButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        u2fButton.setId("login");
        u2fButton.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        u2fButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getFlow().forward(U2fAuthenticationFlowlet.class);
            }
        });

        final Button googleAuthenticatorButton = new Button(getSite().localize("button-google-authenticator"));
        layout.addComponent(googleAuthenticatorButton);
        googleAuthenticatorButton.setId("login");
        googleAuthenticatorButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                getFlow().forward(GoogleAuthenticatorFlowlet.class);
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
