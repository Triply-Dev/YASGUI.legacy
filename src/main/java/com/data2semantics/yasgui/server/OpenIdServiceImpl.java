package com.data2semantics.yasgui.server;

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

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;


import com.data2semantics.yasgui.client.services.OpenIdService;
import com.data2semantics.yasgui.server.openid.OpenIdServlet;
import com.data2semantics.yasgui.shared.LoginResult;
import com.data2semantics.yasgui.shared.UserDetails;
import com.data2semantics.yasgui.shared.exceptions.FetchException;
import com.data2semantics.yasgui.shared.exceptions.OpenIdException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class OpenIdServiceImpl extends RemoteServiceServlet implements OpenIdService {
	private final static Logger LOGGER = Logger.getLogger(OpenIdServiceImpl.class.getName());
	
	public LoginResult login(String appBaseUrl, boolean inDebugMode, String openIdURL) throws FetchException {
		LOGGER.info("trying to log in for " + openIdURL);
		HttpServletRequest request = getThreadLocalRequest();
		LOGGER.info("we have the request. now pass it on to openid servlet");
		LoginResult loginResult = new LoginResult();
		try {
			UserDetails userDetails;
			userDetails = OpenIdServlet.getRequestUserInfo(new File(getServletContext().getRealPath("/")), request);
			if (userDetails.isLoggedIn()) {
				loginResult.setIsLoggedIn(true);
			} else {
				//not logged in
				loginResult.setIsLoggedIn(false);
				loginResult.setAuthenticationLink(OpenIdServlet.getAuthenticationURL(openIdURL, appBaseUrl, inDebugMode));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (JSONException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new FetchException(e.getMessage(), e);
		} catch (OpenIdException e) {
			//not logged in
			loginResult.setIsLoggedIn(false);
			loginResult.setAuthenticationLink(OpenIdServlet.getAuthenticationURL(openIdURL, appBaseUrl, inDebugMode));
		}
		return loginResult;
		
	}
	public UserDetails getCurrentUser() throws OpenIdException {
		UserDetails details = new UserDetails();
		try {
			details = OpenIdServlet.getRequestUserInfo(new File(getServletContext().getRealPath("/")), getThreadLocalRequest());
		} catch (Exception e) {
			//do nothing. just use empty user details
			e.printStackTrace();
		}
		return details;
	}
	public void logout() throws OpenIdException {
//		getThreadLocalRequest().getSession().removeAttribute("app-openid-identifier");
//		getThreadLocalRequest().getSession().removeAttribute("app-openid-uniqueid");
		
	}
	
	
	
}
