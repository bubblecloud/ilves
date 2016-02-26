package org.bubblecloud.ilves.api;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import com.googlecode.jsonrpc4j.ProxyUtil;
import org.apache.log4j.xml.DOMConfigurator;
import org.bubblecloud.ilves.Ilves;
import org.bubblecloud.ilves.module.audit.AuditModule;
import org.bubblecloud.ilves.module.content.ContentModule;
import org.bubblecloud.ilves.module.customer.CustomerModule;
import org.eclipse.jetty.server.Server;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URL;

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
        // Configure logging.
        DOMConfigurator.configure("log4j.xml");

        // Construct jetty server.
        final Server server = Ilves.configure(PROPERTIES_FILE_PREFIX, LOCALIZATION_BUNDLE_PREFIX, PERSISTENCE_UNIT);

        // Initialize modules
        Ilves.initializeModule(AuditModule.class);
        Ilves.initializeModule(CustomerModule.class);
        Ilves.initializeModule(ContentModule.class);

        Ilves.addApi(ApiMock.class, new ApiMockImpl());

        // Start server.
        server.start();

        JsonRpcHttpClient client = new JsonRpcHttpClient(
                new URL("http://localhost:8080/api/apimock"));

        ApiMock apiMock = ProxyUtil.createClientProxy(
                getClass().getClassLoader(),
                ApiMock.class,
                client);

        Assert.assertEquals("test", apiMock.testMethod("test"));

        server.stop();
    }

}
