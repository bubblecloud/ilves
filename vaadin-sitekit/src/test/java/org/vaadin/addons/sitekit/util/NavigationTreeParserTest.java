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
     * Tests navigation tree parsing.
     */
    @Test
    public void testParsing() {
        final Map<String, List<String>> navigationTreeMap = NavigationTreeParser.parse("a;#aa;#ab;##aba;b;c;#ca");

        Assert.assertEquals("[a, b, c]", navigationTreeMap.get(NavigationTreeParser.ROOTS).toString());
        Assert.assertEquals("[aa, ab]", navigationTreeMap.get("a").toString());
        Assert.assertEquals("[aba]", navigationTreeMap.get("ab").toString());
        Assert.assertEquals("[ca]", navigationTreeMap.get("c").toString());
        Assert.assertEquals(4, navigationTreeMap.size());
    }

}
