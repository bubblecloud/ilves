package org.bubblecloud.ilves.security;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.encoders.Base64;
import org.bouncycastle.util.encoders.Hex;
import org.bubblecloud.ilves.model.User;
import org.bubblecloud.ilves.util.PropertiesUtil;
import org.bubblecloud.ilves.util.StringUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Arrays;

/**
 * Security utility methods.
 *
 * @author Tommi S.E. Laukkanen
 */
public class SecurityUtil {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(SecurityUtil.class);

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
     * The symmetric encryption algorithm.
     */
    public static final String SYMMETRIC_ENCRYPTION_ALGORITHM = "AES";
    /**
     * The symmetric encryption key size.
     */
    public static final int SYMMETRIC_ENCRYPTION_KEY_SIZE = 128;
    /**
     * The charset used to get bytes from password string for hashing.
     */
    public static final Charset CHARSET = Charset.forName("UTF-8");
    /**
     * Configuration encoding secret key.
     */
    public static final byte[] CONFIGURATION_ENCODING_SECRET_KEY = Hex.decode("8cf46ed9ec6db5243e634b6cfe965788");
    /**
     * Default initialization vector for encrypted configuration.
     */
    public static final byte[] CONFIGURATION_ENCRYPTION_IV = Hex.decode("1aa13e4a6f1a022b51b550fffcd43021");
    /** The secure random. */
    private static SecureRandom random = new SecureRandom();
    /** The access token lifetime in milliseconds. */
    public static final long ACCESS_TOKEN_LIFETIME_MILLIS = 15 * 60 * 1000;

    /**
     * Method for generating keyt encryption secret key.
     *
     * @return new system secret key
     */
    public static String generateKeyEncryptionSecretKey() {
        final SecureRandom secureRandom = new SecureRandom();
        byte[] secretKeyBytes = new byte[SYMMETRIC_ENCRYPTION_KEY_SIZE / 8];
        secureRandom.nextBytes(secretKeyBytes);
        return encodeConfiguration(Hex.toHexString(secretKeyBytes));
    }

    /**
     * Encrypts plain text.
     *
     * @param iv the initialization vector
     * @param secretKey the secret key
     * @param plainText the plain text
     * @return the cipher text
     */
    private static String encrypt(final byte[] iv, final byte[] secretKey, final String plainText) {
        try {
            final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey,
                    SYMMETRIC_ENCRYPTION_ALGORITHM);
            final Cipher cipher = Cipher.getInstance(secretKeySpec.getAlgorithm() + "/CBC/PKCS5Padding", PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
            return new String(Base64.encode(cipher.doFinal(plainText.getBytes(CHARSET))), CHARSET);
        } catch (final Exception e) {
            throw new SecurityException("Error encoding", e);
        }
    }

    /**
     * Decrypts cipher text.
     *
     * @param iv the initialization vector
     * @param secretKey the secret key
     * @param cipherText the cipher text
     * @return the plain text
     */
    private static String decrypt(final byte[] iv, final byte[] secretKey, final String cipherText) {
        try {
            final SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey,
                    SYMMETRIC_ENCRYPTION_ALGORITHM);
            final Cipher cipher = Cipher.getInstance(secretKeySpec.getAlgorithm() + "/CBC/PKCS5Padding", PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(iv));
            return new String(cipher.doFinal(Base64.decode(cipherText.getBytes(CHARSET))), CHARSET);
        } catch (final Exception e) {
            throw new SecurityException("Error dencoding", e);
        }
    }

    /**
     * Encode configuration.
     *
     * @param plainText the configuration text to encrypt
     * @return the encoded configuration text
     */
    private static String encodeConfiguration(final String plainText) {
        return encrypt(CONFIGURATION_ENCRYPTION_IV, CONFIGURATION_ENCODING_SECRET_KEY, plainText);
    }

    /**
     * Decode configuration.
     *
     * @param encodedText the encoded configuration to decrypt
     * @return the plain configuration
     */
    private static String decodeConfiguration(final String encodedText) {
        return decrypt(CONFIGURATION_ENCRYPTION_IV, CONFIGURATION_ENCODING_SECRET_KEY, encodedText);
    }

    /**
     * Encrypts with key encryption secret key.
     *
     * @param plainText the plain text
     * @return the cipher text
     */
    public static String encryptSecretKey(final String plainText) {
        final String systemEncodedSecretKey = PropertiesUtil.getProperty("site", "key-encryption-secret-key");
        final String systemSecretKey = decodeConfiguration(systemEncodedSecretKey);
        return encrypt(CONFIGURATION_ENCRYPTION_IV, Hex.decode(systemSecretKey), plainText);
    }

    /**
     * Decrypts with key encryption secret key.
     *
     * @param cipherText the cipher text
     * @return the cipher text
     */
    public static String decryptSecretKey(final String cipherText) {
        if (!PropertiesUtil.hasProperty("site", "key-encryption-secret-key")) {
            LOGGER.error("Server key encryption key is not defined. Candidate key generated to key-encryption-secret-key-candidate.properties. Please copy the line to site-ext.properties ");
            try {
                FileUtils.writeStringToFile(new File("key-encryption-secret-key-candidate.properties"), "key-encryption-secret-key = " + generateKeyEncryptionSecretKey(), false);
            } catch (IOException e) {
                LOGGER.error("Attempt to write candidate key to key-encryption-secret-key-candidate.properties failed", e);
            }
        }
        final String systemEncodedSecretKey = PropertiesUtil.getProperty("site", "key-encryption-secret-key");
        final String systemSecretKey = decodeConfiguration(systemEncodedSecretKey);
        return decrypt(CONFIGURATION_ENCRYPTION_IV, Hex.decode(systemSecretKey), cipherText);
    }

    /**
     * Calculate hash for string.
     * @param stringValue the string value
     * @return the hash as hex encoded string
     */
    public static String calculateHash(String stringValue) {
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(stringValue.getBytes("UTF-8")); // Change this to "UTF-16" if needed
        } catch (final Exception e) {
            throw new RuntimeException("Unable to compute session ID hash.", e);
        }
        return org.apache.commons.codec.binary.Hex.encodeHexString(md.digest());
    }

    /**
     * Generates access token.
     * @return the access token
     */
    public static char[] generateAccessToken() {
        final char[] accessToken;
        synchronized (random) {
            accessToken = org.apache.commons.codec.binary.Hex.encodeHex(new BigInteger(130, random).toByteArray());
        }
        return accessToken;
    }

    public static String getSecretHash(final char[] secret) {
        final byte[] accessTokenHashBytes = convertCharactersToBytes(secret);
        final MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException(e);
        }
        return StringUtil.toHexString(md.digest(accessTokenHashBytes));
    }

    /**
     * Converts character array to byte array
     * @param characters the character array
     * @return the byte array
     */
    public static byte[] convertCharactersToBytes(char[] characters) {
        CharBuffer charBuffer = CharBuffer.wrap(characters);
        final ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
        byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
        Arrays.fill(charBuffer.array(), '\u0000');
        Arrays.fill(byteBuffer.array(), (byte) 0);
        return bytes;
    }
}
