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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.server.db.DbHelper;
import com.data2semantics.yasgui.server.fetchers.PropertiesFetcher;
import com.data2semantics.yasgui.shared.AutocompleteKeys;
import com.google.common.collect.HashMultimap;

public class AutocompleteServlet extends HttpServlet {
	private static final long serialVersionUID = -8887854790329786302L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			checkRequest(request, AutocompleteKeys.REQUEST_QUERY, AutocompleteKeys.REQUEST_ENDPOINT, AutocompleteKeys.REQUEST_TYPE, AutocompleteKeys.REQUEST_MAX_RESULTS);
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", new Date().getTime());
			response.setContentType("application/json");
			try {
				PrintWriter out = response.getWriter();
				out.println(getJson(request, response).toString());
				out.close();
			} catch(JSONException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}
	}
	
	private void checkRequest(HttpServletRequest request, String... argsToCheck) throws IllegalArgumentException {
		for (String argToCheck: argsToCheck) {
			if (request.getParameter(argToCheck) == null || request.getParameter(argToCheck).length() == 0) {
				throw new IllegalArgumentException("Missing parameter in request (" + argToCheck + ")");
			}
		}
	}

	private JSONObject getJson(HttpServletRequest request, HttpServletResponse response) throws JSONException, ClassNotFoundException, FileNotFoundException, SQLException, IOException, ParseException {
		JSONObject resultObject = new JSONObject();
		if (request.getParameter(AutocompleteKeys.REQUEST_TYPE).equals(AutocompleteKeys.TYPE_CLASS)) {
			
		} else {
			resultObject = getPropertyJson(request, response);
		}
		
		return resultObject;
	}
	
	private JSONObject getPropertyJson(HttpServletRequest request, HttpServletResponse response) throws JSONException, ClassNotFoundException, FileNotFoundException, SQLException, IOException, ParseException {
		JSONObject resultObject = new JSONObject();
		
		String methodInRequest = request.getParameter(AutocompleteKeys.REQUEST_METHOD); //can be null, in that case retrieve both methods
		String endpoint = request.getParameter(AutocompleteKeys.REQUEST_ENDPOINT); //can be null, in that case retrieve both methods
		String partialProperty = request.getParameter(AutocompleteKeys.REQUEST_QUERY); //can be null, in that case retrieve both methods
		int maxResults = Integer.parseInt(request.getParameter(AutocompleteKeys.REQUEST_MAX_RESULTS));
		
		DbHelper dbHelper = new DbHelper(new File(getServletContext().getRealPath("/")));
		HashMultimap<String, String> map = dbHelper.getProperties(endpoint, partialProperty, maxResults, methodInRequest);
		
		JSONArray propertyMethodResults = new JSONArray();
		JSONArray lazyMethodResults = new JSONArray();
		for (String uri: map.keySet()) {
			for (String method: map.get(uri)) {
				if (method.equals("lazy")) {
					lazyMethodResults.put(uri);
				} else {
					propertyMethodResults.put(uri);
				}
			}
			
		}
		int resultSize = propertyMethodResults.length() + lazyMethodResults.length();
		
		if (methodInRequest == null || methodInRequest.equals("property")) {
			String status = null;
			if (propertyMethodResults.length() == 0) {
				if (!dbHelper.propertyRetrievalEnabled(endpoint, "property")) {
					status = "Disabled";
				} else {
					if (dbHelper.lastFetchesFailed(endpoint,5)) {
						status = "Failed fetching properties";
					} else {
						PropertiesFetcher fetcher = new PropertiesFetcher(new File(getServletContext().getRealPath("/")), endpoint);
						fetcher.fetch();
						map = dbHelper.getProperties(endpoint, partialProperty, maxResults, "property");
						for (String uri: map.keySet()) {
							for (String method: map.get(uri)) {
								if (method.equals("property")) {
									propertyMethodResults.put(uri);
								}
							}
							
						}
					}
				}
			}
		
			JSONObject propertyMethodObject = new JSONObject();
			if (status != null) {
				propertyMethodObject.put(AutocompleteKeys.RESPONSE_STATUS, status);
			}
			int totalSize = propertyMethodResults.length();
			if (resultSize == maxResults) {
				//there are probably more results than the maximum we have retrieved
				totalSize = dbHelper.getPropertiesCount(endpoint, partialProperty, "property");
			}
			propertyMethodObject.put(AutocompleteKeys.RESPONSE_RESULTS, propertyMethodResults);
			propertyMethodObject.put(AutocompleteKeys.RESPONSE_RESULT_SIZE, totalSize);
			resultObject.put(AutocompleteKeys.RESPONSE_METHOD_PROPERTY, propertyMethodObject);
		}
		
		if (methodInRequest == null || methodInRequest.equals("lazy")) {
			JSONObject lazyMethodObject = new JSONObject();
			int totalSize = lazyMethodResults.length();
			if (resultSize == maxResults) {
				//there are probably more results than the maximum we have retrieved
				totalSize = dbHelper.getPropertiesCount(endpoint, partialProperty, "lazy");
			}
			lazyMethodObject.put(AutocompleteKeys.RESPONSE_RESULTS, lazyMethodResults);
			lazyMethodObject.put(AutocompleteKeys.RESPONSE_RESULT_SIZE, totalSize);
			resultObject.put(AutocompleteKeys.RESPONSE_METHOD_LAZY, lazyMethodObject);
		}
		return resultObject;
	}

	
}
