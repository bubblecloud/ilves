package org.bubblecloud.ilves.rpc;

import org.apache.log4j.Logger;
import org.bubblecloud.ilves.site.SiteContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * API invocation handler which checks access grants.
 *
 * @author Tommi S.E. Laukkanen
 */
public class ApiInvocationHandler implements InvocationHandler {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(ApiInvocationHandler.class);
    /** The site context. */
    private SiteContext context;
    /** The instance. */
    private final Object instance;

    /**
     * Constructor for setting site context and instance to the invokation handler.
     * @param context the context
     * @param instance the instance
     */
    public ApiInvocationHandler(final SiteContext context, final Object instance) {
        this.context = context;
        this.instance = instance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final long startTimeMillis = System.currentTimeMillis();
        try {
            final AccessGrant accessGrant = method.getAnnotation(AccessGrant.class);

            if (accessGrant == null) {
                LOGGER.warn(method.getDeclaringClass().getSimpleName() + "." + method.getName()
                        + " missing access control annotation.");
                throw new SecurityException("access_denied");
            }

            if (accessGrant.roles().length > 0) {
                boolean roleAccessGranted = false;
                for (final String role : accessGrant.roles()) {
                    if (context.getRoles().contains(role)) {
                        roleAccessGranted = true;
                        continue;
                    }
                }
                if (!roleAccessGranted) {
                    LOGGER.warn(method.getDeclaringClass().getSimpleName() + "." + method.getName()
                            + " access denied.");
                    throw new SecurityException("access_denied");
                }
            }

            return method.invoke(instance, args);

        } catch (final Throwable t) {
            LOGGER.error("API call caused unhandled exception: " + method.getName(), t);
            throw t;
        } finally {
            LOGGER.debug("API: " + method.getDeclaringClass().getSimpleName() + "." + method.getName() + " " + (System.currentTimeMillis() - startTimeMillis) + " ms.");
        }
    }

}
