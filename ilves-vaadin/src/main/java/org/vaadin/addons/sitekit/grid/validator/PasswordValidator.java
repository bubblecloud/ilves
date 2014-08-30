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

import org.vaadin.addons.sitekit.grid.ValidatingEditor;
import org.vaadin.addons.sitekit.site.Site;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;

/**
 * Password validator.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class PasswordValidator implements Validator {
    /** The default serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The site. */
    private final Site site;
    /** The editor. */
    private ValidatingEditor editor;
    /**
     * The original password property used to set value on each change so it is
     * available for the other validator.
     */
    private final Property originalPasswordProperty;
    /**
     * The password verification property ID used to trigger revalidation of
     * that property when original password property changes.
     */
    private final String passwordVerificationPropertyId;

    /**
     * Constructor for setting the required parameters for validator.
     * @param site the site
     * @param originalPasswordProperty The original password property used to
     *            set value on each change so it is available for the other
     *            validator.
     * @param passwordVerificationPropertyId The password verification property
     *            ID used to trigger revalidation of that property when original
     *            password property changes.
     */
    public PasswordValidator(final Site site, final Property originalPasswordProperty, final String passwordVerificationPropertyId) {
        this.site = site;
        this.originalPasswordProperty = originalPasswordProperty;
        this.passwordVerificationPropertyId = passwordVerificationPropertyId;
    }

    /**
     * @return the editor
     */
    public ValidatingEditor getEditor() {
        return editor;
    }

    /**
     * @param editor the editor to set
     */
    public void setEditor(final ValidatingEditor editor) {
        this.editor = editor;
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

        originalPasswordProperty.setValue(value);
        editor.validateField(passwordVerificationPropertyId);

        if (password.length() < 8) {
            return site.localize("message-too-short-password");
        }

        return null;
    }
}
