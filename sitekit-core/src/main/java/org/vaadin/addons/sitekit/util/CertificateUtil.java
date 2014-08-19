package org.vaadin.addons.sitekit.util;

import org.apache.commons.lang.time.DateUtils;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.UUID;

/**
 * Utility class for certificate management.
 *
 * @author Tommi S.E. Laukkanen
 */
public class CertificateUtil {
    /**
     * Ensure that BouncyCastle provider is loaded.
     */
    static {
        if (Security.getProvider("BC") == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
    /**
     * The security provider.
     */
    public static final String PROVIDER = "BC";
    /**
     * The keystore type.
     */
    public static final String KEY_STORE_TYPE = "BKS";
    /**
     * The certificate asymmetric encryption algorithm.
     */
    public static final String CERTIFICATE_ENCRYPTION_ALGORITHM = "RSA";
    /**
     * The certificate asymmetric encryption key size.
     */
    public static final int CERTIFICATE_KEY_SIZE = 1024;
    /**
     * The certificate signature algorithm.
     */
    public static final String CERTIFICATE_SIGNATURE_ALGORITHM = "SHA256WithRSAEncryption";

    /**
     * Checks that server certificate exists and if it does not then generates self signed certificate it.
     */
    public static void ensureServerCertificateExists(final String alias, final String commonName) {
        final String keyStorePath = PropertiesUtil.getProperty("site", "key-store-path");
        final String keyStorePassword = PropertiesUtil.getProperty("site", "key-store-password");

        if (!hasCertificate(alias, keyStorePath, keyStorePassword)) {
            generateSelfSignedCertificate(alias, commonName, keyStorePath, keyStorePassword);
        }
    }

    /**
     * Checks whether key store contains given certificate.
     * @param alias the certificate alias
     * @param keyStorePath the key store path
     * @param keyStorePassword the key store password
     * @return
     */
    public static boolean hasCertificate(final String alias, final String keyStorePath,
                                  final String keyStorePassword) {
        try {
            final KeyStore keyStore = loadKeyStore(keyStorePath, keyStorePassword);
            return keyStore.containsAlias(alias);
        } catch (final Exception e) {
            throw new SecurityException("Error checking if certificate exists: " + alias + " in key store: " +
                    keyStorePath, e);
        }
    }

    /**
     * Generates and self signed certificate and saves it to key store.
     * @param alias the certificate alias
     * @param commonName the certificate common name
     * @param keyStorePath the key store path
     * @param keyStorePassword the key store password
     */
    public static void generateSelfSignedCertificate(final String alias, final String commonName,
                                                     final String keyStorePath,
                                                     final String keyStorePassword) {
        try {
            final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(CERTIFICATE_ENCRYPTION_ALGORITHM, PROVIDER);
            keyGen.initialize(CERTIFICATE_KEY_SIZE);
            final KeyPair keyPair = keyGen.generateKeyPair();
            final X509Certificate certificate = buildCertificate(commonName, keyPair);
            final KeyStore keyStore = loadKeyStore(keyStorePath, keyStorePassword);
            keyStore.setKeyEntry(alias, (Key) keyPair.getPrivate(), keyStorePassword.toCharArray(),
                    new X509Certificate[]{certificate});
            saveKeyStore(keyStore, keyStorePath, keyStorePassword);
        } catch (final Exception e) {
            throw new RuntimeException("Unable to generate self signed certificate.", e);
        }
    }

    /**
     * Loads key store.
     * @param keyStorePath the key store path
     * @param keyStorePassword the key store password
     * @return the key store
     */
    public static KeyStore loadKeyStore(final String keyStorePath, final String keyStorePassword) {
        try {
            final File keyStoreFile = new File(keyStorePath);
            if (keyStoreFile.exists()) {
                final FileInputStream keyStoreInputStream = new FileInputStream(keyStoreFile);
                final KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE, PROVIDER);
                keyStore.load(keyStoreInputStream, keyStorePassword.toCharArray());
                keyStoreInputStream.close();
                return keyStore;
            } else {
                final KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE, PROVIDER);
                keyStore.load(null, keyStorePassword.toCharArray());
                return keyStore;
            }
        } catch (final Exception e) {
            throw new SecurityException("Unable to load key store: " + keyStorePath, e);
        }
    }

    /**
     * Saves key store.
     * @param keyStore the key store
     * @param keyStorePath the key store path
     * @param keyStorePassword the key store password
     */
    public static void saveKeyStore(final KeyStore keyStore, final String keyStorePath, final String keyStorePassword) {
        try {
            final FileOutputStream keyStoreOutputStream = new FileOutputStream(keyStorePath, false);
            keyStore.store(keyStoreOutputStream, keyStorePassword.toCharArray());
            keyStoreOutputStream.close();
        } catch (final Exception e) {
            throw new SecurityException("Unable to save key store: " + keyStorePath, e);
        }
    }

    /**
     * Build self signed certificate from key pair.
     * @param commonName the certificate common name
     * @param keyPair the key pair.
     * @return the certificate
     * @throws Exception if error occurs in certificate generation process.
     */
    private static X509Certificate buildCertificate(final String commonName, KeyPair keyPair) throws Exception {

        final Date notBefore = new Date(System.currentTimeMillis() - 1000 * 60 * 60 * 24);
        final Date notAfter = DateUtils.addYears(notBefore, 100);
        final BigInteger serial = BigInteger.valueOf(System.currentTimeMillis());

        final X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        nameBuilder.addRDN(BCStyle.CN, commonName);
        final SubjectPublicKeyInfo subjectPublicKeyInfo = new SubjectPublicKeyInfo(
                ASN1Sequence.getInstance(keyPair.getPublic().getEncoded()));

        final X509v3CertificateBuilder certGen = new X509v3CertificateBuilder(nameBuilder.build(),
                serial, notBefore, notAfter, nameBuilder.build(), subjectPublicKeyInfo);
        final ContentSigner sigGen = new JcaContentSignerBuilder(CERTIFICATE_SIGNATURE_ALGORITHM)
                .setProvider(PROVIDER).build(keyPair.getPrivate());
        final X509Certificate cert = new JcaX509CertificateConverter().setProvider(PROVIDER)
                .getCertificate(certGen.build(sigGen));

        return cert;
    }

}
