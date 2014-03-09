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

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.markdown4j.Markdown4jProcessor;
import org.vaadin.addons.sitekit.flow.AbstractFlowViewlet;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.module.content.model.Content;
import org.vaadin.addons.sitekit.site.Site;
import org.vaadin.addons.sitekit.site.SiteException;
import org.vaadin.addons.sitekit.viewlet.administrator.privilege.PrivilegesFlowlet;

import javax.persistence.EntityManager;
import java.io.IOException;

/**
 * Viewlet which renders image from Theme.
 * @author Tommi S.E. Laukkanen
 */
public final class RenderFlowlet extends AbstractFlowlet {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    private Content content;

    private final Button editButton;

    public RenderFlowlet(final Content content) {
        this.content = content;
        editButton = getSite().getButton("edit");
        editButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                final ContentFlowlet contentFlowlet = getFlow().getFlowlet(ContentFlowlet.class);
                contentFlowlet.edit(content, false);
                ((AbstractFlowViewlet) getFlow()).getTopLayout().removeComponent(editButton);
                ((AbstractFlowViewlet) getFlow()).getTopLayout().setSizeUndefined();
                getFlow().forward(ContentFlowlet.class);
            }
        });
    }

    @Override
    public String getFlowletKey() {
        return "render";
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void enter() {
        final EntityManager entityManager = Site.getCurrent().getSiteContext().getObject(EntityManager.class);
        final String html;
        try {
            html = new Markdown4jProcessor().process(content.getMarkup());
        } catch (IOException e) {
            throw new SiteException("Error processing markdown.", e);
        }

        ((AbstractFlowViewlet) getFlow()).getTopLayout().removeComponent(editButton);
        ((AbstractFlowViewlet) getFlow()).getTopLayout().setWidth(100, Unit.PERCENTAGE);
        ((AbstractFlowViewlet) getFlow()).getTopLayout().addComponent(editButton);
        ((AbstractFlowViewlet) getFlow()).getBottomLayout().setVisible(false);
        ((AbstractFlowViewlet) getFlow()).getTopLayout().setComponentAlignment(editButton, Alignment.MIDDLE_RIGHT);
        ((AbstractFlowViewlet) getFlow()).getTopLayout().setExpandRatio(editButton, 1);

        final VerticalLayout layout = new VerticalLayout();
        //layout.addComponent(editButton);
        layout.addComponent(new Label(html, ContentMode.HTML));
        layout.setSpacing(true);
        layout.setMargin(true);

        final Panel panel = new Panel();
        panel.setStyleName(Reindeer.PANEL_LIGHT);
        panel.setContent(layout);

        setCompositionRoot(panel);
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected boolean isValid() {
        return false;
    }

}
