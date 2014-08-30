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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.User;
import com.vaadin.ui.UI;
import org.vaadin.addons.sitekit.module.audit.AuditService;

/**
 * Security provider session implementation. Security provider should also
 * include methods for login and this one should be converted to database security provider
 * by moving database logic from LoginFlowlet to here.
 *
 * @author Tommi S.E. Laukkanen
 *
 */
public final class SecurityProviderSessionImpl implements SecurityProvider {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Singleton list. */
    private static final List<String> ANONYMOUS_ROLES = Collections.singletonList("anonymous");

    /** All available roles. */
    private final List<String> availableRoles;

    /**
     * Constructor which allows setting of the available application roles
     * matched against users roles.
     * @param availableRoles List of available application roles.
     */
    public SecurityProviderSessionImpl(final List<String> availableRoles) {
        this.availableRoles = availableRoles;
    }

    /**
     * Constructor which allows setting of the available application roles
     * matched against users roles.
     * @param availableRoles List of available application roles.
     */
    public SecurityProviderSessionImpl(final String... availableRoles) {
        this.availableRoles = Arrays.asList(availableRoles);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUser() {
        final User user = getUserFromSession();
        if (user != null) {
            return user.getEmailAddress();
        } else {
            return null;
        }
    }

    /**
     * Gets user from session.
     * @return the user or null.
     */
    public User getUserFromSession() {
        return (User) ((AbstractSiteUI) UI.getCurrent()).getSession().getAttribute("user");
    }

    /**
     * Sets user to session.
     * @param user the user
     */
    private void setUserToSession(final User user) {
        ((AbstractSiteUI) UI.getCurrent()).getSession().setAttribute("user", user);
    }

    /**
     * Gets groups from session.
     * @return the groups or null.
     */
    private List<Group> getGroupsFromSession() {
        return (List<Group>) ((AbstractSiteUI) UI.getCurrent()).getSession().getAttribute("groups");
    }

    /**
     * Sets groups to session.
     * @param groups the groups
     */
    private void setGroupsToSession(final List<Group> groups) {
        ((AbstractSiteUI) UI.getCurrent()).getSession().setAttribute("groups", groups);
    }


    /**
     * Gets roles from session.
     * @return the roles or null.
     */
    private List<String> getRolesFromSession() {
        return (List<String>) ((AbstractSiteUI) UI.getCurrent()).getSession().getAttribute("roles");
    }

    /**
     * Sets roles to session.
     * @param roles the roles
     */
    private void setRolesToSession(final List<String> roles) {
        ((AbstractSiteUI) UI.getCurrent()).getSession().setAttribute("roles", roles);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getRoles() {
        final List<String> roles = getRolesFromSession();
        if (roles != null) {
            return roles;
        } else {
            return ANONYMOUS_ROLES;
        }
    }

    /**
     * Sets the user.
     * @param user the user
     * @param groups the groups
     */
    public void setUser(final User user, final List<Group> groups) {
        setUserToSession(user);
        setGroupsToSession(groups);
        final List<String> roles = new ArrayList<String>();
        for (final Group group : groups) {
            final String roleNameCandidate = group.getName().toLowerCase();
            if (availableRoles.contains(roleNameCandidate)) {
                roles.add(roleNameCandidate);
            }
        }
        setRolesToSession(roles);
    }

}
