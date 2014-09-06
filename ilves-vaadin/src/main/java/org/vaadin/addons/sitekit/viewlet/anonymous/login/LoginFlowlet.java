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
package org.vaadin.addons.sitekit.viewlet.anonymous.login;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.themes.ValoTheme;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.site.SecurityProviderSessionImpl;
import org.vaadin.addons.sitekit.util.OpenIdUtil;
import org.vaadin.addons.sitekit.util.PasswordLoginUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Login Flowlet.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class LoginFlowlet extends AbstractFlowlet {
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(LoginFlowlet.class);

    @Override
    public String getFlowletKey() {
        return "login";
    }

    @SuppressWarnings("serial")
    @Override
    public void initialize() {

        final Panel panel = new Panel();

        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        panel.setContent(layout);

        final Company company = getSite().getSiteContext().getObject(Company.class);
        if (company.isOpenIdLogin()) {
            final VerticalLayout mainPanel = new VerticalLayout();
            mainPanel.setCaption("OpenID Login");
            layout.addComponent(mainPanel);
            final HorizontalLayout openIdLayout = new HorizontalLayout();
            mainPanel.addComponent(openIdLayout);
            openIdLayout.setMargin(new MarginInfo(false, false, true, false));
            openIdLayout.setSpacing(true);
            final String returnViewName = "openidlogin";
            final Map<String, String> urlIconMap = OpenIdUtil.getOpenIdProviderUrlIconMap();
            for (final String url : urlIconMap.keySet()) {
                openIdLayout.addComponent(OpenIdUtil.getLoginButton(url,urlIconMap.get(url), returnViewName));
            }
        }

        final VerticalLayout loginFormLayout = new VerticalLayout();
        loginFormLayout.setMargin(new MarginInfo(false, false, true, false));
        loginFormLayout.setSpacing(true);

        final TextField emailAddressField = new TextField(getSite().localize("input-email-address"));
        emailAddressField.setId("email");
        emailAddressField.setMaxLength(255);
        emailAddressField.setWidth(400, Unit.PIXELS);
        loginFormLayout.addComponent(emailAddressField);

        final PasswordField passwordField = new PasswordField(getSite().localize("input-password"));
        passwordField.setId("password");
        passwordField.setMaxLength(160);
        passwordField.setWidth(400, Unit.PIXELS);
        loginFormLayout.addComponent(passwordField);

        final Button loginButton = new Button(getSite().localize("button-login"));
        loginButton.setStyleName(ValoTheme.BUTTON_PRIMARY);
        loginButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                login(emailAddressField.getValue(), passwordField.getValue());
            }
        });
        loginFormLayout.addComponent(loginButton);

        layout.addComponent(loginFormLayout);

        final Button registerButton = new Button(getSite().localize("button-register") + " >>");
        registerButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                getFlow().forward(RegisterFlowlet.class);
            }
        });
        layout.addComponent(registerButton);

        if (company.isEmailPasswordReset()) {
            final Button forgotPasswordButton = new Button(getSite().localize("button-forgot-password") + " >>");
            forgotPasswordButton.addClickListener(new ClickListener() {
                @Override
                public void buttonClick(final ClickEvent event) {
                    getFlow().forward(ForgotPasswordFlowlet.class);
                }
            });
            layout.addComponent(forgotPasswordButton);
        }

        setViewContent(panel);

    }


    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public void enter() {
    }

    public void login(final String emailAddress, final String password) {

        if (emailAddress == null) {
            Notification.show(getSite().localize("message-login-failed"), Notification.Type.WARNING_MESSAGE);
            return;
        }

        if (password == null) {
            Notification.show(getSite().localize("message-login-failed"), Notification.Type.WARNING_MESSAGE);
            return;
        }

        final HttpServletRequest request = ((VaadinServletRequest) VaadinService.getCurrentRequest())
                .getHttpServletRequest();

        final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);
        final Company company = getSite().getSiteContext().getObject(Company.class);
        final User user = UserDao.getUser(entityManager, company, emailAddress);
        final List<Group> groups = UserDao.getUserGroups(entityManager, company, user);

        final String errorKey = PasswordLoginUtil.login(emailAddress, request.getRemoteHost(),
                request.getRemoteAddr(), request.getRemotePort(),
                entityManager, company, user, password);

        if (errorKey == null) {
            // Login success
            ((SecurityProviderSessionImpl) getSite().getSecurityProvider()).setUser(user, groups);
            UI.getCurrent().getNavigator().navigateTo(getSite().getCurrentNavigationVersion().getDefaultPageName());
            if (user.getPasswordExpirationDate() != null
                    && new DateTime().plusDays(14).toDate().getTime() > user.getPasswordExpirationDate().getTime() ) {
                final DateTime expirationDate = new DateTime(user.getPasswordExpirationDate());
                final DateTime currentDate = new DateTime();
                final long daysUntilExpiration = new Duration(currentDate.toDate().getTime(),
                        expirationDate.toDate().getTime()).getStandardDays();
                Notification.show(getSite().localize("message-password-expires-in-days") + ": "
                        + daysUntilExpiration, Notification.Type.WARNING_MESSAGE);
            }
        } else {
            // Login failure
            Notification.show(getSite().localize(errorKey), Notification.Type.WARNING_MESSAGE);
        }

    }

}
