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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Universal second factor (U2F) integration to Ilves.
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

    /**
     * Constructor for setting up the JavaScript connector.
     */
    public U2fConnector() {
        extend(UI.getCurrent());

        addFunction("onRegisterResponse", new JavaScriptFunction() {
            @Override
            public void call(final JsonArray arguments) {
                onReqisterResponse(arguments);
            }
        });
    }

    /**
     * Start the registration process.
     */
    public void startRegistration() {
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
        sendRegisterRequest();
        UI.getCurrent().addWindow(registerWindow);
    }

    /**
     * Send registration request to U2F JavaScript API.
     */
    private void sendRegisterRequest() {
        final Site site = Site.getCurrent();
        final Company company = site.getSiteContext().getObject(Company.class);
        final User user = ((SecurityProviderSessionImpl) site.getSecurityProvider()).getUserFromSession();
        final String appId = company.getUrl();

        final RegisterRequestData registerRequestData = u2f.startRegistration(appId, getRegistrations(user.getEmailAddress()));

        requests.put(registerRequestData.getRequestId(), registerRequestData.toJson());

        callFunction("register", registerRequestData.toJson());
    }

    /**
     * Event handler for register response from U2F JavaScript API.
     * @param arguments the response arguments (data and error code)
     */
    public void onReqisterResponse(JsonArray arguments) {
        registerWindow.close();

        final Site site = Site.getCurrent();
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

            addRegistration(user.getEmailAddress(), registration);
            AuditService.log(site.getSiteContext(), "u2f device register");

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

    //TODO replace with database

    private final List<DeviceRegistration> registrations = new ArrayList<DeviceRegistration>();

    private Iterable<DeviceRegistration> getRegistrations(String username) {
        return registrations;
    }

    private void addRegistration(String emailAddress, DeviceRegistration registration) {
        registrations.add(registration);
    }
}