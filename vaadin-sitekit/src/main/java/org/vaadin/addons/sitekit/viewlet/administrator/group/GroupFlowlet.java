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
package org.vaadin.addons.sitekit.viewlet.administrator.group;

import com.vaadin.data.util.filter.Compare;
import com.vaadin.ui.Table;
import org.vaadin.addons.lazyquerycontainer.EntityContainer;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.grid.*;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.GroupMember;
import org.vaadin.addons.sitekit.site.SiteFields;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import org.vaadin.addons.sitekit.util.ContainerUtil;
import org.vaadin.addons.sitekit.viewlet.administrator.user.UserGroupMemberFlowlet;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Group edit flow.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class GroupFlowlet extends AbstractFlowlet implements ValidatingEditorStateListener {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The entity manager. */
    private EntityManager entityManager;
    /** The group flow. */
    private Group entity;

    /** The entity form. */
    private ValidatingEditor groupEditor;
    /** The save button. */
    private Button saveButton;
    /** The discard button. */
    private Button discardButton;

    /** The entity container. */
    private EntityContainer<GroupMember> childContainer;
    /** The list button layout. */
    private HorizontalLayout childListButtonLayout;
    /** The user element grid. */
    private Grid childGrid;

    @Override
    public String getFlowletKey() {
        return "group";
    }

    @Override
    public boolean isDirty() {
        return groupEditor.isModified();
    }

    @Override
    public boolean isValid() {
        return groupEditor.isValid();
    }

    @Override
    public void initialize() {
        entityManager = getSite().getSiteContext().getObject(EntityManager.class);

        final GridLayout layout = new GridLayout(2, 3);
        layout.setSizeFull();
        layout.setMargin(false);
        layout.setSpacing(true);
        layout.setRowExpandRatio(1, 1f);
        setViewContent(layout);

        groupEditor = new ValidatingEditor(SiteFields.getFieldDescriptors(Group.class));
        groupEditor.setCaption("Group");
        groupEditor.addListener((ValidatingEditorStateListener) this);
        layout.addComponent(groupEditor, 0, 1);

        final List<FieldDescriptor> childFieldDescriptors = SiteFields.getFieldDescriptors(GroupMember.class);
        final List<FilterDescriptor> childFilterDescriptors = new ArrayList<FilterDescriptor>();
        childContainer = new EntityContainer<GroupMember>(entityManager, GroupMember.class, "groupMemberId", 1000,
                true, false, false);
        childContainer.getQueryView().getQueryDefinition().setDefaultSortState(
                new String[] {"user.firstName", "user.lastName"}, new boolean[] {true, true});

        ContainerUtil.addContainerProperties(childContainer, childFieldDescriptors);

        final Table childTable = new FormattingTable();
        childGrid = new Grid(childTable, childContainer);
        childGrid.setFields(childFieldDescriptors);
        childGrid.setFilters(childFilterDescriptors);

        childTable.setColumnCollapsed("created", true);

        layout.addComponent(childGrid, 1, 1);

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
                groupEditor.commit();
                entityManager.getTransaction().begin();
                try {
                    entity = entityManager.merge(entity);
                    entityManager.persist(entity);
                    entityManager.getTransaction().commit();
                    //entityManager.detach(entity);
                } catch (final Throwable t) {
                    if (entityManager.getTransaction().isActive()) {
                        entityManager.getTransaction().rollback();
                    }
                    throw new RuntimeException("Failed to save entity: " + entity, t);
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
                groupEditor.discard();
            }
        });


        childListButtonLayout = new HorizontalLayout();
        childListButtonLayout.setSpacing(true);
        childListButtonLayout.setSizeUndefined();
        layout.addComponent(childListButtonLayout, 1, 0);

        final Button addButton = getSite().getButton("add");
        childListButtonLayout.addComponent(addButton);
        addButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                final GroupMember groupMember = new GroupMember();
                groupMember.setGroup(entity);
                groupMember.setCreated(new Date());
                final GroupUserMemberFlowlet userGroupMemberFlowlet = getViewSheet().forward(GroupUserMemberFlowlet.class);
                userGroupMemberFlowlet.edit(groupMember, true);
            }
        });

        final Button removeButton = getSite().getButton("remove");
        childListButtonLayout.addComponent(removeButton);
        removeButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                childContainer.removeItem(childGrid.getSelectedItemId());
                childContainer.commit();
            }
        });
    }

    /**
     * Edit an existing group.
     * @param entity entity to be edited.
     * @param newEntity true if entity to be edited is new.
     */
    public void edit(final Group entity, final boolean newEntity) {
        this.entity = entity;
        groupEditor.setItem(new BeanItem<Group>(entity), newEntity);
        childContainer.getQueryView().getQueryDefinition().removeDefaultFilters();
        childContainer.getQueryView().getQueryDefinition().addDefaultFilter(new Compare.Equal("group", entity));
        childGrid.refresh();
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
        if (entity != null && entity.getGroupId() != null) {
            entityManager.refresh(entity);
        }
        childContainer.getQueryView().getQueryDefinition().removeDefaultFilters();
        childContainer.getQueryView().getQueryDefinition().addDefaultFilter(new Compare.Equal("group", entity));
        childGrid.refresh();
    }

}
