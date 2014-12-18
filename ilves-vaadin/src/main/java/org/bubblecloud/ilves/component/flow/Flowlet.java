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
package org.bubblecloud.ilves.component.flow;

/**
 * Interface for flowlets. Portlet like components which are placed in FlowSheets.
 *
 * @author Tommi S.E. Laukkanen
 */
public interface Flowlet {

    /**
     * Gets the flowlet key.
     * @return the flow key.
     */
    String getFlowletKey();

    /**
     * @return the flow
     */
    Flow getFlow();

    /**
     * Sets the Flow.
     * @param viewSheet the flow sheet.
     */
    void setFlow(final Flow viewSheet);

    /**
     * True if flowlet content is dirty.
     * @return true if flow content is modified.
     */
    boolean isDirty();

    /**
     * Flowlet entered.
     */
    void enter();

}
