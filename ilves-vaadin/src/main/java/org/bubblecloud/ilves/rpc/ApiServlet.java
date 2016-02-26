package org.bubblecloud.ilves.rpc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcServer;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import org.apache.log4j.Logger;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.site.DefaultSiteUI;
import org.bubblecloud.ilves.site.SecurityProvider;
import org.bubblecloud.ilves.site.SiteContext;
import org.bubblecloud.ilves.util.WebSecurityUtil;
import org.eclipse.jetty.http.HttpStatus;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.*;

/**
 * The API servlet.
 *
 * @author Tommi S.E. Laukkanen
 */
public class ApiServlet extends HttpServlet {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(ApiServlet.class);
    /** The object mapper. */
    private LinkedList<ObjectMapper> objectMapperPool = new LinkedList<ObjectMapper>();
    /** The thread safe API objects. */
    private List<Object> apis = new ArrayList<>();

    /**
     * Post processing method.
     * @param request the request
     * @param response the response
     * @throws ServletException if servlet exception occurs during processing.
     * @throws IOException if IO exception occurs during processing.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebSecurityUtil.setSecurityHeaders(response);

        String uri = request.getRequestURI();
        if (uri.lastIndexOf('/') == uri.length() - 1) {
            uri = uri.substring(0, uri.length() - 1);
        }

        try {
            // The entity managet factory.
            final EntityManagerFactory entityManagerFactory = DefaultSiteUI.getEntityManagerFactory();
            // Construct entity manager for this site context.
            final EntityManager entityManager = entityManagerFactory.createEntityManager();
            // Construct audit entity manager for this site context.
            final EntityManager auditEntityManager = entityManagerFactory.createEntityManager();
            // Choose company for this site context.
            final VaadinServletRequest servletRequest = (VaadinServletRequest) VaadinService.getCurrentRequest();
            // The virtual host based on URL.
            final Company company = DefaultSiteUI.resolveCompany(entityManager, servletRequest);
            // The security provider.
            final SecurityProvider securityProvider = null;

            // The context.
            final SiteContext context = new SiteContext(entityManager, auditEntityManager, servletRequest, securityProvider);
            context.putObject(EntityManager.class, entityManager);
            context.putObject(EntityManagerFactory.class, entityManagerFactory);
            context.putObject(Company.class, company);

            Object api = null;
            for (final Object apiCandidate : apis) {
                final String apikey = apiCandidate.getClass().getSimpleName().toLowerCase();
                if (uri.endsWith(apikey)) {
                    api = apiCandidate;
                    break;
                }
            }
            if (api == null) {
                LOGGER.warn("API not found for URI: " + uri);
                response.setStatus(HttpStatus.NOT_FOUND_404);
            }

            ObjectMapper objectMapper;
            synchronized (objectMapperPool) {
                if (objectMapperPool.size() == 0) {
                    objectMapperPool.push(new ObjectMapper());
                }
                objectMapper = objectMapperPool.pop();
            }

            final JsonRpcServer jsonRpcServer = new JsonRpcServer(objectMapper, new ApiInvocationHandler(context, api));
            final long startTimeMillis = System.currentTimeMillis();
            jsonRpcServer.handle(request, response);
            LOGGER.trace("RPC CALL time: " + (System.currentTimeMillis() - startTimeMillis) + " ms");

            synchronized (objectMapperPool) {
                objectMapperPool.push(objectMapper);
            }

        } catch (final IllegalAccessError e) {
            LOGGER.warn("Access denied: " + request.getRequestURI() + ": " + e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED_401);
        } catch (final AccessControlException e) {
            LOGGER.warn("Access denied: " + request.getRequestURI() + ": " + e.getMessage());
            response.setStatus(HttpStatus.UNAUTHORIZED_401);
        } catch (final Throwable t) {
            LOGGER.error("Error processing: " + request.getRequestURI(), t);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR_500);
        }
    }

}