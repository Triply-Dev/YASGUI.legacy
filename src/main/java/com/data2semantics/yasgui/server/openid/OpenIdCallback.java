package com.data2semantics.yasgui.server.openid;

import java.util.logging.Logger;

import org.openid4java.discovery.Identifier;

import com.data2semantics.yasgui.server.OpenIdServiceImpl;
import com.data2semantics.yasgui.server.openid.OpenIdServlet.Callback;
import com.data2semantics.yasgui.shared.StaticConfig;

public class OpenIdCallback implements Callback{
	String baseUrl;
	private final static Logger LOGGER = Logger.getLogger(OpenIdCallback.class.getName());
	public String getOpenIdServletURL(String baseUrl) {
		this.baseUrl = baseUrl;
		return baseUrl += StaticConfig.OPEN_ID_SERVLET;
	}

	public String getLoginURL() {
		//always called after getOpenIdServletURL (so we already stored the base url)
		return baseUrl;
	}

	public String createUniqueIdForUser(String user) {
		System.out.println(user);
		return user;
	}

	public void saveIdentifierForUniqueId(String uniqueId, Identifier identifier) {
		LOGGER.info("unique ID: " + uniqueId );
		
		LOGGER.info("identifier: " + identifier.getIdentifier());
		LOGGER.info("identifier tostring: " + identifier.toString());
	}

}
