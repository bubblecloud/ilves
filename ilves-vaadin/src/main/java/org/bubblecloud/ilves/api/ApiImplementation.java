package org.bubblecloud.ilves.api;

import org.bubblecloud.ilves.site.SiteContext;

/**
 * Common interface for API implementations
 *
 * @author Tommi S.E. Laukkanen
 */
public interface ApiImplementation {
    /**
     * Sets the site context.
     * @param context the site context
     */
    void setContext(final SiteContext context);
}
