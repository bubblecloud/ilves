package org.vaadin.addons.sitekit.cache;

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
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.StringReader;
import java.util.List;

/**
 * Cache for user TLS client certificates.
 */
public class ClientCertificateCache {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(ClientCertificateCache.class);

    /** The entity manager factory used to access the user client certificates. */
    private static EntityManagerFactory entityManagerFactory;

    public static void init(final EntityManagerFactory entityManagerFactory) {
        ClientCertificateCache.entityManagerFactory = entityManagerFactory;
    }

    public static void load() {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();

        final List<Company> companies = CompanyDao.getCompanies(entityManager);


        for (final Company company : companies) {
            for (final User user : UserDao.getUsers(entityManager, company)) {
                if (user.getCertificate() == null) {
                    continue;
                }
                    try {
                        final CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
                        final Certificate certificate = certificateFactory.generateCertificate(
                             new ByteArrayInputStream(user.getCertificate()));
                    } catch (final Exception e) {
                        LOGGER.error("Error loading user client certificate: " + user.getUserId(), e);
                    }
            }
        }
    }

}
