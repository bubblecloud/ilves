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
package org.vaadin.addons.sitekit.module.content;

import com.vaadin.data.util.converter.StringToIntegerConverter;
import org.apache.commons.lang.StringUtils;
import org.vaadin.addons.sitekit.cache.PrivilegeCache;
import org.vaadin.addons.sitekit.security.DefaultRoles;
import org.vaadin.addons.sitekit.security.UserDao;
import org.vaadin.addons.sitekit.grid.FieldSetDescriptor;
import org.vaadin.addons.sitekit.grid.FieldSetDescriptorRegister;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.model.Group;
import org.vaadin.addons.sitekit.model.User;
import org.vaadin.addons.sitekit.module.content.dao.ContentDao;
import org.vaadin.addons.sitekit.module.content.model.Asset;
import org.vaadin.addons.sitekit.module.content.model.Content;
import org.vaadin.addons.sitekit.module.content.model.MarkupType;
import org.vaadin.addons.sitekit.module.content.view.*;
import org.vaadin.addons.sitekit.site.*;
import org.vaadin.addons.sitekit.site.view.DefaultValoView;

import javax.persistence.EntityManager;
import java.util.*;

/**
 * Content module adds support for Wiki content api.
 *
 * @author Tommi S.E. Laukkanen
 */
public class ContentModule implements SiteModule {

    @Override
    public void initialize() {
        final SiteDescriptor siteDescriptor = DefaultSiteUI.getContentProvider().getSiteDescriptor();

        final NavigationVersion navigationVersion = siteDescriptor.getNavigation().getProductionVersion();
        navigationVersion.addChildPage("configuration", "account", "content");
        navigationVersion.addChildPage("configuration", "content", "assets");

        // Describe content view.
        final ViewDescriptor contentView = new ViewDescriptor("content", "Content", DefaultValoView.class);
        contentView.setViewerRoles(DefaultRoles.ADMINISTRATOR);
        contentView.setViewletClass("content", ContentFlow.class);
        siteDescriptor.getViewDescriptors().add(contentView);

        final ViewDescriptor assetsView = new ViewDescriptor("assets", "Assets", DefaultValoView.class);
        assetsView.setViewerRoles(DefaultRoles.ADMINISTRATOR);
        assetsView.setViewletClass("content", AssetFlow.class);
        siteDescriptor.getViewDescriptors().add(assetsView);

        // Describe feedback view fields.
        final FieldSetDescriptor contentFields = new FieldSetDescriptor(Content.class);

        contentFields.setVisibleFieldIds(new String[]{
                "page", "title", "parentPage", "afterPage", "markupType", "markup", "created", "modified"
        });

        contentFields.getFieldDescriptor("created").setReadOnly(true);
        contentFields.getFieldDescriptor("created").setCollapsed(true);
        contentFields.getFieldDescriptor("modified").setReadOnly(true);
        contentFields.getFieldDescriptor("modified").setCollapsed(true);
        contentFields.getFieldDescriptor("page").setRequired(false);
        contentFields.getFieldDescriptor("parentPage").setRequired(false);
        contentFields.getFieldDescriptor("parentPage").setCollapsed(true);
        contentFields.getFieldDescriptor("afterPage").setRequired(false);
        contentFields.getFieldDescriptor("afterPage").setCollapsed(true);
        contentFields.getFieldDescriptor("markupType").setRequired(true);
        contentFields.getFieldDescriptor("markupType").setFieldClass(MarkupTypeField.class);
        contentFields.getFieldDescriptor("markupType").setConverter(null);
        contentFields.getFieldDescriptor("markup").setFieldClass(MarkupField.class);
        contentFields.getFieldDescriptor("markup").setWidth(700);
        contentFields.getFieldDescriptor("markup").getValidators().clear();
        contentFields.getFieldDescriptor("markup").setCollapsed(true);
        contentFields.getFieldDescriptor("title").setWidth(-1);

        FieldSetDescriptorRegister.registerFieldSetDescriptor(Content.class, contentFields);

        // Describe feedback view fields.
        final FieldSetDescriptor assetFields = new FieldSetDescriptor(Asset.class);

        assetFields.setVisibleFieldIds(new String[]{
                "name", "size", "extension", "type", "description", "created", "modified"
        });

        assetFields.getFieldDescriptor("created").setReadOnly(true);
        assetFields.getFieldDescriptor("created").setCollapsed(true);
        assetFields.getFieldDescriptor("modified").setReadOnly(true);
        assetFields.getFieldDescriptor("modified").setCollapsed(true);

        assetFields.getFieldDescriptor("name").setReadOnly(true);
        assetFields.getFieldDescriptor("name").setRequired(true);
        assetFields.getFieldDescriptor("extension").setReadOnly(true);
        assetFields.getFieldDescriptor("extension").setRequired(true);
        assetFields.getFieldDescriptor("type").setReadOnly(true);
        assetFields.getFieldDescriptor("type").setRequired(true);
        assetFields.getFieldDescriptor("size").setReadOnly(true);
        assetFields.getFieldDescriptor("size").setRequired(true);
       // assetFields.getFieldDescriptor("size").setDefaultValue(0);
        assetFields.getFieldDescriptor("size").setConverter(new StringToIntegerConverter());
        assetFields.getFieldDescriptor("description").setRequired(false);

        FieldSetDescriptorRegister.registerFieldSetDescriptor(Asset.class, assetFields);

    }

