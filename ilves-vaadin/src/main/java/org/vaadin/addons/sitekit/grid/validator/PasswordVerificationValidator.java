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
package org.vaadin.addons.sitekit.grid.validator;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import org.vaadin.addons.sitekit.site.Site;

/**
 * Password verification validator.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class PasswordVerificationValidator implements Validator {
    /** The default serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The site. */
    private final Site site;
    /**
     * The original password property used to get the value of the original
     * password.
     */
    private final Property originalPasswordProperty;

    /**
     * Constructor for setting the required parameters for the validator.
     * @param site the site.
     * @param originalPasswordProperty the original password property used to
     *            get the value of the original password.
     */
    public PasswordVerificationValidator(final Site site, final Property originalPasswordProperty) {
        this.site = site;
        this.originalPasswordProperty = originalPasswordProperty;
    }

    @Override
    public void validate(final Object value) throws InvalidValueException {
        final String error = checkForError(value);
        if (error != null) {
            throw new InvalidValueException(error);
        }
    }

    /**
     * Checks for password errors.
     * @param value the password value
     * @return the error or null if success.
     */
    private String checkForError(final Object value) {
        final String password = (String) value;

        if (!password.equals(originalPasswordProperty.getValue())) {
            return site.localize("message-passwords-do-not-match");
        }

        return null;
    }
}
