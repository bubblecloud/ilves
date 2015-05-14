package org.bubblecloud.ilves.util;

import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.template.JadeTemplate;
import de.neuland.jade4j.template.TemplateLoader;
import org.apache.log4j.Logger;
import org.bubblecloud.ilves.exception.SiteException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Class for parsing Jade templates.
 *
 * @author Tommi S.E. Laukkanen
 */
public class JadeUtil {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(JadeUtil.class);

    /**
     * Parse given Jade file input stream to HTML input stream.
     * @param templatePath the Jade template path
     * @return a HTML input stream
     */
    public static InputStream parse(final String templatePath) {
        final InputStream inputStream = JadeUtil.class.getResourceAsStream(templatePath);
        final Map<String, Object> model = new HashMap<String, Object>();

        final JadeConfiguration config = new JadeConfiguration();
        config.setTemplateLoader(new TemplateLoader() {
            @Override
            public long getLastModified(String name) throws IOException {
                return 0;
            }
            @Override
            public Reader getReader(String name) throws IOException {
                return new InputStreamReader(inputStream);
            }
        });

        try {
            final JadeTemplate template = config.getTemplate("name");
            final String htmlString = config.renderTemplate(template, model).replace(
                    "UITRANSACTIONID", UUID.randomUUID().toString());
            return new ByteArrayInputStream(htmlString.getBytes("UTF-8"));
        } catch (final IOException e) {
            LOGGER.error("Error parsing JADE template: " + templatePath, e);
            throw new SiteException("Error parsing JADE template: " + templatePath, e);
        }
    }

}
