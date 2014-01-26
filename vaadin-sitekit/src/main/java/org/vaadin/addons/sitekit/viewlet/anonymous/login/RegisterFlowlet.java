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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.site.SiteFields;
import org.vaadin.addons.sitekit.util.EmailUtil;
import org.vaadin.addons.sitekit.util.PropertiesUtil;
import org.vaadin.addons.sitekit.util.StringUtil;
import org.vaadin.addons.sitekit.grid.validator.PasswordValidator;
import org.vaadin.addons.sitekit.grid.validator.PasswordVerificationValidator;
import com.vaadin.ui.Notification;
import org.apache.log4j.Logger;
import org.vaadin.addons.lazyquerycontainer.CompositeItem;

import org.vaadin.addons.sitekit.dao.CustomerDao;
import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.grid.FieldDescriptor;
import org.vaadin.addons.sitekit.grid.ValidatingEditor;
import org.vaadin.addons.sitekit.grid.ValidatingEditorStateListener;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.Customer;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.PostalAddress;
import org.vaadin.addons.sitekit.model.User;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.VerticalLayout;

/**
 * Register Flowlet.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class RegisterFlowlet extends AbstractFlowlet {

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(RegisterFlowlet.class);

    /** Default serial version UID. */
    private static final long serialVersionUID = 1L;
    /** Customer. */
    private Customer customer;
    /** Original password property. */
    private Property originalPasswordProperty;
    /** Verification password property. */
    private Property verifiedPasswordProperty;
    /** Validating editor. */
    private ValidatingEditor editor;

    @Override
    public String getFlowletKey() {
        return "register";
    }

    /**
     * Reset data.
     */
    public void reset() {
        customer = new Customer();
        final BeanItem<Customer> customerItem = new BeanItem<Customer>(customer);
        final PropertysetItem passwordItem = new PropertysetItem();
        passwordItem.addItemProperty("password1", originalPasswordProperty);
        passwordItem.addItemProperty("password2", verifiedPasswordProperty);
        final CompositeItem compositeItem = new CompositeItem();
        compositeItem.addItem(CompositeItem.DEFAULT_ITEM_KEY, customerItem);
        compositeItem.addItem("passwordItem", passwordItem);
        originalPasswordProperty.setValue(null);
        verifiedPasswordProperty.setValue(null);
        editor.setItem(compositeItem, true);
    }

    @Override
    public void initialize() {
        originalPasswordProperty = new ObjectProperty<String>(null, String.class);
        verifiedPasswordProperty = new ObjectProperty<String>(null, String.class);

        final List<FieldDescriptor> fieldDescriptors = new ArrayList<FieldDescriptor>();

        final PasswordValidator passwordValidator = new PasswordValidator(getSite(), originalPasswordProperty, "password2");

        //fieldDescriptors.addAll(SiteFields.getFieldDescriptors(Customer.class));

        for (final FieldDescriptor fieldDescriptor : SiteFields.getFieldDescriptors(Customer.class)) {
            if (fieldDescriptor.getId().equals("adminGroup")) {
                continue;
            }
            if (fieldDescriptor.getId().equals("memberGroup")) {
                continue;
            }
            if (fieldDescriptor.getId().equals("created")) {
                continue;
            }
            if (fieldDescriptor.getId().equals("modified")) {
                continue;
            }
            fieldDescriptors.add(fieldDescriptor);
        }

        //fieldDescriptors.remove(fieldDescriptors.size() - 1);
        //fieldDescriptors.remove(fieldDescriptors.size() - 1);
        fieldDescriptors.add(new FieldDescriptor("password1", getSite().localize("input-password"),
                PasswordField.class, null, 150, null, String.class, null,
                false, true, true
                ).addValidator(passwordValidator));
        fieldDescriptors.add(new FieldDescriptor("password2", getSite().localize("input-password-verification"),
                PasswordField.class, null, 150, null,
                String.class, null, false, true,
                true).addValidator(new PasswordVerificationValidator(getSite(), originalPasswordProperty)));

        editor = new ValidatingEditor(fieldDescriptors);
        passwordValidator.setEditor(editor);

        final Button registerButton = new Button(getSite().localize("button-register"));
        registerButton.addListener(new ClickListener() {
            /** The default serial version ID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                editor.commit();
                customer.setCreated(new Date());
                customer.setModified(customer.getCreated());
                final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);
                final Company company = getSite().getSiteContext().getObject(Company.class);

                final PostalAddress invoicingAddress = new PostalAddress();
                final PostalAddress deliveryAddress = new PostalAddress();
                customer.setInvoicingAddress(invoicingAddress);
                customer.setDeliveryAddress(deliveryAddress);

                if (UserDao.getUser(entityManager, company, customer.getEmailAddress()) != null) {
                    Notification.show(getSite().localize("message-user-email-address-registered"),
                            Notification.Type.WARNING_MESSAGE);
                    return;
                }

                try {
                    final byte[] passwordAndSaltBytes = (customer.getEmailAddress()
                            + ":" + ((String) originalPasswordProperty.getValue()))
                            .getBytes("UTF-8");
                    final MessageDigest md = MessageDigest.getInstance("SHA-256");
                    final byte[] passwordAndSaltDigest = md.digest(passwordAndSaltBytes);

                    customer.setOwner(company);
                    final User user = new User(company, customer.getFirstName(), customer.getLastName(),
                            customer.getEmailAddress(), customer.getPhoneNumber(), StringUtil.toHexString(passwordAndSaltDigest));

                    if (UserDao.getGroup(entityManager, company, "user") == null) {
                        UserDao.addGroup(entityManager, new Group(company, "user", "Default user group."));
                    }

                    UserDao.addUser(entityManager, user, UserDao.getGroup(entityManager, company, "user"));
                    CustomerDao.saveCustomer(entityManager, customer);
                    UserDao.addGroupMember(entityManager, customer.getAdminGroup(), user);
                    UserDao.addGroupMember(entityManager, customer.getMemberGroup(), user);

                    final HttpServletRequest request = ((VaadinServletRequest) VaadinService.getCurrentRequest())
                            .getHttpServletRequest();
                    final String url = company.getUrl() +
                            "#!validate/" + user.getUserId();

                    final Thread emailThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            EmailUtil.send(PropertiesUtil.getProperty("bare-site", "smtp-host"),
                                    user.getEmailAddress(), company.getSupportEmailAddress(), "Email Validation",
                                    "Please validate your email by browsing to this URL: " + url);
                        }
                    });
                    emailThread.start();

                    Notification.show(getSite().localize("message-registration-success"),
                            Notification.Type.HUMANIZED_MESSAGE);

                    getViewSheet().back();
                } catch (final Exception e) {
                    LOGGER.error("Error adding user.", e);
                    Notification.show(getSite().localize("message-registration-error"),
                            Notification.TYPE_WARNING_MESSAGE);
                }
                reset();
            }
        });

        editor.addListener(new ValidatingEditorStateListener() {
            @Override
            public void editorStateChanged(final ValidatingEditor source) {
                if (source.isValid()) {
                    registerButton.setEnabled(true);
                } else {
                    registerButton.setEnabled(false);
                }
            }
        });

        reset();

        final VerticalLayout panel = new VerticalLayout();
        panel.addComponent(editor);
        panel.addComponent(registerButton);
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
