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

/**
 * NavigationDescriptor information class.
 * @author Tommi S.E. Laukkanen
 */
public final class NavigationDescriptor {
    /** The navigation name. */
    private String name;
    /** The navigation development version. */
    private NavigationVersion developmentVersion;
    /** The navigation test version. */
    private NavigationVersion testVersion;
    /** The navigation production version. */
    private NavigationVersion productionVersion;

    /**
     * @param name The navigation name.
     * @param developmentVersion The navigation development version.
     * @param testVersion The navigation test version.
     * @param productionVersion The navigation production version.
     */
    public NavigationDescriptor(final String name, final NavigationVersion developmentVersion,
                                final NavigationVersion testVersion, final NavigationVersion productionVersion) {
        super();
        this.name = name;
        this.developmentVersion = developmentVersion;
        this.testVersion = testVersion;
        this.productionVersion = productionVersion;
    }

    /**
     * Default constructor for cloning.
     */
    public NavigationDescriptor() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @return the developmentVersion
     */
    public NavigationVersion getDevelopmentVersion() {
        return developmentVersion;
    }
    /**
     * @param developmentVersion the developmentVersion to set
     */
    public void setDevelopmentVersion(final NavigationVersion developmentVersion) {
        this.developmentVersion = developmentVersion;
    }
    /**
     * @return the testVersion
     */
    public NavigationVersion getTestVersion() {
        return testVersion;
    }
    /**
     * @param testVersion the testVersion to set
     */
    public void setTestVersion(final NavigationVersion testVersion) {
        this.testVersion = testVersion;
    }
    /**
     * @return the productionVersion
     */
    public NavigationVersion getProductionVersion() {
        return productionVersion;
    }
    /**
     * @param productionVersion the productionVersion to set
     */
    public void setProductionVersion(final NavigationVersion productionVersion) {
        this.productionVersion = productionVersion;
    }
    /**
     * @return clone
     */
    public NavigationDescriptor clone() {
        final NavigationDescriptor clone = new NavigationDescriptor();
        clone.name = name;
        if (developmentVersion != null) {
            clone.developmentVersion = developmentVersion.clone();
        }
        if (testVersion != null) {
            clone.testVersion = testVersion.clone();
        }
        if (productionVersion != null) {
            clone.productionVersion = productionVersion.clone();
        }
        return clone;
    }
}
