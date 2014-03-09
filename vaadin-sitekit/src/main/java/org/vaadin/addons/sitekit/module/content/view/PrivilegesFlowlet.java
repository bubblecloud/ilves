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

import com.vaadin.data.Property;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.Reindeer;
import org.vaadin.addons.sitekit.dao.UserDao;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.module.content.dao.ContentDao;
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
    private GridLayout matrix;
    private CheckBox[] checkBoxes;
    private VerticalLayout matrixLayout;
    private Label titleLabel;

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
        matrixLayout.setSpacing(false);
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
                saveMatrix();
            }
        });
        discardButton = getSite().getButton("discard");
        buttonLayout.addComponent(discardButton);
        discardButton.addClickListener(new Button.ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final Button.ClickEvent event) {
                refreshMatrix();
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
     * Sets the data ID and privilege keys to show in the privilege matrix.
     * @param dataLabel the data label
     * @param dataId the data ID
     * @param privilegeKey the privilege key
     */
    public void edit(final String dataLabel, final String dataId, final String... privilegeKey) {
        this.dataLabel = dataLabel;
        this.dataId = dataId;
        this.privilegeKeys = privilegeKey;

        titleLabel.setValue("<h1>" + dataLabel + " " + getSite().localize("view-privileges") + "</h1>");
        refreshMatrix();
    }

    private void refreshMatrix() {
        final EntityManager entityManager = Site.getCurrent().getSiteContext().getObject(EntityManager.class);
        final Company company = Site.getCurrent().getSiteContext().getObject(Company.class);

        if (matrix != null) {
            matrixLayout.removeComponent(matrix);
        }
        final List<Group> groups = UserDao.getGroups(entityManager, company);

        checkBoxes = new CheckBox[privilegeKeys.length * groups.size()];
        matrix = new GridLayout(privilegeKeys.length + 1, groups.size() + 1);
        matrixLayout.addComponent(matrix);
        for (int i = 0; i < checkBoxes.length; i++) {
            checkBoxes[i] = new CheckBox();
            checkBoxes[i].setImmediate(true);
            checkBoxes[i].addValueChangeListener(new Property.ValueChangeListener() {
                @Override
                public void valueChange(Property.ValueChangeEvent event) {
                    dirty = true;
                    saveButton.setEnabled(true);
                    discardButton.setEnabled(true);
                }
            });
        }

        matrix.addComponent(new Label("<b>" + getSite().localize("label-group") + "</b>", ContentMode.HTML), 0, 0);

        for (int j = 0; j < groups.size(); j++) {
            final Label label = new Label(groups.get(j).getDescription());
            label.setWidth(200, Unit.PIXELS);
            matrix.addComponent(label, 0, j + 1);
        }

        for (int i = 0; i < privilegeKeys.length; i++) {
            final Label label = new Label("<b>" + getSite().localize("privilege-" + privilegeKeys[i])
                    + "</b>", ContentMode.HTML);
            label.setWidth(50, Unit.PIXELS);
            matrix.addComponent(label, i + 1, 0);
        }

        for (int i = 0; i < privilegeKeys.length; i++) {
            for (int j = 0; j < groups.size(); j++) {
                final int checkBoxIndex = i + j * privilegeKeys.length;
                matrix.addComponent(checkBoxes[checkBoxIndex], i + 1, j + 1);
                checkBoxes[checkBoxIndex].setValue(
                        UserDao.hasGroupPrivilege(entityManager, groups.get(j), privilegeKeys[i], dataId));
            }
        }
        dirty = false;
        saveButton.setEnabled(false);
        discardButton.setEnabled(false);
    }

    private void saveMatrix() {
        final EntityManager entityManager = Site.getCurrent().getSiteContext().getObject(EntityManager.class);
        final Company company = Site.getCurrent().getSiteContext().getObject(Company.class);

        final List<Group> groups = UserDao.getGroups(entityManager, company);

        for (int i = 0; i < privilegeKeys.length; i++) {
            for (int j = 0; j < groups.size(); j++) {
                final int checkBoxIndex = i + j * privilegeKeys.length;
                final boolean privileged = checkBoxes[checkBoxIndex].getValue();
                final boolean privilegedInDatabase =
                        UserDao.hasGroupPrivilege(entityManager, groups.get(j), privilegeKeys[i], dataId);
                if (privileged && !privilegedInDatabase) {
                    UserDao.addGroupPrivilege(entityManager, groups.get(j), privilegeKeys[i], dataId);
                } else if (!privileged && privilegedInDatabase) {
                    UserDao.removeGroupPrivilege(entityManager, groups.get(j), privilegeKeys[i], dataId);
                }
            }
        }

        refreshMatrix();
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
