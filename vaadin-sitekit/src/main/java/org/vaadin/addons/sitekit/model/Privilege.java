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
package org.vaadin.addons.sitekit.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Date;

/**
 * InvoiceElement.
 *
 * @author Tommi S.E. Laukkanen
 */
@Entity
@Table(name = "privilege", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_userid", "group_groupid" }) })
public final class Privilege implements Serializable {
    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;

    /** Unique UUID of the entity. */
    @Id
    @GeneratedValue(generator = "uuid")
    private String privilegeId;

    /** Group. */
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, optional = true)
    private Group group;

    /** Code of the product or service sold. */
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, optional = true)
    private User user;

    /** Key. */
    @Column(nullable = false)
    private String key;

    /** Data ID. */
    @Column(nullable = true)
    private String dataId;

    /** Created time of the task. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date created;

    /**
     * The default constructor.
     */
    public Privilege() {
        super();
    }

    /**
     * @param group the group
     * @param user the user
     * @param key the key
     * @param dataId the data ID
     */
    public Privilege(final Group group, final User user, final String key, final String dataId) {
        super();
        this.group = group;
        this.user = user;
        this.key = key;
        this.dataId = dataId;
        this.created = new Date();
    }

    /**
     * @return the privilegeId
     */
    public String getPrivilegeId() {
        return privilegeId;
    }

    /**
     * @param privilegeId the privilegeId to set
     */
    public void setPrivilegeId(final String privilegeId) {
        this.privilegeId = privilegeId;
    }

    /**
     * @return the group
     */
    public Group getGroup() {
        return group;
    }

    /**
     * @param group the group to set
     */
    public void setGroup(final Group group) {
        this.group = group;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(final User user) {
        this.user = user;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(final String key) {
        this.key = key;
    }

    /**
     * @return the dataId
     */
    public String getDataId() {
        return dataId;
    }

    /**
     * @param dataId the dataId to set
     */
    public void setDataId(final String dataId) {
        this.dataId = dataId;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(final Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        if (user != null) {
            return user.toString() + " " + key + " on " + dataId;
        } else {
            return group.toString() + " " + key + " on " + dataId;
        }
    }

    @Override
    public int hashCode() {
        return privilegeId.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof Group && privilegeId.equals(((Group) obj).getGroupId());
    }
}
