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
package org.vaadin.addons.sitekit.cache;

import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.Privilege;
import org.vaadin.addons.sitekit.model.User;

import javax.persistence.EntityManager;
import java.util.*;

/**
 * Cache for privileges
 * @author Tommi S.E. Laukkanen
 */
public class PrivilegeCache {
    /** The cached group privileges. */
    private static Map<Company, InMemoryCache<Group, Map<String, Set<String>>>> groupPrivileges =
            new HashMap<Company, InMemoryCache<Group, Map<String, Set<String>>>>();

    /** The cached user privileges. */
    private static Map<Company, InMemoryCache<User, Map<String, Set<String>>>> userPrivileges =
            new HashMap<Company, InMemoryCache<User, Map<String, Set<String>>>>();

    public static synchronized void flush(final Company company) {
        groupPrivileges.remove(company);
        userPrivileges.remove(company);
    }



    public static synchronized void load(final EntityManager entityManager, final Company company, final Group group) {
        if (!groupPrivileges.get(company).containsKey(group)) {
            groupPrivileges.get(company).put(group, new HashMap<String, Set<String>>());
        }
        final List<Privilege> privileges = UserDao.getGroupPrivileges(entityManager, group);
        for (final Privilege privilege : privileges) {
            if (!groupPrivileges.get(company).get(group).containsKey(privilege.getKey())) {
                groupPrivileges.get(company).get(group).put(privilege.getKey(), new HashSet<String>());
            }
            groupPrivileges.get(company).get(group).get(privilege.getKey()).add(privilege.getDataId());
        }
    }

    public static synchronized void load(final EntityManager entityManager, final Company company, final User user) {
        if (!userPrivileges.get(company).containsKey(user)) {
            userPrivileges.get(company).put(user, new HashMap<String, Set<String>>());
        }
        final List<Privilege> privileges = UserDao.getUserPrivileges(entityManager, user);
        for (final Privilege privilege : privileges) {
            if (!userPrivileges.get(company).get(user).containsKey(privilege.getKey())) {
                userPrivileges.get(company).get(user).put(privilege.getKey(), new HashSet<String>());
            }
            userPrivileges.get(company).get(user).get(privilege.getKey()).add(privilege.getDataId());
        }
    }

    public static  synchronized boolean hasPrivilege(final EntityManager entityManager, final Company company,
                                                     final Group group, final String key, final String dataId) {
        if (!groupPrivileges.containsKey(company)) {
            groupPrivileges.put(company, new InMemoryCache<Group, Map<String, Set<String>>>(
                    5 * 60 * 1000, 60 * 1000, 100
            ));
        }
        if (!groupPrivileges.get(company).containsKey(group)) {
            load(entityManager, company, group);
        }
        if (!groupPrivileges.get(company).get(group).containsKey(key)) {
            return false;
        }
        return groupPrivileges.get(company).get(group).get(key).contains(dataId);
    }

    public static  synchronized boolean hasPrivilege(final EntityManager entityManager, final Company company,
                                                     final User user, final String key, final String dataId) {
        if (!userPrivileges.containsKey(company) || !userPrivileges.containsKey(company)) {
            userPrivileges.put(company, new InMemoryCache<User, Map<String, Set<String>>>(
                    5 * 60 * 1000, 60 * 1000, 1000
            ));
        }
        if (!userPrivileges.get(company).containsKey(user)) {
            load(entityManager, company, user);
            return false;
        }
        if (!userPrivileges.get(company).get(user).containsKey(key)) {
            return false;
        }
        return userPrivileges.get(company).get(user).get(key).contains(dataId);
    }

    public static  synchronized boolean hasPrivilege(final EntityManager entityManager, final Company company,
                                                     final User user, final List<Group> groups, final String key,
                                                     final String dataId) {
        if (user != null) {
            if (PrivilegeCache.hasPrivilege(entityManager, company, user, key, dataId)) {
                return true;
            } else {
                for (final Group group : groups) {
                    if (PrivilegeCache.hasPrivilege(entityManager, company, group, key, dataId)) {
                        return true;
                    }
                }
                return false;
            }
        } else {
            final Group group = UserDao.getGroup(entityManager, company, "anonymous");
            if (PrivilegeCache.hasPrivilege(entityManager, company, group, key, dataId)) {
                return true;
            } else {
                return false;
            }
        }
    }
}
