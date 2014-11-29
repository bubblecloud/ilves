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

import org.vaadin.addons.sitekit.valo.DefaultValoView;
import org.vaadin.addons.sitekit.viewlet.AccessDeniedViewlet;
import org.vaadin.addons.sitekit.viewlet.administrator.company.CompanyFlowViewlet;
import org.vaadin.addons.sitekit.viewlet.administrator.customer.CustomerFlowViewlet;
import org.vaadin.addons.sitekit.viewlet.administrator.directory.UserDirectoryFlowViewlet;
import org.vaadin.addons.sitekit.viewlet.administrator.group.GroupFlowViewlet;
import org.vaadin.addons.sitekit.viewlet.administrator.user.UserFlowViewlet;
import org.vaadin.addons.sitekit.viewlet.anonymous.*;
import org.vaadin.addons.sitekit.viewlet.anonymous.login.LoginFlowViewlet;
import org.vaadin.addons.sitekit.viewlet.anonymous.login.OpenIdLoginViewlet;
import org.vaadin.addons.sitekit.viewlet.user.AccountFlowViewlet;
import org.vaadin.addons.sitekit.viewlet.user.OpenIdLinkViewlet;
import org.vaadin.addons.sitekit.viewlet.user.ProfileImageViewlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bare site content provider.
 */
public class DefaultContentProvider implements ContentProvider {

    /**
     * The site descriptor.
     */
    private final SiteDescriptor siteDescriptor;

    public DefaultContentProvider() {
        final List<ViewDescriptor> viewDescriptors = Collections.synchronizedList(new ArrayList<ViewDescriptor>());

        final ViewDescriptor master = new ViewDescriptor("master", "Master", DefaultValoView.class);
        master.setViewerRoles("superuser");
        master.setViewletClass("logo", ImageViewlet.class, "logo.png");
        master.setViewletClass("navigation", MenuNavigationViewlet.class);
        master.setViewletClass("profile", ProfileImageViewlet.class);
        master.setViewletClass("footer", CompanyFooterViewlet.class);
        viewDescriptors.add(master);

        final ViewDescriptor configuration = new ViewDescriptor("configuration", "Configuration", DefaultValoView.class);
        configuration.setViewerRoles("user", "administrator");
        viewDescriptors.add(configuration);

        final ViewDescriptor users = new ViewDescriptor("users", "Users", DefaultValoView.class);
        users.setViewerRoles("administrator");
        users.setViewletClass("content", UserFlowViewlet.class);
        viewDescriptors.add(users);

        final ViewDescriptor groups = new ViewDescriptor("groups", "Groups", DefaultValoView.class);
        groups.setViewerRoles("administrator");
        groups.setViewletClass("content", GroupFlowViewlet.class);
        viewDescriptors.add(groups);

        final ViewDescriptor customers = new ViewDescriptor("customers", "Customers", DefaultValoView.class);
        customers.setViewerRoles("administrator");
        customers.setViewletClass("content", CustomerFlowViewlet.class);
        viewDescriptors.add(customers);

        final ViewDescriptor directories = new ViewDescriptor("directories", "Directories", DefaultValoView.class);
        directories.setViewerRoles("administrator");
        directories.setViewletClass("content", UserDirectoryFlowViewlet.class);
        viewDescriptors.add(directories);

        final ViewDescriptor companies = new ViewDescriptor("companies", "Companies", DefaultValoView.class);
        companies.setViewerRoles("administrator");
        companies.setViewletClass("content", CompanyFlowViewlet.class);
        viewDescriptors.add(companies);

        final ViewDescriptor accessDenied = new ViewDescriptor("denied", "Access Denied", DefaultValoView.class);
        accessDenied.setViewerRoles("anonymous");
        accessDenied.setViewletClass("content", AccessDeniedViewlet.class);
        viewDescriptors.add(accessDenied);

        final ViewDescriptor login = new ViewDescriptor("login", "Login", DefaultValoView.class);
        login.setViewerRoles("anonymous");
        login.setViewletClass("content", LoginFlowViewlet.class);
        viewDescriptors.add(login);

        final ViewDescriptor account = new ViewDescriptor("account", "Account", DefaultValoView.class);
        account.setViewerRoles("user", "administrator");
        account.setViewletClass("content", AccountFlowViewlet.class);
        viewDescriptors.add(account);

        final ViewDescriptor validate = new ViewDescriptor("validate", "Email Validation", DefaultValoView.class);
        validate.setViewletClass("content", EmailValidationViewlet.class);
        viewDescriptors.add(validate);

        final ViewDescriptor openidlink = new ViewDescriptor("openidlink", "OpenID Link", DefaultValoView.class);
        openidlink.setViewerRoles("user", "administrator");
        openidlink.setViewletClass("content", OpenIdLinkViewlet.class);
        viewDescriptors.add(openidlink);

        final ViewDescriptor openidlogin = new ViewDescriptor("openidlogin", "OpenID Login", DefaultValoView.class);
        openidlogin.setViewerRoles("anonymous");
        openidlogin.setViewletClass("content", OpenIdLoginViewlet.class);
        viewDescriptors.add(openidlogin);

        final ViewDescriptor reset = new ViewDescriptor("reset", "Password Reset", DefaultValoView.class);
        reset.setViewletClass("content", PasswordResetViewlet.class);
        viewDescriptors.add(reset);

        final NavigationVersion navigationVersion = new NavigationVersion(0, "users", null, true);

        navigationVersion.addRootPage("login");
        navigationVersion.addRootPage("configuration");
        navigationVersion.addChildPage("configuration", "account");
        navigationVersion.addChildPage("configuration", "customers");
        navigationVersion.addChildPage("configuration", "users");
        navigationVersion.addChildPage("configuration", "groups");
        navigationVersion.addChildPage("configuration", "directories");
        navigationVersion.addChildPage("configuration", "companies");

        final NavigationDescriptor navigationDescriptor = new NavigationDescriptor("navigation", null, null,
                navigationVersion);

        siteDescriptor = new  SiteDescriptor("Test site.", "test site", "This is a test site.",
                navigationDescriptor, viewDescriptors);
    }

    @Override
    public SiteDescriptor getSiteDescriptor() {
        return siteDescriptor;
    }

    @Override
    public SiteDescriptor getDynamicSiteDescriptor() {
        final SiteDescriptor dynamicSiteDescriptor = siteDescriptor.clone();
        SiteModuleManager.injectDynamicContent(dynamicSiteDescriptor);
        return dynamicSiteDescriptor;
    }

}
