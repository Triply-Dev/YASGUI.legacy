package com.data2semantics.yasgui.server.openid;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import com.data2semantics.yasgui.server.db.DbHelper;
import com.data2semantics.yasgui.server.openid.OpenIdServlet.Callback;
import com.data2semantics.yasgui.shared.StaticConfig;
import com.data2semantics.yasgui.shared.UserDetails;

public class OpenIdCallback implements Callback{
	String baseUrl;
	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger.getLogger(OpenIdCallback.class.getName());
	public String getOpenIdServletURL(String baseUrl) {
		this.baseUrl = baseUrl;
		return baseUrl += StaticConfig.OPEN_ID_SERVLET;
	}

	public String getLoginURL() {
		//always called after getOpenIdServletURL (so we already stored the base url)
		return baseUrl;
	}

	public String createUniqueIdForUser(JSONObject config, String loginString) {
		String rand = RandomStringUtils.randomAlphanumeric(12);
		return rand;
	}
	public void saveIdentifierForUniqueId(JSONObject config, UserDetails userDetails) throws ClassNotFoundException, FileNotFoundException, SQLException, IOException, org.json.JSONException {
//		System.out.println("saved unique Id: " + userDetails.getUniqueId());
		DbHelper db = new DbHelper(config);
		db.storeUserInfo(userDetails);
//		LOGGER.info("unique ID: " + uniqueId );
	}
//	public void saveIdentifierForUniqueId(String uniqueId, Identifier identifier, UserDetails userDetails) {
//		LOGGER.info("unique ID: " + uniqueId );
//		
//		LOGGER.info("identifier: " + identifier.getIdentifier());
//		LOGGER.info("identifier tostring: " + identifier.toString());
//	}

	public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, JSONException, SQLException, IOException {
		OpenIdCallback callback = new OpenIdCallback();
		System.out.println(callback.createUniqueIdForUser(null, null));
	}

}
