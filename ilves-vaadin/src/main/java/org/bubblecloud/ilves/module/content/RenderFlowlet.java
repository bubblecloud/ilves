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
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.bubblecloud.ilves.component.flow.AbstractFlowViewlet;
import org.bubblecloud.ilves.component.flow.AbstractFlowlet;
import org.bubblecloud.ilves.exception.SiteException;
import org.bubblecloud.ilves.site.Site;
import org.markdown4j.Markdown4jProcessor;

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

    private final Button topEditButton;

    public RenderFlowlet(final Content content) {
        this.content = content;
        topEditButton = getSite().getButton("edit");
        topEditButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                final ContentFlowlet contentFlowlet = getFlow().getFlowlet(ContentFlowlet.class);
                contentFlowlet.edit(content, false);
                ((AbstractFlowViewlet) getFlow()).getTopRightLayout().removeComponent(topEditButton);
                ((AbstractFlowViewlet) getFlow()).refreshPathLabels();
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

        ((AbstractFlowViewlet) getFlow()).getTopRightLayout().removeComponent(topEditButton);
        ((AbstractFlowViewlet) getFlow()).getTopRightLayout().addComponent(topEditButton);
        ((AbstractFlowViewlet) getFlow()).refreshPathLabels();

        final CssLayout layout = new CssLayout();
        //layout.addComponent(topEditButton);
        layout.setStyleName("wiki-content");
        layout.addComponent(new Label(html, ContentMode.HTML));

        setCompositionRoot(layout);
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected boolean isValid() {
        return false;
    }

}
