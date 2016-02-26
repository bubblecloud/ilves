package org.bubblecloud.ilves.api.apis;

import org.apache.log4j.Logger;
import org.bubblecloud.ilves.api.ApiImplementation;
import org.bubblecloud.ilves.model.AuthenticationDevice;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.model.User;
import org.bubblecloud.ilves.model.UserSession;
import org.bubblecloud.ilves.security.*;
import org.bubblecloud.ilves.site.SiteContext;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;

/**
 * The security API implementation.
 *
 * @author Tommi S.E. Laukkanen
 */
public class SecurityImpl implements Security, ApiImplementation {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(SecurityImpl.class);
    /** The context. */
    private SiteContext context;

    @Override
    public void setContext(final SiteContext context) {
        this.context = context;
    }

    @Override
    public RequestAccessTokenResult requestAccessToken(final String account, final String password) {
        final String emailAddress = account;
        final EntityManager entityManager = context.getEntityManager();
        final Company company = context.getObject(Company.class);
        final User user = UserDao.getUser(entityManager, company, emailAddress);

        if (user == null) {
            LOGGER.warn("Request access token failed due to account not found: " + emailAddress);
            return null;
        }

        final List<AuthenticationDevice> authenticationDevices = AuthenticationDeviceDao.getAuthenticationDevices(entityManager, user);
        if (authenticationDevices.size() > 0) {
            LOGGER.warn("Request access token failed due to account has authentication devices defined. OAuth should be used to get access token: " + emailAddress);
            return null;
        }

        final char[] accessToken = SecurityUtil.generateAccessToken();
        final RequestAccessTokenResult result = new RequestAccessTokenResult();
        result.setAccessToken(new String(accessToken));
        result.setExpirationTime(new Date(System.currentTimeMillis() + SecurityUtil.ACCESS_TOKEN_LIFETIME_MILLIS));

        final String errorKey = LoginService.login(context, company,
                user, emailAddress, password.toCharArray(), context.getSession().getId(), accessToken);

        if (errorKey == null) {
            return result;
        } else {
            return null;
        }
    }

    @Override
    public void invalidateAccessToken(final String account, final String accessToken) {
        final String emailAddress = account;
        final EntityManager entityManager = context.getEntityManager();
        final Company company = context.getObject(Company.class);
        final User user = UserDao.getUser(entityManager, company, emailAddress);

        if (user == null) {
            LOGGER.warn("Invalidate access token failed due to account not found: " + emailAddress);
            return;
        }

        final String accessTokenHash = SecurityUtil.getSecretHash(accessToken.toCharArray());
        final UserSession userSession = SecurityService.getUserSessionByAccessTokenHash(entityManager, accessTokenHash);

        if (userSession != null) {
            SecurityService.removeUserSession(entityManager, userSession);
        }

        return;
    }

}
