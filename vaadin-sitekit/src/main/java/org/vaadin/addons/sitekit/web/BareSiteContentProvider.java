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

import org.vaadin.addons.sitekit.site.*;
import org.vaadin.addons.sitekit.viewlet.administrator.company.CompanyFlowViewlet;
import org.vaadin.addons.sitekit.viewlet.administrator.customer.CustomerFlowViewlet;
import org.vaadin.addons.sitekit.viewlet.administrator.group.GroupFlowViewlet;
import org.vaadin.addons.sitekit.viewlet.administrator.user.UserFlowViewlet;
import org.vaadin.addons.sitekit.viewlet.anonymous.*;
import org.vaadin.addons.sitekit.viewlet.anonymous.login.LoginFlowViewlet;
import org.vaadin.addons.sitekit.viewlet.user.AccountFlowViewlet;

import java.util.ArrayList;
import java.util.List;

/**
 * Bare site content provider.
 */
public class BareSiteContentProvider implements ContentProvider {

    @Override
    public SiteDescriptor getSiteDescriptor() {
        final List<ViewDescriptor> viewDescriptors = new ArrayList<ViewDescriptor>();

        final ViewDescriptor master = new ViewDescriptor("master", "Master", DefaultCustomView.class);
        master.setViewerRoles("superuser");
        master.setViewletClass("logo", ImageViewlet.class, "logo.png");
        master.setViewletClass("navigation", HorizontalNavigationViewlet.class);
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

}
