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

import org.eclipse.persistence.annotations.JoinFetch;
import org.eclipse.persistence.annotations.JoinFetchType;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Company.
 *
 * @author Tommi S.E. Laukkanen
 */
@Entity
@Table(name = "company")
public final class Company implements Serializable {
    /** Java serialization version UID. */
    private static final long serialVersionUID = 1L;
    /** Unique UUID of the entity. */
    @Id
    @GeneratedValue(generator = "uuid")
    private String companyId;

    /** Sales email address. */
    @Column(nullable = false)
    private String salesEmailAddress;

    /** Support email address. */
    @Column(nullable = false)
    private String supportEmailAddress;

    /** Invoicing email address which handles incoming and out going invoicing. */
    @Column(nullable = false)
    private String invoicingEmailAddress;

    /** Phone number. */
    @Column(nullable = false)
    private String phoneNumber;

    /** Name. */
    @Column(nullable = false)
    private String companyName;

    /** Terms and conditions. */
    @Column(nullable = true, length = 4096)
    private String termsAndConditions;

    /** Code. */
    @Column(nullable = false)
    private String companyCode;

    /** Host. */
    @Column(nullable = false)
    private String host;

    /** URL. */
    @Column(nullable = false)
    private String url;

    /** IBAN. */
    @Column(nullable = false)
    private String iban;

    /** BIC. */
    @Column(nullable = false)
    private String bic;

    /** Flag for enabling user self registration. */
    @Column(nullable = false)
    private boolean selfRegistration;

    /** Flag for enabling email password reset. */
    @Column(nullable = false)
    private boolean emailPasswordReset;

    /** Maximum failed login count. */
    @Column(nullable = true)
    private Integer maxFailedLoginCount;

    /** Flag for enabling open ID login. */
    @Column(nullable = false)
    private boolean openIdLogin;

    /** Flag for enabling client certificate login. */
    @Column(nullable = false)
    private boolean certificateLogin;

    /** Flag for enabling OAuth login. */
    @Column(nullable = false)
    private boolean oAuthLogin;

    /** Flag for enabling OAuth self registration. */
    @Column(nullable = false)
    private boolean oAuthSelfRegistration;

    /** GitHub client ID. */
    @Column(nullable = true)
    private String gitHubClientId;

    /** GitHub client secret. */
    @Column(nullable = true)
    private String gitHubClientSecret;

    /** Password validity period in days. 0 corresponds to password never expiring. */
    @Column(nullable = true)
    private int passwordValidityPeriodDays;

    /** Google analytics tracking code. */
    @Column(nullable = true)
    private String gaTrackingId;

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
    @JoinFetch(value = JoinFetchType.OUTER)
    private PostalAddress invoicingAddress;

    /** Delivery address. */
    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinFetch(value = JoinFetchType.OUTER)
    private PostalAddress deliveryAddress;



    /**
     * The default constructor for JPA.
     */
    public Company() {
        super();
    }

    /**
     * @param salesEmailAddress salesEmailAddress
     * @param supportEmailAddress supportEmailAddress
     * @param invoicingEmailAddress invoicingEmailAddress
     * @param phoneNumber phoneNumber
     * @param companyName companyName
     * @param termsAndConditions termsAndConditions
     * @param companyCode companyCode
     * @param host host
     * @param url url
     * @param iban iban
     * @param bic bic
     * @param invoicingAddress invoicingAddress
     * @param deliveryAddress deliveryAddress
     */
    public Company(final String salesEmailAddress, final String supportEmailAddress, final String invoicingEmailAddress, final String phoneNumber,
            final String companyName,
            final String termsAndConditions, final String companyCode, final String host, final String url, final String iban, final String bic,
            final PostalAddress invoicingAddress, final PostalAddress deliveryAddress) {
        super();
        this.salesEmailAddress = salesEmailAddress;
        this.supportEmailAddress = supportEmailAddress;
        this.invoicingEmailAddress = invoicingEmailAddress;
        this.phoneNumber = phoneNumber;
        this.companyName = companyName;
        this.termsAndConditions = termsAndConditions;
        this.companyCode = companyCode;
        this.host = host;
        this.url = url;
        this.iban = iban;
        this.bic = bic;
        this.invoicingAddress = invoicingAddress;
        this.deliveryAddress = deliveryAddress;
        this.created = new Date();
        this.modified = this.created;
    }

    /**
     * @return the companyId
     */
    public String getCompanyId() {
        return companyId;
    }

    /**
     * @param companyId the companyId to set
     */
    public void setCompanyId(final String companyId) {
        this.companyId = companyId;
    }

    /**
     * @return the termsAndConditions
     */
    public String getTermsAndConditions() {
        return termsAndConditions;
    }

