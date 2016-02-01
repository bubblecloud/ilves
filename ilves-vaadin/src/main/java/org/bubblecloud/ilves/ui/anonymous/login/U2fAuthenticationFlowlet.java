package org.bubblecloud.ilves.ui.anonymous.login;

import com.vaadin.ui.*;
import org.bubblecloud.ilves.component.flow.AbstractFlowlet;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.security.SiteAuthenticationService;
import org.bubblecloud.ilves.security.U2fAuthenticationListener;
import org.bubblecloud.ilves.security.U2fConnector;

import java.util.UUID;

/**
 * Created by tlaukkan on 1/31/2016.
 */
public class U2fAuthenticationFlowlet extends AbstractFlowlet {
    @Override
    public String getFlowletKey() {
        return "u2f-authenticate";
    }

    @Override
    protected void initialize() {
        final Company company = getSite().getSiteContext().getObject(Company.class);

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
            public void onDeviceAuthenticationSuccess() {
                SiteAuthenticationService.login(emailAddress, password, null, UUID.randomUUID().toString());
            }

            @Override
            public void onDeviceAuthenticationFailure() {
                getFlow().forward(LoginFlowlet.class);
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
