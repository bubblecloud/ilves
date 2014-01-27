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

/**
 * Viewlet descriptor.
 * @author Tommi S.E. Laukkanen
 */
public final class ViewletDescriptor {
    /** The page slot. */
    private String slot;
    /** The viewlet caption. */
    private String caption;
    /** The viewlet description. */
    private String description;
    /** The viewlet configuration. */
    private Object configuration;
    /** The viewlet component class. */
    private String componentClass;
    /**
     * @param slot The page slot.
     * @param caption The viewlet caption.
     * @param description The viewlet description.
     * @param configuration The viewlet configuration.
     * @param componentClass The viewlet component class.
     */
    public ViewletDescriptor(final String slot, final String caption,
                            final String description, final Object configuration, final String componentClass) {
        super();
        this.slot = slot;
        this.caption = caption;
        this.description = description;
        this.configuration = configuration;
        this.componentClass = componentClass;
    }
    /**
     * @return the slot
     */
    public String getSlot() {
        return slot;
    }
    /**
     * @param slot the slot to set
     */
    public void setSlot(final String slot) {
        this.slot = slot;
    }
    /**
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }
    /**
     * @param caption the caption to set
     */
    public void setCaption(final String caption) {
        this.caption = caption;
    }
    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    /**
     * @param description the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }
    /**
     * @return the configuration
     */
    public <T> T getConfiguration() {
        return (T) configuration;
    }
    /**
     * @param configuration the configuration to set
     */
    public void setConfiguration(final String configuration) {
        this.configuration = configuration;
    }
    /**
     * @return the componentClass
     */
    public String getComponentClass() {
        return componentClass;
    }
    /**
     * @param componentClass the componentClass to set
     */
    public void setComponentClass(final String componentClass) {
        this.componentClass = componentClass;
    }

}
