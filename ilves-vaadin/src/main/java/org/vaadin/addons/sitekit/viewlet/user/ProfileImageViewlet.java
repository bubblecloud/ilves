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
package org.vaadin.addons.sitekit.viewlet.user;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.vaadin.addons.sitekit.site.AbstractViewlet;
import org.vaadin.addons.sitekit.util.GravatarUtil;

import java.net.URL;

/**
 * Viewlet renders user Gravatar picture or login image link.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class ProfileImageViewlet extends AbstractViewlet {

    /** Serial version UID. */
    private static final long serialVersionUID = 1L;
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(ProfileImageViewlet.class);

    /**
     * Default constructor which sets up widget content.
     */
    public ProfileImageViewlet() {
    }

    @Override
    public void enter(final String parameters) {
        final String user = getSite().getSecurityProvider().getUser();
        if (user == null) {
            final VerticalLayout layout = new VerticalLayout();
            layout.setMargin(false);
            final Link link = new Link(null, new ExternalResource("#!login"));
            link.setStyleName("gravatar");
            link.setIcon(getSite().getIcon("view-icon-login"));
            link.setWidth(32, Unit.PIXELS);
            link.setHeight(32, Unit.PIXELS);
            layout.addComponent(link);
            layout.setComponentAlignment(link, Alignment.MIDDLE_CENTER);
            this.setCompositionRoot(layout);
            return;
        }
        try {
            final VerticalLayout layout = new VerticalLayout();
            layout.setMargin(false);
            final Link link = GravatarUtil.getGravatarImageLink(user);
            layout.addComponent(link);
            layout.setComponentAlignment(link, Alignment.MIDDLE_CENTER);
            this.setCompositionRoot(layout);
        } catch (final Exception e) {
            LOGGER.warn("Error reading gravatar image for user: " + user, e);
        }
    }


}
