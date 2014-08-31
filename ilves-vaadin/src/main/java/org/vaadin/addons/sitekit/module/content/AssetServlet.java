package org.vaadin.addons.sitekit.module.content;

import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.eclipse.jetty.http.HttpStatus;
import org.vaadin.addons.sitekit.cache.PrivilegeCache;
import org.vaadin.addons.sitekit.dao.CompanyDao;
import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.Privilege;
import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.module.content.dao.ContentDao;
import org.vaadin.addons.sitekit.module.content.model.Asset;
import org.vaadin.addons.sitekit.site.AbstractSiteUI;
import org.vaadin.addons.sitekit.site.DefaultSiteUI;
import org.vaadin.addons.sitekit.util.PersistenceUtil;
import org.vaadin.addons.sitekit.util.PropertiesUtil;
import org.vaadin.addons.sitekit.viewlet.user.privilege.PrivilegesFlowlet;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Properties;

/**
 * Servlet for sharing assets.
 *
 * @author Tommi S.E. Laukkanen
 */
public class AssetServlet extends HttpServlet {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(AssetServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final User user = (User) req.getSession().getAttribute("user");
        final List<Group> groups =  (List<Group>) req.getSession().getAttribute("groups");

        final EntityManager entityManager = DefaultSiteUI.getEntityManagerFactory().createEntityManager();

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

        final String name = req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/') + 1);
        final Asset asset = ContentDao.getAsset(entityManager, company, name);

        if (asset == null) {
            resp.setStatus(404);
            return;
        }

        if (!PrivilegeCache.hasPrivilege(entityManager, company, user, groups, "view", asset.getAssetId())) {
            resp.setStatus(401);
            return;
        }

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
                resp.setContentType(asset.getType());
                resp.setContentLength(asset.getSize());
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
            final FileInputStream inputStream = new FileInputStream(assetCacheFile);
            IOUtils.copy(inputStream, resp.getOutputStream());
            inputStream.close();
            resp.setStatus(200);
        } else {
            resp.setStatus(500);
        }

    }
}
