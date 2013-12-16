package com.data2semantics.yasgui.server.servlets;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.server.db.DbHelper;
import com.data2semantics.yasgui.shared.autocompletions.AccessibilityStatus;
import com.data2semantics.yasgui.shared.autocompletions.AutocompleteKeys;
import com.data2semantics.yasgui.shared.autocompletions.EndpointPrivateFlag;
import com.data2semantics.yasgui.shared.autocompletions.FetchMethod;
import com.data2semantics.yasgui.shared.autocompletions.FetchType;
import com.data2semantics.yasgui.shared.autocompletions.Util;
import com.data2semantics.yasgui.shared.exceptions.EndpointIdException;

public class AutocompletionsSaverServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			//parse json from request
			JSONObject jsonObj = null;
			jsonObj = getJsonObject(request);
		
			
			DbHelper dbHelper = new DbHelper(new File(getServletContext().getRealPath("/")), request);
			
			//is user logged in?
			int userId = dbHelper.getUserId();
			if (userId < 0) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "You should be logged in before submitting completions");
				return;
			}
			
			
			FetchType type = getTypeFromJson(jsonObj);
			String endpoint = getEndpointFromJson(jsonObj);
			JSONArray completions = jsonObj.getJSONArray(AutocompleteKeys.REQUEST_COMPLETIONS);
			
			//does endpoint already exist as a public endpoint?? If so, ignore..
			try {
				dbHelper.getEndpointId(endpoint, EndpointPrivateFlag.PUBLIC);
				//hmm, no exception thrown. there is a public version of this endpoint!!! 
				response.sendError(HttpServletResponse.SC_CONFLICT, "Endpoint " + endpoint + " is stored as -public- endpoint already. Using this endpoint url as a private endpoint is not allowed");
				return;
			} catch (EndpointIdException e) {
				//this is fine ;)
			}
			
			//try to get endpoint id for this specific user
			int endpointId;
			try {
				endpointId = dbHelper.getEndpointId(endpoint, EndpointPrivateFlag.OWN);
			} catch (EndpointIdException e) {
				//endpoint does not exist yet for this user. create a private one!
				endpointId = dbHelper.generateIdForEndpoint(endpoint, AccessibilityStatus.INACCESSIBLE);	
			}
			dbHelper.storeCompletionFetchesFromLocalhost(endpointId, type, FetchMethod.QUERY_RESULTS, completions);
			//done!
			
			
			PrintWriter out = response.getWriter();
			out.println("{status: 'success'}");
			out.close();
		} catch (JSONException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unable to parse request json :" + e.getMessage());
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to make connection to database: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (ClassNotFoundException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} catch (ParseException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		} 
		
	}
	

	
	private FetchType getTypeFromJson(JSONObject jsonObject) throws IllegalArgumentException, JSONException {
		String typeString = jsonObject.getString(AutocompleteKeys.REQUEST_TYPE);
		if (typeString == null) {
			throw new IllegalArgumentException("Required arg in json missing: " + AutocompleteKeys.REQUEST_TYPE);
		} else {
			try {
				return Util.stringToFetchType(typeString);
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Unrecognized value given in json. " + AutocompleteKeys.REQUEST_ENDPOINT + ": " + typeString);
			}
		}
	}
	private String getEndpointFromJson(JSONObject jsonObject) throws IllegalArgumentException, JSONException {
		String endpoint = jsonObject.getString(AutocompleteKeys.REQUEST_ENDPOINT);
		if (endpoint == null) {
			throw new IllegalArgumentException("Required arg in json missing: " + AutocompleteKeys.REQUEST_TYPE);
		} else {
			return endpoint;
		}
	}

	private JSONObject getJsonObject(HttpServletRequest request) throws IOException, JSONException {
		
		BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		String jsonString = "";
		if (br != null) {
			jsonString = br.readLine();
		}
		JSONObject requestJson = null;
		requestJson = new JSONObject(jsonString);
		return requestJson;
	}
	
}
