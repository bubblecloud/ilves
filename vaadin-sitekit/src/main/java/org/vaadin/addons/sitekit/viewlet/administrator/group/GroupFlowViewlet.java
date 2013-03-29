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

import org.vaadin.addons.sitekit.flow.AbstractFlowViewlet;
import org.vaadin.addons.sitekit.flow.Flowlet;

/**
 * Group management Flow.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class GroupFlowViewlet extends AbstractFlowViewlet {

    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;

    @Override
    protected void addFlowlets() {
        final Flowlet groupsView = new GroupsFlowlet();
        addFlowlet(groupsView);
        final Flowlet groupView = new GroupFlowlet();
        addFlowlet(groupView);
        setRootFlowlet(groupsView);
    }

}
