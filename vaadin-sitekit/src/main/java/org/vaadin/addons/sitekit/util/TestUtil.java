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
import liquibase.logging.LogFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.vaadin.addons.sitekit.site.SiteException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.sql.Connection;

/**
 * Test utilities.
 *
 * @author Tommi S.E.Laukkanen
 */
public class TestUtil {

    static {
        LogFactory.getLogger().setLogLevel("severe");
    }

    private static EntityManagerFactory entityManagerFactory;

    public static void before() {
        entityManagerFactory = PersistenceUtil.getEntityManagerFactory("site", "site");
    }

    public static void after() {
        final EntityManager entityManager = entityManagerFactory.createEntityManager();
        try {
            final String changeLog = PropertiesUtil.getProperty(
                    "site", "liquibase-change-log");
            entityManager.getTransaction().begin();
            final Connection connection = entityManager.unwrap(Connection.class);
            final Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
                    new JdbcConnection(connection));
            final Liquibase liquibase = new Liquibase(changeLog, new ClassLoaderResourceAccessor(), database);
            liquibase.dropAll();
        } catch (Exception e) {
            throw new SiteException("Error clearing database.", e);
        }
        entityManager.close();
        entityManagerFactory.close();
        PersistenceUtil.removeEntityManagerFactory("site", "site");
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
}
