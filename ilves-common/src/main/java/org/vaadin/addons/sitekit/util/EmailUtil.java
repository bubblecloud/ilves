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
package org.vaadin.addons.sitekit.util;

import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;


/**
 * Email utility.
 *
 * @author Tommi S.E. Laukkanen
 */
public class EmailUtil {

    /**
     * Sends email.
     * @param smtpHost the SMTP host
     * @param to target email address
     * @param from from email address
     * @param subject the email subject
     * @param body the email body
     */
    public static void send(final String smtpHost,
                            final String to, final String from, final String subject, final String body) {
        try {
            final Properties properties = System.getProperties();
            properties.put("mail.smtp.host", smtpHost);
            final Session session = Session.getDefaultInstance(properties, null);

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
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
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
