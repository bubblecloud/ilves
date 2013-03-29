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
package org.vaadin.addons.sitekit.site;

import java.util.List;

/**
 * Security Provider interface for accessing user and role information.
 *
 * @author Tommi S.E. Laukkanen
 */
public interface SecurityProvider {
    /**
     * Gets current user or null if use has not logged in.
     * @return the user name.
     */
    String getUser();
    /**
     * Gets user roles.
     * @return array of user roles.
     */
    List<String> getRoles();
}
