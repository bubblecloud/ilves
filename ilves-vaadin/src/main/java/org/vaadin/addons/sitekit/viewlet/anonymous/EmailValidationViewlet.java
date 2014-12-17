package org.vaadin.addons.sitekit.viewlet.anonymous;

import com.vaadin.ui.Notification;
import org.vaadin.addons.sitekit.security.SecurityService;
import org.vaadin.addons.sitekit.security.UserDao;
import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.site.AbstractViewlet;

import javax.persistence.EntityManager;

/**
 * Viewlet for email validation.
 */
public class EmailValidationViewlet extends AbstractViewlet {

    @Override
    public final void enter(final String parameters) {
        final String userId = parameters;
        final EntityManager entityManager = getSite().getSiteContext().getObject(EntityManager.class);

        final User user = UserDao.getUser(entityManager, userId);

        if (user != null) {
            user.setEmailAddressValidated(true);
            SecurityService.updateUser(getSite().getSiteContext(), user);
            Notification.show(getSite().localize("message-email-verification.success"),
                    Notification.Type.HUMANIZED_MESSAGE);
        } else {
            Notification.show(getSite().localize("message-email-verification.error"),
                    Notification.Type.WARNING_MESSAGE);
        }

    }
}
