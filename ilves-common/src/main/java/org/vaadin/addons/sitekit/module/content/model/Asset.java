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
package org.vaadin.addons.sitekit.module.content.model;

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
@Table(name = "asset", uniqueConstraints = { @UniqueConstraint(columnNames = { "owner_companyid", "name" }) })
public final class Asset implements Serializable {
    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;
    /** Unique UUID of the entity. */

    @Id
    @GeneratedValue(generator = "uuid")
    private String assetId;

    /** Owning company. */
    @JoinColumn(nullable = false)
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
    private Company owner;

    /** File name. */
    @Column(nullable = false)
    private String name;

    /** File extension. */
    @Column(nullable = false)
    private String extension;

    /** Mime type. */
    @Column(nullable = false)
    private String type;

    /** File size. */
    @Column(nullable = false)
    private Integer size = 0;

    /** Description. */
    @Column(nullable = false)
    private String description;

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
    public Asset() {
        super();
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public Company getOwner() {
        return owner;
    }

    public void setOwner(Company owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    @Override
    public String toString() {
        return name + "." + extension + "(" + size + ")";
    }

    @Override
    public int hashCode() {
        return assetId.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof Asset && assetId.equals(((Asset) obj).getAssetId());
    }

}
