package org.bubblecloud.ilves.security;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by tlaukkan on 5/15/2015.
 */
public class GoogleAuthenticatorTest {

    @Test
    @Ignore
    public void testSecretKeyGeneration() {
        final String secretKey = GoogleAuthenticatorService.generateSecretKey();
        final String qrBarUrl = GoogleAuthenticatorService.getQRBarcodeURL("test", "ilves.herokuapp.com", secretKey);
        System.out.println(qrBarUrl);
    }
}
