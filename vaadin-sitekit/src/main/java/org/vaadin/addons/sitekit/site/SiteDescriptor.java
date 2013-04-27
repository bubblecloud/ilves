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

import java.util.List;

/**
 * The site collects pages and navigation together.
 * @author Tommi S.E. Laukkanen
 */
public final class SiteDescriptor {

    /** The site title. */
    private String title;
    /** The site keywords. */
    private String keywords;
    /** The site description. */
    private String description;
    /** The site navigation. */
    private NavigationDescriptor navigation;
    /** The site pages. */
    private List<ViewDescriptor> viewDescriptors;

    /**
     * @param title The site title.
     * @param keywords The site keywords.
     * @param description The site description.
     * @param navigation The site navigation.
     * @param viewDescriptors The site page descriptors.
     */
    public SiteDescriptor(final String title, final String keywords,
            final String description, final NavigationDescriptor navigation,
            final List<ViewDescriptor> viewDescriptors) {
        super();
        this.title = title;
        this.keywords = keywords;
        this.description = description;
        this.navigation = navigation;
        this.viewDescriptors = viewDescriptors;
    }
    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }
    /**
     * @param title the title to set
     */
    public void setTitle(final String title) {
        this.title = title;
    }
    /**
     * @return the keywords
     */
    public String getKeywords() {
        return keywords;
    }
    /**
     * @param keywords the keywords to set
     */
    public void setKeywords(final String keywords) {
        this.keywords = keywords;
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
     * @return the navigation
     */
    public NavigationDescriptor getNavigation() {
        return navigation;
    }
    /**
     * @param navigation the navigation to set
     */
    public void setNavigation(final NavigationDescriptor navigation) {
        this.navigation = navigation;
    }
    /**
     * @return the pages
     */
    public List<ViewDescriptor> getViewDescriptors() {
        return viewDescriptors;
    }
    /**
     * @param viewDescriptors the pages to set
     */
    public void setViewDescriptors(final List<ViewDescriptor> viewDescriptors) {
        this.viewDescriptors = viewDescriptors;
    }

}
