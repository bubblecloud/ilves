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
package org.bubblecloud.ilves.site;

import com.vaadin.annotations.Theme;
import com.vaadin.server.*;
import org.apache.log4j.Logger;
import org.bubblecloud.ilves.cache.UserClientCertificateCache;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.model.User;
import org.bubblecloud.ilves.security.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.security.cert.X509Certificate;

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
    private SiteAnalyser analyser;

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

        VaadinSession.getCurrent().addRequestHandler(
                new CredentialPostRequestHandler(this));

        analyser = new SiteAnalyser(this, company.getGaTrackingId());
        this.getNavigator().addViewChangeListener(analyser);
        return new Site(SiteMode.PRODUCTION, contentProvider, localizationProvider, securityProvider, siteContext);
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
