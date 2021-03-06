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
package org.bubblecloud.ilves.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.GitHubTokenResponse;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.model.Customer;
import org.bubblecloud.ilves.model.PostalAddress;
import org.bubblecloud.ilves.model.User;
import org.bubblecloud.ilves.module.customer.CustomerModule;
import org.bubblecloud.ilves.site.SiteContext;
import org.bubblecloud.ilves.site.SiteModuleManager;

import javax.persistence.EntityManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Service for OAuth.
 *
 * @author Tommi S.E Laukkanen
 */
public class OAuthService {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(OAuthService.class);

    public static String requestOAuthLocationUri(final SiteContext context) {
        try {
            final Company company = context.getObject(Company.class);
            if (!company.isoAuthLogin()) {
                return null;
            }
            OAuthClientRequest request = OAuthClientRequest
                    .authorizationProvider(OAuthProviderType.GITHUB)
                    .setClientId(company.getGitHubClientId())
                    .setRedirectURI(company.getUrl() + "oauthredirect")
                    .setScope("user:email")
                    .buildQueryMessage();
            return request.getLocationUri();
        } catch (final Exception e) {
            LOGGER.error("Error in oauth.", e);
            return null;
        }
    }

    public static User processOAuthRedirect(final SiteContext context, final Company company, String code) {
        if (!company.isoAuthLogin()) {
            return null;
        }
        final EntityManager entityManager = context.getEntityManager();

        if (StringUtils.isEmpty(code)) {
            LOGGER.warn("Warning in oauth no code received in redirect.");
            return null;
        }

        try {
            final OAuthClientRequest oAuthClientRequest = OAuthClientRequest
                    .tokenProvider(OAuthProviderType.GITHUB)
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId(company.getGitHubClientId())
                    .setClientSecret(company.getGitHubClientSecret())
                    .setRedirectURI(company.getUrl() + "oauthredirect")
                    .setCode(code)
                    .buildQueryMessage();

            final OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

            final GitHubTokenResponse oAuthResponse = oAuthClient.accessToken(oAuthClientRequest,
                    GitHubTokenResponse.class);

            final String accessToken = oAuthResponse.getAccessToken();

            final String primaryVerifiedEmail = getEmail(accessToken);
            if (primaryVerifiedEmail == null) {
                AuditService.log(context, "oauth login failed, no matching email");
                return null;
            }

            final User existingUser = UserDao.getUser(entityManager, company, primaryVerifiedEmail);
            if (existingUser != null) {
                if (existingUser.isLockedOut()) {
                    AuditService.log(context, "oauth login failed, locked user", "User", existingUser.getUserId(), existingUser.getEmailAddress());
                    return null;
                }
                AuditService.log(context, "oauth login success", "User", existingUser.getUserId(), existingUser.getEmailAddress());
                return existingUser;
            } else {
                if (!company.isoAuthSelfRegistration()) {
                    return null;
                }
                final String name = primaryVerifiedEmail.split("@")[0];
                final String[] nameParts = name.split("\\.");
                final String firstName = capitalizeFirstLetter(nameParts[0]);
                final String lastName = nameParts.length > 1 ? capitalizeFirstLetter(nameParts[nameParts.length - 1]) : "-";
                final String phoneNumber = "-";
                final User newUser = new User(company, firstName, lastName, primaryVerifiedEmail, phoneNumber, "");
                UserDao.addUser(entityManager, newUser, UserDao.getGroup(entityManager, company, "user"));

                if (SiteModuleManager.isModuleInitialized(CustomerModule.class)) {
                    final Customer customer = new Customer(firstName, lastName, primaryVerifiedEmail, phoneNumber, false, "", "");
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

                AuditService.log(context, "oauth-auto-register", "user", newUser.getUserId(), primaryVerifiedEmail);
                return newUser;
            }
        } catch (final Exception e) {
            LOGGER.error("Error exchanging oauth code to access token: " + e.getMessage());
            AuditService.log(context, "oauth login exception");
            return null;
        }
    }

    public static String capitalizeFirstLetter(String original){
        if(original.length() == 0)
            return original;
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }

    public static String getEmail(String accessToken) throws Exception {
        final String response = get("https://api.github.com/user/emails?access_token", accessToken);
        ObjectMapper objectMapper = new ObjectMapper();
        final ArrayList<Map<String, Object>> emailList = objectMapper.readValue(response, ArrayList.class);
        String primaryVerifiedEmail = null;
        for (final Map email : emailList) {
            if (email.containsKey("email") && email.containsKey("verified") && email.containsKey("primary")) {
                if ((Boolean) email.get("verified") && (Boolean) email.get("primary")) {
                    primaryVerifiedEmail = (String) email.get("email");
                }
            }
        }
        return primaryVerifiedEmail;
    }

    public static String get(final String url, final String accessToken) throws Exception {
        final HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(url).openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0");
        httpURLConnection.setRequestProperty("Authorization", "token " + accessToken);
        return IOUtils.toString(httpURLConnection.getInputStream());
    }
}
