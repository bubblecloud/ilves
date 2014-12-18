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
package org.bubblecloud.ilves.ui.administrator.group;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import org.bubblecloud.ilves.component.flow.AbstractFlowlet;
import org.bubblecloud.ilves.component.grid.FieldDescriptor;
import org.bubblecloud.ilves.component.grid.ValidatingEditor;
import org.bubblecloud.ilves.component.grid.ValidatingEditorStateListener;
import org.bubblecloud.ilves.model.GroupMember;
import org.bubblecloud.ilves.security.SecurityService;
import org.bubblecloud.ilves.site.SiteFields;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

/**
 * GroupMember edit Flowlet.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class GroupUserMemberFlowlet extends AbstractFlowlet implements ValidatingEditorStateListener {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The entity manager. */
    private EntityManager entityManager;
    /** The groupMember flow. */
    private GroupMember entity;

    /** The entity form. */
    private ValidatingEditor groupMemberEditor;
    /** The save button. */
    private Button saveButton;
    /** The discard button. */
    private Button discardButton;

    @Override
    public String getFlowletKey() {
        return "group-membership";
    }

    @Override
    public boolean isDirty() {
        return groupMemberEditor.isModified();
    }

    @Override
    public boolean isValid() {
        return groupMemberEditor.isValid();
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

        final List<FieldDescriptor> fieldDescriptors = new ArrayList<FieldDescriptor>(
                SiteFields.getFieldDescriptors(GroupMember.class));

        for (final FieldDescriptor fieldDescriptor : fieldDescriptors) {
            if ("group".equals(fieldDescriptor.getId())) {
                fieldDescriptors.remove(fieldDescriptor);
                break;
            }
        }

        groupMemberEditor = new ValidatingEditor(fieldDescriptors);
        groupMemberEditor.setCaption("GroupMember");
        groupMemberEditor.addListener(this);
        gridLayout.addComponent(groupMemberEditor, 0, 0);

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
                groupMemberEditor.commit();
                SecurityService.addGroupMember(getSite().getSiteContext(), entity.getGroup(), entity.getUser());
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
                groupMemberEditor.discard();
            }
        });

    }

    /**
     * Edit an existing groupMember.
     * @param entity entity to be edited.
     * @param newEntity true if entity to be edited is new.
     */
    public void edit(final GroupMember entity, final boolean newEntity) {
        this.entity = entity;
        groupMemberEditor.setItem(new BeanItem<GroupMember>(entity), newEntity);
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
