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
package org.vaadin.addons.sitekit.site;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * The session context containing application specific objects with UI life
 * time.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class SiteContext extends SecurityContext {

    /** The object map. */
    private final Map<Object, Object> serviceMap = new HashMap<Object, Object>();

    public SiteContext(EntityManager entityManager, EntityManager auditEntityManager, HttpServletRequest request,
                       SecurityProvider securityProvider) {
        super(entityManager, auditEntityManager, request, securityProvider);
    }

    /**
     * Gets object which exists once per session.
     *
     * @param <T> The type of the object.
     * @param objectKey the object key
     * @return the service class singleton instance.
     */
    @SuppressWarnings({ "unchecked" })
    public <T> T getObject(final Object objectKey) {
        return (T) serviceMap.get(objectKey);
    }

    /**
     * Puts object which exists once per session.
     *
     * @param <T> The type of the object.
     * @param objectKey the object key
     * @param object the object
     */
    public <T> void putObject(final Object objectKey, final T object) {
        serviceMap.put(objectKey, object);
    }
}
