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

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * EmailPasswordReset.
 *
 * @author Tommi S.E. Laukkanen
 */
@Entity
@Table(name = "emailpasswordreset")
public final class EmailPasswordReset implements Serializable {
    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;

    /** Unique UUID of the entity. */
    @Id
    @GeneratedValue(generator = "uuid")
    private String emailPasswordResetId;

    /** Code of the product or service sold. */
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
    private User user;

    /** Pin hash required for changing the password. */
    @Column(nullable = false)
    private String pinHash;


    /** Created time of the task. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date created;

    /**
     * The default constructor.
     */
    public EmailPasswordReset() {
        super();
    }

    /**
     * Constructor for new group membership.
     * @param group the group
     * @param user the user
     */
    public EmailPasswordReset(final Group group, final User user) {
        super();
        this.user = user;
        this.created = new Date();
    }

    /**
     * @return the emailPasswordResetId
     */
    public String getEmailPasswordResetId() {
        return emailPasswordResetId;
    }

    /**
     * @param emailPasswordResetId the emailPasswordResetId to set
     */
    public void setEmailPasswordResetId(final String emailPasswordResetId) {
        this.emailPasswordResetId = emailPasswordResetId;
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
     * @return the pin hash
     */
    public String getPinHash() {
        return pinHash;
    }

    /**
     * @param pinHash the pin hash to set
     */
    public void setPinHash(String pinHash) {
        this.pinHash = pinHash;
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
        return "Email password reset for: " + user.toString();
    }

    @Override
    public int hashCode() {
        return emailPasswordResetId.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof Group && emailPasswordResetId.equals(((Group) obj).getGroupId());
    }
}
