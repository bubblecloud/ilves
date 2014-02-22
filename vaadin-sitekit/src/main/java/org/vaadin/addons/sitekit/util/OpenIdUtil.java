package org.vaadin.addons.sitekit.util;

import com.vaadin.event.MouseEvents;
import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinServletRequest;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.BaseTheme;
import org.apache.log4j.Logger;
import org.openid4java.association.AssociationException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryException;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.MessageException;
import org.openid4java.message.ParameterList;
import org.vaadin.addons.sitekit.model.Company;
import org.vaadin.addons.sitekit.site.AbstractSiteUI;
import org.vaadin.addons.sitekit.site.Site;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Utility class for OpenID authentication.
 */
public class OpenIdUtil {
    /** The logger. */
    private static final Logger LOGGER = Logger.getLogger(OpenIdUtil.class);
    private static Map<String,String> urlIconMap;

    /**
     * Gets open ID provider discovery URL and icon mapping. This map can be modified
     * to add or remove OpenId providers.
     *
     * @return map of open ID provider discovery URLs and icons.
     */
    public synchronized static Map<String, String> getOpenIdProviderUrlIconMap() {
        if (urlIconMap == null) {
            urlIconMap = Collections.synchronizedMap(new TreeMap<String, String>());
            urlIconMap.put("https://www.google.com/accounts/o8/id", "openid/google_32");
            urlIconMap.put("https://me.yahoo.com", "openid/yahoo_32");
            urlIconMap.put("https://openid.aol.com/username", "openid/aol_32");
        }
        return urlIconMap;
    }

    public static Component getLoginButton(final String openIdIdentifier, String icon, final String returnViewName) {
        //final Button googleOpenIdButton = new Button("");
        final Site site = ((AbstractSiteUI) UI.getCurrent()).getSite();
        //googleOpenIdButton.setIcon(site.getIcon(icon));
        //googleOpenIdButton.setWidth(16, Sizeable.Unit.PIXELS);
        final Embedded embedded = new Embedded(null, site.getIcon(icon));
        embedded.setStyleName("image-button");
        embedded.addClickListener(new MouseEvents.ClickListener() {
            @Override
            public void click(MouseEvents.ClickEvent event) {
                try {
                    final Company company = site.getSiteContext().getObject(Company.class);
                    final String authenticationUrl = OpenIdUtil.prepareAuthenticationUrl(openIdIdentifier,
                            company.getUrl(), returnViewName);
                    UI.getCurrent().getPage().setLocation(authenticationUrl);
                } catch (final Exception e) {
                    LOGGER.error("Error in open ID discovery.", e);
                    Notification.show("Error in open ID discovery.", Notification.Type.ERROR_MESSAGE);
                }

            }
        });
        return embedded;
    }

    /**
     * Prepares open ID authentication URL.
     *
     * @param openIdIdentifier the open ID identifier to authenticate
     * @param siteUrl the site URL
     * @param returnViewName the return view name
     * @return the authentication URL
     * @throws DiscoveryException if discovery exception occurs.
     * @throws MessageException if message exception occurs.
     * @throws ConsumerException if consume exception occurs.
     */
    public static String prepareAuthenticationUrl(final String openIdIdentifier, final String siteUrl
            , final String returnViewName)
            throws DiscoveryException, MessageException, ConsumerException {
        if (UI.getCurrent().getSession().getAttribute(ConsumerManager.class) == null) {
            UI.getCurrent().getSession().setAttribute(ConsumerManager.class, new ConsumerManager());
        }
        final ConsumerManager manager = UI.getCurrent().getSession().getAttribute(ConsumerManager.class);
        final String returnURL = siteUrl + returnViewName;
        final List discoveries = manager.discover(openIdIdentifier);
        final DiscoveryInformation discovered = manager.associate(discoveries);
        UI.getCurrent().getSession().setAttribute(DiscoveryInformation.class, discovered);
        final AuthRequest authReq = manager.authenticate(discovered, returnURL);
        return authReq.getDestinationUrl(true);
    }

    /**
     * Gets verification result based on session and request parameters. This should be called when
     * processing the OpenId return request.
     *
     * @return the verification result
     * @throws DiscoveryException if discovery exception occurs.
     * @throws MessageException if message exception occurs.
     * @throws ConsumerException if consume exception occurs.
     */
    public static VerificationResult getVerificationResult() throws MessageException, DiscoveryException,
            AssociationException {
        final ConsumerManager consumerManager = UI.getCurrent().getSession().getAttribute(ConsumerManager.class);
        final DiscoveryInformation discovered = UI.getCurrent().getSession().getAttribute(
                DiscoveryInformation.class);
        UI.getCurrent().getSession().setAttribute(ConsumerManager.class, null);
        UI.getCurrent().getSession().setAttribute(DiscoveryInformation.class, null);

        final HttpServletRequest request = ((VaadinServletRequest) VaadinService.getCurrentRequest())
                .getHttpServletRequest();

        final StringBuffer urlBuilder = request.getRequestURL();
        final String queryString = request.getQueryString();
        if (queryString != null) {
            urlBuilder.append('?');
            urlBuilder.append(queryString);
        }
        final String requestUrl = urlBuilder.toString();

        final ParameterList openidResp = new ParameterList(request.getParameterMap());

        // verify the response
        return consumerManager.verify(requestUrl,
                openidResp, discovered);
    }

}
