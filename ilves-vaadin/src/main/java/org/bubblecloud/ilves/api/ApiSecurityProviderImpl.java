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
package org.bubblecloud.ilves.api;

import org.bubblecloud.ilves.model.Group;
import org.bubblecloud.ilves.model.User;
import org.bubblecloud.ilves.site.SecurityProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Security provider session implementation. Security provider should also
 * include methods for login and this one should be converted to database security provider
 * by moving database logic from LoginFlowlet to here.
 *
 * @author Tommi S.E. Laukkanen
 *
 */
public final class ApiSecurityProviderImpl implements SecurityProvider {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Singleton list. */
    private static final List<String> ANONYMOUS_ROLES = Collections.singletonList("anonymous");

    /** All available roles. */
    private final List<String> availableRoles;

    User user = null;

    List<Group> groups = new ArrayList<>();

    List<String> roles = null;

    /**
     * Constructor which allows setting of the available application roles
     * matched against users roles.
     * @param availableRoles List of available application roles.
     */
    public ApiSecurityProviderImpl(final List<String> availableRoles) {
        this.availableRoles = availableRoles;
    }

    /**
     * Constructor which allows setting of the available application roles
     * matched against users roles.
     * @param availableRoles List of available application roles.
     */
    public ApiSecurityProviderImpl(final String... availableRoles) {
        this.availableRoles = Arrays.asList(availableRoles);
    }

    @Override
    public String getUserId() {
        if (user != null) {
            return user.getUserId();
        } else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUser() {
        if (user != null) {
            return user.getEmailAddress();
        } else {
            return null;
        }
    }

    /**
     * Gets user.
     * @return the user or null.
     */
    public User getUserObject() {
        return user;
    }

    /**
     * Sets user.
     * @param user the user
     */
    private void setUser(final User user) {
        this.user = user;
    }

    /**
     * Gets groups.
     * @return the groups or null.
     */
    private List<Group> getGroups() {
        return groups;
    }

    /**
     * Sets groups.
     * @param groups the groups
     */
    private void setGroups(final List<Group> groups) {
        this.groups = groups;
    }

    /**
     * Sets roles.
     * @param roles the roles
     */
    private void setRoles(final List<String> roles) {
        this.roles = roles;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getRoles() {
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
        setUser(user);
        setGroups(groups);
        final List<String> roles = new ArrayList<String>();
        for (final Group group : groups) {
            final String roleNameCandidate = group.getName().toLowerCase();
            if (availableRoles.contains(roleNameCandidate)) {
                roles.add(roleNameCandidate);
            }
        }
        setRoles(roles);
    }

}
