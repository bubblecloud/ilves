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

import java.util.ArrayList;
import java.util.List;

/**
 * The view version.
 * @author Tommi S.E. Laukkanen
 */
public final class ViewVersion {
    /** The view version number. */
    private int version;
    /** The parent view name. */
    private String masterViewName;
    /** The view title. */
    private String title;
    /** The view keywords. */
    private String keywords;
    /** The view description. */
    private String description;
    /** The view window class. */
    private String viewClass;
    /** The view viewer roles. */
    private String[] viewerRoles;
    /** The view viewlet descriptors. */
    private List<ViewletDescriptor> viewletDescriptors;
    /** Whether this is dynamic content. */
    private boolean dynamic = false;

    /**
     * Constructor for populating view version with proper values.
     * @param version The view version number.
     * @param masterViewName The name of the parent view or null.
     * @param title The view title.
     * @param keywords The view keywords.
     * @param description The view description.
     * @param viewClass The view class.
     * @param viewerRoles The view viewer roles.
     * @param viewletDescriptors The view descriptors.
     */
    public ViewVersion(final int version, final String masterViewName,
                       final String title, final String keywords, final String description,
                       final String viewClass, final String[] viewerRoles,
                       final List<ViewletDescriptor> viewletDescriptors) {
        super();
        this.version = version;
        this.masterViewName = masterViewName;
        this.title = title;
        this.keywords = keywords;
        this.description = description;
        this.viewClass = viewClass;
        this.viewerRoles = viewerRoles;
        this.viewletDescriptors = viewletDescriptors;
    }

    /**
     * Sets master to view name to default and version to 0 and sets no viewer role limitations i.e.
     * allows for anonymous access.
     * @param title The view title.
     * @param viewClass The view class.
     */
    public ViewVersion(String title, String viewClass) {
        this.version = 0;
        this.masterViewName = "master";
        this.title = title;
        this.keywords = "";
        this.description = "";
        this.viewClass = viewClass;
        this.viewerRoles = new String[]{};
        this.viewletDescriptors = new ArrayList<ViewletDescriptor>();
    }

    /**
     * @return the version
     */
    public int getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(final int version) {
        this.version = version;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the masterViewName
     */
    public String getMasterViewName() {
        return masterViewName;
    }

    /**
     * @param masterViewName the masterViewName to set
     */
    public void setMasterViewName(final String masterViewName) {
        this.masterViewName = masterViewName;
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
     * @return the viewClass
     */
    public String getViewClass() {
        return viewClass;
    }

    /**
     * @param viewClass the viewClass to set
     */
    public void setViewClass(final String viewClass) {
        this.viewClass = viewClass;
    }

    /**
     * @return the viewerRoles
     */
    public String[] getViewerRoles() {
        return viewerRoles;
    }

    /**
     * @param viewerRole the viewerRoles to set
     */
    public void setViewerRoles(final String... viewerRole) {
        this.viewerRoles = viewerRole;
    }

    /**
     * @return the viewComponents
     */
    public List<ViewletDescriptor> getViewletDescriptors() {
        return viewletDescriptors;
    }

    /**
     * @param viewletDescriptors the widgets to set
     */
    public void setViewletDescriptors(final List<ViewletDescriptor> viewletDescriptors) {
        this.viewletDescriptors = viewletDescriptors;
    }

    /**
     * @return true if is dynamic content
     */
    public boolean isDynamic() {
        return dynamic;
    }

    /**
     * @param dynamic true if is dynamic content
     */
    public void setDynamic(final boolean dynamic) {
        this.dynamic = dynamic;
    }
}
