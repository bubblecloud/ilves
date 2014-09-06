package org.vaadin.addons.sitekit.jetty;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.vaadin.addons.sitekit.util.CertificateUtil;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

/**
 * Class for testing Jetty TSL functionality.
 */
public class JettyTslTest {

    public static final String KEY_STORE_PATH = System.getProperty("java.io.tmpdir") + File.separator + "test.jks";

    @After
    public void after() {
        new File(KEY_STORE_PATH).deleteOnExit();
    }

    @Ignore
    @Test
    public void testTsl() throws Exception {

        final String keyStorePassword = "changeme";
        final String certificatePrivateKeyPassword = "changeme";
        final String trustStorePath = KEY_STORE_PATH;
        final String trustStorePassword = "changeme";

        final String clientCertificateAlias = "client";
        final String clientCertificateCommonName = "client-common-name";
        final String serverCertificateAlias = "server";
        final String serverCertificateSubjectAlternativeNameIpAddress = "127.0.0.1";
        final String serverCertificateCommonName = "server-common-name";

        CertificateUtil.ensureServerCertificateExists(
                clientCertificateCommonName,
                null,
                clientCertificateAlias,
                certificatePrivateKeyPassword,
                KEY_STORE_PATH, keyStorePassword);

        CertificateUtil.ensureServerCertificateExists(
                serverCertificateCommonName,
                serverCertificateSubjectAlternativeNameIpAddress,
                serverCertificateAlias,
                certificatePrivateKeyPassword,
                KEY_STORE_PATH, keyStorePassword);

        final Server server = newServer(
                serverCertificateAlias,
                KEY_STORE_PATH,
                keyStorePassword,
                certificatePrivateKeyPassword,
                trustStorePath,
                trustStorePassword);

        server.setHandler(new AbstractHandler() {
            @Override
            public void handle(final String target, final Request request,
                               final HttpServletRequest httpServletRequest,
                               final HttpServletResponse httpServletResponse)
                    throws IOException, ServletException {
                final X509Certificate[] clientCertificates = (X509Certificate[])
                        httpServletRequest.getAttribute("javax.servlet.request.X509Certificate");
                Assert.assertEquals(1, clientCertificates.length);
                System.out.println(clientCertificates[0].getSubjectDN().getName());
                Assert.assertEquals("CN=" + clientCertificateCommonName, clientCertificates[0].getSubjectDN().getName());

                httpServletResponse.setContentType("text/plain;charset=utf-8");
                httpServletResponse.setStatus(HttpServletResponse.SC_OK);
                request.setHandled(true);
                httpServletResponse.getWriter().println("z");
            }
        });
        server.start();

        final String postUrl = "https://127.0.0.1:8443/test";
        final String postContent = "x=y";

        final HttpsURLConnection httpsUrlConnection = newHttpsUrlConnection(new URL(postUrl),
                clientCertificateAlias,
                KEY_STORE_PATH,
                keyStorePassword,
                certificatePrivateKeyPassword,
                trustStorePath,
                trustStorePassword);
        final int responseCode = writePost(httpsUrlConnection, postContent);

        final X509Certificate[] serverCertificates = (X509Certificate[]) httpsUrlConnection.getServerCertificates();
        Assert.assertEquals(1, serverCertificates.length);
        System.out.println(serverCertificates[0].getSubjectDN().getName());
        Assert.assertEquals("CN=" + serverCertificateCommonName, serverCertificates[0].getSubjectDN().getName());

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

    private HttpsURLConnection newHttpsUrlConnection(final URL url,
                                                     final String certificateAlias,
                                                     final String keyStorePath,
                                                     final String keyStorePassword,
                                                     final String keyManagerPassword,
                                                     final String trustStorePath,
                                                     final String trustStorePassword)
            throws Exception {

        final HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) url.openConnection();

        httpsUrlConnection.setRequestProperty("Connection", "close");
        httpsUrlConnection.setDoInput(true);
        httpsUrlConnection.setDoOutput(true);
        httpsUrlConnection.setUseCaches(false);
        httpsUrlConnection.setConnectTimeout(30000);
        httpsUrlConnection.setReadTimeout(30000);

        final SslContextFactory sslContextFactory = newSslSocketFactory(certificateAlias, keyStorePath, keyStorePassword,
                keyManagerPassword, trustStorePath, trustStorePassword, false);
        sslContextFactory.start();
        final SSLSocketFactory sslSocketFactory = sslContextFactory.getSslContext().getSocketFactory();
        httpsUrlConnection.setSSLSocketFactory(sslSocketFactory);
        return httpsUrlConnection;
    }

    private Server newServer(
            final String certificateAlias,
            final String keyStorePath,
            final String keyStorePassword,
            final String keyManagerPassword,
            final String trustStorePath,
            final String trustStorePassword) throws Exception {
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

        final SslContextFactory sslContextFactory = newSslSocketFactory(certificateAlias, keyStorePath, keyStorePassword,
                keyManagerPassword, trustStorePath, trustStorePassword, true);

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

    private SslContextFactory newSslSocketFactory(final String certificateAlias, String keyStorePath, String keyStorePassword,
                                                  String keyManagerPassword, String trustStorePath,
                                                  String trustStorePassword,
                                                  final boolean clientAuthentication) throws Exception {

        final SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setCertAlias(certificateAlias);
        sslContextFactory.setNeedClientAuth(clientAuthentication);
        sslContextFactory.setKeyStoreType("BKS");
        sslContextFactory.setKeyStorePath(keyStorePath);
        sslContextFactory.setKeyStorePassword(keyStorePassword);
        sslContextFactory.setKeyManagerPassword(keyManagerPassword);
        sslContextFactory.setTrustStoreType("BKS");
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
        sslContextFactory.setRenegotiationAllowed(false);
        return sslContextFactory;
    }
}
