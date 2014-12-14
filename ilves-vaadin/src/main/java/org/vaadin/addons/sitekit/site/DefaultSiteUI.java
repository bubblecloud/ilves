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
package org.vaadin.addons.sitekit.site;

import com.vaadin.annotations.Theme;
import com.vaadin.server.*;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.vaadin.addons.sitekit.analytics.Analyser;
import org.vaadin.addons.sitekit.cache.UserClientCertificateCache;
import org.vaadin.addons.sitekit.dao.CompanyDao;
import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.service.LoginService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Locale;

/**
 * BareSite UI.
 *
 * @author Tommi S.E. Laukkanen
 */
@SuppressWarnings({ "serial", "unchecked" })
@Theme("ilves")
public final class DefaultSiteUI extends AbstractSiteUI {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(DefaultSiteUI.class);
    /** The shared entity manager factory. */
    private static EntityManagerFactory entityManagerFactory;
    /** The shared security provider. */
    private static SecurityProviderSessionImpl securityProvider;
    /** The shared content provider. */
    private static ContentProvider contentProvider;
    /** The shared localization provider. */
    private static LocalizationProvider localizationProvider;
    /** The analyser. */
    private Analyser analyser;

    @Override
    protected Site constructSite(final VaadinRequest request) {
        // Construct entity manager for this site context.
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        // Construct audit entity manager for this site context.
        final EntityManager auditEntityManager = entityManagerFactory.createEntityManager();
        // Choose company for this site context.
        final VaadinServletRequest servletRequest = (VaadinServletRequest) VaadinService.getCurrentRequest();
        // The virtual host based on URL.
        final Company company = resolveCompany(entityManager, servletRequest);

        final SiteContext siteContext = new SiteContext(entityManager, auditEntityManager, servletRequest, securityProvider);
        siteContext.putObject(EntityManager.class, entityManager);
        siteContext.putObject(EntityManagerFactory.class, entityManagerFactory);
        siteContext.putObject(Company.class, company);

        final X509Certificate[] clientCertificates = (X509Certificate[])
                servletRequest.getHttpServletRequest().getAttribute("javax.servlet.request.X509Certificate");

        if (clientCertificates != null && clientCertificates.length == 1
                && securityProvider.getUserFromSession() == null
                && company != null
                && company.isCertificateLogin()) {
            final User user = UserClientCertificateCache.getUserByCertificate(clientCertificates[0], true);
            if (user != null && user.getOwner().equals(company)) {
                securityProvider.setUser(user, UserDao.getUserGroups(entityManager, company, user));
                LOGGER.info("User certificate login: " + user.getEmailAddress() + " Remote address: "
                        + servletRequest.getHttpServletRequest().getRemoteAddr() + ":"
                        + servletRequest.getHttpServletRequest().getRemotePort() + ")");
            }
        }

        addCredentialPostRequestHandler();

        analyser = new Analyser(this, company.getGaTrackingId());
        this.getNavigator().addViewChangeListener(analyser);
        return new Site(SiteMode.PRODUCTION, contentProvider, localizationProvider, securityProvider, siteContext);
    }

    /**
     * Adds handler for credential posts.
     */
    private void addCredentialPostRequestHandler() {

        // Add handler for credentials post.
        VaadinSession.getCurrent().addRequestHandler(
                new RequestHandler() {
                    @Override
                    public boolean handleRequest(VaadinSession session,
                                                 VaadinRequest request,
                                                 VaadinResponse response)
                            throws IOException {
                        if (!StringUtils.isEmpty(request.getParameter("username")) &&
                                !StringUtils.isEmpty(request.getParameter("password")) &&
                                getSession() != null &&
                                getSession().getSession().getAttribute("user") == null) {

                            final String emailAddress = request.getParameter("username");
                            final String password = request.getParameter("password");

                            final EntityManager entityManager = getSite().getSiteContext().getEntityManager();
                            final Locale locale = getLocale();

                            final Company company = resolveCompany(entityManager, (VaadinServletRequest) request);
                            final User user = UserDao.getUser(entityManager, company, emailAddress);

                            final String errorKey = LoginService.login(getSite().getSiteContext(), company,
                                    user, emailAddress, password);

                            if (errorKey == null) {

                                // Login success
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

                                    setNotification( DefaultSiteUI.getLocalizationProvider().localize(
                                            "message-password-expires-in-days", locale)
                                            + ": " + daysUntilExpiration, Notification.Type.WARNING_MESSAGE);
                                } else {
                                    setNotification(DefaultSiteUI.getLocalizationProvider().localize(
                                            "message-login-success", locale), Notification.Type.TRAY_NOTIFICATION);
                                }
                            } else {
                                // Login failure
                                setNotification(DefaultSiteUI.getLocalizationProvider().localize(errorKey, locale),
                                        Notification.Type.WARNING_MESSAGE);
                            }

                        }
                        return false; // No response was written
                    }
                });
    }

    public static Company resolveCompany(EntityManager entityManager, VaadinServletRequest servletRequest) {
        final String hostName = servletRequest.getHttpServletRequest().getServerName();
        Company company = CompanyDao.getCompany(entityManager, hostName);
        if (company == null) {
            company = CompanyDao.getCompany(entityManager, "*");
        }
        return company;
    }

    /**
     * Setter for entity manager factory.
     *
     * @param entityManagerFactory the entity manager factory
     */
    public static void setEntityManagerFactory(EntityManagerFactory entityManagerFactory) {
        DefaultSiteUI.entityManagerFactory = entityManagerFactory;
    }

    /**
     * Sets the security provider.
     *
     * @param securityProvider the security provider.
     */
    public static void setSecurityProvider(SecurityProviderSessionImpl securityProvider) {
        DefaultSiteUI.securityProvider = securityProvider;
    }

    /**
     * Sets the content provider.
     *
     * @param contentProvider the content provider
     */
    public static void setContentProvider(ContentProvider contentProvider) {
        DefaultSiteUI.contentProvider = contentProvider;
    }

    /**
     * Sets the localization provider.
     *
     * @param localizationProvider the localization provider
     */
    public static void setLocalizationProvider(final LocalizationProvider localizationProvider) {
        DefaultSiteUI.localizationProvider = localizationProvider;
        // Configure fields.
        SiteFields.initialize();
    }

    /**
     * Gets the entity manager factory.
     *
     * @return the entity manager factory
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

    /**
     * Gets the security provider.
     *
     * @return the security provider
     */
    public static SecurityProviderSessionImpl getSecurityProvider() {
        return securityProvider;
    }

    /**
     * Gets the content provider.
     *
     * @return the content provider
     */
    public static ContentProvider getContentProvider() {
        return contentProvider;
    }

    /**
     * Gets the localization provider.
     *
     * @return the localization provider
     */
    public static LocalizationProvider getLocalizationProvider() {
        return localizationProvider;
    }

}
