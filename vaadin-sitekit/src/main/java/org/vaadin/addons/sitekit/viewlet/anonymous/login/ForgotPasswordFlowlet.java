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

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.apache.log4j.Logger;
import org.vaadin.addons.sitekit.dao.CustomerDao;
import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.grid.FieldDescriptor;
import org.vaadin.addons.sitekit.grid.ValidatingEditor;
import org.vaadin.addons.sitekit.grid.ValidatingEditorStateListener;
import org.vaadin.addons.sitekit.grid.validator.PasswordVerificationValidator;
import org.vaadin.addons.sitekit.model.*;
import org.vaadin.addons.sitekit.util.EmailUtil;
import org.vaadin.addons.sitekit.util.PropertiesUtil;
import org.vaadin.addons.sitekit.util.StringUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Register Flowlet.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class ForgotPasswordFlowlet extends AbstractFlowlet {

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(ForgotPasswordFlowlet.class);

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    /** Password reset PIN property. */
    private Property pinProperty;
    /** Original password property. */
    private Property emailAddressProperty;
    /** Validating editor. */
    private ValidatingEditor editor;

    @Override
    public String getFlowletKey() {
        return "forgot-password";
    }

    /**
     * Reset data.
     */
    public void reset() {
        final PropertysetItem item = new PropertysetItem();
        pinProperty.setValue(Integer.toString((int) ((Math.random() + 1) / 2 * 9999)));
        item.addItemProperty("pin", pinProperty);
        emailAddressProperty.setValue("");
        item.addItemProperty("emailAddress", emailAddressProperty);
        editor.setItem(item, true);
    }

    @Override
    public void initialize() {
        pinProperty = new ObjectProperty<String>(null, String.class);
        emailAddressProperty = new ObjectProperty<String>(null, String.class);

        final List<FieldDescriptor> fieldDescriptors = new ArrayList<FieldDescriptor>();

        fieldDescriptors.add(new FieldDescriptor("pin", getSite().localize("input-password-reset-pin"),
                TextField.class, null, 150, null, String.class, null,
                true, true, true
        ));
        fieldDescriptors.add(new FieldDescriptor("emailAddress", getSite().localize("input-email-address"),
                TextField.class, null, 150, null, String.class, null,
                false, true, true
                ).addValidator(new EmailValidator("Email address is not valid.")));

        editor = new ValidatingEditor(fieldDescriptors);

        final Button resetPasswordButton = new Button(getSite().localize("button-reset-password"));
        resetPasswordButton.addListener(new ClickListener() {
            /** The default serial version ID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                editor.commit();
                final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);
                final Company company = getSite().getSiteContext().getObject(Company.class);

                final User user = UserDao.getUser(entityManager, company, (String) emailAddressProperty.getValue());
                if (user == null) {
                    Notification.show(getSite().localize("message-user-email-address-not-registered"),
                            Notification.Type.WARNING_MESSAGE);
                    return;
                }

                try {
                    final String pin = (String) pinProperty.getValue();
                    final byte[] pinAndSaltBytes = (user.getEmailAddress() + ":" + pin).getBytes("UTF-8");
                    final MessageDigest md = MessageDigest.getInstance("SHA-256");
                    final byte[] pinAndSaltDigest = md.digest(pinAndSaltBytes);

                    final EmailPasswordReset emailPasswordReset = new EmailPasswordReset();
                    emailPasswordReset.setUser(user);
                    emailPasswordReset.setPinHash(StringUtil.toHexString(pinAndSaltDigest));
                    emailPasswordReset.setCreated(new Date());

                    entityManager.getTransaction().begin();
                    entityManager.persist(emailPasswordReset);
                    entityManager.getTransaction().commit();

                    final String url = company.getUrl() +
                            "#!reset/" + emailPasswordReset.getEmailPasswordResetId();

                    final Thread emailThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EmailUtil.send(PropertiesUtil.getProperty("site", "smtp-host"),
                                    user.getEmailAddress(), company.getSupportEmailAddress(), "Password Reset Link",
                                    "Password reset has been requested for your user account." +
                                    "You can perform the reset using the following link: " + url);
                        }
                    });
                    emailThread.start();

                    Notification.show(getSite().localize("message-password-reset-email-sent")
                            + getSite().localize("message-your-password-reset-pin-is") + pin,
                            Notification.Type.HUMANIZED_MESSAGE);

                    getViewSheet().back();
                } catch (final Exception e) {
                    LOGGER.error("Error preparing password reset.", e);
                    Notification.show(getSite().localize("message-password-reset-prepare-error"),
                            Notification.TYPE_WARNING_MESSAGE);
                }
                reset();
            }
        });

        editor.addListener(new ValidatingEditorStateListener() {
            @Override
            public void editorStateChanged(final ValidatingEditor source) {
                if (source.isValid()) {
                    resetPasswordButton.setEnabled(true);
                } else {
                    resetPasswordButton.setEnabled(false);
                }
            }
        });

        reset();

        final VerticalLayout panel = new VerticalLayout();
        panel.addComponent(editor);
        panel.addComponent(resetPasswordButton);
        panel.setSpacing(true);

        final HorizontalLayout mainLayout = new HorizontalLayout();
        mainLayout.addComponent(panel);

        setViewContent(mainLayout);
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

}
