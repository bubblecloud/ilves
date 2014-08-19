package org.vaadin.addons.sitekit.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

/**
 * Created by tlaukkan on 8/18/14.
 */
public class CertificateUtilTest {

    @Before
    public void before() {
        PropertiesUtil.setProperty("site", "key-store-path", System.getProperty("java.io.tmpdir") + File.separator +
                "site.jks");
    }

     @Test
    public void testEnsureServerCertificateExists() {
        final String alias = "alias";
        final String commonName = "test";
        CertificateUtil.ensureServerCertificateExists(alias, commonName);

        final String keyStorePath = PropertiesUtil.getProperty("site", "key-store-path");
        final String keyStorePassword = PropertiesUtil.getProperty("site", "key-store-password");

        Assert.assertTrue(CertificateUtil.hasCertificate(alias, keyStorePath, keyStorePassword));
    }

}
