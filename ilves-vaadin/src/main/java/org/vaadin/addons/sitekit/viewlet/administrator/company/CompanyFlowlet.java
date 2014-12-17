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
package org.vaadin.addons.sitekit.viewlet.administrator.company;

import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import org.vaadin.addons.sitekit.flow.AbstractFlowlet;
import org.vaadin.addons.sitekit.grid.ValidatingEditor;
import org.vaadin.addons.sitekit.grid.ValidatingEditorStateListener;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.PostalAddress;
import org.vaadin.addons.sitekit.service.SecurityService;
import org.vaadin.addons.sitekit.site.SiteFields;

import javax.persistence.EntityManager;

/**
 * Company edit flow.
 * @author Tommi S.E. Laukkanen
 */
public final class CompanyFlowlet extends AbstractFlowlet implements ValidatingEditorStateListener {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** The entity manager. */
    private EntityManager entityManager;
    /** The company flow. */
    private Company entity;

    /** The entity form. */
    private ValidatingEditor companyEditor;
    /** The entity form. */
    private ValidatingEditor invoicingAddressEditor;
    /** The entity form. */
    private ValidatingEditor deliveryAddressEditor;
    /** The save button. */
    private Button saveButton;
    /** The discard button. */
    private Button discardButton;

    @Override
    public String getFlowletKey() {
        return "company";
    }

    @Override
    public boolean isDirty() {
        return companyEditor.isModified() || invoicingAddressEditor.isModified() || deliveryAddressEditor.isModified();
    }

    @Override
    public boolean isValid() {
        return companyEditor.isValid() && invoicingAddressEditor.isValid() && deliveryAddressEditor.isValid();
    }

    @Override
    public void initialize() {
        entityManager = getSite().getSiteContext().getObject(EntityManager.class);

        final GridLayout gridLayout = new GridLayout(2, 3);
        gridLayout.setSizeFull();
        gridLayout.setMargin(false);
        gridLayout.setSpacing(true);
        gridLayout.setRowExpandRatio(1, 1f);
        setViewContent(gridLayout);

        companyEditor = new ValidatingEditor(SiteFields.getFieldDescriptors(Company.class));
        companyEditor.setCaption("Site");
        companyEditor.addListener((ValidatingEditorStateListener) this);
        gridLayout.addComponent(companyEditor, 0, 0, 0, 1);

        invoicingAddressEditor = new ValidatingEditor(SiteFields.getFieldDescriptors(PostalAddress.class));
        invoicingAddressEditor.setCaption("Invoicing Address");
        invoicingAddressEditor.addListener((ValidatingEditorStateListener) this);
        gridLayout.addComponent(invoicingAddressEditor, 1, 0);

        deliveryAddressEditor = new ValidatingEditor(SiteFields.getFieldDescriptors(PostalAddress.class));
        deliveryAddressEditor.setCaption("Delivery Address");
        deliveryAddressEditor.addListener((ValidatingEditorStateListener) this);
        gridLayout.addComponent(deliveryAddressEditor, 1, 1);

        final HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        gridLayout.addComponent(buttonLayout, 0, 2);

        saveButton = new Button("Save");
        saveButton.setImmediate(true);
        buttonLayout.addComponent(saveButton);
        saveButton.addClickListener(new ClickListener() {
            /** Serial version UID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(final ClickEvent event) {
                companyEditor.commit();
                invoicingAddressEditor.commit();
                deliveryAddressEditor.commit();
                if (entity.getCompanyId() == null) {
                    SecurityService.addCompany(getSite().getSiteContext(), entity);
                } else {
                    SecurityService.updateCompany(getSite().getSiteContext(), entity);
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
                companyEditor.discard();
                invoicingAddressEditor.discard();
                deliveryAddressEditor.discard();
            }
        });

    }

    /**
     * Edit an existing company.
     * @param entity entity to be edited.
     * @param newEntity true if entity to be edited is new.
     */
    public void edit(final Company entity, final boolean newEntity) {
        this.entity = entity;
        companyEditor.setItem(new BeanItem<Company>(entity), newEntity);
        invoicingAddressEditor.setItem(new BeanItem<PostalAddress>(entity.getInvoicingAddress()), newEntity);
        deliveryAddressEditor.setItem(new BeanItem<PostalAddress>(entity.getDeliveryAddress()), newEntity);
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
