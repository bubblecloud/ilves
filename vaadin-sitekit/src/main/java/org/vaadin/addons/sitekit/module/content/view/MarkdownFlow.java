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

import org.vaadin.addons.sitekit.flow.AbstractFlowViewlet;
import org.vaadin.addons.sitekit.flow.Flowlet;
import org.vaadin.addons.sitekit.module.content.model.Content;
import org.vaadin.addons.sitekit.viewlet.administrator.privilege.PrivilegesFlowlet;

/**
 * @author Tommi S.E. Laukkanen
 */
public final class MarkdownFlow extends AbstractFlowViewlet {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected void addFlowlets() {
        final Flowlet markdownFlowlet = new MarkdownFlowlet((Content) getViewletDescriptor().getConfiguration());
        addFlowlet(markdownFlowlet);
        final Flowlet contentFlowlet = new ContentFlowlet();
        addFlowlet(contentFlowlet);
        final Flowlet contentPrivilegesFlowlet = new PrivilegesFlowlet();
        addFlowlet(contentPrivilegesFlowlet);
        setRootFlowlet(markdownFlowlet);
    }

}
