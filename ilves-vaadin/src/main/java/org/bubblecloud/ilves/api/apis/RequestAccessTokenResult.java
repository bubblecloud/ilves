package org.bubblecloud.ilves.api.apis;

import java.util.Date;

/**
 * AccessToken value object.
 *
 * @author Tommi S.E. Laukkanen
 */
public class RequestAccessTokenResult {
    /**
     * The accessToken.
     */
    private String accessToken;
    /**
     * The expiration time.
     */
    private Date expirationTime;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Date getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(Date expirationTime) {
        this.expirationTime = expirationTime;
    }
}
