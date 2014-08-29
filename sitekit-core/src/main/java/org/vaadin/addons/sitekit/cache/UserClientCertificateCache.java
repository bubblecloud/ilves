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
import org.vaadin.addons.sitekit.dao.CompanyDao;
import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.User;

import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static KeyStore tslTrustStore;

    private static Map<Certificate, User> certificateUserMap = new HashMap<Certificate, User>();

    public static void init(final EntityManagerFactory entityManagerFactory, final KeyStore tslTrustStore) {
        UserClientCertificateCache.entityManagerFactory = entityManagerFactory;
        UserClientCertificateCache.tslTrustStore = tslTrustStore;
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
