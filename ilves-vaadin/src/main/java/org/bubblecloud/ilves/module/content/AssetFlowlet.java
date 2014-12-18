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
package org.bubblecloud.ilves.module.content;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.apache.log4j.Logger;
import org.bubblecloud.ilves.component.flow.AbstractFlowlet;
import org.bubblecloud.ilves.component.grid.FieldSetDescriptorRegister;
import org.bubblecloud.ilves.component.grid.ValidatingEditor;
import org.bubblecloud.ilves.component.grid.ValidatingEditorStateListener;
import org.bubblecloud.ilves.exception.SiteException;
import org.bubblecloud.ilves.security.DefaultRoles;
import org.bubblecloud.ilves.ui.user.privilege.PrivilegesFlowlet;
import org.bubblecloud.ilves.util.PropertiesUtil;

import javax.persistence.EntityManager;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 * Asset edit flowlet.
 * @author Tommi S.E. Laukkanen
 */
public final class AssetFlowlet extends AbstractFlowlet implements ValidatingEditorStateListener {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(AssetFlowlet.class);

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The entity manager. */
    private EntityManager entityManager;
    /** The asset flow. */
    private Asset entity;

    /** The entity form. */
    private ValidatingEditor assetEditor;
    /** The save button. */
    private Button saveButton;
    /** The discard button. */
    private Button discardButton;
    private Button editPrivilegesButton;
    private File temporaryFile;

    @Override
    public String getFlowletKey() {
        return "asset";
    }

    @Override
    public boolean isDirty() {
        return assetEditor.isModified();
    }

    @Override
    public boolean isValid() {
        return assetEditor.isValid();
    }

    @Override
    public void initialize() {
        entityManager = getSite().getSiteContext().getObject(EntityManager.class);

        final GridLayout gridLayout = new GridLayout(1, 3);
        gridLayout.setSizeFull();
        gridLayout.setMargin(false);
        gridLayout.setSpacing(true);
        gridLayout.setRowExpandRatio(1, 1f);
        setViewContent(gridLayout);

        assetEditor = new ValidatingEditor(FieldSetDescriptorRegister.getFieldSetDescriptor(
                Asset.class).getFieldDescriptors());
        assetEditor.setCaption("Asset");
        assetEditor.addListener(this);
        gridLayout.addComponent(assetEditor, 0, 1);

        final Upload upload = new Upload(getSite().localize("field-file-upload"), new Upload.Receiver() {
            @Override
            public OutputStream receiveUpload(String filename, String mimeType) {
                try {
                    temporaryFile = File.createTempFile(entity.getAssetId(), ".upload");
                    return new FileOutputStream(temporaryFile, false);
                } catch (IOException e) {
                    throw new SiteException("Unable to create temporary file for upload.", e);
                }
            }
        });
        upload.setButtonCaption(getSite().localize("button-start-upload"));
        upload.addSucceededListener(new Upload.SucceededListener() {
            @Override
            public void uploadSucceeded(Upload.SucceededEvent event) {
                if (event.getLength() == 0) {
                    return;
                }
                if (temporaryFile.length() > Long.parseLong(PropertiesUtil.getProperty("site", "asset-maximum-size"))) {
                    Notification.show(getSite().localize("message-file-too-large"),
                            Notification.Type.ERROR_MESSAGE);
                    return;
                }

                entity.setName(event.getFilename().substring(0, event.getFilename().lastIndexOf('.')));
                entity.setExtension(event.getFilename().substring(event.getFilename().lastIndexOf('.') + 1));
                entity.setType(event.getMIMEType());
                entity.setSize((int) event.getLength());

                assetEditor.setItem(new BeanItem<Asset>(entity), assetEditor.isNewItem());
                save();
            }
        });
        gridLayout.addComponent(upload, 0, 0);


        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        gridLayout.addComponent(buttonLayout, 0, 2);

        saveButton = getSite().getButton("save");
        buttonLayout.addComponent(saveButton);
        saveButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                if (isValid()) {
                    save();
                } else {
                    Notification.show(getSite().localize("message-invalid-form-asset"),
                            Notification.Type.HUMANIZED_MESSAGE);
                }
            }
        });

        discardButton = getSite().getButton("discard");
        buttonLayout.addComponent(discardButton);
        discardButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                assetEditor.discard();
                if (temporaryFile != null) {
                    temporaryFile.deleteOnExit();
                    temporaryFile = null;
                }
            }
        });

        editPrivilegesButton = getSite().getButton("edit-privileges");
        buttonLayout.addComponent(editPrivilegesButton);
        editPrivilegesButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                final PrivilegesFlowlet privilegesFlowlet = getFlow().getFlowlet(PrivilegesFlowlet.class);
                privilegesFlowlet.edit(entity.getName(), entity.getAssetId(), "view", "edit");
                getFlow().forward(PrivilegesFlowlet.class);
            }
        });
    }

    private void save() {
        assetEditor.commit();
        try {
            ContentDao.saveAsset(entityManager, entity);

            if (temporaryFile != null) {
                entityManager.getTransaction().begin();

                final Connection connection = entityManager.unwrap(Connection.class);
                final FileInputStream fileInputStream = new FileInputStream(temporaryFile);
                final PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE asset SET data = ? WHERE assetid = ?");
                preparedStatement.setBinaryStream(1, fileInputStream, (int) temporaryFile.length());
                preparedStatement.setString(2, entity.getAssetId());
                preparedStatement.executeUpdate();
                preparedStatement.close();
                fileInputStream.close();
                entityManager.getTransaction().commit();
                Notification.show(getSite().localize("message-file-uploaded"),
                        Notification.Type.HUMANIZED_MESSAGE);
            }
        } catch (final Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            LOGGER.error("Failed to save binary to database.", e);
            Notification.show(getSite().localize("message-duplicate-file-name"),
                    Notification.Type.ERROR_MESSAGE);
        } finally {
            if (temporaryFile != null) {
                temporaryFile.deleteOnExit();
                temporaryFile = null;
            }
        }

        editPrivilegesButton.setEnabled(true);
    }

    /**
     * Edit an existing asset.
     * @param entity entity to be edited.
     * @param newEntity true if entity to be edited is new.
     */
    public void edit(final Asset entity, final boolean newEntity) {
        this.entity = entity;

        if (temporaryFile != null) {
            temporaryFile.deleteOnExit();
            temporaryFile = null;
        }
        assetEditor.setItem(new BeanItem<Asset>(entity), newEntity);
        editPrivilegesButton.setEnabled(!newEntity
                && getSite().getSecurityProvider().getRoles().contains(DefaultRoles.ADMINISTRATOR));
    }

    @Override
    public void editorStateChanged(final ValidatingEditor source) {
        if (isDirty()) {
            if (isValid() && entity.getSize() > 0) {
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
    }

}
