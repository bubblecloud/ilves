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
package org.bubblecloud.ilves.component.flow;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import org.bubblecloud.ilves.site.AbstractSiteUI;
import org.bubblecloud.ilves.site.Site;


/**
 * Abstract base class for flowlet implementations.
 *
 * @author Tommi S.E. Laukkanen
 */
public abstract class AbstractFlowlet extends CustomComponent implements Flowlet {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The flow sheet. */
    private Flow flow;

    /** The root layout. */
    private GridLayout rootLayout;

    /**
     * Default constructor.
     */
    public AbstractFlowlet() {
        super();
    }

    @Override
    public abstract String getFlowletKey();

    /**
     * @return the site
     */
    protected final Site getSite() {
        return ((AbstractSiteUI) UI.getCurrent()).getSite();
    }

    @Override
    public final Flow getFlow() {
        return flow;
    }

    @Override
    public final void setFlow(final Flow flow) {
        this.flow = flow;
    }

    @Override
    public final void attach() {
        super.attach();
        this.setSizeFull();

        rootLayout = new GridLayout(1, 2);
        rootLayout.setMargin(false);
        rootLayout.setSpacing(true);
        rootLayout.setSizeFull();
        rootLayout.setRowExpandRatio(0, 0f);
        rootLayout.setRowExpandRatio(1, 1f);

        final HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setMargin(new MarginInfo(true, false, true, false));
        titleLayout.setSpacing(true);

        final Embedded titleIcon = new Embedded(null, getSite().getIcon("view-icon-" + getFlowletKey()));
        titleIcon.setWidth(32, Unit.PIXELS);
        titleIcon.setHeight(32, Unit.PIXELS);
        titleLayout.addComponent(titleIcon);

        final Label titleLabel = new Label("<h1>" + getSite().localize("view-" + getFlowletKey()) + "</h1>", ContentMode.HTML);
        titleLayout.addComponent(titleLabel);
        rootLayout.addComponent(titleLayout, 0, 0);

        setCompositionRoot(rootLayout);

        initialize();
    }

    /**
     * Initialize the flow.
     */
    protected abstract void initialize();

    /**
     * True if flow content is valid.
     * @return true if flow content is valid.
     */
    protected abstract boolean isValid();

    /**
     * Set the flow content.
     * @param component the component to set as content.
     */
    public final void setViewContent(final Component component) {
        rootLayout.addComponent(component, 0, 1);
    }
}
