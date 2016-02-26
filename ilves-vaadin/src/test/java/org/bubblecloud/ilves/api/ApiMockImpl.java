package org.bubblecloud.ilves.api;

import org.bubblecloud.ilves.site.SiteContext;

/**
 * Created by tlaukkan on 2/26/2016.
 */
public class ApiMockImpl implements ApiMock, ApiImplementation {

    private SiteContext context;

    @Override
    public void setContext(SiteContext context) {
        this.context = context;
    }

    @Override
    @AccessGrant(roles = {""})
    public String testMethod(final String value) {
        return this.context.getRoles() + ":" + value;
    }

}
