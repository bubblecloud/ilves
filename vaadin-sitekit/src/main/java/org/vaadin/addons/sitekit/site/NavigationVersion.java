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
 * The navigation version.
 * @author Tommi S.E. Laukkanen
 */
public final class NavigationVersion {
    /** The navigation version number. */
    private int version;
    /** The name of the default page. */
    private String defaultPageName;
    /** The navigation tree. */
    private String tree;
    /** True if navigation is enabled. */
    private boolean enabled;

    /**
     * Constructor for populating navigation version with proper values.
     * @param version The navigation version number.
     * @param defaultPageName The name of the default page.
     * @param tree The navigation tree.
     * @param enabled True if navigation is enabled.
     */
    public NavigationVersion(final int version, final String defaultPageName,
            final String tree, final boolean enabled) {
        super();
        this.version = version;
        this.defaultPageName = defaultPageName;
        this.tree = tree;
        this.enabled = enabled;
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
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the defaultPageName
     */
    public String getDefaultPageName() {
        return defaultPageName;
    }

    /**
     * @param defaultPageName the defaultPageName to set
     */
    public void setDefaultPageName(final String defaultPageName) {
        this.defaultPageName = defaultPageName;
    }

    /**
     * Gets the navigation tree in format: "a;#aa;#ab;##aba;b;c;#ca".
     * @return the tree
     */
    public String getTree() {
        return tree;
    }

    /**
     * Sets the navigation tree in format: "a;#aa;#ab;##aba;b;c;#ca".
     * @param tree the tree to set
     */
    public void setTree(final String tree) {
        this.tree = tree;
    }

}
