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
package org.vaadin.addons.sitekit.cache;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.vaadin.addons.sitekit.model.User;

import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * Cache for user TLS client certificates.
 *
 * @author Tommi S.E. Laukkanen
 */
public class UserClientCertificateCache {

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(UserClientCertificateCache.class);

    /** The entity manager factory used to access the user client certificates. */
    private static EntityManagerFactory entityManagerFactory;

    /**
     * The certificate cache.
     */
    private static InMemoryCache<Certificate, User> certificateCache;
    /**
     * The blacklisted certificate cache.
     */
    private static InMemoryCache<Certificate, Certificate> blacklistCache;

    public static void init(final EntityManagerFactory entityManagerFactory) {
        UserClientCertificateCache.entityManagerFactory = entityManagerFactory;
        certificateCache = new InMemoryCache<Certificate, User>(10 * 60 * 1000, 60 * 1000, 1000);
        blacklistCache = new InMemoryCache<Certificate, Certificate>(2 * 60 * 1000, 30 * 1000, 1000);
    }

    /**
     * Get user by certificate.
     *
     * @param clientCertificate the client certificate
     * @return the user or null if no matching user or more than one matching user was found.
     */
    public static synchronized User getUserByCertificate(final Certificate clientCertificate) {
        if (blacklistCache.get(clientCertificate) != null) {
            LOGGER.debug("Blacklisted TSL client certificate: "
                    + ((X509Certificate) clientCertificate).getSubjectDN());
            return null;
        }
        final User cachedUser = certificateCache.get(clientCertificate);
        if (cachedUser != null) {
            LOGGER.debug("User matching TSL client certificate in cache: " + cachedUser.getUserId());
            return cachedUser;
        }
        final String encodedCertificateString;
        try {
            encodedCertificateString = Base64.encodeBase64String(clientCertificate.getEncoded());
        } catch (CertificateEncodingException e) {
            LOGGER.error("Error encoding TSL client certificate for finding user from database.");
            return null;
        }

        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        final CriteriaBuilder criteriaBuilder =  entityManager.getCriteriaBuilder();
        final CriteriaQuery<User> criteria = criteriaBuilder.createQuery(User.class);
        final Root<User> root = criteria.from(User.class);
        criteria.where(criteriaBuilder.equal(root.get("certificate"), encodedCertificateString));
        final TypedQuery<User> query = entityManager.createQuery(criteria);
        final List<User> users = query.getResultList();
        if (users.size() == 1) {
            LOGGER.info("User found matching TSL client certificate: " + users.get(0).getUserId());
            certificateCache.put(clientCertificate, users.get(0));
            return users.get(0);
        } else if (users.size() > 1) {
            blacklistCache.put(clientCertificate, clientCertificate);
            LOGGER.error("Blacklisted TSL client certificate. More than one user had the certificate: " + clientCertificate);
            return null;
        } else {
            blacklistCache.put(clientCertificate, clientCertificate);
            LOGGER.warn("Blacklisted TSL client certificate. User not found matching the certificate: "
                    + ((X509Certificate) clientCertificate).getSubjectDN());
            return null;
        }
    }
}
