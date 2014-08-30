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

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import java.io.Serializable;
import java.util.Date;

/**
 * Feedback.
 *
 * @author Tommi S.E. Laukkanen
 */
@Entity
@Table(name = "feedback")
public final class Feedback implements Serializable {
    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;
    /** Unique UUID of the entity. */

    @Id
    @GeneratedValue(generator = "uuid")
    private String feedbackId;

    /** Owning company. */
    @JsonIgnore
    @JoinColumn(nullable = false)
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
    private Company owner;

    /** Title. */
    @Column(nullable = false)
    private String title;

    /** Title. */
    @Column(length = 2048, nullable = false)
    private String description;

    /** Email address. */
    @Column(nullable = false)
    private String emailAddress;

    /** First name. */
    @Column(nullable = true)
    private String firstName;

    /** Last name. */
    @Column(nullable = true)
    private String lastName;

    /** Company name. */
    @Column(nullable = true)
    private String organizationName;

    /** Company name. */
    @Column(nullable = true)
    private Integer organizationSize;

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
    public Feedback() {
        super();
    }

    public Feedback(Company owner, Date created, Date modified) {
        this.owner = owner;
        this.created = created;
        this.modified = modified;
    }

    /**
     * @return the feedbackId
     */
    public String getFeedbackId() {
        return feedbackId;
    }

    /**
     * @return the owning company
     */
    public Company getOwner() {
        return owner;
    }

    /**
     * @return the feedback title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the feedback title
     */
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * @return the feedback description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the feedback description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * @param emailAddress the emailAddress
     */
    public void setEmailAddress(final String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName
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
     * @param lastName the lastName
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the organizationName
     */
    public String getOrganizationName() {
        return organizationName;
    }

    /**
     * @param organizationName the organizationName
     */
    public void setOrganizationName(final String organizationName) {
        this.organizationName = organizationName;
    }

    /**
     * @return the organizationSite
     */
    public Integer getOrganizationSize() {
        return organizationSize;
    }

    /**
     * @param organizationSize the organizationSite
     */
    public void setOrganizationSize(final Integer organizationSize) {
        this.organizationSize = organizationSize;
    }

    /**
     * @return the created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @return the modified
     */
    public Date getModified() {
        return modified;
    }

    @Override
    public String toString() {
        return emailAddress;
    }

    @Override
    public int hashCode() {
        return feedbackId != null ? feedbackId.hashCode() : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof Feedback && feedbackId.equals(((Feedback) obj).feedbackId);
    }

}
