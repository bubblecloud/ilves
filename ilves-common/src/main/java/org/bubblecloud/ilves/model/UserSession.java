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

import org.eclipse.persistence.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * EmailPasswordReset.
 *
 * @author Tommi S.E. Laukkanen
 */
@Entity
@Table(name = "usersession")
public final class UserSession implements Serializable {
    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;

    /** Unique UUID of the entity. */
    @Id
    @GeneratedValue(generator = "uuid")
    private String userSessionId;

    @Index(unique = true)
    @Column(nullable = false)
    private String sessionIdHash;

    @Index(unique = true)
    @Column(nullable = false)
    private String loginTransactionIdHash;

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
    public UserSession() {
        super();
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getSessionIdHash() {
        return sessionIdHash;
    }

    public void setSessionIdHash(String sessionIdHash) {
        this.sessionIdHash = sessionIdHash;
    }

    public String getLoginTransactionIdHash() {
        return loginTransactionIdHash;
    }

    public void setLoginTransactionIdHash(String loginTransactionIdHash) {
        this.loginTransactionIdHash = loginTransactionIdHash;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(String userSessionId) {
        this.userSessionId = userSessionId;
    }

    @Override
    public String toString() {
        return "User session for: " + user.toString() + " started at " + created;
    }

    @Override
    public int hashCode() {
        return userSessionId.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof Group && userSessionId.equals(((Group) obj).getGroupId());
    }
}
