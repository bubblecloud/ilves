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
package org.bubblecloud.ilves.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * InvoiceElement.
 *
 * @author Tommi S.E. Laukkanen
 */
@Entity
@Table(name = "groupmember", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_userid", "group_groupid" }) })
public final class GroupMember implements Serializable {
    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;

    /** Unique UUID of the entity. */
    @Id
    @GeneratedValue(generator = "uuid")
    private String groupMemberId;

    /** Group. */
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
    private Group group;

    /** Code of the product or service sold. */
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
    private User user;

    /** Created time of the task. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date created;

    /**
     * The default constructor.
     */
    public GroupMember() {
        super();
    }

    /**
     * Constructor for new group membership.
     * @param group the group
     * @param user the user
     */
    public GroupMember(final Group group, final User user) {
        super();
        this.group = group;
        this.user = user;
        this.created = new Date();
    }

    /**
     * @return the groupMemberId
     */
    public String getGroupMemberId() {
        return groupMemberId;
    }

    /**
     * @param groupMemberId the groupMemberId to set
     */
    public void setGroupMemberId(final String groupMemberId) {
        this.groupMemberId = groupMemberId;
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
        return user.toString() + " member of " + group.toString();
    }

    @Override
    public int hashCode() {
        return groupMemberId.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof Group && groupMemberId.equals(((Group) obj).getGroupId());
    }
}
