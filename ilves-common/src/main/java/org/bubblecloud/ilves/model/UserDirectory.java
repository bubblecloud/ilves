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

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * User directory.
 *
 * @author Tommi S.E. Laukkanen
 */
@Entity
@Table(name = "userdirectory", uniqueConstraints = { @UniqueConstraint(columnNames = { "owner_companyid", "address", "port" }) })
public final class UserDirectory implements Serializable {
    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;

    /** Unique UUID of the entity. */
    @Id
    @GeneratedValue(generator = "uuid")
    private String userDirectoryId;

    /** Owning company. */
    @JsonIgnore
    @JoinColumn(nullable = false)
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
    private Company owner;

    /** LDAP address. */
    @Column(nullable = false)
    private String address;

    /** LDAP port. */
    @Column(nullable = false)
    private int port;

    /**
     * LDAP login DN.
     */
    @Column(nullable = false)
    private String loginDn = "uid=admin,ou=system";

    /**
     * LDAP login password.
     */
    @JsonIgnore
    @Column(nullable = false)
    private String loginPassword = "password";

    /**
     * LDAP user email attribute.
     */
    @Column(nullable = false)
    private String userEmailAttribute = "sn";

    /**
     * LDAP user search base DN.
     */
    @Column(nullable = false)
    private String userSearchBaseDn = "ou=users,ou=system";

    /**
     * LDAP group search base DN.
     */
    @Column(nullable = false)
    private String groupSearchBaseDn = "ou=groups,ou=system";

    /**
     * Mapping from remote LDAP groups to local user groups.
     *
     * Format: a=x,y=b,c=d
     */
    @Column(nullable = false)
    private String remoteLocalGroupMapping;

    /** Required remote group for login. */
    @Column(nullable = false)
    private String requiredRemoteGroup;
    /**
     * Allowed subnets to login from.
     *
     * Format: XXX.XXX.XXX.XXX/YYY, XXX.XXX.XXX.XXX/YYY
     */
    @Column(nullable = false)
    private String subNetWhiteList;

    /** Enabled. */
    @Column(nullable = false)
    private boolean enabled;

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
    public UserDirectory() {
        super();
    }

    /**
     * @return the user directory ID
     */
    public String getUserDirectoryId() {
        return userDirectoryId;
    }

    /**
     * @param userDirectoryId the user directory ID to set
     */
    public void setUserDirectoryId(final String userDirectoryId) {
        this.userDirectoryId = userDirectoryId;
    }

    /**
     * @return the owning company
     */
    public Company getOwner() {
        return owner;
    }

    /**
     * @param owner the owning company to set
     */
    public void setOwner(final Company owner) {
        this.owner = owner;
    }

    /**
     * @return the LDAP address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the LDAP address to set
     */
    public void setAddress(final String address) {
        this.address = address;
    }

    /**
     * @return the LDAP port
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port the LDAP port to set
     */
    public void setPort(final int port) {
        this.port = port;
    }

    /**
     * @return the LDAP login DN
     */
    public String getLoginDn() {
        return loginDn;
    }

    /**
     * @param loginDn the LDAP login DN to set
     */
    public void setLoginDn(final String loginDn) {
        this.loginDn = loginDn;
    }

    /**
     * @return the LDAP login password
     */
    public String getLoginPassword() {
        return loginPassword;
    }

    /**
     * @param loginPassword the LDAP login password to set
     */
    public void setLoginPassword(final String loginPassword) {
        this.loginPassword = loginPassword;
    }

    /**
     * @return the LDAP user email attribute
     */
    public String getUserEmailAttribute() {
        return userEmailAttribute;
    }

    /**
     * @param userEmailAttribute the LDAP user email attribute to set
     */
    public void setUserEmailAttribute(final String userEmailAttribute) {
        this.userEmailAttribute = userEmailAttribute;
    }

    /**
     * @return the LDAP user search base DN
     */
    public String getUserSearchBaseDn() {
        return userSearchBaseDn;
    }

    /**
     * @param userSearchBaseDn the LDAP user search base DN to set
     */
    public void setUserSearchBaseDn(final String userSearchBaseDn) {
        this.userSearchBaseDn = userSearchBaseDn;
    }

    /**
     * @return the LDAP group search base DN
     */
    public String getGroupSearchBaseDn() {
        return groupSearchBaseDn;
    }

    /**
     * @param groupSearchBaseDn the LDAP group search base DN to set
     */
    public void setGroupSearchBaseDn(final String groupSearchBaseDn) {
        this.groupSearchBaseDn = groupSearchBaseDn;
    }

    /**
     * Gets allowed sub nets to login from.
     *
     * Format: XXX.XXX.XXX.XXX/YYY, XXX.XXX.XXX.XXX/YYY
     * @return the sub net white list
     */
    public String getSubNetWhiteList() {
        return subNetWhiteList;
    }

    /**
     * Sets allowed sub nets to login from.
     *
     * Format: XXX.XXX.XXX.XXX/YYY, XXX.XXX.XXX.XXX/YYY
     * @param subNetWhiteList the sub net white list to set
     */
    public void setSubNetWhiteList(final String subNetWhiteList) {
        this.subNetWhiteList = subNetWhiteList;
    }

    /**
     * @return the required remote group for login
     */
    public String getRequiredRemoteGroup() {
        return requiredRemoteGroup;
    }

    /**
     * @param requiredRemoteGroup the required remote group for login to set
     */
    public void setRequiredRemoteGroup(final String requiredRemoteGroup) {
        this.requiredRemoteGroup = requiredRemoteGroup;
    }

    /**
     * Gets the mapping from remote LDAP groups to local user groups.
     *
     * Format: a=x,y=b,c=d
     * @return the remote to local group mapping
     */
    public String getRemoteLocalGroupMapping() {
        return remoteLocalGroupMapping;
    }

    /**
     * Sets the mapping from remote LDAP groups to local user groups.
     *
     * Format: a=x,y=b,c=d
     * @param remoteLocalGroupMapping the remote to local group mapping to set
     */
    public void setRemoteLocalGroupMapping(final String remoteLocalGroupMapping) {
        this.remoteLocalGroupMapping = remoteLocalGroupMapping;
    }

    /**
     * @return flag reflecting whether user directory is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled true to enabled the user directory
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
        return address + " " + port;
    }

    @Override
    public int hashCode() {
        return userDirectoryId != null ? userDirectoryId.hashCode() : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof UserDirectory
                && userDirectoryId.equals(((UserDirectory) obj).getUserDirectoryId());
    }

}
