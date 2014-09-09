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
package org.vaadin.addons.sitekit.module.content.view;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.grid.FieldSetDescriptorRegister;
import org.vaadin.addons.sitekit.grid.ValidatingEditor;
import org.vaadin.addons.sitekit.grid.ValidatingEditorStateListener;
import org.vaadin.addons.sitekit.module.content.dao.ContentDao;
import org.vaadin.addons.sitekit.module.content.model.Content;
import org.vaadin.addons.sitekit.viewlet.user.privilege.PrivilegesFlowlet;

import javax.persistence.EntityManager;

/**
 * Content edit flowlet.
 * @author Tommi S.E. Laukkanen
 */
public final class ContentFlowlet extends AbstractFlowlet implements ValidatingEditorStateListener {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The entity manager. */
    private EntityManager entityManager;
    /** The content flow. */
    private Content entity;

    /** The entity form. */
    private ValidatingEditor contentEditor;
    /** The save button. */
    private Button saveButton;
    /** The discard button. */
    private Button discardButton;
    private Button editPrivilegesButton;

    @Override
    public String getFlowletKey() {
        return "content";
    }

    @Override
    public boolean isDirty() {
        return contentEditor.isModified();
    }

    @Override
    public boolean isValid() {
        return contentEditor.isValid();
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

        contentEditor = new ValidatingEditor(FieldSetDescriptorRegister.getFieldSetDescriptor(
                Content.class).getFieldDescriptors());
        contentEditor.setCaption("Content");
        contentEditor.addListener(this);
        gridLayout.addComponent(contentEditor, 0, 0);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        gridLayout.addComponent(buttonLayout, 0, 1);

        saveButton = getSite().getButton("save");
        buttonLayout.addComponent(saveButton);
        saveButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                contentEditor.commit();
                ContentDao.saveContent(entityManager, entity);
                editPrivilegesButton.setEnabled(true);
            }
        });

        discardButton = getSite().getButton("discard");
        buttonLayout.addComponent(discardButton);
        discardButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                contentEditor.discard();
            }
        });

        editPrivilegesButton = getSite().getButton("edit-privileges");
        buttonLayout.addComponent(editPrivilegesButton);
        editPrivilegesButton.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                final PrivilegesFlowlet privilegesFlowlet = getFlow().getFlowlet(PrivilegesFlowlet.class);
                privilegesFlowlet.edit(entity.getPage(), entity.getContentId(), "view", "edit");
                getFlow().forward(PrivilegesFlowlet.class);
            }
        });
    }

    /**
     * Edit an existing content.
     * @param entity entity to be edited.
     * @param newEntity true if entity to be edited is new.
     */
    public void edit(final Content entity, final boolean newEntity) {
        this.entity = entity;
        contentEditor.setItem(new BeanItem<Content>(entity), newEntity);
        editPrivilegesButton.setEnabled(!newEntity
                && getSite().getSecurityProvider().getRoles().contains("administrator"));
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
