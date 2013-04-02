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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

/**
 * Customer.
 *
 * @author Tommi S.E. Laukkanen
 */
@Entity
@Table(name = "customer")
public final class Customer implements Serializable {
    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;
    /** Unique UUID of the entity. */
    @Id
    @GeneratedValue(generator = "uuid")
    private String customerId;

    /** Owning company. */
    @JoinColumn(nullable = false)
    @ManyToOne(cascade = { CascadeType.DETACH, CascadeType.MERGE, CascadeType.REFRESH }, optional = false)
    private Company owner;

    /** First name. */
    @Column(nullable = false)
    private String firstName;

    /** Last name. */
    @Column(nullable = false)
    private String lastName;

    /** Email address. */
    @Column(nullable = false)
    private String emailAddress;

    /** Phone number. */
    @Column(nullable = false)
    private String phoneNumber;

    /** Is company. */
    @Column(nullable = false)
    private boolean company;

    /** Name. */
    @Column(nullable = true)
    private String companyName;

    /** Code. */
    @Column(nullable = true)
    private String companyCode;

    /** Created time of the task. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date created;

    /** Created time of the task. */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date modified;

    /** Billing address. */
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private PostalAddress invoicingAddress;

    /** Delivery address. */
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private PostalAddress deliveryAddress;

    /**
     * The default constructor for JPA.
     */
    public Customer() {
        super();
    }

    /**
     * @param firstName The first name of primary contact person.
     * @param lastName The last name of primary contact person.
     * @param emailAddress The email address of primary contact person.
     * @param phoneNumber The phone number of primary contact person.
     * @param company True if customer is company.
     * @param companyName The company name.
     * @param companyCode The company code.
     */
    public Customer(final String firstName, final String lastName, final String emailAddress, final String phoneNumber, final boolean company,
                    final String companyName, final String companyCode) {
        super();
        this.firstName = firstName;
        this.lastName = lastName;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.company = company;
        this.companyName = companyName;
        this.companyCode = companyCode;
        this.created = new Date();
        this.modified = this.created;
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
     * @return the customerId
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * @param customerId the customerId to set
     */
    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
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
     * @return the company
     */
    public boolean isCompany() {
        return company;
    }

    /**
     * @param company the company to set
     */
    public void setCompany(final boolean company) {
        this.company = company;
    }

    /**
     * @return the companyName
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * @param companyName the companyName to set
     */
    public void setCompanyName(final String companyName) {
        this.companyName = companyName;
    }

    /**
     * @return the companyCode
     */
    public String getCompanyCode() {
        return companyCode;
    }

    /**
     * @param companyCode the companyCode to set
     */
    public void setCompanyCode(final String companyCode) {
        this.companyCode = companyCode;
    }

    /**
     * @return the invoicingAddress
     */
    public PostalAddress getInvoicingAddress() {
        return invoicingAddress;
    }

    /**
     * @param invoicingAddress the invoicingAddress to set
     */
    public void setInvoicingAddress(final PostalAddress invoicingAddress) {
        this.invoicingAddress = invoicingAddress;
    }

    /**
     * @return the deliveryAddress
     */
    public PostalAddress getDeliveryAddress() {
        return deliveryAddress;
    }

    /**
     * @param deliveryAddress the deliveryAddress to set
     */
    public void setDeliveryAddress(final PostalAddress deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
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
        if (company) {
            return companyName + " (" + lastName + " " + firstName + ")";
        } else {
            return lastName + " " + firstName;
        }
    }

    @Override
    public int hashCode() {
        return customerId.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof Customer && customerId.equals(((Customer) obj).getCustomerId());
    }

}
