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
package org.vaadin.addons.sitekit.site;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Abstract base class for site view implementations.
 * @author Tommi S.E. Laukkanen
 */
public abstract class AbstractSiteView extends GridLayout implements View, SiteView {

    /** Default version UID. */
    private static final long serialVersionUID = 1L;
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(AbstractSiteView.class);
    /** The site this flow belongs to. */
    private Site site;
    /** The page descriptor represented by this site flow. */
    private ViewDescriptor page;
    /** The page version presented by this site flow. */
    private ViewVersion pageVersion;
    /** The site viewlet descriptors. */
    private final Map<String, ViewletDescriptor> slotViewletDescriptorMap = new HashMap<String, ViewletDescriptor>();
    /** The site components. */
    private final Map<String, AbstractComponent> slotComponentMap = new HashMap<String, AbstractComponent>();

    /**
     * Default constructor.
     */
    public AbstractSiteView() {
        super();
        this.site = ((AbstractSiteUI) UI.getCurrent()).getSite();
    }

    /**
     * {@inheritDoc}
     */
    public final void setViewVersion(final ViewVersion viewVersion) {
        this.pageVersion = viewVersion;
        for (final ViewletDescriptor viewletDescriptor : viewVersion.getViewletDescriptors()) {
            slotViewletDescriptorMap.put(viewletDescriptor.getSlot(), viewletDescriptor);
        }

        if (viewVersion.getMasterViewName() != null) {
            final ViewVersion masterPageVersion = site.getCurrentViewVersion(viewVersion.getMasterViewName());
            if (masterPageVersion != null) {
                for (final ViewletDescriptor viewletDescriptor : masterPageVersion.getViewletDescriptors()) {
                    if (slotViewletDescriptorMap.get(viewletDescriptor.getSlot()) == null) {
                        slotViewletDescriptorMap.put(viewletDescriptor.getSlot(), viewletDescriptor);
                    }
                }
            }
        }
    }

    /**
     * @return the pageVersion
     */
    public final ViewVersion getViewVersion() {
        return pageVersion;
    }

    /**
     * @return the page
     */
    public final ViewDescriptor getViewDescriptor() {
        return page;
    }

    /**
     * @param page the page to set
     */
    public final void setViewDescriptor(final ViewDescriptor page) {
        this.page = page;
    }

    /**
     * Instantiates component for given slot.
     * @param slot The slot component will be placed to.
     * @return The instantiated component
     */
    protected final AbstractComponent getComponent(final String slot) {
        try {
            final ViewletDescriptor viewletDescriptor = slotViewletDescriptorMap.get(slot);
            if (viewletDescriptor != null) {
                final Class<?> componentClass = Class.forName(viewletDescriptor.getComponentClass());
                final AbstractComponent component = (AbstractComponent) componentClass.newInstance();
                //component.setDescription(viewletDescriptor.getDescription());
                if (component instanceof Viewlet) {
                    ((Viewlet) component).setViewletDescriptor(viewletDescriptor);
                }
                slotComponentMap.put(slot, component);
                return component;
            } else {
                final Panel panel = new Panel();
                panel.setContent(new Label(slot));
                return panel;
            }
        } catch (final Exception e) {
            throw new SiteException("Error instantiating viewlet for page: " + pageVersion.getTitle()
                    + " version: " + pageVersion.getVersion() + " slot: "
                    + slot, e);
        }
    }

    /**
     * Initialize window components.
     */
    protected abstract void initializeComponents();

    @Override
    public final void initialize() {
        initializeComponents();
    }

    @Override
    public final void enter(final ViewChangeListener.ViewChangeEvent event) {
        LOGGER.debug("View enter: " + event.getViewName() + " (" + this.getViewDescriptor().getName()
                + "." + this.getViewVersion().getVersion() + ") parameters: " + event.getParameters());
        UI.getCurrent().getPage().setTitle(site.localize(getViewVersion().getTitle()));

        if (pageVersion.getViewerRoles().length > 0) {
            boolean roleMatch = false;
            for (final String role : pageVersion.getViewerRoles()) {
                if ("anonymous".equals(role)) {
                    roleMatch = true;
                    break;
                }
                if (site.getSecurityProvider().getRoles().contains(role)) {
                    roleMatch = true;
                    break;
                }
            }
            if (!roleMatch) {
                UI.getCurrent().getNavigator().navigateTo("login");
                return;
            }
        }

        for (final AbstractComponent component : slotComponentMap.values()) {
            if (component instanceof Viewlet) {
                ((Viewlet) component).enter(event.getParameters());
            }
        }
    }

}
