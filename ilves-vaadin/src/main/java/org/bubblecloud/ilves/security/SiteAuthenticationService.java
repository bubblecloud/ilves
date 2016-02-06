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

import com.vaadin.server.Page;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.yubico.u2f.data.DeviceRegistration;
import org.apache.log4j.Logger;
import org.bubblecloud.ilves.model.*;
import org.bubblecloud.ilves.site.AbstractSiteUI;
import org.bubblecloud.ilves.site.DefaultSiteUI;
import org.bubblecloud.ilves.site.Site;
import org.bubblecloud.ilves.site.SiteContext;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Service for user login and logout functionality.
 */
public class SiteAuthenticationService {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(LoginService.class);

    /**
     * Gets current authentication device type for email address.
     * @param emailAddress_ the email address
     * @return the current authentication device type or null if multiple device types exist.
     */
    public static AuthenticationDeviceType getAuthenticationDeviceType(final String emailAddress_) {
        final String emailAddress = emailAddress_.toLowerCase();
        final AbstractSiteUI ui = ((AbstractSiteUI) UI.getCurrent());
        final EntityManager entityManager = ui.getSite().getSiteContext().getEntityManager();
        final Company company = ui.getSite().getSiteContext().getObject(Company.class);
        final User user = UserDao.getUser(entityManager, company, emailAddress);

        if (user == null) {
            return AuthenticationDeviceType.NONE;
        }

        entityManager.refresh(user);

        final List<AuthenticationDevice> authenticationDevices = SiteAuthenticationService.getAuthenticationDevices(emailAddress);
        if (authenticationDevices.size() == 0) {
            return AuthenticationDeviceType.NONE;
        }
        AuthenticationDeviceType authenticationDeviceType = null;
        for (final AuthenticationDevice authenticationDevice : authenticationDevices) {
            if (authenticationDeviceType == null) {
                authenticationDeviceType = authenticationDevice.getType();
            } else {
                if (authenticationDevice.getType() != authenticationDeviceType) {
                    return null;
                }
            }
        }
        return authenticationDeviceType;
    }

    /**
     * Gets device registrations.
     * @param emailAddress the email address
     * @return list of device registrations
     */
    public static List<AuthenticationDevice> getAuthenticationDevices(final String emailAddress) {
        final AbstractSiteUI ui = ((AbstractSiteUI) UI.getCurrent());
        final SiteContext context = ui.getSite().getSiteContext();
            final Company company = context.getObject(Company.class);
        final EntityManager entityManager = context.getEntityManager();
            final User user = UserDao.getUser(entityManager, company, emailAddress);
        return  AuthenticationDeviceDao.getAuthenticationDevices(entityManager, user);
    }

    /**
     * Logs user in to the site.
     * @param emailAddress_ the email address
     * @param password the password
     * @param transactionId the authentication transaction ID
     */
    public static void login(final String emailAddress_, final char[] password, final String transactionId) {
        final  String emailAddress = emailAddress_.toLowerCase();
        final AbstractSiteUI ui = ((AbstractSiteUI)UI.getCurrent());
        final EntityManager entityManager = ui.getSite().getSiteContext().getEntityManager();
        final Company company = ui.getSite().getSiteContext().getObject(Company.class);
        final User user = UserDao.getUser(entityManager, company, emailAddress);

        final Locale locale;
        if (ui.getLocale() == null) {
            locale = Locale.ENGLISH;
        } else {
            locale = ui.getLocale();
        }

        if (user == null) {
            new Notification(DefaultSiteUI.getLocalizationProvider().localize("message-login-failed", locale),
                    Notification.Type.WARNING_MESSAGE).show(Page.getCurrent());
            return;
        }

        entityManager.refresh(user);


        final String errorKey = LoginService.login(ui.getSite().getSiteContext(), company,
                user, emailAddress, password, VaadinSession.getCurrent().getSession().getId(), transactionId);

        if (errorKey == null) {
            login(locale, entityManager, company, user);
        } else if (errorKey.equals("message-login-failed-duplicate-login-for-login-transaction-id")) {
            // Silently fail.
        } else {
            // Login failure
            new Notification(DefaultSiteUI.getLocalizationProvider().localize(errorKey, locale),
                    Notification.Type.WARNING_MESSAGE).show(Page.getCurrent());
        }
    }

    /**
     * Helper function for final login.
     * @param locale the locale
     * @param entityManager the entity manager
     * @param company the company
     * @param user the user
     */
    private static void login(Locale locale, EntityManager entityManager, Company company, User user) {
        final AbstractSiteUI ui = ((AbstractSiteUI)UI.getCurrent());
        final List<Group> groups = UserDao.getUserGroups(entityManager, company, user);
        DefaultSiteUI.getSecurityProvider().setUser(user, groups);

        // Check for imminent password expiration.
        if (user.getPasswordExpirationDate() != null
                && new DateTime().plusDays(14).toDate().getTime()
                > user.getPasswordExpirationDate().getTime() ) {
            final DateTime expirationDate = new DateTime(user.getPasswordExpirationDate());
            final DateTime currentDate = new DateTime();
            final long daysUntilExpiration = new Duration(currentDate.toDate().getTime(),
                    expirationDate.toDate().getTime()).getStandardDays();

            new Notification(DefaultSiteUI.getLocalizationProvider().localize(
                    "message-password-expires-in-days", locale)
                    + ": " + daysUntilExpiration, Notification.Type.WARNING_MESSAGE).show(Page.getCurrent());
        } else {
            ui.setNotification(DefaultSiteUI.getLocalizationProvider().localize(
                    "message-login-success", locale), Notification.Type.TRAY_NOTIFICATION);
        }

        ui.getPage().setLocation(company.getUrl());
    }
}