    /**
     * @param termsAndConditions the termsAndConditions to set
     */
    public void setTermsAndConditions(final String termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    /**
     * @return the salesEmailAddress
     */
    public String getSalesEmailAddress() {
        return salesEmailAddress;
    }

    /**
     * @param salesEmailAddress the salesEmailAddress to set
     */
    public void setSalesEmailAddress(final String salesEmailAddress) {
        this.salesEmailAddress = salesEmailAddress;
    }

    /**
     * @return the supportEmailAddress
     */
    public String getSupportEmailAddress() {
        return supportEmailAddress;
    }

    /**
     * @param supportEmailAddress the supportEmailAddress to set
     */
    public void setSupportEmailAddress(final String supportEmailAddress) {
        this.supportEmailAddress = supportEmailAddress;
    }

    /**
     * @return the invoicingEmailAddress
     */
    public String getInvoicingEmailAddress() {
        return invoicingEmailAddress;
    }

    /**
     * @param invoicingEmailAddress the invoicingEmailAddress to set
     */
    public void setInvoicingEmailAddress(final String invoicingEmailAddress) {
        this.invoicingEmailAddress = invoicingEmailAddress;
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

    @Override
    public String toString() {
        return getCompanyName() + " (" + getCompanyCode() + ")";
    }

    /**
     * @return the iban
     */
    public String getIban() {
        return iban;
    }

    /**
     * @param iban the iban to set
     */
    public void setIban(final String iban) {
        this.iban = iban;
    }

    /**
     * @return the bic
     */
    public String getBic() {
        return bic;
    }

    /**
     * @param bic the bic to set
     */
    public void setBic(final String bic) {
        this.bic = bic;
    }

    /**
     * @return the host
     */
    public String getHost() {
        return host;
    }

    /**
     * @param host the host to set
     */
    public void setHost(final String host) {
        this.host = host;
    }

    /**
     * @return the site URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the site URL to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return true if self registration is enabled
     */
    public boolean isSelfRegistration() {
        return selfRegistration;
    }

    /**
     * @param selfRegistration true if self registration is to be enabled
     */
    public void setSelfRegistration(boolean selfRegistration) {
        this.selfRegistration = selfRegistration;
    }

    /**
     * @return true if email password reset is enabled
     */
    public boolean isEmailPasswordReset() {
        return emailPasswordReset;
    }

    /**
     * @param emailPasswordReset true if email password reset is to be enabled
     */
    public void setEmailPasswordReset(boolean emailPasswordReset) {
        this.emailPasswordReset = emailPasswordReset;
    }

    /**
     * @return the max failed login count
     */
    public Integer getMaxFailedLoginCount() {
        return maxFailedLoginCount;
    }

    /**
     * @param maxFailedLoginCount the max failed login count
     */
    public void setMaxFailedLoginCount(final Integer maxFailedLoginCount) {
        this.maxFailedLoginCount = maxFailedLoginCount;
    }

    /**
     * Gets the password validity period in days. 0 corresponds to password never expiring.
     * @return the password validity period in days
     */
    public int getPasswordValidityPeriodDays() {
        return passwordValidityPeriodDays;
    }

    /**
     * Sets the password validity period in days. 0 corresponds to password never expiring.
     * @param passwordValidityPeriodDays the password validity period in days.
     */
    public void setPasswordValidityPeriodDays(final int passwordValidityPeriodDays) {
        this.passwordValidityPeriodDays = passwordValidityPeriodDays;
    }

    /**
     * @return true if open ID login is enabled
     */
    public boolean isOpenIdLogin() {
        return openIdLogin;
    }

    /**
     * @param openIdLogin true if open ID login is to be enabled
     */
    public void setOpenIdLogin(boolean openIdLogin) {
        this.openIdLogin = openIdLogin;
    }

    /**
     * @return true if client certificate login is enabled
     */
    public boolean isCertificateLogin() {
        return certificateLogin;
    }

    /**
     * @param certificateLogin true if client certificate login is to be enabled
     */
    public void setCertificateLogin(final boolean certificateLogin) {
        this.certificateLogin = certificateLogin;
    }

    public String getGaTrackingId() {
        return gaTrackingId;
    }

    public void setGaTrackingId(String gaTrackingId) {
        this.gaTrackingId = gaTrackingId;
    }

    public String getGitHubClientId() {
        return gitHubClientId;
    }

    public void setGitHubClientId(String gitHubClientId) {
        this.gitHubClientId = gitHubClientId;
    }

    public String getGitHubClientSecret() {
        return gitHubClientSecret;
    }

    public void setGitHubClientSecret(String gitHubClientSecret) {
        this.gitHubClientSecret = gitHubClientSecret;
    }

    public boolean isoAuthLogin() {
        return oAuthLogin;
    }

    public void setoAuthLogin(boolean oAuthLogin) {
        this.oAuthLogin = oAuthLogin;
    }

    public boolean isoAuthSelfRegistration() {
        return oAuthSelfRegistration;
    }

    public void setoAuthSelfRegistration(boolean oAuthSelfRegistration) {
        this.oAuthSelfRegistration = oAuthSelfRegistration;
    }

    @Override
    public int hashCode() {
        return companyId.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof Company && companyId.equals(((Company) obj).getCompanyId());
    }

}
