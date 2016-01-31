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
package org.bubblecloud.ilves.security;

import com.vaadin.annotations.JavaScript;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.server.Page;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.*;
import com.yubico.u2f.U2F;
import com.yubico.u2f.data.DeviceRegistration;
import com.yubico.u2f.data.messages.RegisterRequestData;
import com.yubico.u2f.data.messages.RegisterResponse;
import elemental.json.JsonArray;
import elemental.json.impl.JreJsonNull;
import org.apache.log4j.Logger;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.model.User;
import org.bubblecloud.ilves.site.SecurityProviderSessionImpl;
import org.bubblecloud.ilves.site.Site;

import java.util.HashMap;
import java.util.Map;

/**
 * Universal second factor (U2F) JavaScript API connector.
 *
 * @author Tommi S.E. Laukkanen
 */
@JavaScript({"u2f-api.js", "u2f_connector.js"})
public class U2fConnector extends AbstractJavaScriptExtension {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(U2fConnector.class);

    /**
     * The server side U2F API implementation.
     */
    private final U2F u2f = new U2F();
    /**
     * The existing device register requests.
     */
    private final Map<String, String> requests = new HashMap<>();
    /**
     * The register window.
     */
    private final Window registerWindow = new Window(Site.getCurrent().localize("header-register-u2f-device"));
    private final Site site;
    private final Company company;
    private final String appId;
    private final U2fListener u2fListener;

    /**
     * Constructor for setting up the JavaScript connector.
     */
    public U2fConnector(final U2fListener u2fListener) {
        extend(UI.getCurrent());

        this.u2fListener = u2fListener;
        addFunction("onRegisterResponse", new JavaScriptFunction() {
            @Override
            public void call(final JsonArray arguments) {
                onReqisterResponse(arguments);
            }
        });

        site = Site.getCurrent();
        company = site.getSiteContext().getObject(Company.class);
        appId = company.getUrl().charAt(company.getUrl().length() - 1) == '/' ?
            company.getUrl().substring(0, company.getUrl().length() - 1) : company.getUrl();
    }

    /**
     * Start the registration process.
     */
    public void startRegistration() {
        sendRegisterRequest();

        registerWindow.setModal(true);
        final VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setMargin(true);
        final Label label = new Label(Site.getCurrent().localize("message-insert-u2f-device"));
        verticalLayout.addComponent(label);
        verticalLayout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        registerWindow.setContent(verticalLayout);
        registerWindow.setResizable(false);
        registerWindow.setWidth(300, Sizeable.Unit.PIXELS);
        registerWindow.setHeight(200, Sizeable.Unit.PIXELS);
        registerWindow.center();
        UI.getCurrent().addWindow(registerWindow);
    }

    /**
     * Send registration request to U2F JavaScript API.
     */
    private void sendRegisterRequest() {
        final User user = ((SecurityProviderSessionImpl) site.getSecurityProvider()).getUserFromSession();

        try {
            final RegisterRequestData registerRequestData = u2f.startRegistration(appId, U2fService.getDeviceRegistrations(site.getSiteContext(), user.getEmailAddress()));
            requests.put(registerRequestData.getRequestId(), registerRequestData.toJson());
            callFunction("register", registerRequestData.toJson());
        } catch(final Exception e) {
            LOGGER.error("Error sending U2F registration request.", e);
            new Notification(
                    site.localize("message-u2f-device-registration-failed"),
                    Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
        }
    }

    /**
     * Event handler for register response from U2F JavaScript API.
     * @param arguments the response arguments (data and error code)
     */
    public void onReqisterResponse(JsonArray arguments) {
        registerWindow.close();

        try {
            final User user = ((SecurityProviderSessionImpl) site.getSecurityProvider()).getUserFromSession();

            if (arguments.length() == 2 && !(arguments.get(1) instanceof JreJsonNull)) {
                final double errorCode = arguments.getNumber(1);
                LOGGER.error("Error processing U2F registration due to error code: " + errorCode);
                new Notification(
                        site.localize("message-u2f-device-registration-failed") + " (" + errorCode + ")",
                        Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
                return;
            }

            final RegisterResponse registerResponse = RegisterResponse.fromJson(arguments.getString(0));

            final RegisterRequestData registerRequestData = RegisterRequestData.fromJson(requests.remove(registerResponse.getRequestId()));
            final DeviceRegistration registration = u2f.finishRegistration(registerRequestData, registerResponse);
            U2fService.addDeviceRegistration(site.getSiteContext(), user.getEmailAddress(), registration);
            AuditService.log(site.getSiteContext(), "u2f device register");

            u2fListener.onDeviceRegistrationSuccess();
            new Notification(
                    site.localize("message-u2f-device-registered"),
                    Notification.Type.HUMANIZED_MESSAGE).show(Page.getCurrent());
        } catch(final Exception e) {
            LOGGER.error("Error processing U2F registration response.", e);
            new Notification(
                    site.localize("message-u2f-device-registration-failed"),
                    Notification.Type.ERROR_MESSAGE).show(Page.getCurrent());
        }
    }

}