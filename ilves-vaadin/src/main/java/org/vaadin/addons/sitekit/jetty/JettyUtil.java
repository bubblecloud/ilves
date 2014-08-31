/**
 * Copyright 2013 Tommi S.E. Laukkanen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaadin.addons.sitekit.jetty;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.vaadin.addons.sitekit.cache.UserClientCertificateCache;
import org.vaadin.addons.sitekit.site.DefaultSiteUI;
import org.vaadin.addons.sitekit.util.CertificateUtil;
import org.vaadin.addons.sitekit.util.PropertiesUtil;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyStore;
import java.security.cert.CRL;
import java.security.cert.CertificateException;
import java.util.Collection;

/**
 * Utility class for creating embedded Jetty sites.
 *
 * @author Tommi S.E. Laukkanen
 */
public class JettyUtil {

    /**
     * Constructs Jetty server.
     * @param httpPort the HTTP port
     * @param httpsPort the HTTPS port
     * @param requireClientAuthentication true if client authentication is required
     * @return the Jetty server
     * @throws Exception if exception occurs in construction
     */
    public static Server newServer(
            final int httpPort,
            final int httpsPort,
            final boolean requireClientAuthentication) throws Exception {
        UserClientCertificateCache.init(DefaultSiteUI.getEntityManagerFactory());

        final String keyStorePath = PropertiesUtil.getProperty("site", "key-store-path");
        final String keyStorePassword = PropertiesUtil.getProperty("site", "key-store-password");

        final String certificateAlias = PropertiesUtil.getProperty("site", "server-certificate-entry-alias");
        final String certificatePassword = PropertiesUtil.getProperty("site", "server-certificate-entry-password");

        final String selfSignedCertificateHostName =
                PropertiesUtil.getProperty("site", "server-certificate-self-sign-host-name");
        final String selfSignedCertificateIpAddress =
                PropertiesUtil.getProperty("site", "server-certificate-self-sign-ip-address");

        final Server server = new Server();

        final HttpConfiguration httpConfiguration = new HttpConfiguration();
        httpConfiguration.setSecureScheme("https");
        httpConfiguration.setSecurePort(httpsPort);
        httpConfiguration.setOutputBufferSize(32768);
        httpConfiguration.setRequestHeaderSize(8192);
        httpConfiguration.setResponseHeaderSize(8192);
        httpConfiguration.setSendServerVersion(false);
        httpConfiguration.setSendDateHeader(false);

        if (httpPort > 0) {
            final ServerConnector httpConnector = new ServerConnector(server,
                    new HttpConnectionFactory(httpConfiguration));
            httpConnector.setPort(httpPort);
            httpConnector.setIdleTimeout(30000);

            server.addConnector(httpConnector);
        }

        if (httpsPort > 0) {
            CertificateUtil.ensureServerCertificateExists(
                    selfSignedCertificateHostName,
                    selfSignedCertificateIpAddress,
                    certificateAlias,
                    certificatePassword,
                    keyStorePath, keyStorePassword);

            final JettySiteSslContextFactory sslContextFactory = newSslSocketFactory(certificateAlias,
                    keyStorePath, keyStorePassword,
                    certificatePassword, requireClientAuthentication);

            final HttpConfiguration httpsConfiguration = new HttpConfiguration(httpConfiguration);
            httpsConfiguration.addCustomizer(new SecureRequestCustomizer());

            final ServerConnector httpsConnector = new ServerConnector(server,
                    new SslConnectionFactory(sslContextFactory, "http/1.1"),
                    new HttpConnectionFactory(httpsConfiguration));
            httpsConnector.setPort(8443);
            httpsConnector.setIdleTimeout(30000);

            server.addConnector(httpsConnector);
        }
        return server;
    }

    /**
     * Constructs SSL context factory.
     * @param certificateAlias the certificate alias
     * @param keyStorePath the key store path
     * @param keyStorePassword the key store password
     * @param certificatePassword the certificate password
     * @param requireClientAuthentication true if client authentication is required
     * @return the constructed SSL context factory
     * @throws Exception if exception occurs in construction
     */
    private static JettySiteSslContextFactory newSslSocketFactory(final String certificateAlias,
                                                                  final String keyStorePath,
                                                                  final String keyStorePassword,
                                                                  final String certificatePassword,
                                                                  final boolean requireClientAuthentication)
            throws Exception {

        final JettySiteSslContextFactory sslContextFactory = new JettySiteSslContextFactory();
        sslContextFactory.setCertAlias(certificateAlias);
        sslContextFactory.setNeedClientAuth(requireClientAuthentication);
        sslContextFactory.setWantClientAuth(true);
        sslContextFactory.setKeyStoreType("BKS");
        sslContextFactory.setKeyStorePath(keyStorePath);
        sslContextFactory.setKeyStorePassword(keyStorePassword);
        sslContextFactory.setKeyManagerPassword(certificatePassword);
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

    /**
     * Jetty Site SSL context factory.
     *
     * @author Tommi S.E. Laukkanen
     */
    public static class JettySiteSslContextFactory extends SslContextFactory
    {

        @Override
        protected TrustManager[] getTrustManagers(KeyStore trustStore, Collection<? extends CRL> crls) throws Exception
        {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("PKIX");
            trustManagerFactory.init(trustStore);
            final TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {
                    if (x509Certificates.length != 1) {
                        throw new CertificateException("Certificate paths not supported.");
                    }
                    if (UserClientCertificateCache.getUserByCertificate(x509Certificates[0]) == null) {
                        throw new CertificateException("Unknown certificate.");
                    }
                }

                @Override
                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] x509Certificates, String s) throws CertificateException {
                    throw new CertificateException("Unsupported operation.");
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }
            };
            return new TrustManager[] {trustManager};
        }

    }

}
