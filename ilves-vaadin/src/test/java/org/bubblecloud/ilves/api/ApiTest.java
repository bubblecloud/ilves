package org.bubblecloud.ilves.api;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
import org.apache.log4j.xml.DOMConfigurator;
import org.bubblecloud.ilves.Ilves;
import org.bubblecloud.ilves.api.apis.RequestAccessTokenResult;
import org.bubblecloud.ilves.api.apis.Security;
import org.bubblecloud.ilves.api.apis.SecurityImpl;
import org.bubblecloud.ilves.module.audit.AuditModule;
import org.bubblecloud.ilves.module.content.ContentModule;
import org.bubblecloud.ilves.module.customer.CustomerModule;
import org.eclipse.jetty.server.Server;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Test method for API.
 */
public class ApiTest {
    /** The properties file prefix.*/
    public static final String PROPERTIES_FILE_PREFIX = "site";
    /** The localization bundle. */
    public static final String LOCALIZATION_BUNDLE_PREFIX = "site-localization";
    /** The persistence unit to be used. */
    public static final String PERSISTENCE_UNIT = "site";

    @Test
    @Ignore
    public void testApi() throws Exception {
        // Construct jetty server.
        final Server server = Ilves.configure(PROPERTIES_FILE_PREFIX, LOCALIZATION_BUNDLE_PREFIX, PERSISTENCE_UNIT);

        // Initialize modules
        Ilves.initializeModule(AuditModule.class);
        Ilves.initializeModule(CustomerModule.class);
        Ilves.initializeModule(ContentModule.class);

        Ilves.addApi(Security.class, SecurityImpl.class);
        Ilves.addApi(ApiMock.class, ApiMockImpl.class);

        // Start server.
        server.start();

        final JsonRpcHttpClient apiMockClient = new JsonRpcHttpClient(
                new URL("http://localhost:8080/api/apimock"));

        final ApiMock apiMock = ProxyUtil.createClientProxy(
                getClass().getClassLoader(),
                ApiMock.class, apiMockClient);

        final Security security = ProxyUtil.createClientProxy(
                getClass().getClassLoader(),
                Security.class,
                new JsonRpcHttpClient(
                        new URL("http://localhost:8080/api/security")));

        final RequestAccessTokenResult result = security.requestAccessToken("admin@admin.org", "password");

        final Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + new String(result.getAccessToken()));

        apiMockClient.setHeaders(headers);

        Assert.assertEquals("[administrator]:test", apiMock.testMethod("test"));

        server.stop();
    }

}
