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
package org.bubblecloud.ilves.site;

import com.vaadin.navigator.View;
import com.vaadin.ui.Component;

/**
 * SiteView descriptor class.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class ViewDescriptor {
    /** The view name. */
    private String name;
    /** The view development version. */
    private ViewVersion developmentVersion;
    /** The view test version. */
    private ViewVersion testVersion;
    /** The view production version. */
    private ViewVersion productionVersion;

    /**
     * @param name The view name.
     * @param developmentVersion The view development version.
     * @param testVersion The view test version.
     * @param productionVersion The view production version.
     */
    public ViewDescriptor(final String name, final ViewVersion developmentVersion,
                          final ViewVersion testVersion, final ViewVersion productionVersion) {
        super();
        this.name = name;
        this.developmentVersion = developmentVersion;
        this.testVersion = testVersion;
        this.productionVersion = productionVersion;
    }

    /**
     * Constructor for setting view parameters with only production view.
     * @param name the view name
     * @param title the view title
     * @param viewClass the view class
     */
    public ViewDescriptor(final String name, final String title, final Class<? extends View> viewClass) {
        this.name = name;
        this.productionVersion = new ViewVersion(title, viewClass.getCanonicalName());
    }

    /**
     * Constructor for setting view parameters with only production view.
     * Sets title to be same as name.
     *
     * @param name the view name
     * @param viewClass the view class
     */
    public ViewDescriptor(final String name, final Class<? extends View> viewClass) {
        this.name = name;
        this.productionVersion = new ViewVersion("page-title-" + name, viewClass.getCanonicalName());
    }

    /**
     * Sets viewlet descriptor to production version.
     *
     * @param slot the viewlet slot
     * @param componentClass the viewlet component class
     */
    public void setViewletClass(final String slot, final Class<? extends Viewlet> componentClass) {
        final ViewletDescriptor viewletDescriptor = new ViewletDescriptor(
                slot, "", "", null,
                componentClass.getCanonicalName());
        this.productionVersion.getViewletDescriptors().add(viewletDescriptor);
    }

    /**
     * Sets Vaadin component as implementation to given slot.
     *
     * @param slot the slot
     * @param componentClass the Vaadin component class
     */
    public void setComponentClass(final String slot, final Class<? extends Component> componentClass) {
        final ViewletDescriptor viewletDescriptor = new ViewletDescriptor(
                slot, "", "", null,
                componentClass.getCanonicalName());
        this.productionVersion.getViewletDescriptors().add(viewletDescriptor);
    }

    /**
     * @param viewerRole the viewerRoles to production version.
     */
    public void setViewerRoles(final String... viewerRole) {
        this.productionVersion.setViewerRoles(viewerRole);
    }

    /**
     * Sets viewlet descriptor to production version.
     *
     * @param slot the viewlet slot
     * @param componentClass the viewlet component class
     * @param configuration the viewlet configuration
     */
    public void setViewletClass(final String slot, final Class<? extends Viewlet> componentClass,
                                final Object configuration) {
        final ViewletDescriptor viewletDescriptor = new ViewletDescriptor(
                slot, "", "", configuration,
                componentClass.getCanonicalName());
        this.productionVersion.getViewletDescriptors().add(viewletDescriptor);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @return the developmentVersion
     */
    public ViewVersion getDevelopmentVersion() {
        return developmentVersion;
    }
    /**
     * @param developmentVersion the developmentVersion to set
     */
    public void setDevelopmentVersion(final ViewVersion developmentVersion) {
        this.developmentVersion = developmentVersion;
    }
    /**
     * @return the testVersion
     */
    public ViewVersion getTestVersion() {
        return testVersion;
    }
    /**
     * @param testVersion the testVersion to set
     */
    public void setTestVersion(final ViewVersion testVersion) {
        this.testVersion = testVersion;
    }
    /**
     * @return the productionVersion
     */
    public ViewVersion getProductionVersion() {
        return productionVersion;
    }
    /**
     * @param productionVersion the productionVersion to set
     */
    public void setProductionVersion(final ViewVersion productionVersion) {
        this.productionVersion = productionVersion;
    }

}
