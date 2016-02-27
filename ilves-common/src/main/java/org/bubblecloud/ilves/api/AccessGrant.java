package org.bubblecloud.ilves.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for granting roles access to an API method.
 *
 * @author Tommi S.E. Laukkanen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessGrant {
    String[] roles();
}