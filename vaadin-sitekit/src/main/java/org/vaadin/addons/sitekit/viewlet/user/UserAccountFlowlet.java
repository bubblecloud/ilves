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
package org.vaadin.addons.sitekit.viewlet.user;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.apache.log4j.Logger;
import org.vaadin.addons.sitekit.cache.ClientCertificateCache;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.grid.ValidatingEditor;
import org.vaadin.addons.sitekit.grid.ValidatingEditorStateListener;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.site.SiteFields;
import org.vaadin.addons.sitekit.util.OpenIdUtil;
import org.vaadin.addons.sitekit.util.PasswordLoginUtil;
import org.vaadin.addons.sitekit.util.StringUtil;

import javax.persistence.EntityManager;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * User edit Flowlet.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class UserAccountFlowlet extends AbstractFlowlet implements ValidatingEditorStateListener {

    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(UserAccountFlowlet.class);

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The entity manager. */
    private EntityManager entityManager;
    /** The user flow. */
    private User user;

    /** The entity form. */
    private ValidatingEditor editor;
    /** The save button. */
    private Button saveButton;
    /** The discard button. */
    private Button discardButton;

    @Override
    public String getFlowletKey() {
        return "user";
    }

    @Override
    public boolean isDirty() {
        return editor.isModified();
    }

    @Override
    public boolean isValid() {
        return editor.isValid();
    }

    @Override
    public void initialize() {
        entityManager = getSite().getSiteContext().getObject(EntityManager.class);

        final GridLayout layout = new GridLayout(1, 3);
        layout.setSizeFull();
        layout.setMargin(false);
        layout.setSpacing(true);
        layout.setRowExpandRatio(1, 1f);
        layout.setColumnExpandRatio(1, 1f);
        setViewContent(layout);

        editor = new ValidatingEditor(SiteFields.getFieldDescriptors(User.class));
        editor.setCaption("User");
        editor.addListener((ValidatingEditorStateListener) this);
        editor.setWidth("380px");
        layout.addComponent(editor, 0, 1);

        final HorizontalLayout editorButtonLayout = new HorizontalLayout();
        editorButtonLayout.setSpacing(true);
        layout.addComponent(editorButtonLayout, 0, 2);

        saveButton = new Button("Save");
        saveButton.setImmediate(true);
        editorButtonLayout.addComponent(saveButton);
        saveButton.addListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                editor.commit();
                entityManager.getTransaction().begin();
                try {

                    if (user.getPasswordHash() != null) {
                        final int hashSize = 64;
                        if (user.getPasswordHash().length() != hashSize) {
                            try {
                                PasswordLoginUtil.setUserPasswordHash(user.getOwner(), user, user.getPasswordHash());
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    // UserLogic.updateUser(user,
                    // UserDao.getGroupMembers(entityManager, user));
                    user = entityManager.merge(user);
                    entityManager.persist(user);
                    entityManager.getTransaction().commit();
                    editor.setItem(new BeanItem<User>(user), false);
                    entityManager.detach(user);
                    ClientCertificateCache.load();
                } catch (final Throwable t) {
                    if (entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().rollback();
                    }
                    throw new RuntimeException("Failed to save entity: " + user, t);
                }
            }
        });

        discardButton = new Button("Discard");
        discardButton.setImmediate(true);
        editorButtonLayout.addComponent(discardButton);
        discardButton.addListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                editor.discard();
            }
        });

    }

    /**
     * Edit an existing user.
     * @param entity entity to be edited.
     * @param newEntity true if entity to be edited is new.
     */
    public void edit(final User entity, final boolean newEntity) {
        this.user = entity;
        editor.setItem(new BeanItem<User>(entity), newEntity);
    }

    @Override
    public void editorStateChanged(final ValidatingEditor source) {
        if (isDirty()) {
            if (isValid()) {
                saveButton.setEnabled(true);
            } else {
                saveButton.setEnabled(false);
            }
            discardButton.setEnabled(true);
        } else {
            saveButton.setEnabled(false);
            discardButton.setEnabled(false);
        }
    }

    @Override
    public void enter() {
        user = entityManager.merge(user);
        entityManager.refresh(user);
        editor.setItem(new BeanItem<User>(user), false);
    }

}
