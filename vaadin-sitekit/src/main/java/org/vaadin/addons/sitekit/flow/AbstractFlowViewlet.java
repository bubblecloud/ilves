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
package org.vaadin.addons.sitekit.flow;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.vaadin.addons.sitekit.site.AbstractViewlet;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

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
    private HorizontalLayout bottomLayout;
    /** The bottom layout. */
    private HorizontalLayout topLayout;

    @Override
    public final void attach() {
        super.attach();

        final GridLayout layout = new GridLayout(1, 3);
        layout.setSizeFull();
        this.setCompositionRoot(layout);
        layout.setRowExpandRatio(1, 1.0f);
        layout.setMargin(false);
        layout.setSpacing(true);

        topLayout = new HorizontalLayout();
        layout.addComponent(topLayout, 0, 0);

        topBackButton = new Button(getSite().localize("button-back"));
        topBackButton.setEnabled(false);
        topBackButton.addListener(this);
        topLayout.addComponent(topBackButton);

        topPathLabel = new Label("", Label.CONTENT_XHTML);

        topLayout.addComponent(topPathLabel);
        topLayout.setComponentAlignment(topPathLabel, Alignment.MIDDLE_LEFT);

        bottomLayout = new HorizontalLayout();
        layout.addComponent(bottomLayout, 0, 2);

        bottomBackButton = new Button(getSite().localize("button-back"));
        bottomBackButton.setEnabled(false);
        bottomBackButton.addListener(this);
        bottomLayout.addComponent(bottomBackButton);

        bottomPathLabel = new Label("", Label.CONTENT_XHTML);

        bottomLayout.addComponent(bottomPathLabel);
        bottomLayout.setComponentAlignment(bottomPathLabel, Alignment.MIDDLE_LEFT);

        tabSheet = new TabSheet();
        tabSheet.setStyleName("flow-sheet");
        tabSheet.hideTabs(true);
        tabSheet.setSizeFull();
        layout.addComponent(tabSheet, 0, 1);

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
        topPathLabel.setValue("&nbsp;&nbsp;&nbsp;" + pathLabelBuilder.toString());
        bottomPathLabel.setValue("&nbsp;&nbsp;&nbsp;" + pathLabelBuilder.toString());
        topBackButton.setEnabled(viewPath.size() > 1);
        bottomBackButton.setEnabled(viewPath.size() > 1);
    }

    /**
     * Implementations override this class to construct their flows.
     */
    protected abstract void addFlowlets();

    @Override
    public final void addFlowlet(final Flowlet flowlet) {
        flowlet.setFlowSheet(this);
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
            Notification.show("Please save or discard changes.", Notification.TYPE_WARNING_MESSAGE);
            return null;
        }
    }

    @Override
    public final void buttonClick(final ClickEvent event) {
        back();
    }

    @Override
    public final void enter(final String parameters) {
        /*for (final Flowlet view : flows.values()) {
            view.enter();
        }*/
        viewPath.getLast().enter();
    }

}
