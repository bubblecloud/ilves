/**
 *
 */
package org.vaadin.addons.sitekit.site;


/**
 * Interface for site flows.
 * @author Tommi Laukkanen
 */
public interface SiteView {
    /**
     * Sets the ViewDescriptor represented by this view.
     * @param viewDescriptor the ViewDescriptor to set
     */
    void setViewDescriptor(final ViewDescriptor viewDescriptor);
    /**
     * @return the ViewDescriptor
     */
    ViewDescriptor getViewDescriptor();
    /**
     * Sets the view version presented by this view.
     * @param viewVersion The view version.
     */
    void setViewVersion(final ViewVersion viewVersion);
    /**
     * @return the view version
     */
    ViewVersion getViewVersion();
    /**
     * Initialize the view.
     */
    void initialize();
}
