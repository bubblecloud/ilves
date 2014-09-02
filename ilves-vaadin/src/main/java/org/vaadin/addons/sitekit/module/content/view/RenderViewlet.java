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
package org.vaadin.addons.sitekit.module.content.view;

import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.apache.log4j.Logger;
import org.markdown4j.Markdown4jProcessor;
import org.vaadin.addons.sitekit.site.AbstractViewlet;
import org.vaadin.addons.sitekit.site.SiteException;

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

    @Override
    public void attach() {
        super.attach();
        final String html;
        try {
            final long startTimeMillis = System.currentTimeMillis();
            html = new Markdown4jProcessor().process((String) getViewletDescriptor().getConfiguration());
            LOGGER.debug("Mark4j processing took: " + (System.currentTimeMillis() -  startTimeMillis) + " ms.");
        } catch (IOException e) {
            throw new SiteException("Error processing markdown.", e);
        }

        final VerticalLayout layout = new VerticalLayout();
        layout.addComponent(new Label(html, ContentMode.HTML));
        layout.setSpacing(true);
        layout.setMargin(false);
        setStyleName("wiki-content");
        //final Panel panel = new Panel();
        //panel.setStyleName("wiki-panel");
        //panel.setStyleName(Reindeer.PANEL_LIGHT);
        //panel.setContent(layout);

        setCompositionRoot(layout);
    }

    /**
     * SiteView constructSite occurred.
     */
    @Override
    public void enter(final String parameters) {
    }

}
