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

import org.bubblecloud.ilves.util.NavigationTreeParser;

import java.util.*;

/**
 * The navigation version.
 * @author Tommi S.E. Laukkanen
 */
public final class NavigationVersion {
    /** The navigation version number. */
    private int version;
    /** The name of the default page. */
    private String defaultPageName;
    /** The navigation hierarchy as map. */
    private Map<String, List<String>> navigationMap;
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
        if (tree != null && tree.length() > 0) {
            this.navigationMap = NavigationTreeParser.parse(tree);
        } else {
            this.navigationMap = new HashMap<String, List<String>>();
        }
        this.enabled = enabled;
    }

    /**
     * Default constructor for cloning.
     */
    public NavigationVersion() {
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
        return NavigationTreeParser.format(navigationMap);
    }

    /**
     * Sets the navigation tree in format: "a;#aa;#ab;##aba;b;c;#ca".
     * @param tree the tree to set
     */
    public void setTree(final String tree) {
        navigationMap = NavigationTreeParser.parse(tree);
    }

    /**
     * Gets list of root page names.
     * @return list of root page names
     */
    public List<String> getRootPages() {
        return Collections.unmodifiableList(navigationMap.get(NavigationTreeParser.ROOTS));
    }

    /**
     * Checks whether page with given name has child pages.
     * @param parentPage the parent page name
     * @return true if child pages exist.
     */
    public boolean hasChildPages(final String parentPage) {
        return navigationMap.containsKey(parentPage);
    }

    /**
     * Gets list of child page names.
     * @param parentPage the parent page name
     * @return list of child page names or null if no child pages exists.
     */
    public List<String> getChildPages(final String parentPage) {
        return Collections.unmodifiableList(navigationMap.get(parentPage));
    }

    /**
     * Adds root page as last to the root page list.
     * @param rootPage the root page name
     */
    public void addRootPage(final String rootPage) {
        addChildPage(NavigationTreeParser.ROOTS, rootPage);
    }

    /**
     * Adds root page at igven index.
     * @param index the index in the root page list where the new root page should be inserted at
     * @param rootPage the root page name
     */
    public void addRootPage(int index, final String rootPage) {
        addChildPage(NavigationTreeParser.ROOTS, index, rootPage);
    }

    /**
     * Adds root page after given root page.
     * @param previousPage the page after which this page should be added
     * @param rootPage the root page name
     */
    public void addRootPage(final String previousPage, final String rootPage) {
        addChildPage(NavigationTreeParser.ROOTS, previousPage, rootPage);
    }

    /**
     * Adds child page as last to the child page list.
     * @param parentPage the parent page name
     * @param childPage the child page name
     */
    public void addChildPage(final String parentPage, final String childPage) {
        if (!navigationMap.containsKey(parentPage)) {
            navigationMap.put(parentPage, new ArrayList<String>());
        }
        navigationMap.get(parentPage).add(childPage);
    }

    /**
     * Adds child page at given index in the child page list.
     * @param parentPage the parent page name
     * @param index the index in the child page list where the new child page should be inserted at
     * @param childPage the child page name
     */
    public void addChildPage(final String parentPage, int index, final String childPage) {
        if (!navigationMap.containsKey(parentPage)) {
            navigationMap.put(parentPage, new ArrayList<String>());
        }
        navigationMap.get(parentPage).add(index, childPage);
    }

    /**
     * Adds child page at given index in the child page list.
     * @param parentPage the parent page name
     * @param previousPage the page after which to add the pae
     * @param childPage the child page name
     */
    public void addChildPage(final String parentPage, final String previousPage, final String childPage) {
        if (!navigationMap.containsKey(parentPage)) {
            navigationMap.put(parentPage, new ArrayList<String>());
        }
        navigationMap.get(parentPage).add(navigationMap.get(parentPage).indexOf(previousPage) + 1, childPage);
    }

    /**
     * @return clone
     */
    public NavigationVersion clone() {
        final NavigationVersion clone = new NavigationVersion();
        clone.version = version;
        clone.defaultPageName = defaultPageName;
        clone.navigationMap = new HashMap<String, List<String>>();
        for (final String key : navigationMap.keySet()) {
            clone.navigationMap.put(key, new ArrayList<String>(navigationMap.get(key)));
        }
        clone.enabled = enabled;
        return clone;
    }
}
