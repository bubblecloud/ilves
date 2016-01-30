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
package org.bubblecloud.ilves.site;

import org.bubblecloud.ilves.security.SecurityContext;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * The security context.
 *
 * @author Tommi S.E. Laukkanen
 */
public class SiteContext extends SecurityContext {

    private SecurityProvider securityProvider;

    /**
     * Constructor which sets entity managers, HTTP request and security provider for the site context.
     * @param entityManager the entity manager
     * @param auditEntityManager the audit entity manager
     * @param request the request
     * @param securityProvider the security provider
     */
    public SiteContext(EntityManager entityManager,
                           EntityManager auditEntityManager,
                           HttpServletRequest request,
                           SecurityProvider securityProvider) {
        super(entityManager, auditEntityManager, request);
        this.securityProvider = securityProvider;
    }

    @Override
    public List<String> getRoles() {
        return securityProvider.getRoles();
    }

    @Override
    public String getUserName() {
        return securityProvider.getUser();
    }

    @Override
    public String getUserId() {
        return securityProvider.getUserId();
    }

    public SecurityProvider getSecurityProvider() {
        return securityProvider;
    }
}
