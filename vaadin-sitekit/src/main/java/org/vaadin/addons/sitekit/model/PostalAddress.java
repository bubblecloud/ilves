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

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * PostalAddress.
 *
 * @author Tommi S.E. Laukkanen
 */
@Entity
@Table(name = "postaladdress")
public final class PostalAddress implements Serializable {
    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;

	/** Unique UUID of the entity. */
    @Id
    @GeneratedValue(generator = "uuid")
    private String postalAddressId;

	/** Address line one. */
	private String addressLineOne;
    /** Address line two. */
    private String addressLineTwo;
    /** Address line three. */
    private String addressLineThree;
    /** City. */
    private String city;
    /** Postal code. */
    private String postalCode;
    /** Country. */
    private String country;

    /**
     * Default constructor for JPA.
     */
    public PostalAddress() {
        super();
    }

    /**
     * Constructor which allows initializing address values.
     * @param addressLineOne Address line one.
     * @param addressLineTwo Address line two.
     * @param addressLineThree Address line three.
     * @param city City.
     * @param postalCode Postal code.
     * @param country Country.
     */
    public PostalAddress(final String addressLineOne, final String addressLineTwo, final String addressLineThree,
            final String city, final String postalCode, final String country) {
        super();
        this.addressLineOne = addressLineOne;
        this.addressLineTwo = addressLineTwo;
        this.addressLineThree = addressLineThree;
        this.city = city;
        this.postalCode = postalCode;
        this.country = country;
    }

    /**
     * @return the postalAddressId
     */
    public String getPostalAddressId() {
        return postalAddressId;
    }

    /**
     * @param postalAddressId the postalAddressId to set
     */
    public void setPostalAddressId(final String postalAddressId) {
        this.postalAddressId = postalAddressId;
    }

    /**
     * @return the addressLineOne
     */
    public String getAddressLineOne() {
        return addressLineOne;
    }

    /**
     * @param addressLineOne the addressLineOne to set
     */
    public void setAddressLineOne(final String addressLineOne) {
        this.addressLineOne = addressLineOne;
    }

    /**
     * @return the addressLineTwo
     */
    public String getAddressLineTwo() {
        return addressLineTwo;
    }

    /**
     * @param addressLineTwo the addressLineTwo to set
     */
    public void setAddressLineTwo(final String addressLineTwo) {
        this.addressLineTwo = addressLineTwo;
    }

    /**
     * @return the addressLineThree
     */
    public String getAddressLineThree() {
        return addressLineThree;
    }

    /**
     * @param addressLineThree the addressLineThree to set
     */
    public void setAddressLineThree(final String addressLineThree) {
        this.addressLineThree = addressLineThree;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(final String city) {
        this.city = city;
    }

    /**
     * @return the postalCode
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @param postalCode the postalCode to set
     */
    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(final String country) {
        this.country = country;
    }

}
