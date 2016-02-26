package org.bubblecloud.ilves.util;

import javax.servlet.http.HttpServletResponse;

/**
 * Utility for web security methods.
 * @author Tommi S.E. Laukkanen
 */
public class WebSecurityUtil {
    public static void setSecurityHeaders(final HttpServletResponse response) {
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("X-Frame-Options", "deny");
    }
}
