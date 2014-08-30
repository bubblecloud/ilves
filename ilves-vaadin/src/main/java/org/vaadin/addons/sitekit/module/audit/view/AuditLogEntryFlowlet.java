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
package org.vaadin.addons.sitekit.module.audit.view;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.grid.FieldSetDescriptorRegister;
import org.vaadin.addons.sitekit.grid.ValidatingEditor;
import org.vaadin.addons.sitekit.grid.ValidatingEditorStateListener;
import org.vaadin.addons.sitekit.module.audit.model.AuditLogEntry;

import javax.persistence.EntityManager;

/**
 * AuditLogEntry edit flowlet.
 * @author Tommi S.E. Laukkanen
 */
public final class AuditLogEntryFlowlet extends AbstractFlowlet implements ValidatingEditorStateListener {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The entity manager. */
    private EntityManager entityManager;
    /** The content flow. */
    private AuditLogEntry entity;

    /** The entity form. */
    private ValidatingEditor auditLogEntryEditor;

    @Override
    public String getFlowletKey() {
        return "audit-log-entry";
    }

    @Override
    public boolean isDirty() {
        return auditLogEntryEditor.isModified();
    }

    @Override
    public boolean isValid() {
        return auditLogEntryEditor.isValid();
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

        auditLogEntryEditor = new ValidatingEditor(FieldSetDescriptorRegister.getFieldSetDescriptor(
                AuditLogEntry.class).getFieldDescriptors());
        auditLogEntryEditor.setReadOnly(true);
        auditLogEntryEditor.setCaption("AuditLogEntry");
        auditLogEntryEditor.addListener(this);
        gridLayout.addComponent(auditLogEntryEditor, 0, 0);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        gridLayout.addComponent(buttonLayout, 0, 1);

    }

    /**
     * Edit an existing audit log entry.
     * @param entity entity to be edited.
     * @param newEntity true if entity to be edited is new.
     */
    public void edit(final AuditLogEntry entity, final boolean newEntity) {
        this.entity = entity;
        auditLogEntryEditor.setItem(new BeanItem<AuditLogEntry>(entity), newEntity);
    }

    @Override
    public void editorStateChanged(final ValidatingEditor source) {
    }

    @Override
    public void enter() {
    }

}
