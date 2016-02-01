package org.bubblecloud.ilves.ui.anonymous.login;

import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import org.bubblecloud.ilves.component.flow.AbstractFlowlet;
import org.bubblecloud.ilves.model.AuthenticationDeviceType;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.security.SiteAuthenticationService;

import java.util.UUID;

/**
 * Created by tlaukkan on 1/31/2016.
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