    @Override
    public void injectDynamicContent(final SiteDescriptor dynamicSiteDescriptor) {
        final Company company = Site.getCurrent().getSiteContext().getObject(Company.class);
        final EntityManager entityManager = Site.getCurrent().getSiteContext().getObject(EntityManager.class);
        final User user = ((SecurityProviderSessionImpl) Site.getCurrent().getSecurityProvider()).getUserFromSession();
        final List<Group> groups;
        if (user == null) {
            groups = new ArrayList<Group>();
            groups.add(UserDao.getGroup(entityManager, company, "anonymous"));
        } else {
            groups = UserDao.getUserGroups(entityManager, company, user);
        }


        final List<Content> contents = ContentDao.getContens(entityManager, company);
        final LinkedList<Content> queue = new LinkedList<Content>();
        final Map<String, List<Content>> dependencies = new HashMap<String, List<Content>>();
        for (final Content content : contents) {
            final String dependency;
            if (!StringUtils.isEmpty(content.getAfterPage())) {
                dependency = content.getAfterPage();
            } else if (!StringUtils.isEmpty(content.getParentPage())) {
                dependency = content.getParentPage();
            } else {
                dependency = null;
            }
            if (dependency != null) {
                if (!dependencies.containsKey(dependency)) {
                    dependencies.put(dependency, new ArrayList<Content>());
                }
                dependencies.get(dependency).add(content);
            } else {
                queue.add(content);
            }
        }

        final List<Content> ordered = new ArrayList<Content>();
        while (queue.size() > 0) {
            final Content content = queue.removeFirst();
            ordered.add(content);
            if (dependencies.containsKey(content.getPage())) {
                queue.addAll(dependencies.get(content.getPage()));
            }
        }


        final NavigationVersion navigationVersion = dynamicSiteDescriptor.getNavigation().getProductionVersion();

        for (final Content content : ordered) {
            boolean viewPrivilege = PrivilegeCache.hasPrivilege(entityManager, company,
                    user, "view", content.getContentId());
            if (!viewPrivilege) {
                for (final Group group : groups) {
                    if (PrivilegeCache.hasPrivilege(entityManager, company, group, "view", content.getContentId())) {
                        viewPrivilege = true;
                        break;
                    }
                }
            }

            if (!viewPrivilege) {
                continue;
            }

            boolean editPrivilege = UserDao.hasUserPrivilege(entityManager, user, "edit", content.getContentId());
            if (!editPrivilege) {
                for (final Group group : groups) {
                    if (UserDao.hasGroupPrivilege(entityManager, group, "edit", content.getContentId())) {
                        editPrivilege = true;
                        break;
                    }
                }
            }

            final String page = content.getPage();
            if (page == null) {
                continue;
            }
            final String parentPage = content.getParentPage();
            final String afterPage = content.getAfterPage();
            final String title = content.getTitle();
            final MarkupType markupType = content.getMarkupType();
            final String markup = content.getMarkup();

            if (StringUtils.isEmpty(parentPage)) {
                if (StringUtils.isEmpty(afterPage)) {
                    navigationVersion.addRootPage(0, page);
                    navigationVersion.setDefaultPageName(page);
                } else {
                    navigationVersion.addRootPage(afterPage, page);
                }
            } else {
                if (StringUtils.isEmpty(afterPage)) {
                    navigationVersion.addChildPage(parentPage, page);
                } else {
                    navigationVersion.addChildPage(parentPage, afterPage, page);
                }
            }

            // Describe content view.
            final ViewDescriptor viewDescriptor = new ViewDescriptor(page, title, DefaultValoView.class);
            viewDescriptor.getProductionVersion().setDynamic(true);
            if (editPrivilege) {
                viewDescriptor.setViewletClass("content", RenderFlow.class, content);
            } else {
                viewDescriptor.setViewletClass("content", RenderViewlet.class, markup);
            }
            dynamicSiteDescriptor.getViewDescriptors().add(viewDescriptor);
        }

    }
}
