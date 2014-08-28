package org.vaadin.addons.sitekit.cache;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.vaadin.addons.sitekit.dao.CompanyDao;
import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.User;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cache for user TLS client certificates.
 */
public class ClientCertificateCache {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(ClientCertificateCache.class);

    /** The entity manager factory used to access the user client certificates. */
    private static EntityManagerFactory entityManagerFactory;

    private static KeyStore tslTrustStore;

    private static Map<Certificate, User> certificateUserMap = new HashMap<Certificate, User>();

    public static void init(final EntityManagerFactory entityManagerFactory, final KeyStore tslTrustStore) {
        ClientCertificateCache.entityManagerFactory = entityManagerFactory;
        ClientCertificateCache.tslTrustStore = tslTrustStore;
        load();
    }

    public static void load() {
        synchronized (entityManagerFactory) {
            LOGGER.info("Loading TSL client certificates.");
            final EntityManager entityManager = entityManagerFactory.createEntityManager();

            final List<Company> companies = CompanyDao.getCompanies(entityManager);

            for (final Company company : companies) {
                for (final User user : UserDao.getUsers(entityManager, company)) {
                    try {
                        if (user.getCertificate() == null) {
                            if (tslTrustStore.containsAlias(user.getUserId())) {
                                final Certificate certificate = tslTrustStore.getCertificate(user.getUserId());
                                tslTrustStore.deleteEntry(user.getUserId());
                                synchronized (certificateUserMap) {
                                    certificateUserMap.remove(certificate);
                                }
                                LOGGER.info("Removed TSL client certificate for user ID: " + user.getUserId());
                            }
                            continue;
                        }

                        final CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
                        final Certificate certificate = certificateFactory.generateCertificate(
                                new ByteArrayInputStream(Base64.decodeBase64(user.getCertificate())));
                        if (!tslTrustStore.containsAlias(user.getUserId())) {
                            tslTrustStore.setCertificateEntry(user.getUserId(), certificate);
                            synchronized (certificateUserMap) {
                                certificateUserMap.put(certificate, user);
                            }
                            LOGGER.info("Added TSL client certificate for user ID: " + user.getUserId());
                        }
                    } catch (final Exception e) {
                        LOGGER.error("Error adding / removing user client certificate: " + user.getUserId(), e);
                    }
                }
            }
        }
    }

    public static KeyStore getTslTrustStore() {
        return tslTrustStore;
    }

    public static User getUserByCertificate(final Certificate clientCertificate) {
        synchronized (certificateUserMap) {
            return certificateUserMap.get(clientCertificate);
        }
    }
}
