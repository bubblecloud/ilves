package org.bubblecloud.ilves.api;

/**
 * Created by tlaukkan on 2/26/2016.
 */
public class ApiMockImpl implements ApiMock {

    @Override
    @AccessGrant(roles = {""})
    public String testMethod(final String value) {
        return value;
    }
}
