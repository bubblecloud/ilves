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
package org.vaadin.addons.sitekit.viewlet.anonymous.login;

import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.LoginForm;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.apache.log4j.Logger;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.Identifier;
import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.module.audit.AuditService;
import org.vaadin.addons.sitekit.site.AbstractSiteUI;
import org.vaadin.addons.sitekit.site.AbstractViewlet;
import org.vaadin.addons.sitekit.site.ProcessingContext;
import org.vaadin.addons.sitekit.site.SecurityProviderSessionImpl;
import org.vaadin.addons.sitekit.util.OpenIdUtil;
import org.vaadin.addons.sitekit.util.StringUtil;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.util.List;

/**
 * Viewlet which links returning OpenId authentication to user account.
 * @author Tommi S.E. Laukkanen
 */
public final class OpenIdLoginViewlet extends AbstractViewlet {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(OpenIdLoginViewlet.class);

    @Override
    public void attach() {
        super.attach();

    }

    /**
     * SiteView constructSite occurred.
     */
    @Override
    public void enter(final String parameterString) {

        final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);
        final Company company = getSite().getSiteContext().getObject(Company.class);
        final HttpServletRequest request = ((VaadinServletRequest) VaadinService.getCurrentRequest())
                .getHttpServletRequest();

        try {
            final VerificationResult verification = OpenIdUtil.getVerificationResult(company.getUrl(), "openidlogin");
            final Identifier identifier = verification.getVerifiedId();

            if (identifier == null) {
                ((AbstractSiteUI) UI.getCurrent()).redirectTo(company.getUrl(), "login",
                        getSite().localize("message-login-failed")
                                + ":" + verification.getStatusMsg(),
                        Notification.Type.ERROR_MESSAGE
                );
            }

            final User user = UserDao.getUserByOpenIdIdentifier(entityManager, company, identifier.getIdentifier());

            if (user == null) {
                LOGGER.warn("User OpenID login failed due to not registered Open ID identifier: "
                        + identifier.getIdentifier()
                        + " (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")");
                ((AbstractSiteUI) UI.getCurrent()).redirectTo(company.getUrl(), "login",
                        getSite().localize("message-login-failed"),
                        Notification.Type.WARNING_MESSAGE);
                return;
            }

            if (user.isLockedOut()) {
                LOGGER.warn("User login failed due to user being locked out: " + user.getEmailAddress()
                        + " (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")");
                ((AbstractSiteUI) UI.getCurrent()).redirectTo(company.getUrl(), "login",
                        getSite().localize("message-login-failed"),
                        Notification.Type.WARNING_MESSAGE);
                return;
            }

            final ProcessingContext processingContext = new ProcessingContext(entityManager, entityManager,
                    request, user,
                    getSite().getSecurityProvider().getRoles());

            LOGGER.info("User login: " + user.getEmailAddress()
                    + " (IP: " + request.getRemoteHost() + ":" + request.getRemotePort() + ")");
            AuditService.log(processingContext, "openid password login");

            final List<Group> groups = UserDao.getUserGroups(entityManager, company, user);

            UserDao.updateUser(entityManager, user);

            ((SecurityProviderSessionImpl) getSite().getSecurityProvider()).setUser(user, groups);

            ((AbstractSiteUI) UI.getCurrent()).redirectTo(company.getUrl(),
                    getSite().getCurrentNavigationVersion().getDefaultPageName(),
                    getSite().localize("message-login-success") + " (" + user.getEmailAddress() + ")",
                    Notification.Type.HUMANIZED_MESSAGE);

        } catch (final Exception exception) {
            LOGGER.error("Error logging in OpenID user.", exception);
            ((AbstractSiteUI) UI.getCurrent()).redirectTo(company.getUrl(), "login",
                    getSite().localize("message-login-error"),
                    Notification.Type.ERROR_MESSAGE);
        }
    }

}
