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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import java.io.Serializable;
import java.util.Date;

/**
 * User.
 *
 * @author Tommi S.E. Laukkanen
 */
@Entity
@Table(name = "user_", uniqueConstraints = { @UniqueConstraint(columnNames = { "owner_companyid", "emailAddress" }) })
public final class User implements Serializable {
    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;
    /** Unique UUID of the entity. */

    @Id
    @GeneratedValue(generator = "uuid")
    private String userId;

    /** Owning company. */
    @JoinColumn(nullable = false)
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
    private Company owner;

    /** Email address. */
    @Column(nullable = false)
    private String emailAddress;

    /** Email address validated. */
    @Column(nullable = false)
    private boolean emailAddressValidated;

    /** Password hash. */
    @Column(nullable = false)
    private String passwordHash;

    /** First name. */
    @Column(nullable = false)
    private String firstName;

    /** Last name. */
    @Column(nullable = false)
    private String lastName;

    /** Phone number. */
    @Column(nullable = false)
    private String phoneNumber;

    /** Failed login count. */
    @Column(nullable = false)
    private int failedLoginCount;

    /** Flag reflecting whether user has been locked out. */
    @Column(nullable = false)
    private boolean lockedOut;

    /** Open ID identifier. */
    @Column(nullable = true)
    private String openIdIdentifier;

    /** Created time of the task. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date created;

    /** Created time of the task. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date modified;

    /**
     * The default constructor for JPA.
     */
    public User() {
        super();
    }

    /**
     * @param owner the owner
     * @param firstName The first name of primary contact person.
     * @param lastName The last name of primary contact person.
     * @param emailAddress The email address of primary contact person.
     * @param phoneNumber The phone number of primary contact person.
     * @param passwordHash The password hash.
     */
    public User(final Company owner, final String firstName, final String lastName, final String emailAddress, final String phoneNumber,
            final String passwordHash) {
        super();
        this.owner = owner;
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.created = new Date();
        this.modified = this.created;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(final String userId) {
        this.userId = userId;
    }

    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @param emailAddress the emailAddress to set
     */
    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * @return is email address validated
     */
    public boolean isEmailAddressValidated() {
        return emailAddressValidated;
    }

    /**
     * @param emailAddressValidated is email address validated
     */
    public void setEmailAddressValidated(final boolean emailAddressValidated) {
        this.emailAddressValidated = emailAddressValidated;
    }

    /**
     * @return the passwordHash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * @param passwordHash the passwordHash to set
     */
    public void setPasswordHash(final String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * @return the owner
     */
    public Company getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(final Company owner) {
        this.owner = owner;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber the phoneNumber to set
     */
    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return the failed login count
     */
    public int getFailedLoginCount() {
        return failedLoginCount;
    }

    /**
     * @param failedLoginCount the failed login count to set
     */
    public void setFailedLoginCount(final int failedLoginCount) {
        this.failedLoginCount = failedLoginCount;
    }

    /**
     * @return the locked out state
     */
    public boolean isLockedOut() {
        return lockedOut;
    }

    /**
     * @param lockedOut the locked out state to set
     */
    public void setLockedOut(boolean lockedOut) {
        this.lockedOut = lockedOut;
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

    /**
     * @return the modified
     */
    public Date getModified() {
        return modified;
    }

    /**
     * @param modified the modified to set
     */
    public void setModified(final Date modified) {
        this.modified = modified;
    }

    /**
     * @return the OpenID identifier
     */
    public String getOpenIdIdentifier() {
        return openIdIdentifier;
    }

    /**
     * @param openIdIdentifier the OpenID identifier to set
     */
    public void setOpenIdIdentifier(final String openIdIdentifier) {
        this.openIdIdentifier = openIdIdentifier;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof User && userId.equals(((User) obj).getUserId());
    }

}
