package org.bubblecloud.ilves.site;

import com.vaadin.server.*;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.model.Group;
import org.bubblecloud.ilves.model.User;
import org.bubblecloud.ilves.security.*;
import org.joda.time.DateTime;
import org.joda.time.Duration;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Handler for credential post requests like password login post or oauth redirect post.
 *
 * @author Tommi S.E. Laukkanen
 */
public class CredentialPostRequestHandler implements RequestHandler {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(CredentialPostRequestHandler.class);

    private DefaultSiteUI ui;

    public CredentialPostRequestHandler(DefaultSiteUI ui) {
        this.ui = ui;
    }

    @Override
    public boolean handleRequest(VaadinSession session,
                                 VaadinRequest request,
                                 VaadinResponse response)
            throws IOException {
        final VaadinServletResponse vaadinServletResponse = (VaadinServletResponse) response;
        final String pathInfo = request.getPathInfo();

        if (pathInfo != null && pathInfo.contains("oauthredirect")) {
            final EntityManager entityManager = ui.getSite().getSiteContext().getEntityManager();
            final Company company = DefaultSiteUI.resolveCompany(entityManager, (VaadinServletRequest) request);

            if (VaadinSession.getCurrent().getSession().getAttribute("user") == null) {
                final String code = request.getParameter("code");
                Locale locale = ui.getLocale();
                if (locale == null) {
                    locale = Locale.ENGLISH;
                }
                final User user = OAuthService.processOAuthRedirect(ui.getSite().getSiteContext(), company, code);

                if (user != null) {
                    entityManager.refresh(user);
                    login(locale, entityManager, company, user);
                } else {
                    ui.setNotification(DefaultSiteUI.getLocalizationProvider().localize("message-login-failed",
                            locale), Notification.Type.WARNING_MESSAGE);
                }
            }

            vaadinServletResponse.sendRedirect(company.getUrl());
            return true;
        }

        if (!StringUtils.isEmpty(request.getParameter("username")) &&
                !StringUtils.isEmpty(request.getParameter("password")) &&
                VaadinSession.getCurrent() != null &&
                VaadinSession.getCurrent().getSession().getAttribute("user") == null) {

            final String emailAddress = request.getParameter("username");
            final String password = request.getParameter("password");
            final String transactionId = request.getParameter("uiTransactionId");

            Locale locale = ui.getLocale();
            if (locale == null) {
                locale = Locale.ENGLISH;
            }

            final EntityManager entityManager = ui.getSite().getSiteContext().getEntityManager();
            final Company company = DefaultSiteUI.resolveCompany(entityManager, (VaadinServletRequest) request);
            final User user = UserDao.getUser(entityManager, company, emailAddress);

            if (user == null) {
                return false;
            }

            entityManager.refresh(user);

            if (user.getGoogleAuthenticatorSecret() != null) {
                final String code = request.getParameter("code");
                if (code == null || !GoogleAuthenticatorService.checkCode(SecurityUtil.decryptSecretKey(user.getGoogleAuthenticatorSecret()), code)) {
                    if (ui.getSession() == null) {
                        LOGGER.error("Vaadin UI not initialized when CredentialPostRequestHandler was invoked.");
                        return false;
                    }
                    ui.setNotification(DefaultSiteUI.getLocalizationProvider().localize("message-login-failed", locale),
                            Notification.Type.WARNING_MESSAGE);
                    return false;
                }
            }

            final String errorKey = LoginService.login(ui.getSite().getSiteContext(), company,
                    user, emailAddress, password.toCharArray(), VaadinSession.getCurrent().getSession().getId(), transactionId);

            if (errorKey == null) {
                login(locale, entityManager, company, user);
            } else if (errorKey.equals("message-login-failed-duplicate-login-for-login-transaction-id")) {
                // Silently fail.
            } else {
                // Login failure
                ui.setNotification(DefaultSiteUI.getLocalizationProvider().localize(errorKey, locale),
                        Notification.Type.WARNING_MESSAGE);
            }

        }
        return false; // No response was written
    }

    public void login(Locale locale, EntityManager entityManager, Company company, User user) {
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

            ui.setNotification(DefaultSiteUI.getLocalizationProvider().localize(
                    "message-password-expires-in-days", locale)
                    + ": " + daysUntilExpiration, Notification.Type.WARNING_MESSAGE);
        } else {
            ui.setNotification(DefaultSiteUI.getLocalizationProvider().localize(
                    "message-login-success", locale), Notification.Type.TRAY_NOTIFICATION);
        }
    }
}
