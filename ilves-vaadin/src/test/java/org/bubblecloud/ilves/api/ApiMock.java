package org.bubblecloud.ilves.api;

/**
 * Created by tlaukkan on 2/26/2016.
 */
public interface ApiMock {
    @AccessGrant(roles = {"user"})
    String testMethod(String value);
}
