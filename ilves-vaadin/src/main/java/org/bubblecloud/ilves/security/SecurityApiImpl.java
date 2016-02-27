package org.bubblecloud.ilves.security;

import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.bubblecloud.ilves.api.ApiImplementation;
import org.bubblecloud.ilves.api.apis.RequestAccessTokenResult;
import org.bubblecloud.ilves.api.apis.SecurityApi;
import org.bubblecloud.ilves.model.*;
import org.bubblecloud.ilves.module.customer.CustomerModule;
import org.bubblecloud.ilves.site.SiteContext;
import org.bubblecloud.ilves.site.SiteModuleManager;
import org.bubblecloud.ilves.util.StringUtil;

import javax.persistence.EntityManager;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

/**
 * The security API implementation.
 *
 * @author Tommi S.E. Laukkanen
 */
public class SecurityApiImpl implements SecurityApi, ApiImplementation {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(SecurityApiImpl.class);
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
            final RequestAccessTokenResult result = new RequestAccessTokenResult();
            result.setErrorKey("message-login-failed");
            return result;
        }

        final List<AuthenticationDevice> authenticationDevices = AuthenticationDeviceDao.getAuthenticationDevices(entityManager, user);
        if (authenticationDevices.size() > 0) {
            LOGGER.warn("Request access token failed due to account has authentication devices defined. OAuth should be used to get access token: " + emailAddress);
            final RequestAccessTokenResult result = new RequestAccessTokenResult();
            result.setErrorKey("message-login-failed");
            return result;
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
            result.setAccessToken(null);
            result.setExpirationTime(null);
            result.setErrorKey(errorKey);
            return result;
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

    @Override
    public boolean selfRegisterUser(final String firstName, final String lastName, final String emailAddress, final String phoneNumber, final String password) {
        final EntityManager entityManager = context.getEntityManager();
        final Company company = context.getObject(Company.class);

        final User existingUser = UserDao.getUser(entityManager, company, emailAddress);
        if (existingUser != null) {
            return false;
        } else {
            if (!company.isSelfRegistration()) {
                return false;
            }

            final User newUser = new User(company, firstName, lastName, emailAddress, phoneNumber, "");
            UserDao.addUser(entityManager, newUser, UserDao.getGroup(entityManager, company, "user"));

            final byte[] passwordAndSaltBytes = SecurityUtil.convertCharactersToBytes(ArrayUtils.addAll((newUser.getUserId() + ":").toCharArray(), password.toCharArray()));
            final MessageDigest md;
            try {
                md = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e) {
                throw new SecurityException(e);
            }
            final byte[] passwordAndSaltDigest = md.digest(passwordAndSaltBytes);
            newUser.setPasswordHash(StringUtil.toHexString(passwordAndSaltDigest));
            UserDao.updateUser(entityManager, newUser);

            if (SiteModuleManager.isModuleInitialized(CustomerModule.class)) {
                final Customer customer = new Customer(firstName, lastName, emailAddress, phoneNumber, false, "", "");
                customer.setCreated(new Date());
                customer.setModified(customer.getCreated());
                customer.setOwner(company);
                final PostalAddress invoicingAddress = new PostalAddress();
                invoicingAddress.setAddressLineOne("-");
                invoicingAddress.setAddressLineTwo("-");
                invoicingAddress.setAddressLineThree("-");
                invoicingAddress.setCity("-");
                invoicingAddress.setPostalCode("-");
                invoicingAddress.setCountry("-");
                final PostalAddress deliveryAddress = new PostalAddress();
                deliveryAddress.setAddressLineOne("-");
                deliveryAddress.setAddressLineTwo("-");
                deliveryAddress.setAddressLineThree("-");
                deliveryAddress.setCity("-");
                deliveryAddress.setPostalCode("-");
                deliveryAddress.setCountry("-");
                customer.setInvoicingAddress(invoicingAddress);
                customer.setDeliveryAddress(deliveryAddress);
                CustomerDao.addCustomer(entityManager, customer);
                UserDao.addGroupMember(context.getEntityManager(), customer.getAdminGroup(), newUser);
                UserDao.addGroupMember(context.getEntityManager(), customer.getMemberGroup(), newUser);
            }

            AuditService.log(context, "api-self-register", "user", newUser.getUserId(), emailAddress);
            return true;
        }
    }

}
