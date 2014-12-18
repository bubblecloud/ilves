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
package org.bubblecloud.ilves.ui.administrator.directory;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import org.bubblecloud.ilves.component.flow.AbstractFlowlet;
import org.bubblecloud.ilves.component.grid.ValidatingEditor;
import org.bubblecloud.ilves.component.grid.ValidatingEditorStateListener;
import org.bubblecloud.ilves.model.UserDirectory;
import org.bubblecloud.ilves.security.SecurityService;
import org.bubblecloud.ilves.site.SiteFields;

import javax.persistence.EntityManager;

/**
 * UserDirectory edit flow.
 * @author Tommi S.E. Laukkanen
 */
public final class UserDirectoryFlowlet extends AbstractFlowlet implements ValidatingEditorStateListener {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The entity manager. */
    private EntityManager entityManager;
    /** The userDirectory flow. */
    private UserDirectory entity;

    /** The entity form. */
    private ValidatingEditor userDirectoryEditor;
    /** The save button. */
    private Button saveButton;
    /** The discard button. */
    private Button discardButton;

    @Override
    public String getFlowletKey() {
        return "directory";
    }

    @Override
    public boolean isDirty() {
        return userDirectoryEditor.isModified();
    }

    @Override
    public boolean isValid() {
        return userDirectoryEditor.isValid();
    }

    @Override
    public void initialize() {
        entityManager = getSite().getSiteContext().getObject(EntityManager.class);

        final GridLayout gridLayout = new GridLayout(1, 2);
        gridLayout.setSizeFull();
        gridLayout.setMargin(false);
        gridLayout.setSpacing(true);
        gridLayout.setRowExpandRatio(1, 1f);
        setViewContent(gridLayout);

        userDirectoryEditor = new ValidatingEditor(SiteFields.getFieldDescriptors(UserDirectory.class));
        userDirectoryEditor.setCaption("UserDirectory");
        userDirectoryEditor.addListener((ValidatingEditorStateListener) this);
        gridLayout.addComponent(userDirectoryEditor, 0, 0);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        gridLayout.addComponent(buttonLayout, 0, 1);

        saveButton = new Button("Save");
        saveButton.setImmediate(true);
        buttonLayout.addComponent(saveButton);
        saveButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                userDirectoryEditor.commit();
                if (entity.getUserDirectoryId() == null) {
                    SecurityService.addUserDirectory(getSite().getSiteContext(), entity);
                } else {
                    SecurityService.updateUserDirectory(getSite().getSiteContext(), entity);
                }
            }
        });

        discardButton = new Button("Discard");
        discardButton.setImmediate(true);
        buttonLayout.addComponent(discardButton);
        discardButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                userDirectoryEditor.discard();
            }
        });

    }

    /**
     * Edit an existing userDirectory.
     * @param entity entity to be edited.
     * @param newEntity true if entity to be edited is new.
     */
    public void edit(final UserDirectory entity, final boolean newEntity) {
        this.entity = entity;
        userDirectoryEditor.setItem(new BeanItem<UserDirectory>(entity), newEntity);
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
    }

}
