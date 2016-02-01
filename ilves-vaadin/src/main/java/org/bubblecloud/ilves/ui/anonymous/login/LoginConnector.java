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

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.*;
import elemental.json.JsonArray;
import org.apache.log4j.Logger;

/**
 * Login form JavaScript connector.
 *
 * @author Tommi S.E. Laukkanen
 */
@JavaScript("login_connector.js")
public class LoginConnector extends AbstractJavaScriptExtension {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(LoginConnector.class);
    /** The credentials listener. */
    private LoginConnectorCredentialsListener credentialsListener;
    /** The save listener. */
    private LoginConnectorSaveListener saveListener;

    /**
     * Constructor for setting up the JavaScript connector.
     */
    public LoginConnector() {
        extend(UI.getCurrent());

        addFunction("onCredentials", new JavaScriptFunction() {
            @Override
            public void call(final JsonArray arguments) {
                final String username = arguments.getString(0);
                final String password = arguments.getString(1);
                credentialsListener.onCredentials(username, password);
            }
        });

        addFunction("onSave", new JavaScriptFunction() {
            @Override
            public void call(final JsonArray arguments) {
                saveListener.onSave();
            }
        });
    }

    /**
     * Get credentials from login form.
     */
    public void getCredentials(final LoginConnectorCredentialsListener listener) {
        this.credentialsListener = listener;
        callFunction("getCredentials");
    }

    /**
     * Save credentials to browser password manager.
     */
    public void saveCredentials(final LoginConnectorSaveListener listener) {
        this.saveListener = listener;
        callFunction("saveCredentials");
    }

}