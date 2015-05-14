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

import org.apache.commons.codec.binary.Hex;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.model.User;
import org.bubblecloud.ilves.model.UserSession;

import java.security.MessageDigest;
import java.util.Date;

/**
 * Login service for performing directory / database layer login / logout operations. This service does not
 * perform user login / logout for web container layer.
 *
 * @author Tommi S.E. Laukkanen
 */
public class LoginService {

    /**
     * Execute login operations on directory / database layer with given email address and password.
     * This function does not perform user login for web container layer.
     * @param context the security context
     * @param company the company
     * @param user the user
     * @param emailAddress the email addres
     * @param password the password
     * @param sessionId the session ID for blocking duplicated login posts for same session
     * @return null if success or error key
     */
    public static String login(final SecurityContext context, final Company company, final User user, final String emailAddress, final String password, final String sessionId, final String loginTransactionId) {
        final String sessionIdHash = calculateIdHash(sessionId);
        final String loginTransactionIdHash = calculateIdHash(loginTransactionId);

        if (SecurityService.getUserSessionByIdHash(context.getEntityManager(), sessionIdHash) != null) {
            return "message-login-failed-duplicate-login-for-session";
        }
        if (SecurityService.getUserSessionByLoginTransactionIdHash(context.getEntityManager(), loginTransactionIdHash) != null) {
            return "message-login-failed-duplicate-login-for-login-transaction-id";
        }


        final String errorKey = PasswordLoginUtil.login(emailAddress, context.getRemoteHost(),
                context.getRemoteIpAddress(), context.getRemotePort(),
                context.getEntityManager(), company, user, password);
        if (errorKey == null) {

            final UserSession userSession = new UserSession();
            userSession.setSessionIdHash(sessionIdHash);
            userSession.setLoginTransactionIdHash(loginTransactionIdHash);
            userSession.setUser(user);
            userSession.setCreated(new Date());

            SecurityService.addUserSession(context.getEntityManager(), userSession);

            AuditService.log(context, "password login success", "User", user.getUserId(), user.getEmailAddress());
        } else {
            AuditService.log(context, "password login failure", "User", user != null ? user.getUserId() : null, emailAddress);
        }
        return errorKey;
    }

    public static String calculateIdHash(String sessionId) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(sessionId.getBytes("UTF-8")); // Change this to "UTF-16" if needed
        } catch (final Exception e) {
            throw new RuntimeException("Unable to compute session ID hash.", e);
        }
        return Hex.encodeHexString(md.digest());
    }

    /**
     * Execute logout operations on directory / database layer with given email address and password.
     * This function does not perform user logout for web container layer.
     * @param context the security context
     */
    public static void logout(final SecurityContext context) {
        AuditService.log(context, " logout");
        context.getEntityManager().clear();
        context.getAuditEntityManager().clear();
    }
}
