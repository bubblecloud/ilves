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

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.diff.DiffResult;
import liquibase.diff.compare.CompareControl;
import liquibase.diff.output.DiffOutputControl;
import liquibase.diff.output.changelog.DiffToChangeLog;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.vaadin.addons.sitekit.site.SiteException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Connection;
import java.util.HashMap;
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

                final String changeLog = PropertiesUtil.getProperty(
                        propertiesCategory, "liquibase-change-log");

                try {
                    final EntityManager entityManager = entityManagerFactory.createEntityManager();
                    entityManager.getTransaction().begin();
                    final Connection connection = entityManager.unwrap(Connection.class);
                    final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                            new JdbcConnection(connection));
                    final Liquibase liquibase = new Liquibase(changeLog, new ClassLoaderResourceAccessor(), database);
                    liquibase.update("");
                    if (entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().commit();
                    }
                } catch (Exception e) {
                    throw new SiteException("Error updating database.", e);
                }

                entityManagerFactories.put(entityManagerFactoryKey, entityManagerFactory);
            }


            return entityManagerFactories.get(entityManagerFactoryKey);
        }
    }

    /**
     * Allows removing entity manager factory in case of database failure.
     *
     * @param persistenceUnit the persistence unit
     * @param propertiesCategory the properties category
     */
    public static void removeEntityManagerFactory(final String persistenceUnit,
                                                               final String propertiesCategory) {
        final String entityManagerFactoryKey = persistenceUnit + "-" + propertiesCategory;
        synchronized (entityManagerFactories) {
            if (entityManagerFactories.containsKey(entityManagerFactoryKey)) {
                entityManagerFactories.remove(entityManagerFactoryKey);
            }
        }
    }

    /**
     * Diffs database schema against new changes in JPA model.
     *
     * @param persistenceUnit the persistence unit
     * @param propertiesCategory the properties category
     * @return the entity manager factory singleton
     */
    public static String diff(final String persistenceUnit, final String propertiesCategory) throws Exception {

        final String refJdbcUrl = PropertiesUtil.getProperty(
                propertiesCategory, PersistenceUnitProperties.JDBC_URL) + "ref";

        // Construct schema according to liquibase changelog.
        PropertiesUtil.setProperty(propertiesCategory, PersistenceUnitProperties.DDL_GENERATION, "none");
        final EntityManagerFactory factory = getEntityManagerFactory(persistenceUnit, propertiesCategory);

        // Construct ref schema according to liquibase changelog.
        PropertiesUtil.setProperty(propertiesCategory, PersistenceUnitProperties.JDBC_URL, refJdbcUrl);
        getEntityManagerFactory(persistenceUnit, propertiesCategory);

        // Update ref schema according to JPA changes.
        PropertiesUtil.setProperty(propertiesCategory, PersistenceUnitProperties.DDL_GENERATION, "create-and-extend-tables");
        PropertiesUtil.setProperty(propertiesCategory, PersistenceUnitProperties.JDBC_URL, refJdbcUrl);
        final EntityManagerFactory refFactory = getEntityManagerFactory(persistenceUnit, propertiesCategory);


        final String changeLog = PropertiesUtil.getProperty(
                propertiesCategory, "liquibase-change-log");

        final EntityManager entityManager = factory.createEntityManager();
        final EntityManager refEntityManager = refFactory.createEntityManager();
        entityManager.getTransaction().begin();
        refEntityManager.getTransaction().begin();
        final Connection connection = entityManager.unwrap(Connection.class);
        final Connection refConnection = refEntityManager.unwrap(Connection.class);
        final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
        final Database refDatabase = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(refConnection));
        final Liquibase liquibase = new Liquibase(changeLog, new ClassLoaderResourceAccessor(), database);
        final DiffResult diffResult = liquibase.diff(refDatabase, database, CompareControl.STANDARD);

        final ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        final PrintStream printStream=new PrintStream(byteArrayOutputStream);
        final DiffToChangeLog diffToChangeLog = new DiffToChangeLog(diffResult, new DiffOutputControl());
        diffToChangeLog.print(printStream);

        entityManager.getTransaction().rollback();
        refEntityManager.getTransaction().rollback();

        return byteArrayOutputStream.toString();
    }
}
