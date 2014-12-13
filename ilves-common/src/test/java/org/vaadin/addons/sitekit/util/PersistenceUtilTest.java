package org.vaadin.addons.sitekit.util;

import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Test;

/**
 * Created by tlaukkan on 12/13/2014.
 */
public class PersistenceUtilTest {

    @Test
    @Ignore
    public void testDiff() throws Exception {
        final String diff = PersistenceUtil.diff("site", "site");
        System.out.println(diff);
    }
}
