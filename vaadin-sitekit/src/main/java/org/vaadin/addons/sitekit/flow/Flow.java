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
 */package org.vaadin.addons.sitekit.flow;

/**
 * Flow allows creating hierarchy of of flowlets.
 *
 * @author Tommi S.E. Laukkanen
 */
public interface Flow {

    /**
     * Adds a flowlet to this Flow.
     * @param flowlet the flow to be added.
     */
    void addFlowlet(final Flowlet flowlet);

    /**
     * Gets flowlet for given flowlet class.
     * @param flowletClass the class of the flowlet.
     * @param <T> the flowlet type.
     * @return the flowlet or null.
     */
    <T extends Flowlet> T getFlowlet(final Class<?> flowletClass);

    /**
     * @param flowlet the root flowlet to set
     */
    void setRootFlowlet(final Flowlet flowlet);

    /**
     * Goes forward to next flowlet of choice.
     * @param flowletClass the class of the flowlet.
     * @param <T> the flowlet type.
     * @return the active flowlet after moving forward.
     */
    <T extends Flowlet> T forward(final Class<?> flowletClass);

    /**
     * Goes backward to previous flowlet.
     * @param <T> the flowlet type.
     * @return the active flowlet after moving back.
     */
    <T extends Flowlet> T back();

    /**
     * Gets the root flowlet.
     * @param <T> the flowlet type.
     * @return the root flowlet.
     */
    <T extends Flowlet> T getRootFlowlet();

}
