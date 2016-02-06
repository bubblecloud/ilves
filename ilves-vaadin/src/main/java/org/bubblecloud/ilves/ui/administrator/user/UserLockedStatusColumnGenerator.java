/**
 * Copyright 2010 Tommi S.E. Laukkanen
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
package org.bubblecloud.ilves.ui.administrator.user;

import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.ColumnGenerator;
import org.bubblecloud.ilves.model.AuthenticationDevice;
import org.bubblecloud.ilves.model.User;
import org.bubblecloud.ilves.security.SiteAuthenticationService;

import java.util.List;

/**
 * Helper class for Vaadin tables to generate user MFA status column.
 *
 * @author Tommi S.E. Laukkanen
 */
public final class UserLockedStatusColumnGenerator implements ColumnGenerator {
    /**
     * Serial version UID of this class.
     */
    private static final long serialVersionUID = 1L;
    /**
     * Construct which sets the application instance.
     */
    public UserLockedStatusColumnGenerator() {
    }

    /**
     * Generates cell component.
     *
     * @param source   The table this cell is generated for.
     * @param itemId   ID of the item this cell is presenting property of.
     * @param columnId ID of the column this cell is located at.
     * @return Component used to render this cell.
     */
    public Component generateCell(final Table source, final Object itemId, final Object columnId) {
        if (itemId == null) {
            return new Label();
        }
        final User user = (User) ((BeanItem)source.getItem(itemId)).getBean();

        if (user.isLockedOut()) {
            final Label label = new Label(FontAwesome.BAN.getHtml(), ContentMode.HTML);
            return label;
        } else {
            return new Label();
        }
    }


}
