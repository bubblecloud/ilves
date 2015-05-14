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
package org.bubblecloud.ilves.util;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.activation.DataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;


/**
 * Email utility.
 *
 * @author Tommi S.E. Laukkanen
 */
public class EmailUtil {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(EmailUtil.class);

    /**
     * Sends email.
     * @param to target email addresses
     * @param from from email address
     * @param subject the email subject
     * @param body the email body
     */
    public static void send(final String to, final String from, final String subject, final String body) {
        final String smtpHost = PropertiesUtil.getProperty("site", "smtp-host");
        final String smtpPort = PropertiesUtil.getProperty("site", "smtp-port");
        final String smtpUser = PropertiesUtil.getProperty("site", "smtp-user");
        final String smtpPassword = PropertiesUtil.getProperty("site", "smtp-password");
        send(smtpHost, smtpPort, smtpUser, smtpPassword, Collections.singletonList(to), from, subject, body);
    }

    /**
     * Sends email.
     * @param smtpHost the SMTP host
     * @param smtpPort the SMTP host port
     * @param smtpUser the SMTP user
     * @param smtpPassword the SMTP user password
     * @param to target email addresses
     * @param from from email address
     * @param subject the email subject
     * @param body the email body
     */
    public static void send(final String smtpHost, final String smtpPort,
                            final String smtpUser, final String smtpPassword,
                            final List<String> to, final String from, final String subject, final String body) {
        try {
            final Properties properties = System.getProperties();
            properties.put("mail.smtp.host", smtpHost);

            if (!StringUtils.isEmpty(smtpPort)) {
                properties.put("mail.smtp.port", smtpPort);
            }

            final Session session;
            if (StringUtils.isEmpty(smtpUser) || StringUtils.isEmpty(smtpPassword)) {
                session = Session.getDefaultInstance(properties, null);
                LOGGER.info("Sending unauthenticated plain text transmission of email via "
                        + smtpHost + ": " + smtpPort + " from address: " + from);
            } else {
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
                session = Session.getInstance(properties,
                        new javax.mail.Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication(smtpUser, smtpPassword);
                            }
                        });
                LOGGER.info("Sending authenticated TLS encrypted transmission of email via "
                        + smtpHost + ": " + smtpPort + " from address: " + from);
            }

            // Text part
            final MimeBodyPart textPart = new MimeBodyPart();
            textPart.setHeader("MIME-Version", "1.0");
            textPart.setHeader("Content-Type", textPart.getContentType());
            textPart.setText(body);

            // HTML part
            final MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setHeader("MIME-Version", "1.0");
            htmlPart.setHeader("Content-Type", htmlPart.getContentType());

            String htmlContent = "<html><head><title>"
                    + subject
                    + "</title></head><body><p><pre>"
                    + body
                    + "</pre></p></body></html>";

            htmlContent = htmlContent.replace("€", "&euro;");

            htmlPart.setContent(htmlContent, "text/html");

            final Multipart multiPartContent = new MimeMultipart("alternative");
            multiPartContent.addBodyPart(textPart);
            multiPartContent.addBodyPart(htmlPart);

            final Message message = new MimeMessage(session);
            message.setHeader("MIME-Version", "1.0");
            message.setHeader("Content-Type", multiPartContent.getContentType());
            message.setHeader("X-Mailer", "Site Kit");
            message.setSentDate(new Date());
            message.setFrom(new InternetAddress(from));

            if (to.size() == 1) {
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to.get(0), false));
            } else {
                final InternetAddress[] recipientAddresses = new InternetAddress[to.size()];
                for (int i = 0; i < to.size(); i++) {
                    final InternetAddress[] parsedAddress = InternetAddress.parse(to.get(i), false);
                    if (parsedAddress.length == 1) {
                        recipientAddresses[i] = parsedAddress[0];
                    }
                }
                message.setRecipients(Message.RecipientType.BCC, recipientAddresses);
            }

            message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(from, false));
            message.setSubject(subject);
            message.setContent(multiPartContent);

            Transport.send(message);
        } catch (final Throwable t) {
            throw new RuntimeException("Invoice email sending failed.", t);
        }
    }

    /**
     * Inner class to act as a JAF datasource to send HTML e-mail content.
     */
    static class HTMLDataSource implements DataSource {
        /** The HTML content. */
        private final String htmlContent;

        /**
         * Default constructor for setting the source HTML.
         * @param htmlContent the HTML content
         */
        public HTMLDataSource(final String htmlContent) {
            this.htmlContent = htmlContent;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(htmlContent.getBytes());
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getContentType() {
            return "text/html";
        }

        @Override
        public String getName() {
            return "JAF text/html dataSource to send e-mail.";
        }
    }
}
