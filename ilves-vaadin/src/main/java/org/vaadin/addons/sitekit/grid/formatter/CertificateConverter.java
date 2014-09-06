package org.vaadin.addons.sitekit.grid.formatter;

import com.vaadin.data.util.converter.Converter;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Locale;

/**
 * Percentage converter.
 */
public class CertificateConverter implements Converter<String, String> {

    @Override
    public String convertToModel(String value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (value == null || value.length() == 0) {
            return null;
        } else {
            try {
                final StringReader stringReader = new StringReader(value);
                final PemReader pemReader = new PemReader(stringReader);
                final byte[] x509Data = pemReader.readPemObject().getContent();
                final CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
                final Certificate certificate = certificateFactory.generateCertificate(
                        new ByteArrayInputStream(x509Data));
                return Base64.encodeBase64String(certificate.getEncoded());
            } catch (final Exception e) {
                throw new ConversionException("Error parsing ASCII X509 certificate.", e);
            }
        }
    }

    @Override
    public String convertToPresentation(String value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (value == null) {
            return null;
        } else {
            try {
                final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
                final InputStream in = new ByteArrayInputStream(Base64.decodeBase64(value));
                final Certificate certificate = certFactory.generateCertificate(in);
                final StringWriter stringWriter = new StringWriter();
                final PemWriter pemWriter = new PemWriter(stringWriter);
                final PemObject pemObject = new PemObject("CERTIFICATE", certificate.getEncoded());
                pemWriter.writeObject(pemObject);
                pemWriter.flush();
                return stringWriter.toString();
            } catch (final Exception e) {
                throw new ConversionException("Error generating X509 certificate from database byte array.", e);
            }
        }
    }

    @Override
    public Class<String> getModelType() {
        return String.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
