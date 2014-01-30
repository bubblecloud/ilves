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

import org.vaadin.addons.sitekit.dao.CompanyDao;
import org.vaadin.addons.sitekit.model.Company;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * BareSite UI.
 *
 * @author Tommi S.E. Laukkanen
 */
@SuppressWarnings({ "serial", "unchecked" })
@Theme("sitekit")
public final class DefaultSiteUI extends AbstractSiteUI {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(DefaultSiteUI.class);
    /** The shared entity manager factory. */
    private static EntityManagerFactory entityManagerFactory;
    /** The shared security provider. */
    private static SecurityProviderSessionImpl securityProvider;
    /** The shared content provider. */
    private static ContentProvider contentProvider;
    /** The shared localization provider. */
    private static LocalizationProvider localizationProvider;

    @Override
    protected Site constructSite(final VaadinRequest request) {
        final SiteContext siteContext = new SiteContext();

        // Construct entity manager for this site context.
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        siteContext.putObject(EntityManager.class, entityManager);

        // Choose company for this site context.
        final VaadinServletRequest servletRequest = (VaadinServletRequest) VaadinService.getCurrentRequest();
        final String hostName = servletRequest.getHttpServletRequest().getServerName();
        final Company company = CompanyDao.getCompany(entityManager, hostName);
        if (company == null) {
            siteContext.putObject(Company.class, CompanyDao.getCompany(entityManager, "*"));
        } else {
            siteContext.putObject(Company.class, company);
        }

        return new Site(SiteMode.PRODUCTION, contentProvider, localizationProvider, securityProvider, siteContext);
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
    public static void setLocalizationProvider(LocalizationProvider localizationProvider) {
        DefaultSiteUI.localizationProvider = localizationProvider;
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
