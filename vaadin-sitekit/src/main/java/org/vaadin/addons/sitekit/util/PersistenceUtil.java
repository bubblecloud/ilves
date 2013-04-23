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
package org.vaadin.addons.sitekit.util;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.vaadin.addons.sitekit.model.SchemaVersion;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility for persistence operations.
 * @author Tommi S.E Laukkanen
 */
public final class PersistenceUtil {
    /**
     * The entity manager factory for test.
     */
    private static Map<String, EntityManagerFactory> entityManagerFactories = new HashMap<String, EntityManagerFactory>();

    /**
     * Private constructor to disable construction of utility class.
     */
    private PersistenceUtil() {
    }

    /**
     * Gets singleton entity manager factory for given persistence unit and properties category.
     * @param persistenceUnit the persistence unit
     * @param propertiesCategory the properties category
     * @return the entity manager factory singleton
     */
    public static EntityManagerFactory getEntityManagerFactory(final String persistenceUnit,
                                                               final String propertiesCategory) {
        final String entityManagerFactoryKey = persistenceUnit + "-" + propertiesCategory;
        synchronized (entityManagerFactories) {
            if (!entityManagerFactories.containsKey(entityManagerFactoryKey)) {
                final String schemaName = PropertiesUtil.getProperty(
                        propertiesCategory, "schema-name");
                final String schemaVersion = PropertiesUtil.getProperty(
                        propertiesCategory, "schema-version");

                final Map properties = new HashMap();
                properties.put(PersistenceUnitProperties.JDBC_DRIVER, PropertiesUtil.getProperty(
                        propertiesCategory, PersistenceUnitProperties.JDBC_DRIVER));
                properties.put(PersistenceUnitProperties.JDBC_URL, PropertiesUtil.getProperty(
                        propertiesCategory, PersistenceUnitProperties.JDBC_URL));
                properties.put(PersistenceUnitProperties.JDBC_USER, PropertiesUtil.getProperty(
                        propertiesCategory, PersistenceUnitProperties.JDBC_USER));
                properties.put(PersistenceUnitProperties.JDBC_PASSWORD, PropertiesUtil.getProperty(
                        propertiesCategory, PersistenceUnitProperties.JDBC_PASSWORD));
                properties.put(PersistenceUnitProperties.DDL_GENERATION, PropertiesUtil.getProperty(
                        propertiesCategory, PersistenceUnitProperties.DDL_GENERATION));

                final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory(
                        persistenceUnit, properties);

                final EntityManager entityManager = entityManagerFactory.createEntityManager();

                final TypedQuery<SchemaVersion> query = entityManager.createQuery("select e from SchemaVersion as e " +
                        "where e.schemaName=:schemaName order by e.created desc",
                        SchemaVersion.class);

                query.setParameter("schemaName", schemaName);
                query.setMaxResults(1);

                final List<SchemaVersion> schemaVersions = query.getResultList();

                if (schemaVersions.size() == 0) {
                    throw new RuntimeException("Database schema is not installed.");
                }
                if (!schemaVersions.get(0).getSchemaVersion().equals(schemaVersion)) {
                    throw new RuntimeException("Database schema is in version: " +
                            schemaVersions.get(0).getSchemaVersion() + " but software expects: " + schemaVersion
                    );
                }

                entityManagerFactories.put(entityManagerFactoryKey, entityManagerFactory);
            }


            return entityManagerFactories.get(entityManagerFactoryKey);
        }
    }
}
