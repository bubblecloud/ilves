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

import org.bubblecloud.ilves.oauth.OpenAuthViewlet;
import org.bubblecloud.ilves.security.DefaultRoles;
import org.bubblecloud.ilves.site.view.valo.DefaultValoView;
import org.bubblecloud.ilves.ui.AccessDeniedViewlet;
import org.bubblecloud.ilves.ui.administrator.company.CompanyFlowViewlet;
import org.bubblecloud.ilves.ui.administrator.directory.UserDirectoryFlowViewlet;
import org.bubblecloud.ilves.ui.administrator.group.GroupFlowViewlet;
import org.bubblecloud.ilves.ui.administrator.user.UserFlowViewlet;
import org.bubblecloud.ilves.ui.anonymous.EmailValidationViewlet;
import org.bubblecloud.ilves.ui.anonymous.PasswordResetViewlet;
import org.bubblecloud.ilves.ui.anonymous.login.LoginFlowViewlet;
import org.bubblecloud.ilves.ui.anonymous.login.OpenIdLoginViewlet;
import org.bubblecloud.ilves.ui.user.AccountFlowViewlet;
import org.bubblecloud.ilves.ui.user.OpenIdLinkViewlet;

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
        viewDescriptors.add(master);

        final ViewDescriptor configuration = new ViewDescriptor("configuration", "Configuration", DefaultValoView.class);
        configuration.setViewerRoles(DefaultRoles.USER, DefaultRoles.ADMINISTRATOR);
        viewDescriptors.add(configuration);

        final ViewDescriptor users = new ViewDescriptor("users", "Users", DefaultValoView.class);
        users.setViewerRoles(DefaultRoles.ADMINISTRATOR);
        users.setViewletClass("content", UserFlowViewlet.class);
        viewDescriptors.add(users);

        final ViewDescriptor groups = new ViewDescriptor("groups", "Groups", DefaultValoView.class);
        groups.setViewerRoles(DefaultRoles.ADMINISTRATOR);
        groups.setViewletClass("content", GroupFlowViewlet.class);
        viewDescriptors.add(groups);

        final ViewDescriptor directories = new ViewDescriptor("directories", "Directories", DefaultValoView.class);
        directories.setViewerRoles(DefaultRoles.ADMINISTRATOR);
        directories.setViewletClass("content", UserDirectoryFlowViewlet.class);
        viewDescriptors.add(directories);

        final ViewDescriptor companies = new ViewDescriptor("companies", "Companies", DefaultValoView.class);
        companies.setViewerRoles(DefaultRoles.ADMINISTRATOR);
        companies.setViewletClass("content", CompanyFlowViewlet.class);
        viewDescriptors.add(companies);

        final ViewDescriptor accessDenied = new ViewDescriptor("denied", "Access Denied", DefaultValoView.class);
        accessDenied.setViewerRoles(DefaultRoles.ANONYMOUS);
        accessDenied.setViewletClass("content", AccessDeniedViewlet.class);
        viewDescriptors.add(accessDenied);

        final ViewDescriptor login = new ViewDescriptor("login", "Login", DefaultValoView.class);
        login.setViewerRoles(DefaultRoles.ANONYMOUS);
        login.setViewletClass("content", LoginFlowViewlet.class);
        viewDescriptors.add(login);

        final ViewDescriptor account = new ViewDescriptor("account", "Account", DefaultValoView.class);
        account.setViewerRoles(DefaultRoles.USER, DefaultRoles.ADMINISTRATOR);
        account.setViewletClass("content", AccountFlowViewlet.class);
        viewDescriptors.add(account);

        final ViewDescriptor validate = new ViewDescriptor("validate", "Email Validation", DefaultValoView.class);
        validate.setViewletClass("content", EmailValidationViewlet.class);
        viewDescriptors.add(validate);

        final ViewDescriptor openidlink = new ViewDescriptor("openidlink", "OpenID Link", DefaultValoView.class);
        openidlink.setViewerRoles(DefaultRoles.USER, DefaultRoles.ADMINISTRATOR);
        openidlink.setViewletClass("content", OpenIdLinkViewlet.class);
        viewDescriptors.add(openidlink);

        final ViewDescriptor openidlogin = new ViewDescriptor("openidlogin", "OpenID Login", DefaultValoView.class);
        openidlogin.setViewerRoles(DefaultRoles.ANONYMOUS);
        openidlogin.setViewletClass("content", OpenIdLoginViewlet.class);
        viewDescriptors.add(openidlogin);

        final ViewDescriptor oauth = new ViewDescriptor("oauth", "OAuth", DefaultValoView.class);
        oauth.setViewerRoles(DefaultRoles.ANONYMOUS);
        oauth.setViewletClass("content", OpenAuthViewlet.class);
        viewDescriptors.add(oauth);

        final ViewDescriptor reset = new ViewDescriptor("reset", "Password Reset", DefaultValoView.class);
        reset.setViewletClass("content", PasswordResetViewlet.class);
        viewDescriptors.add(reset);

        final NavigationVersion navigationVersion = new NavigationVersion(0, "users", null, true);

        navigationVersion.addRootPage("login");
        navigationVersion.addRootPage("login", "configuration");
        navigationVersion.addChildPage("configuration", "account");
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
