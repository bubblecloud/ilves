package org.bubblecloud.ilves.api.apis;

import org.bubblecloud.ilves.api.AccessGrant;

/**
 * The security API.
 *
 * @author Tommi S.E. Laukkanen
 */
public interface Security {

    /**
     * Requests access token.
     * @param account the account
     * @param password the password
     * @return the access token
     */
    @AccessGrant(roles = {"anonymous"})
    RequestAccessTokenResult requestAccessToken(final String account, final String password);

    /**
     * Invalidates access token.
     * @param account the account
     * @param accessToken the password
     * @return the access token
     */
    @AccessGrant(roles = {"anonymous"})
    void invalidateAccessToken(final String account, final String accessToken);

}
