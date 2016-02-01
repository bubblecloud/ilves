package org.bubblecloud.ilves.security;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.bubblecloud.ilves.cache.InMemoryCache;
import org.bubblecloud.ilves.cache.PrivilegeCache;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.model.Group;
import org.bubblecloud.ilves.model.User;
import org.bubblecloud.ilves.module.content.Asset;
import org.bubblecloud.ilves.module.content.ContentDao;
import org.bubblecloud.ilves.site.DefaultSiteUI;
import org.bubblecloud.ilves.util.PropertiesUtil;

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
import java.util.UUID;

/**
 * Servlet for returning random UUID.
 *
 * @author Tommi S.E. Laukkanen
 */
public class UuidServlet extends HttpServlet {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(UuidServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().print(UUID.randomUUID());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.getWriter().print(UUID.randomUUID());
    }
}
