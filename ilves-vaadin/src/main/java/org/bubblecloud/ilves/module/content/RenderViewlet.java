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
package org.bubblecloud.ilves.module.content;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.bubblecloud.ilves.cache.InMemoryCache;
import org.bubblecloud.ilves.exception.SiteException;
import org.bubblecloud.ilves.site.AbstractViewlet;
import org.markdown4j.Markdown4jProcessor;

import java.io.IOException;

/**
 * Viewlet which renders image from Theme.
 * @author Tommi S.E. Laukkanen
 */
public final class RenderViewlet extends AbstractViewlet {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(RenderViewlet.class);
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The view HTML. */
    private static InMemoryCache<String, String> markupHtmlMap = new InMemoryCache<>(
            24 * 60 * 60 * 1000, 60 * 1000, 100
    );

    @Override
    public void attach() {
        super.attach();
        final String markup = (String) getViewletDescriptor().getConfiguration();

        try {
            if (!markupHtmlMap.containsKey(markup)) {
                final long startTimeMillis = System.currentTimeMillis();
                final String html = new Markdown4jProcessor().process(RenderFlowlet.escapeHtml(markup));
                markupHtmlMap.put(markup, html);
                LOGGER.debug("Markup processing took: " + (System.currentTimeMillis() -  startTimeMillis) + " ms.");
            }
        } catch (IOException e) {
            throw new SiteException("Error processing markdown.", e);
        }

        setStyleName("wiki-content");
        final Label label = new Label(markupHtmlMap.get(markup), ContentMode.HTML);
        setCompositionRoot(label);
    }

    /**
     * SiteView constructSite occurred.
     */
    @Override
    public void enter(final String parameters) {
    }

}
