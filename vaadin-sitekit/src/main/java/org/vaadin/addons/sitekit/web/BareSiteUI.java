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
package org.vaadin.addons.sitekit.web;

import org.apache.log4j.xml.DOMConfigurator;
import org.vaadin.addons.sitekit.dao.CompanyDao;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.site.*;
import org.vaadin.addons.sitekit.util.PersistenceUtil;
import org.vaadin.addons.sitekit.viewlet.administrator.company.CompanyFlowViewlet;
import org.vaadin.addons.sitekit.viewlet.administrator.customer.CustomerFlowViewlet;
import org.vaadin.addons.sitekit.viewlet.administrator.group.GroupFlowViewlet;
import org.vaadin.addons.sitekit.viewlet.administrator.user.UserFlowViewlet;
import org.vaadin.addons.sitekit.viewlet.anonymous.*;
import org.vaadin.addons.sitekit.viewlet.anonymous.HorizontalNavigationViewlet;
import org.vaadin.addons.sitekit.viewlet.anonymous.login.LoginFlowViewlet;
import com.vaadin.annotations.Theme;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;
import org.vaadin.addons.sitekit.viewlet.user.AccountFlowViewlet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * BareSite UI.
 *
 * @author Tommi S.E. Laukkanen
 */
@SuppressWarnings({ "serial", "unchecked" })
@Theme("sitekit")
public final class BareSiteUI extends AbstractSiteUI {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(BareSiteUI.class);
    /** The properties category used in instantiating default services. */
    private static final String PROPERTIES_CATEGORY = "bare-site";
    /** The persistence unit to be used. */
    public static final String PERSISTENCE_UNIT = "bare-site";
    /** The shared entity manager factory. */
    private static EntityManagerFactory entityManagerFactory;
    /** The shared security provider. */
    private static final SecurityProviderSessionImpl securityProvider;
    /** The shared content provider. */
    private static final ContentProvider contentProvider;
    /** The shared localization provider. */
    private static final LocalizationProvider localizationProvider;

    /**
     * Static initialization.
     */
    static {
        DOMConfigurator.configure("./log4j.xml");

        entityManagerFactory = PersistenceUtil.getEntityManagerFactory(PERSISTENCE_UNIT, PROPERTIES_CATEGORY);
        securityProvider = new SecurityProviderSessionImpl("administrator", "user");
        contentProvider = new BareSiteContentProvider();
        localizationProvider = new LocalizationProviderBundleImpl("bare-site-localization");
        SiteFields.initialize(localizationProvider);
    }

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

}
