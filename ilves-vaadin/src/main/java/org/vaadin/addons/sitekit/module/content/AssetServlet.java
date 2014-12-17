package org.vaadin.addons.sitekit.module.content;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.vaadin.addons.sitekit.cache.InMemoryCache;
import org.vaadin.addons.sitekit.cache.PrivilegeCache;
import org.vaadin.addons.sitekit.security.CompanyDao;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.module.content.dao.ContentDao;
import org.vaadin.addons.sitekit.module.content.model.Asset;
import org.vaadin.addons.sitekit.site.DefaultSiteUI;
import org.vaadin.addons.sitekit.util.PropertiesUtil;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet for sharing assets.
 *
 * @author Tommi S.E. Laukkanen
 */
public class AssetServlet extends HttpServlet {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(AssetServlet.class);

    private static Map<Company, InMemoryCache<String, Asset>> nameAssetCache =
            new HashMap<Company, InMemoryCache<String, Asset>>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Allocate entity manager.
        final EntityManager entityManager = DefaultSiteUI.getEntityManagerFactory().createEntityManager();

        // Find user and groups from session or assume anonymous.
        final User user = (User) req.getSession().getAttribute("user");
        final List<Group> groups =  (List<Group>) req.getSession().getAttribute("groups");

        // Find company object either from user object, session or load from database.
        Company company =  null;
        if (user != null) {
            company = user.getOwner();
        } else {
            company = (Company) req.getSession().getAttribute("company");
            if (company == null) {
                company = CompanyDao.getCompany(entityManager, req.getServerName());
                req.getSession().setAttribute("company", company);
            }
            if (company == null) {
                company = CompanyDao.getCompany(entityManager, "*");
                req.getSession().setAttribute("company", company);
            }
        }

        // Find Asset object based on name either from cache or database.
        final String name = req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/') + 1);
        if (!nameAssetCache.containsKey(company)) {
            nameAssetCache.put(company, new InMemoryCache<String, Asset>(
                    10 * 60 * 1000, 60 * 1000, 1000));
        }
        Asset asset = nameAssetCache.get(company).get(name);
        if (asset == null) {
            asset = ContentDao.getAsset(entityManager, company, name);
            nameAssetCache.get(company).put(name, asset);
        }

        if (asset == null) {
            resp.setStatus(404);
            return;
        }

        if (!PrivilegeCache.hasPrivilege(entityManager, company, user, groups, "view", asset.getAssetId())) {
            resp.setStatus(401);
            return;
        }

        // Load asset from file cache if exists and not modified before last modified of the asset file.
        final String assetCachePath = PropertiesUtil.getProperty("site", "asset-cache-path");
        final File assetCache = new File(assetCachePath);
        if (!assetCache.exists()) {
            assetCache.mkdir();
        }
        final String assetCacheFilePath = assetCache.getCanonicalPath() + File.separator + asset.getAssetId();
        final File assetCacheFile = new File(assetCacheFilePath);

        if (assetCacheFile.exists()) {
            if (assetCacheFile.lastModified() < asset.getModified().getTime()) {
                assetCacheFile.delete();
            }
        }

        if (!assetCacheFile.exists()) {
            entityManager.getTransaction().begin();
            try {
                final Connection connection = entityManager.unwrap(Connection.class);
                final PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT data FROM asset WHERE assetid = ?");
                preparedStatement.setString(1, asset.getAssetId());
                final ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    final InputStream inputStream = resultSet.getBinaryStream(1);
                    final FileOutputStream outputStream = new FileOutputStream(assetCacheFile);
                    IOUtils.copy(inputStream, outputStream);
                    inputStream.close();
                    outputStream.close();
                }
                resultSet.close();
                preparedStatement.close();

                entityManager.getTransaction().rollback();
            } catch (final Exception e) {
                LOGGER.error("Error reading asset from database.", e);
                resp.setStatus(500);
            } finally {
                if (entityManager.getTransaction().isActive()) {
                    entityManager.getTransaction().rollback();
                }
            }
        }

        if (assetCacheFile.exists()) {
            final int cacheAgeSeconds = 3600;
            final long expiry = System.currentTimeMillis() + cacheAgeSeconds * 1000;
            resp.setDateHeader("Expires", expiry);
            resp.setHeader("Cache-Control", "max-age="+ cacheAgeSeconds);
            resp.setContentType(asset.getType());
            resp.setContentLength(asset.getSize());

            final FileInputStream inputStream = new FileInputStream(assetCacheFile);
            IOUtils.copy(inputStream, resp.getOutputStream());
            inputStream.close();

            resp.setStatus(200);
        } else {
            resp.setStatus(500);
        }

    }

}
