package org.bubblecloud.ilves.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcServer;
import org.apache.log4j.Logger;
import org.bubblecloud.ilves.model.Company;
import org.bubblecloud.ilves.security.DefaultRoles;
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
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
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
    private static Map<Class, Object> apis = new HashMap<>();

    /**
     * Add API object to the API servlet class.
     * @param apiInterface the API interface
     * @param apiImplementation the API implementation
     */
    public static void addApi(final Class apiInterface, final Object apiImplementation) {
        apis.put(apiInterface, apiImplementation);
    }

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
            // The virtual host based on URL.
            final Company company = DefaultSiteUI.resolveCompany(entityManager, request.getServerName());
            // The security provider.
            final SecurityProvider securityProvider = new ApiSecurityProviderImpl(DefaultRoles.ADMINISTRATOR, DefaultRoles.USER);

            // The context.
            final SiteContext context = new SiteContext(entityManager, auditEntityManager, request, securityProvider);
            context.putObject(EntityManager.class, entityManager);
            context.putObject(EntityManagerFactory.class, entityManagerFactory);
            context.putObject(Company.class, company);

            Class apiInterface = null;
            Object apiImplementation = null;
            for (final Class apiCandidate : apis.keySet()) {
                final String apikey = apiCandidate.getSimpleName().toLowerCase();
                if (uri.endsWith(apikey)) {
                    apiInterface = apiCandidate;
                    apiImplementation = apis.get(apiCandidate);
                    break;
                }
            }
            if (apiInterface == null) {
                LOGGER.warn("API not found for URI: " + uri);
                response.setStatus(HttpStatus.NOT_FOUND_404);
                return;
            }

            ObjectMapper objectMapper;
            synchronized (objectMapperPool) {
                if (objectMapperPool.size() == 0) {
                    objectMapperPool.push(new ObjectMapper());
                }
                objectMapper = objectMapperPool.pop();
            }

            final ClassLoader classLoader = this.getClass().getClassLoader();
            final Class[] interfaces = new Class[] {apiInterface};
            final InvocationHandler invocationHandler = new ApiInvocationHandler(context, apiImplementation);
            final JsonRpcServer jsonRpcServer = new JsonRpcServer(objectMapper, Proxy.newProxyInstance(classLoader, interfaces, invocationHandler));
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