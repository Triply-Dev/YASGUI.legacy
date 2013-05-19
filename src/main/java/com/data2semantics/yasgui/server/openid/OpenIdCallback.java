package com.data2semantics.yasgui.server.openid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import com.data2semantics.yasgui.server.db.DbConnection;
import com.data2semantics.yasgui.server.openid.OpenIdServlet.Callback;
import com.data2semantics.yasgui.shared.StaticConfig;
import com.data2semantics.yasgui.shared.UserDetails;

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

	public String createUniqueIdForUser(JSONObject config, String loginString) throws ClassNotFoundException, FileNotFoundException, JSONException, SQLException, IOException {
		String rand = RandomStringUtils.random(12);
		System.out.println("create unique id: " + rand);
		return rand;
	}
	public void saveIdentifierForUniqueId(JSONObject config, UserDetails userDetails) throws ClassNotFoundException, FileNotFoundException, SQLException, IOException, org.json.JSONException {
//		System.out.println("saved unique Id: " + userDetails.getUniqueId());
		DbConnection con = new DbConnection(config);
		con.storeUserInfo(userDetails);
//		LOGGER.info("unique ID: " + uniqueId );
	}
//	public void saveIdentifierForUniqueId(String uniqueId, Identifier identifier, UserDetails userDetails) {
//		LOGGER.info("unique ID: " + uniqueId );
//		
//		LOGGER.info("identifier: " + identifier.getIdentifier());
//		LOGGER.info("identifier tostring: " + identifier.toString());
//	}

	

}
