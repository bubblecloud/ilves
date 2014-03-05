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
package org.vaadin.addons.sitekit.model.wiki;

import org.vaadin.addons.sitekit.model.Company;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Content.
 *
 * @author Tommi S.E. Laukkanen
 */
@Entity
@Table(name = "content", uniqueConstraints = { @UniqueConstraint(columnNames = { "owner_companyid", "name" }) })
public final class Content implements Serializable {
    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;
    /** Unique UUID of the entity. */

    @Id
    @GeneratedValue(generator = "uuid")
    private String contentId;

    /** Owning company. */
    @JoinColumn(nullable = false)
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
    private Company owner;

    /** Name. */
    @Column(nullable = false)
    private String name;

    /** Description. */
    @Column(nullable = false)
    private String description;

    /** Markup. */
    @Column(nullable = false)
    private String markup;

    /** Markup Type. */
    @Column(nullable = false)
    private MarkupType markupType;

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
    public Content() {
        super();
    }

    /**
     * @return the contentId
     */
    public String getContentId() {
        return contentId;
    }

    /**
     * @param contentId the contentId to set
     */
    public void setContentId(final String contentId) {
        this.contentId = contentId;
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the markup
     */
    public String getMarkup() {
        return markup;
    }

    /**
     * @param markup the markup to set
     */
    public void setMarkup(String markup) {
        this.markup = markup;
    }

    /**
     * @return the markup type
     */
    public MarkupType getMarkupType() {
        return markupType;
    }

    /**
     * @param markupType the markup type to set
     */
    public void setMarkupType(MarkupType markupType) {
        this.markupType = markupType;
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

    @Override
    public String toString() {
        return description;
    }

    @Override
    public int hashCode() {
        return contentId.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof Content && contentId.equals(((Content) obj).getContentId());
    }

}
