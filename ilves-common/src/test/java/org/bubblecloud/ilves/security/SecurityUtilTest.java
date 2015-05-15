package org.bubblecloud.ilves.security;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by tlaukkan on 5/15/2015.
 */
public class SecurityUtilTest {
    @Test
    public void testKeyEncryption() {
        final String plainText = "test-string";
        final String cipherText = SecurityUtil.encryptSecretKey(plainText);
        Assert.assertFalse(cipherText.equals(plainText));
        Assert.assertEquals(plainText, SecurityUtil.decryptSecretKey(cipherText));
    }
}
