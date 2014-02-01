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

import junit.framework.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Unit test for NavigationTreeParser class.
 */
public class NavigationTreeParserTest {

    /**
     * Tests navigation tree parsing and formatting.
     */
    @Test
    public void testParsingAndFormatting() {
        final String originalNavigationString = "a;#aa;#ab;##aba;b;c;#ca";
        final Map<String, List<String>> navigationMap = NavigationTreeParser.parse(originalNavigationString);

        Assert.assertEquals("[a, b, c]", navigationMap.get(NavigationTreeParser.ROOTS).toString());
        Assert.assertEquals("[aa, ab]", navigationMap.get("a").toString());
        Assert.assertEquals("[aba]", navigationMap.get("ab").toString());
        Assert.assertEquals("[ca]", navigationMap.get("c").toString());
        Assert.assertEquals(4, navigationMap.size());

        final String formattedNavigationString = NavigationTreeParser.format(navigationMap);
        Assert.assertEquals(originalNavigationString, formattedNavigationString);
    }

}
