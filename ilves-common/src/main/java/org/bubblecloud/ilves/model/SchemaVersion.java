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
 * Database schema version.
 *
 * @author Tommi S.E. Laukkanen
 */
@Entity
@Table(name = "schemaversion")
public final class SchemaVersion implements Serializable {
    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;

    /** Created time. */
    @Id
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date created;

    /** Schema name. */
    @Column(nullable = false)
    private String schemaName;

    /** Schema version. */
    @Column(nullable = false)
    private String schemaVersion;

    /**
     * The default constructor for JPA.
     */
    public SchemaVersion() {
        super();
    }

    /**
     * @return schemaName
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * @param schemaName the schemaName
     */
    public void setSchemaName(final String schemaName) {
        this.schemaName = schemaName;
    }

    /**
     * @return schemaVersion
     */
    public String getSchemaVersion() {
        return schemaVersion;
    }

    /**
     * @param schemaVersion schemaVersion
     */
    public void setSchemaVersion(final String schemaVersion) {
        this.schemaVersion = schemaVersion;
    }

    /**
     * @return created
     */
    public Date getCreated() {
        return created;
    }

    /**
     * @param created the created
     */
    public void setCreated(final Date created) {
        this.created = created;
    }

    @Override
    public int hashCode() {
        return (schemaName + schemaVersion).hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof SchemaVersion
                && (schemaName + schemaVersion).equals(
                (((SchemaVersion) obj).getSchemaName() + ((SchemaVersion) obj).getSchemaVersion()));
    }

}
