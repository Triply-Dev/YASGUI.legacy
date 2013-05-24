package com.data2semantics.yasgui.server.openid;

/*
 * #%L
 * YASGUI
 * %%
 * Copyright (C) 2013 Laurens Rietveld
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
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
	public void saveIdentifierForUniqueId(File configDir, UserDetails userDetails) throws ClassNotFoundException, FileNotFoundException, SQLException, IOException, org.json.JSONException, ParseException {
//		System.out.println("saved unique Id: " + userDetails.getUniqueId());
		DbHelper db = new DbHelper(configDir);
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
