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
package org.bubblecloud.ilves.site;

import com.vaadin.data.Validator;
import com.vaadin.data.util.converter.StringToIntegerConverter;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import org.bubblecloud.ilves.component.field.DatePartField;
import org.bubblecloud.ilves.component.field.GroupField;
import org.bubblecloud.ilves.component.field.TimestampField;
import org.bubblecloud.ilves.component.field.UserField;
import org.bubblecloud.ilves.component.formatter.CertificateConverter;
import org.bubblecloud.ilves.component.formatter.DatePartConverter;
import org.bubblecloud.ilves.component.formatter.TimestampConverter;
import org.bubblecloud.ilves.component.grid.FieldDescriptor;
import org.bubblecloud.ilves.exception.SiteException;
import org.bubblecloud.ilves.model.*;

import java.util.*;

/**
 * Default Site Kit field descriptors.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class SiteFields {
    /**
     * Flag reflecting whether initialization of field descriptors has been done
     * for JVM.
     */
    private static boolean initialized = false;

    /**
     * Map of entity class field descriptors.
     */
    private static Map<Class<?>, List<FieldDescriptor>> fieldDescriptors = new HashMap<Class<?>, List<FieldDescriptor>>();

    /**
     * Adds a field descriptor for given entity class.
     * @param entityClass The entity class.
     * @param fieldDescriptor The field descriptor to add.
     */
    public static void add(final Class<?> entityClass, final FieldDescriptor fieldDescriptor) {
        if (!fieldDescriptors.containsKey(entityClass)) {
            fieldDescriptors.put(entityClass, new ArrayList<FieldDescriptor>());
        }
        fieldDescriptors.get(entityClass).add(fieldDescriptor);
    }

    /**
     * Adds a field descriptor for given entity class.
     * @param entityClass The entity class.
     * @param fieldDescriptor The field descriptor to add.
     * @param validator The field validator.
     */
    public static void add(final Class<?> entityClass, final FieldDescriptor fieldDescriptor, final Validator validator) {
        fieldDescriptor.addValidator(validator);
        add(entityClass, fieldDescriptor);
    }

    /**
     * Gets field descriptors for given entity class.
     * @param entityClass The entity class.
     * @return an unmodifiable list of field descriptors.
     */
    public static List<FieldDescriptor> getFieldDescriptors(final Class<?> entityClass) {
        if (!fieldDescriptors.containsKey(entityClass)) {
            throw new SiteException("No fields defined for class: " + entityClass.getCanonicalName());
        }
        return Collections.unmodifiableList(fieldDescriptors.get(entityClass));
    }

    /**
     * Initialize field descriptors if not done yet.
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        initialized = true;

        SiteFields.add(Company.class, new FieldDescriptor("url", "Site URL", TextField.class, null, -1, null, String.class, "", false, true, true));
        SiteFields.add(Company.class, new FieldDescriptor("host", "Host Name", TextField.class, null, 300, null, String.class, "", false, true, true));
        SiteFields.add(Company.class, new FieldDescriptor("termsAndConditions", "Terms & Conditions", TextArea.class, null, 100, null, String.class, "", false,
                true, false));
        SiteFields.add(Company.class, new FieldDescriptor("companyName", "Company Name", TextField.class, null, -1, null, String.class, "", false, true, true));
        SiteFields.add(Company.class, new FieldDescriptor("companyCode", "Company Code", TextField.class, null, 100, null, String.class, "", false, true, true));
        SiteFields.add(Company.class, new FieldDescriptor("iban", "IBAN", TextField.class, null, 100, null, String.class, "", false, true, true));
        SiteFields.add(Company.class, new FieldDescriptor("bic", "BIC", TextField.class, null, 100, null, String.class, "", false, true, true));
        SiteFields.add(Company.class, new FieldDescriptor("phoneNumber", "Phone Number", TextField.class, null, 150, null, String.class, "", false, true, true));
        SiteFields.add(Company.class, new FieldDescriptor("salesEmailAddress", "Sales Email Address", TextField.class, null, 150, null, String.class, "", false,
                true, true), new EmailValidator("Email address is not valid."));
        SiteFields.add(Company.class, new FieldDescriptor("supportEmailAddress", "Support Email Address", TextField.class, null, 150, null, String.class, "",
                false, true, true), new EmailValidator("Email address is not valid."));
        SiteFields.add(Company.class, new FieldDescriptor("invoicingEmailAddress", "Invoicing Email Address", TextField.class, null, 150, null, String.class, "",
                false, true, true), new EmailValidator("Email address is not valid."));
        SiteFields.add(Company.class, new FieldDescriptor("selfRegistration", "Self Registration", CheckBox.class, null, 100, null, Boolean.class, false, false, true, false));
        SiteFields.add(Company.class, new FieldDescriptor("emailPasswordReset", "Email Password Reset", CheckBox.class, null, 100, null, Boolean.class, false, false, true, false));
        SiteFields.add(Company.class, new FieldDescriptor("maxFailedLoginCount", "Max Failed Logins", TextField.class, new StringToIntegerConverter(), 50, null, Integer.class, null,
                false, true, false).addValidator(new IntegerRangeValidator("Invalid value", 0, 10)));
        SiteFields.add(Company.class, new FieldDescriptor("passwordValidityPeriodDays", "Password Validity Period", TextField.class, new StringToIntegerConverter(), 50, null, Integer.class, null,
                false, true, true).addValidator(new IntegerRangeValidator("Invalid value", 0, 10)));
        SiteFields.add(Company.class, new FieldDescriptor("openIdLogin", "Open ID Login", CheckBox.class, null, 100, null, Boolean.class, false, false, true, false));
        SiteFields.add(Company.class, new FieldDescriptor("certificateLogin", "Certificate Login", CheckBox.class, null, 100, null, Boolean.class, false, false, true, false));
        SiteFields.add(Company.class, new FieldDescriptor("oAuthLogin", "OAuth Login", CheckBox.class, null, 100, null, Boolean.class, false, false, true, false));
        SiteFields.add(Company.class, new FieldDescriptor("oAuthSelfRegistration", "OAuth Self Registration", CheckBox.class, null, 100, null, Boolean.class, false, false, true, false));
        SiteFields.add(Company.class, new FieldDescriptor("gitHubClientId", "GitHub Client ID", TextField.class, null, -1, null, String.class, "", false, true, false));
        SiteFields.add(Company.class, new FieldDescriptor("gitHubClientSecret", "GitHub Client Secret", TextField.class, null, -1, null, String.class, "", false, true, false));
        SiteFields.add(Company.class, new FieldDescriptor("gaTrackingId", "GA Tracking ID", TextField.class, null, 100, null, String.class, "", false, true, false));
        SiteFields.add(Company.class, new FieldDescriptor("created", "Created", TimestampField.class, new TimestampConverter(), 150, null, Date.class, null, true,
                true, true));
        SiteFields.add(Company.class, new FieldDescriptor("modified", "Modified", TimestampField.class, new TimestampConverter(), 150, null, Date.class, null,
                true, true, true));

        SiteFields.add(Customer.class, new FieldDescriptor("firstName", "First Name", TextField.class, null, 150, null, String.class, "", false, true, true));
        SiteFields.add(Customer.class, new FieldDescriptor("lastName", "Last Name", TextField.class, null, 150, null, String.class, "", false, true, true));
        SiteFields.add(Customer.class, new FieldDescriptor("phoneNumber", "Phone Number", TextField.class, null, 100, null, String.class, "", false, true, true));
        SiteFields.add(Customer.class, new FieldDescriptor("emailAddress", "Email Address", TextField.class, null, 150, null, String.class, "", false, true, true),
                new EmailValidator("Email address is not valid."));
        SiteFields.add(Customer.class, new FieldDescriptor("memberGroup", "Members", GroupField.class, null, 100, null, Group.class,
                null, true, true, false));
        SiteFields.add(Customer.class, new FieldDescriptor("adminGroup", "Admins", GroupField.class, null, 100, null, Group.class,
                null, true, true, false));
        SiteFields.add(Customer.class, new FieldDescriptor("company", "Is Company", CheckBox.class, null, 100, null, Boolean.class, false, false, true, false));
        SiteFields.add(Customer.class, new FieldDescriptor("companyName", "Company Name", TextField.class, null, -1, null, String.class, "", false, true, false));
        SiteFields.add(Customer.class, new FieldDescriptor("companyCode", "Company Code", TextField.class, null, 100, null, String.class, "", false, true, false));
        SiteFields.add(Customer.class, new FieldDescriptor("created", "Created", TimestampField.class, new TimestampConverter(), 150, null, Date.class, null, true,
                true, true));
        SiteFields.add(Customer.class, new FieldDescriptor("modified", "Modified", TimestampField.class, new TimestampConverter(), 150, null, Date.class, null,
                true, true, true));

        SiteFields.add(PostalAddress.class, new FieldDescriptor("addressLineOne", "Line #1", TextField.class, null, 100, null, String.class, "",
                false, true, true));
        SiteFields.add(PostalAddress.class, new FieldDescriptor("addressLineTwo", "Line #2", TextField.class, null, 100, null, String.class, "",
                false, true, true));
        SiteFields.add(PostalAddress.class, new FieldDescriptor("addressLineThree", "Line #3", TextField.class, null, 100, null, String.class, "",
                false, true, true));
        SiteFields.add(PostalAddress.class, new FieldDescriptor("city", "City", TextField.class, null, 100, null, String.class, "",
                false, true, true));
        SiteFields.add(PostalAddress.class, new FieldDescriptor("postalCode", "Postal Code", TextField.class, null, 100, null, String.class, "",
                false, true, true));
        SiteFields.add(PostalAddress.class, new FieldDescriptor("country", "Country", TextField.class, null, 100, null, String.class, "",
                false, true, true));

        SiteFields.add(Group.class, new FieldDescriptor("name", "Name", TextField.class, null, 200, null, String.class, "", false, true, true));
        SiteFields.add(Group.class, new FieldDescriptor("description", "Description", TextField.class, null, -1, null, String.class, "", false, true, true));
        SiteFields.add(Group.class, new FieldDescriptor("created", "Created", TimestampField.class, new TimestampConverter(), 150, null, Date.class, null, true,
                true, true));
        SiteFields.add(Group.class, new FieldDescriptor("modified", "Modified", TimestampField.class, new TimestampConverter(), 150, null, Date.class, null,
                true, true, true));

        SiteFields.add(User.class, new FieldDescriptor("firstName", "First Name", TextField.class, null, 150, null, String.class, "", false, true, true));
        SiteFields.add(User.class, new FieldDescriptor("lastName", "Last Name", TextField.class, null, 150, null, String.class, "", false, true, true));
        SiteFields.add(User.class, new FieldDescriptor("emailAddress", "Email Address", TextField.class, null, -1, null, String.class, "", false, true, true), new EmailValidator("Email address is not valid."));
        SiteFields.add(User.class, new FieldDescriptor("emailAddressValidated", "Email Validated", CheckBox.class, null, 100, null, Boolean.class, false, false, true, false));
        SiteFields.add(User.class, new FieldDescriptor("passwordHash", "Password", PasswordField.class, null, 100, null, String.class, "", false, true, true));
        SiteFields.add(User.class, new FieldDescriptor("passwordExpirationDate", "Password Expiration", DatePartField.class, new DatePartConverter(), 100, null, Date.class, null, true,
                true, false));
        SiteFields.add(User.class, new FieldDescriptor("phoneNumber", "Phone Number", TextField.class, null, 150, null, String.class, "", false, true, true));
        SiteFields.add(User.class, new FieldDescriptor("lockedOut", "Locked Out", CheckBox.class, null, 100, null, Boolean.class, false, true, true, false));
        SiteFields.add(User.class, new FieldDescriptor("failedLoginCount", "Failed Logins", TextField.class, new StringToIntegerConverter(), 50, null, Integer.class, null,
                true, true, true));
        SiteFields.add(User.class, new FieldDescriptor("openIdIdentifier", "Open ID Identifier", TextField.class, null, -1, null, String.class, "", false, true, false));
        SiteFields.add(User.class, new FieldDescriptor("certificate", "Certificate (ASCII X.509)", TextArea.class, new CertificateConverter(), 200, null, String.class, "", false,
                true, false));
        SiteFields.add(User.class, new FieldDescriptor("created", "Created", TimestampField.class, new TimestampConverter(), 150, null, Date.class, null, true,
                true, true));
        SiteFields.add(User.class, new FieldDescriptor("modified", "Modified", TimestampField.class, new TimestampConverter(), 150, null, Date.class, null,
                true, true, true));

        SiteFields.add(GroupMember.class, new FieldDescriptor("group", "Group", GroupField.class, null, 100, null, Group.class,
                null, false, true, true));
        SiteFields.add(GroupMember.class, new FieldDescriptor("user", "User", UserField.class, null, 100, null, User.class,
                null, false, true, true));
        SiteFields.add(GroupMember.class, new FieldDescriptor("created", "Created", TimestampField.class, new TimestampConverter(), 150, null, Date.class, null, true,
                true, true));

        SiteFields.add(UserDirectory.class, new FieldDescriptor("address", "LDAP Address", TextField.class, null, -1, null, String.class, "", false, true, true));
        SiteFields.add(UserDirectory.class, new FieldDescriptor("port", "LDAP Port", TextField.class, new StringToIntegerConverter(), 100, null, Integer.class, null,
                false, true, true));

        SiteFields.add(UserDirectory.class, new FieldDescriptor("loginDn", "LDAP Login DN", TextField.class, null, 100, null, String.class, "", false, true, true));
        SiteFields.add(UserDirectory.class, new FieldDescriptor("loginPassword", "LDAP Login Password", PasswordField.class, null, 100, null, String.class, "", false, false, true));
        SiteFields.add(UserDirectory.class, new FieldDescriptor("userEmailAttribute", "LDAP User Email Attribute", TextField.class, null, 100, null, String.class, "", false, true, true));
        SiteFields.add(UserDirectory.class, new FieldDescriptor("userSearchBaseDn", "LDAP User search Base DN", TextField.class, null, 100, null, String.class, "", false, true, true));
        SiteFields.add(UserDirectory.class, new FieldDescriptor("groupSearchBaseDn", "LDAP Group search Base DN", TextField.class, null, 100, null, String.class, "", false, true, true));

        SiteFields.add(UserDirectory.class, new FieldDescriptor("remoteLocalGroupMapping", "Remote-Local Group Mapping", TextField.class, null, 200, null, String.class, "", false, true, true));
        SiteFields.add(UserDirectory.class, new FieldDescriptor("requiredRemoteGroup", "Required Remote Group", TextField.class, null, 200, null, String.class, "", false, true, true));
        SiteFields.add(UserDirectory.class, new FieldDescriptor("subNetWhiteList", "Sub Net White List", TextField.class, null, 200, null, String.class, "", false, true, true));
        SiteFields.add(UserDirectory.class, new FieldDescriptor("enabled", "Enabled", CheckBox.class, null, 100, null, Boolean.class, false, false, true, false));
        SiteFields.add(UserDirectory.class, new FieldDescriptor("created", "Created", TimestampField.class, new TimestampConverter(), 150, null, Date.class, null, true,
                true, true));
        SiteFields.add(UserDirectory.class, new FieldDescriptor("modified", "Modified", TimestampField.class, new TimestampConverter(), 150, null, Date.class, null,
                true, true, true));

    }
}
