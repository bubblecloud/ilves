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
package org.vaadin.addons.sitekit.module.content.dao;

import org.apache.log4j.Logger;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.module.content.model.Asset;
import org.vaadin.addons.sitekit.module.content.model.Content;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

/**
 * Content data access object.
 *
 * @author Tommi S.E. Laukkanen
 *
 */
public class ContentDao {

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(ContentDao.class);

    /**
     * Saves content to database.
     * @param entityManager the entity manager
     * @param content the content
     */
    public static void saveContent(final EntityManager entityManager, final Content content) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            if (content.getContentId() != null) {
                content.setCreated(new Date());
            }
            content.setModified(new Date());

            entityManager.persist(content);
            transaction.commit();
        } catch (final Exception e) {
            LOGGER.error("Error in add content.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves asset to database.
     * @param entityManager the entity manager
     * @param asset the asset
     */
    public static void saveAsset(final EntityManager entityManager, final Asset asset) {
        final EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            if (asset.getAssetId() != null) {
                asset.setCreated(new Date());
            }
            asset.setModified(new Date());

            entityManager.persist(asset);
            transaction.commit();
        } catch (final Exception e) {
            LOGGER.error("Error in add content.", e);
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets list of contents.
     * @param entityManager the entity manager.
     * @param owner the owning company
     * @return list of contents
     */
    public static final List<Content> getContens(final EntityManager entityManager, final Company owner) {
        final TypedQuery<Content> query = entityManager.createQuery(
                "select e from Content as e where e.owner=:owner order by e.page",
                Content.class);
        query.setParameter("owner", owner);
        return query.getResultList();
    }

    /**
     * Gets list of contents.
     * @param entityManager the entity manager.
     * @param owner the owning company
     * @param name the asset name
     * @return list of contents
     */
    public static final Asset getAsset(final EntityManager entityManager, final Company owner, final String name) {
        final TypedQuery<Asset> query = entityManager.createQuery(
                "select e from Asset as e where e.owner=:owner and e.name = :name",
                Asset.class);
        query.setParameter("owner", owner);
        query.setParameter("name", name);
        final List<Asset> resultList = query.getResultList();
        if (resultList.size() == 1) {
            return resultList.get(0);
        } else {
            return null;
        }
    }
}
