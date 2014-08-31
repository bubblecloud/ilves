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
import org.vaadin.addons.sitekit.viewlet.user.privilege.PrivilegesFlowlet;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

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
        final List<String> roles = (List<String>) req.getSession().getAttribute("roles");

        final EntityManager entityManager = DefaultSiteUI.getEntityManagerFactory().createEntityManager();

        final String hostName = req.getServerName();
        Company company = CompanyDao.getCompany(entityManager, hostName);
        if (company == null) {
            company = CompanyDao.getCompany(entityManager, "*");
        }

        final String name = req.getRequestURI().substring(req.getRequestURI().lastIndexOf('/') + 1);
        final Asset asset = ContentDao.getAsset(entityManager, company, name);

        if (asset == null) {
            resp.setStatus(404);
            return;
        }

        if (user != null) {
            if (!PrivilegeCache.hasPrivilege(entityManager, company, user, "view", asset.getAssetId())) {
                boolean privileged = false;
                for (final Group group : groups) {
                    if (PrivilegeCache.hasPrivilege(entityManager, company, group, "view", asset.getAssetId())) {
                        privileged = true;
                        break;
                    }
                }
                if (!privileged) {
                    resp.setStatus(401);
                    return;
                }
            }
        } else {
            final Group group = UserDao.getGroup(entityManager, company, "anonymous");
            if (!PrivilegeCache.hasPrivilege(entityManager, company, group, "view", asset.getAssetId())) {
                resp.setStatus(401);
                return;
            }
        }

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
                IOUtils.copy(inputStream, resp.getOutputStream());
            }
            resultSet.close();
            preparedStatement.close();

            entityManager.getTransaction().rollback();
            resp.setStatus(200);
        } catch (final Exception e) {
            LOGGER.error("Error reading asset from database.", e);
            resp.setStatus(500);
        } finally {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
        }

    }
}
