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
import org.vaadin.addons.sitekit.viewlet.anonymous.CompanyFooterViewlet;
import org.vaadin.addons.sitekit.viewlet.anonymous.EmailValidationViewlet;
import org.vaadin.addons.sitekit.viewlet.anonymous.FeedbackViewlet;
import org.vaadin.addons.sitekit.viewlet.anonymous.ImageViewlet;
import org.vaadin.addons.sitekit.viewlet.anonymous.NavigationViewlet;
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
public final class BareSiteUI extends AbstractSiteUI implements ContentProvider {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(BareSiteUI.class);
    /** The properties category used in instantiating default services. */
    private static final String PROPERTIES_CATEGORY = "bare-site";
    /** The persistence unit to be used. */
    public static final String PERSISTENCE_UNIT = "bare-site";

    /**
     * Main method for running BareSiteUI.
     * @param args the commandline arguments
     * @throws Exception if exception occurs in jetty startup.
     */
    public static void main(final String[] args) throws Exception {
        DOMConfigurator.configure("./log4j.xml");

        entityManagerFactory = PersistenceUtil.getEntityManagerFactory(PERSISTENCE_UNIT, PROPERTIES_CATEGORY);

        final boolean test = BareSiteUI.class.getClassLoader()
                .getResource("webapp/").toExternalForm().startsWith("file:");

        final String webappUrl;
        if (test) {
            webappUrl = "src/main/resources/webapp/";
        } else {
            webappUrl = BareSiteUI.class.getClassLoader().getResource("webapp/").toExternalForm();
        }

        final Server server = new Server(8081);

        final WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        context.setDescriptor(webappUrl + "/WEB-INF/web.xml");
        context.setResourceBase(webappUrl);
        context.setParentLoaderPriority(true);
        if (test) {
            context.setInitParameter("cacheControl","no-cache");
            context.setInitParameter("useFileMappedBuffer", "false");
            context.setInitParameter("maxCachedFiles", "0");
        }
        server.setHandler(context);
        server.start();
        server.join();
    }

    @Override
    protected Site constructSite(final VaadinRequest request) {

        final ContentProvider contentProvider = this;

        final LocalizationProvider localizationProvider =
                new LocalizationProviderBundleImpl(new String[] {"bare-site-localization"});
        BareSiteFields.initialize(localizationProvider, getLocale());

        final SiteContext siteContext = new SiteContext();
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        siteContext.putObject(EntityManager.class, entityManager);

        Company company = CompanyDao.getCompany(entityManager,
                ((VaadinServletRequest) VaadinService.getCurrentRequest()).getHttpServletRequest().getServerName());
        if (company == null) {
            // If no exact host match exists then try to find global company marked with *.
            company = CompanyDao.getCompany(entityManager, "*");
        }
        siteContext.putObject(Company.class, company);

        final SecurityProviderSessionImpl securityProvider = new SecurityProviderSessionImpl(
                Arrays.asList("administrator", "user"));

        return new Site(SiteMode.PRODUCTION, contentProvider, localizationProvider, securityProvider, siteContext);
    }

    @Override
    public SiteDescriptor getSiteDescriptor() {
        final List<ViewDescriptor> viewDescriptors = new ArrayList<ViewDescriptor>();

        final ViewDescriptor master = new ViewDescriptor("master", "Master", DefaultCustomView.class);
        master.setViewerRoles("superuser");
        master.setViewletClass("logo", ImageViewlet.class, "logo.png");
        master.setViewletClass("navigation", NavigationViewlet.class);
        master.setViewletClass("footer", CompanyFooterViewlet.class);
        viewDescriptors.add(master);

        final ViewDescriptor feedback = new ViewDescriptor("default", "Default", DefaultCustomView.class);
        feedback.setViewletClass("content", FeedbackViewlet.class);
        viewDescriptors.add(feedback);

        final ViewDescriptor users = new ViewDescriptor("users", "Users", DefaultCustomView.class);
        users.setViewerRoles("administrator");
        users.setViewletClass("content", UserFlowViewlet.class);
        viewDescriptors.add(users);

        final ViewDescriptor groups = new ViewDescriptor("groups", "Groups", DefaultCustomView.class);
        groups.setViewerRoles("administrator");
        groups.setViewletClass("content", GroupFlowViewlet.class);
        viewDescriptors.add(groups);

        final ViewDescriptor customers = new ViewDescriptor("customers", "Customers", DefaultCustomView.class);
        customers.setViewerRoles("administrator");
        customers.setViewletClass("content", CustomerFlowViewlet.class);
        viewDescriptors.add(customers);

        final ViewDescriptor companies = new ViewDescriptor("companies", "Companies", DefaultCustomView.class);
        companies.setViewerRoles("administrator");
        companies.setViewletClass("content", CompanyFlowViewlet.class);
        viewDescriptors.add(companies);

        final ViewDescriptor login = new ViewDescriptor("login", "Login", DefaultCustomView.class);
        login.setViewerRoles("anonymous");
        login.setViewletClass("content", LoginFlowViewlet.class);
        viewDescriptors.add(login);

        final ViewDescriptor account = new ViewDescriptor("account", "Account", DefaultCustomView.class);
        account.setViewerRoles("user", "administrator");
        account.setViewletClass("content", AccountFlowViewlet.class);
        viewDescriptors.add(account);

        final ViewDescriptor validate = new ViewDescriptor("validate", "Email Validation", DefaultCustomView.class);
        validate.setViewletClass("content", EmailValidationViewlet.class);
        viewDescriptors.add(validate);

        final NavigationDescriptor navigationDescriptor = new NavigationDescriptor("navigation", null, null,
                new NavigationVersion(0, "default", "default;login;customers;users;groups;companies;account", true));

        return new SiteDescriptor("Test site.", "test site", "This is a test site.",
                navigationDescriptor, viewDescriptors);

    }

    /** The entity manager factory for test. */
    private static EntityManagerFactory entityManagerFactory;

}
