package org.bubblecloud.ilves.site;

import com.vaadin.annotations.JavaScript;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.UI;
import org.apache.commons.lang.StringUtils;

@JavaScript("analytics_connector.js")
public class SiteAnalyser extends AbstractJavaScriptExtension implements ViewChangeListener {
    private String gaTrackingId;

    public SiteAnalyser(UI ui, String gaTrackingId) {
        this.gaTrackingId = gaTrackingId;
        if (!StringUtils.isEmpty(gaTrackingId)) {
            extend(ui);
            pushCommand("_setAccount", gaTrackingId);
        }
    }

    public void trackPageView(String name) {
        if (!StringUtils.isEmpty(gaTrackingId)) {
            pushCommand("_trackPageview", name);
        }
    }

    private void pushCommand(Object... commandAndArguments) {
        if (!StringUtils.isEmpty(gaTrackingId)) {
            // Cast to Object to use Object[] commandAndArguments as the first
            // varargs argument instead of as the full varargs argument array.
            callFunction("pushCommand", (Object) commandAndArguments);
        }
    }

    @Override
    public boolean beforeViewChange(ViewChangeEvent viewChangeEvent) {
        return true;
    }

    @Override
    public void afterViewChange(ViewChangeEvent viewChangeEvent) {
        trackPageView(viewChangeEvent.getViewName());
    }
}