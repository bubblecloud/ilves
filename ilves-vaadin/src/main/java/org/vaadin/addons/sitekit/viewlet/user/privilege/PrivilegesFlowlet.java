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
package org.vaadin.addons.sitekit.viewlet.user.privilege;

import com.vaadin.data.Property;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.vaadin.addons.sitekit.cache.PrivilegeCache;
import org.vaadin.addons.sitekit.security.SecurityService;
import org.vaadin.addons.sitekit.security.UserDao;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.site.Site;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * Privileges edit flowlet.
 * @author Tommi S.E. Laukkanen
 */
public class PrivilegesFlowlet extends AbstractFlowlet {

    private boolean dirty = false;
    private String dataLabel;
    private String dataId;
    private String[] privilegeKeys;
    /** The save button. */
    private Button saveButton;
    /** The discard button. */
    private Button discardButton;
    private VerticalLayout matrixLayout;

    private Label titleLabel;

    private GridLayout groupMatrix;
    private CheckBox[] groupCheckBoxes;
    private GridLayout userMatrix;
    private CheckBox[] userCheckBoxes;

    @Override
    public String getFlowletKey() {
        return "privileges";
    }

    @Override
    protected void initialize() {
        final HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setMargin(new MarginInfo(true, false, true, false));
        titleLayout.setSpacing(true);
        final Embedded titleIcon = new Embedded(null, getSite().getIcon("view-icon-privileges"));
        titleIcon.setWidth(32, Unit.PIXELS);
        titleIcon.setHeight(32, Unit.PIXELS);
        titleLayout.addComponent(titleIcon);
        titleLabel = new Label(
                "<h1>" + getSite().localize("view-privileges") + "</h1>", ContentMode.HTML);
        titleLayout.addComponent(titleLabel);

        matrixLayout = new VerticalLayout();
        matrixLayout.setSpacing(true);
        matrixLayout.setMargin(false);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        saveButton = getSite().getButton("save");
        buttonLayout.addComponent(saveButton);
        saveButton.addClickListener(new Button.ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final Button.ClickEvent event) {
                saveGroupMatrix();
                saveUserMatrix();
                PrivilegeCache.flush((Company) Site.getCurrent().getSiteContext().getObject(Company.class));
            }
        });
        discardButton = getSite().getButton("discard");
        buttonLayout.addComponent(discardButton);
        discardButton.addClickListener(new Button.ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final Button.ClickEvent event) {
                refreshGroupMatrix();
                refreshUserMatrix();
            }
        });

        final VerticalLayout panel = new VerticalLayout();
        panel.addComponent(titleLayout);
        panel.addComponent(matrixLayout);
        panel.addComponent(buttonLayout);
        panel.setSpacing(true);
        panel.setMargin(true);

        final Panel mainLayout = new Panel();
        mainLayout.setStyleName(Reindeer.PANEL_LIGHT);
        mainLayout.setContent(panel);

        setCompositionRoot(mainLayout);
    }

    @Override
    public void enter() {

    }

    /**
     * Sets the data ID and privilege keys to show in the privilege groupMatrix.
     * @param dataLabel the data label
     * @param dataId the data ID
     * @param privilegeKey the privilege key
     */
    public void edit(final String dataLabel, final String dataId, final String... privilegeKey) {
        this.dataLabel = dataLabel;
        this.dataId = dataId;
        this.privilegeKeys = privilegeKey;

        titleLabel.setValue("<h1>" + dataLabel + " " + getSite().localize("view-privileges") + "</h1>");
        refreshGroupMatrix();
        refreshUserMatrix();
    }

    private void refreshGroupMatrix() {
        final EntityManager entityManager = Site.getCurrent().getSiteContext().getObject(EntityManager.class);
        final Company company = Site.getCurrent().getSiteContext().getObject(Company.class);

        if (groupMatrix != null) {
            matrixLayout.removeComponent(groupMatrix);
        }
        final List<Group> groups = UserDao.getGroups(entityManager, company);

        groupCheckBoxes = new CheckBox[privilegeKeys.length * groups.size()];
        groupMatrix = new GridLayout(privilegeKeys.length + 1, groups.size() + 1);
        matrixLayout.addComponent(groupMatrix);
        for (int i = 0; i < groupCheckBoxes.length; i++) {
            groupCheckBoxes[i] = new CheckBox();
            groupCheckBoxes[i].setImmediate(true);
            groupCheckBoxes[i].addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    dirty = true;
                    saveButton.setEnabled(true);
                    discardButton.setEnabled(true);
                }
            });
        }

        groupMatrix.addComponent(new Label("<b>" + getSite().localize("label-group") + "</b>", ContentMode.HTML), 0, 0);

        for (int j = 0; j < groups.size(); j++) {
            final Label label = new Label(groups.get(j).getDescription());
            label.setWidth(200, Unit.PIXELS);
            groupMatrix.addComponent(label, 0, j + 1);
        }

        for (int i = 0; i < privilegeKeys.length; i++) {
            final Label label = new Label("<b>" + getSite().localize("privilege-" + privilegeKeys[i])
                    + "</b>", ContentMode.HTML);
            label.setWidth(50, Unit.PIXELS);
            groupMatrix.addComponent(label, i + 1, 0);
        }

        for (int i = 0; i < privilegeKeys.length; i++) {
            for (int j = 0; j < groups.size(); j++) {
                final int checkBoxIndex = i + j * privilegeKeys.length;
                groupMatrix.addComponent(groupCheckBoxes[checkBoxIndex], i + 1, j + 1);
                groupCheckBoxes[checkBoxIndex].setValue(
                        UserDao.hasGroupPrivilege(entityManager, groups.get(j), privilegeKeys[i], dataId));
            }
        }
        dirty = false;
        saveButton.setEnabled(false);
        discardButton.setEnabled(false);
    }

    private void saveGroupMatrix() {
        final EntityManager entityManager = Site.getCurrent().getSiteContext().getObject(EntityManager.class);
        final Company company = Site.getCurrent().getSiteContext().getObject(Company.class);

        final List<Group> groups = UserDao.getGroups(entityManager, company);

        for (int i = 0; i < privilegeKeys.length; i++) {
            for (int j = 0; j < groups.size(); j++) {
                final int checkBoxIndex = i + j * privilegeKeys.length;
                final boolean privileged = groupCheckBoxes[checkBoxIndex].getValue();
                final boolean privilegedInDatabase =
                        UserDao.hasGroupPrivilege(entityManager, groups.get(j), privilegeKeys[i], dataId);
                if (privileged && !privilegedInDatabase) {
                    SecurityService.addGroupPrivilege(getSite().getSiteContext(), groups.get(j), privilegeKeys[i], null, dataId, null);
                } else if (!privileged && privilegedInDatabase) {
                    SecurityService.removeGroupPrivilege(getSite().getSiteContext(), groups.get(j), privilegeKeys[i],  null, dataId, null);
                }
            }
        }

        refreshGroupMatrix();
    }

    private void refreshUserMatrix() {
        final EntityManager entityManager = Site.getCurrent().getSiteContext().getObject(EntityManager.class);
        final Company company = Site.getCurrent().getSiteContext().getObject(Company.class);

        if (userMatrix != null) {
            matrixLayout.removeComponent(userMatrix);
        }
        final List<User> users = UserDao.getUsers(entityManager, company);

        userCheckBoxes = new CheckBox[privilegeKeys.length * users.size()];
        userMatrix = new GridLayout(privilegeKeys.length + 1, users.size() + 1);
        matrixLayout.addComponent(userMatrix);
        for (int i = 0; i < userCheckBoxes.length; i++) {
            userCheckBoxes[i] = new CheckBox();
            userCheckBoxes[i].setImmediate(true);
            userCheckBoxes[i].addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    dirty = true;
                    saveButton.setEnabled(true);
                    discardButton.setEnabled(true);
                }
            });
        }

        userMatrix.addComponent(new Label("<b>" + getSite().localize("label-user") + "</b>", ContentMode.HTML), 0, 0);

        for (int j = 0; j < users.size(); j++) {
            final Label label = new Label(users.get(j).getLastName() + " " + users.get(j).getFirstName());
            label.setWidth(200, Unit.PIXELS);
            userMatrix.addComponent(label, 0, j + 1);
        }

        for (int i = 0; i < privilegeKeys.length; i++) {
            final Label label = new Label("<b>" + getSite().localize("privilege-" + privilegeKeys[i])
                    + "</b>", ContentMode.HTML);
            label.setWidth(50, Unit.PIXELS);
            userMatrix.addComponent(label, i + 1, 0);
        }

        for (int i = 0; i < privilegeKeys.length; i++) {
            for (int j = 0; j < users.size(); j++) {
                final int checkBoxIndex = i + j * privilegeKeys.length;
                userMatrix.addComponent(userCheckBoxes[checkBoxIndex], i + 1, j + 1);
                userCheckBoxes[checkBoxIndex].setValue(
                        UserDao.hasUserPrivilege(entityManager, users.get(j), privilegeKeys[i], dataId));
            }
        }
        dirty = false;
        saveButton.setEnabled(false);
        discardButton.setEnabled(false);
    }

    private void saveUserMatrix() {
        final EntityManager entityManager = Site.getCurrent().getSiteContext().getObject(EntityManager.class);
        final Company company = Site.getCurrent().getSiteContext().getObject(Company.class);

        final List<User> users = UserDao.getUsers(entityManager, company);

        for (int i = 0; i < privilegeKeys.length; i++) {
            for (int j = 0; j < users.size(); j++) {
                final int checkBoxIndex = i + j * privilegeKeys.length;
                final boolean privileged = userCheckBoxes[checkBoxIndex].getValue();
                final boolean privilegedInDatabase =
                        UserDao.hasUserPrivilege(entityManager, users.get(j), privilegeKeys[i], dataId);
                if (privileged && !privilegedInDatabase) {
                    SecurityService.addUserPrivilege(getSite().getSiteContext(), users.get(j), privilegeKeys[i], null, dataId, null);
                } else if (!privileged && privilegedInDatabase) {
                    SecurityService.removeUserPrivilege(getSite().getSiteContext(), users.get(j), privilegeKeys[i], null, dataId, null);
                }
            }
        }

        refreshUserMatrix();
    }

    @Override
    protected boolean isValid() {
        return true;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

}
