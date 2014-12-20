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

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet.Tab;
import org.bubblecloud.ilves.site.AbstractViewlet;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Viewlet which contains flowlets.
 *
 * @author Tommi S.E. Laukkanen
 */
public abstract class AbstractFlowViewlet extends AbstractViewlet implements Flow, ClickListener {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The default flow. */
    private Flowlet rootView = null;
    /** The flow path which user has browsed open. */
    private final LinkedList<Flowlet> viewPath = new LinkedList<Flowlet>();
    /** The flows added to this Flow. */
    private final Map<Class<?>, Flowlet> views = new HashMap<Class<?>, Flowlet>();
    /** The tabs added to this Flow. */
    private final Map<Class<?>, Tab> tabs = new HashMap<Class<?>, Tab>();
    /** Top path label. */
    private Label topPathLabel;
    /** Bottom path label. */
    private Label bottomPathLabel;
    /** Top back button. */
    private Button topBackButton;
    /** Bottom back button. */
    private Button bottomBackButton;
    /** The tab sheet containing flows. */
    private TabSheet tabSheet;
    /** The top layout. */
    private CssLayout bottomLayout;
    /** The bottom layout. */
    private CssLayout topLayout;
    /** The top layout. */
    private CssLayout bottomRightLayout;
    /** The bottom layout. */
    private CssLayout topRightLayout;


    @Override
    public final void attach() {
        super.attach();

        setStyleName("ui-content");

        final CssLayout layout = new CssLayout();
        layout.setSizeFull();
        this.setCompositionRoot(layout);

        topLayout = new CssLayout();
        topLayout.setStyleName("flow-top");

        topBackButton = new Button(getSite().localize("button-back"));
        topBackButton.addClickListener(this);
        topLayout.addComponent(topBackButton);

        topPathLabel = new Label("", ContentMode.HTML);
        topPathLabel.setSizeUndefined();

        topLayout.addComponent(topPathLabel);

        topRightLayout = new CssLayout();
        topLayout.addComponent(topRightLayout);
        topLayout.setWidth(100, Unit.PERCENTAGE);

        bottomLayout = new CssLayout();
        bottomLayout.setStyleName("flow-bottom");

        bottomBackButton = new Button(getSite().localize("button-back"));
        bottomBackButton.addClickListener(this);
        bottomLayout.addComponent(bottomBackButton);

        bottomPathLabel = new Label("", ContentMode.HTML);
        bottomPathLabel.setSizeUndefined();

        bottomLayout.addComponent(bottomPathLabel);

        bottomRightLayout = new CssLayout();
        bottomLayout.addComponent(bottomRightLayout);
        bottomLayout.setWidth(100, Unit.PERCENTAGE);


        tabSheet = new TabSheet();
        tabSheet.setStyleName("flow-sheet");
        tabSheet.hideTabs(true);
        tabSheet.setSizeFull();

        layout.addComponent(topLayout);
        layout.addComponent(tabSheet);
        layout.addComponent(bottomLayout);

        addFlowlets();

        tabSheet.setSelectedTab((Component) getRootFlowlet());
    }

    /**
     * Refreshes path labels.
     */
    public final void refreshPathLabels() {
        topLayout.setVisible(views.size() > 1);
        bottomLayout.setVisible(views.size() > 1);
        final StringBuilder pathLabelBuilder = new StringBuilder();
        for (final Flowlet view : viewPath) {
            if (pathLabelBuilder.length() != 0) {
                pathLabelBuilder.append(" > ");
            }
            pathLabelBuilder.append(getSite().localize("view-" + view.getFlowletKey()));
        }
        topPathLabel.setValue(pathLabelBuilder.toString());
        bottomPathLabel.setValue(pathLabelBuilder.toString());
        topLayout.setVisible(viewPath.size() > 1 || topRightLayout.getComponentCount() > 0);
        bottomLayout.setVisible(viewPath.size() > 1 || bottomRightLayout.getComponentCount() > 0);
        topPathLabel.setVisible(viewPath.size() > 1);
        bottomPathLabel.setVisible(viewPath.size() > 1);
        topBackButton.setVisible(viewPath.size() > 1);
        bottomBackButton.setVisible(viewPath.size() > 1);
    }

    /**
     * Implementations override this class to construct their flows.
     */
    protected abstract void addFlowlets();

    @Override
    public final void addFlowlet(final Flowlet flowlet) {
        flowlet.setFlow(this);
        views.put(flowlet.getClass(), flowlet);
        final Tab tab = tabSheet.addTab((Component) flowlet);
        tabs.put(flowlet.getClass(), tab);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T extends Flowlet> T getFlowlet(final Class<?> flowletClass) {
        return (T) views.get(flowletClass);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T extends Flowlet> T getRootFlowlet() {
        return (T) rootView;
    }

    @Override
    public final void setRootFlowlet(final Flowlet flowlet) {
        this.rootView = flowlet;
        viewPath.clear();
        viewPath.addLast(flowlet);
        refreshPathLabels();
    }

    @Override
    public final <T extends Flowlet> T forward(final Class<?> flowletClass) {
        @SuppressWarnings("unchecked")
        final T view = (T) views.get(flowletClass);
        viewPath.addLast(view);
        refreshPathLabels();
        tabSheet.setSelectedTab((Component) view);
        view.enter();
        return view;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T extends Flowlet> T back() {
        if (!viewPath.getLast().isDirty()) {
            viewPath.removeLast();
            final Flowlet view = viewPath.getLast();
            refreshPathLabels();
            view.enter();
            tabSheet.setSelectedTab((Component) view);
            return (T) view;
        } else {
            Notification.show("Please save or discard changes.", Notification.Type.WARNING_MESSAGE);
            return null;
        }
    }

    @Override
    public final void buttonClick(final ClickEvent event) {
        back();
    }

    @Override
    public final void enter(final String parameters) {
        viewPath.getLast().enter();
    }

    /**
     * Gets top layout.
     * @return the top layout.
     */
    public CssLayout getTopRightLayout() {
        return topRightLayout;
    }

    /**
     * Gets bottom layout.
     * @return the bottom layout.
     */
    public CssLayout getBottomRightLayout() {
        return bottomRightLayout;
    }
}
