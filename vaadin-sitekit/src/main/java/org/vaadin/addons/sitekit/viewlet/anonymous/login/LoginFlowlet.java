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

import java.security.MessageDigest;
import java.util.List;

import javax.persistence.EntityManager;

import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.site.SecurityProviderSessionImpl;
import org.vaadin.addons.sitekit.util.StringUtil;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import org.apache.log4j.Logger;

import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.User;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.LoginForm.LoginEvent;

/**
 * Login Flowlet.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class LoginFlowlet extends AbstractFlowlet implements LoginForm.LoginListener {
    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(LoginFlowlet.class);

    /** The login form. */
    private LoginForm loginForm;;

    @Override
    public String getFlowletKey() {
        return "login";
    }

    @SuppressWarnings("serial")
    @Override
    public void initialize() {

        final VerticalLayout layout = new VerticalLayout();
        //layout.setSpacing(true);
        layout.setWidth(200, AbstractComponent.UNITS_PIXELS);

        loginForm = new LoginForm() {
            @Override
            public String getLoginHTML() {
                return super.getLoginHTML().replace(
                        "<input class='v-textfield v-widget' style='display:block;'",
                        "<input class='v-textfield v-widget' style='margin-bottom:10px; display:block;'");
            }
        };

        loginForm.setLoginButtonCaption(getSite().localize("button-login"));
        loginForm.setUsernameCaption(getSite().localize("input-user-name"));
        loginForm.setPasswordCaption(getSite().localize("input-user-password"));
        loginForm.addListener(this);

        layout.addComponent(loginForm);




        final Button registerButton = new Button(getSite().localize("button-register") + " >>");
        registerButton.addListener(new ClickListener() {
            @Override
            public void buttonClick(final ClickEvent event) {
                getViewSheet().forward(RegisterFlowlet.class);
            }
        });
        layout.addComponent(registerButton);

        setViewContent(layout);

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

    @Override
    public void onLogin(final LoginEvent event) {
        try {
            final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);
            final Company company = getSite().getSiteContext().getObject(Company.class);
            final String userEmailAddress = event.getLoginParameter("username");
            final User user = UserDao.getUser(entityManager, company, userEmailAddress);

            final byte[] passwordAndSaltBytes = (user.getEmailAddress()
                    + ":" + ((String) event.getLoginParameter("password")))
                    .getBytes("UTF-8");
            final MessageDigest md = MessageDigest.getInstance("SHA-256");
            final String passwordAndSaltDigest = StringUtil.toHexString(md.digest(passwordAndSaltBytes));

            final boolean passwordMatch = passwordAndSaltDigest.equals(user.getPasswordHash());
            if (passwordMatch) {
                final List<Group> groups = UserDao.getUserGroups(entityManager, company, user);
                ((SecurityProviderSessionImpl) getSite().getSecurityProvider()).setUser(user, groups);
                UI.getCurrent().getNavigator().navigateTo("default");
            } else {
                Notification.show(getSite().localize("message-login-failed"), Notification.TYPE_WARNING_MESSAGE);
            }
        } catch (final Exception e) {
            LOG.error("Error logging in user.", e);
            Notification.show(getSite().localize("message-login-error"), Notification.TYPE_ERROR_MESSAGE);
        }

    }

}
