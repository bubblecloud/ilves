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
package org.vaadin.addons.sitekit.util;

import org.vaadin.addons.sitekit.site.SiteException;

import java.util.*;

/**
 * Utility class for parsing navigation tree.
 *
 * @author Tommi S.E. Laukkanen
 */
public class NavigationTreeParser {
    /** The list of root nodes in the navigation map. */
    public static final String ROOTS = "roots";

    /**
     * Parses tree navigation to map from format: "a;#aa;#ab;##aba;b;c;#ca".
     *
     * List of roots can be accessed with ROOTS key from the resulting map.
     *
     * @param tree the navigation tree presented as String
     *
     * @return map of parent node name and child node name lists.
     */
    public static Map<String, List<String>> parse(final String tree) {
        final Map<String, List<String>> treeMap = new HashMap<String, List<String>>();
        final LinkedList<String> parentStack = new LinkedList<String>();

        final String[] nodes = tree.split(";");

        String parent = ROOTS;
        String previousNodeName = null;
        for (int i = 0; i < nodes.length; i++) {
            final int depth = parentStack.size();
            final String node = nodes[i];
            final int nodeDepth = node.lastIndexOf('#') + 2;
            final String nodeName = node.substring(nodeDepth - 1);

            if (depth + 1 == nodeDepth) {
                // Child of current parent
            } else if (depth + 2 == nodeDepth) {
                // Child of previous node
                if (previousNodeName == null) {
                    throw new SiteException("Invalid navigation tree hierarchy: " + tree);
                }
                parentStack.push(parent);
                parent = previousNodeName;
            } else if (depth >= nodeDepth) {
                // Child of some earlier parent in stack
                while (parentStack.size() + 1 != nodeDepth) {
                    parent = parentStack.pop();
                }
            } else {
                throw new SiteException("Invalid navigation tree hierarchy: " + tree);
            }

            if (!treeMap.containsKey(parent)) {
                treeMap.put(parent, new ArrayList<String>());
            }
            treeMap.get(parent).add(nodeName);
            previousNodeName = nodeName;
        }
        return treeMap;
    }

    /**
     * Formats the navigation string as map.
     *
     * @param navigationMap
     * @return
     */
    public static String format(final Map<String, List<String>> navigationMap) {
        final StringBuilder tree = new StringBuilder();
        format(ROOTS, -1, navigationMap, tree);
        return tree.toString();
    }

    private static void format(final String parent, final int depth, final Map<String, List<String>> navigationMap,
                               final StringBuilder tree) {
        if (depth >= 0) {
            if (tree.length() > 0) {
                tree.append(';');
            }
            for (int i = 0; i < depth; i++) {
                tree.append('#');
            }
            tree.append(parent);
        }
        if (!navigationMap.containsKey(parent)) {
            return;
        }
        for (final String child : navigationMap.get(parent)) {
            format(child, depth + 1, navigationMap, tree);
        }
    }
}
