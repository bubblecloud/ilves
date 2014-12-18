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
package org.bubblecloud.ilves.site;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;

/**
 * Abstract base class for Viewlet implementations.
 * @author Tommi S.E. Laukkanen
 */
public abstract class AbstractViewlet extends CustomComponent implements Viewlet {
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The portal this window belongs to. */
    private Site site;
    /** The ViewletDescriptor this component represents. */
    private ViewletDescriptor viewletDescriptor;

    /**
     * Default constructor.
     */
    public AbstractViewlet() {
        super();
    }

    /**
     * @return the portal
     */
    public final Site getSite() {
        return ((AbstractSiteUI) UI.getCurrent()).getSite();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void setViewletDescriptor(final ViewletDescriptor viewletDescriptor) {
        this.viewletDescriptor = viewletDescriptor;
    }

    /**
     * @return the widget
     */
    public final ViewletDescriptor getViewletDescriptor() {
        return viewletDescriptor;
    }

}
