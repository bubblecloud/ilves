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
package org.vaadin.addons.sitekit.viewlet.user;

import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import org.apache.log4j.Logger;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.Identifier;
import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.site.AbstractSiteUI;
import org.vaadin.addons.sitekit.site.AbstractViewlet;
import org.vaadin.addons.sitekit.util.OpenIdUtil;

import javax.persistence.EntityManager;

/**
 * Viewlet which links returning OpenId authentication to user account.
 * @author Tommi S.E. Laukkanen
 */
public final class OpenIdLinkViewlet extends AbstractViewlet {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(OpenIdLinkViewlet.class);

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

        try {
            final VerificationResult verification = OpenIdUtil.getVerificationResult(company.getUrl(), "openidlink");
            final Identifier identifier = verification.getVerifiedId();
            if (identifier != null) {
                final String userEmailAddress = getSite().getSecurityProvider().getUser();
                final User user = UserDao.getUser(entityManager, company, userEmailAddress);
                user.setOpenIdIdentifier(identifier.getIdentifier());
                UserDao.updateUser(entityManager, user);
                ((AbstractSiteUI) UI.getCurrent()).redirectTo(company.getUrl(), "account",
                        "OpenID authenticated user as: " + identifier.getIdentifier(),
                        Notification.Type.HUMANIZED_MESSAGE);
            }else {
                ((AbstractSiteUI) UI.getCurrent()).redirectTo(company.getUrl(), "account",
                        "OpenID authentication failed:" + verification.getStatusMsg(),
                        Notification.Type.ERROR_MESSAGE);
            }
        } catch (final Exception exception) {
            LOGGER.error("Error linking OpenID account.", exception);
            ((AbstractSiteUI) UI.getCurrent()).redirectTo(company.getUrl(), "account",
                    "Error linking OpenID account.",
                    Notification.Type.ERROR_MESSAGE);
        }
    }
}
