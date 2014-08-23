package org.vaadin.addons.sitekit.jetty;

import org.junit.Assert;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.Ignore;
import org.junit.Test;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.*;
import java.util.Properties;

/**
 * Class for testing Jetty TSL functionality.
 */
public class JettyTslTest {

    @Ignore
    @Test
    public void testTsl() throws Exception {

        final Server server = newServer();
        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(final String target, final Request request,
                               final HttpServletRequest httpServletRequest,
                               final HttpServletResponse httpServletResponse)
                    throws IOException, ServletException {
                httpServletResponse.setContentType("text/plain;charset=utf-8");
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                request.setHandled(true);
                httpServletResponse.getWriter().println("z");
            }
        });
        server.start();

        final String postUrl = "https://127.0.0.1:8443/test";
        final String postContent = "x=y";

        final HttpsURLConnection httpsUrlConnection = newHttpsUrlConnection(new URL(postUrl));
        final int responseCode = writePost(httpsUrlConnection, postContent);

        final InputStream inputStream;
        if (responseCode == HttpURLConnection.HTTP_OK) {
            inputStream = httpsUrlConnection.getInputStream();
        } else {
            inputStream = httpsUrlConnection.getErrorStream();
        }

        final String response = readResponse(inputStream);

        Assert.assertEquals("z", response);

        inputStream.close();
    }

    private int writePost(HttpsURLConnection httpsUrlConnection, String postContent) throws IOException {
        httpsUrlConnection.setRequestMethod("POST");
        httpsUrlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        httpsUrlConnection.setRequestProperty("Content-Length", Integer.toString(postContent.length()));
        final OutputStream outputStream = httpsUrlConnection.getOutputStream();
        outputStream.write(postContent.getBytes("UTF-8"));
        outputStream.close();

        return httpsUrlConnection.getResponseCode();
    }

    private String readResponse(InputStream inputStream) throws IOException {
        final StringBuilder builder = new StringBuilder();
        final BufferedReader reader = new BufferedReader( new InputStreamReader( inputStream ) );
        String line;
        while(( line = reader.readLine() ) != null) {
            builder.append(line);
        }
        return builder.toString();
    }

    private HttpsURLConnection newHttpsUrlConnection(final URL url)
            throws Exception {

        final HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) url.openConnection();

        httpsUrlConnection.setRequestProperty("Connection", "close");
        httpsUrlConnection.setDoInput(true);
        httpsUrlConnection.setDoOutput(true);
        httpsUrlConnection.setUseCaches(false);
        httpsUrlConnection.setConnectTimeout(30000);
        httpsUrlConnection.setReadTimeout(30000);

        final String keyStorePath = "/path/to/keystore";
        final String keyStorePassword = "changeme";
        final String keyManagerPassword = "changeme";
        final String trustStorePath = "/path/to/truststore";
        final String trustStorePassword = "changeme";

        final SslContextFactory sslContextFactory = newSslSocketFactory(keyStorePath, keyStorePassword,
                keyManagerPassword, trustStorePath, trustStorePassword);
        final SSLSocketFactory sslSocketFactory = sslContextFactory.getSslContext().getSocketFactory();
        httpsUrlConnection.setSSLSocketFactory(sslSocketFactory);
        return httpsUrlConnection;
    }

    private Server newServer() {
        final Server server = new Server();

        final HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSecureScheme("https");
        httpConfiguration.setSecurePort(8443);
        httpConfiguration.setOutputBufferSize(32768);
        httpConfiguration.setRequestHeaderSize(8192);
        httpConfiguration.setResponseHeaderSize(8192);
        httpConfiguration.setSendServerVersion(false);
        httpConfiguration.setSendDateHeader(false);

        final HttpConfiguration httpsConfiguration = new HttpConfiguration(httpConfiguration);
        httpsConfiguration.addCustomizer(new SecureRequestCustomizer()); // <-- HERE

        final String keyStorePath = "/path/to/keystore";
        final String keyStorePassword = "changeme";
        final String keyManagerPassword = "changeme";
        final String trustStorePath = "/path/to/truststore";
        final String trustStorePassword = "changeme";

        final SslContextFactory sslContextFactory = newSslSocketFactory(keyStorePath, keyStorePassword,
                keyManagerPassword, trustStorePath, trustStorePassword);

        final ServerConnector httpConnector = new ServerConnector(server, new HttpConnectionFactory(httpConfiguration));
        httpConnector.setPort(8080);
        httpConnector.setIdleTimeout(30000);

        final ServerConnector httpsConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory,"http/1.1"),
                new HttpConnectionFactory(httpsConfiguration));
        httpsConnector.setPort(8443);
        httpsConnector.setIdleTimeout(30000);

        server.addConnector(httpConnector);
        server.addConnector(httpsConnector);
        return server;
    }

    private SslContextFactory newSslSocketFactory(String keyStorePath, String keyStorePassword,
                                                  String keyManagerPassword, String trustStorePath,
                                                  String trustStorePassword) {
        final SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keyStorePath);
        sslContextFactory.setKeyStorePassword(keyStorePassword);
        sslContextFactory.setKeyManagerPassword(keyManagerPassword);
        sslContextFactory.setTrustStorePath(trustStorePath);
        sslContextFactory.setTrustStorePassword(trustStorePassword);
        sslContextFactory.setExcludeCipherSuites(
                "SSL_RSA_WITH_DES_CBC_SHA",
                "SSL_DHE_RSA_WITH_DES_CBC_SHA",
                "SSL_DHE_DSS_WITH_DES_CBC_SHA",
                "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
                "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");
        return sslContextFactory;
    }
}
