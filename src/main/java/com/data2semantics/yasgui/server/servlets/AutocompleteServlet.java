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
import com.data2semantics.yasgui.server.fetchers.AutocompletionFetcher;
import com.data2semantics.yasgui.server.fetchers.ClassesFetcher;
import com.data2semantics.yasgui.server.fetchers.PropertiesFetcher;
import com.data2semantics.yasgui.shared.autocompletions.AutocompleteKeys;
import com.data2semantics.yasgui.shared.autocompletions.AutocompletionsInfo;
import com.data2semantics.yasgui.shared.autocompletions.FetchMethod;
import com.data2semantics.yasgui.shared.autocompletions.FetchType;
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

	public FetchMethod getMethodInRequest(HttpServletRequest request) {
		String methodInRequest = request.getParameter(AutocompleteKeys.REQUEST_METHOD); //can be null, in that case retrieve both methods
		if (methodInRequest == null) {
			return null;
		} else if (methodInRequest.equals(FetchMethod.QUERY_ANALYSIS.get())) {
			return FetchMethod.QUERY_ANALYSIS;
		} else if (methodInRequest.equals(FetchMethod.QUERY_RESULTS.get())) {
			return FetchMethod.QUERY_RESULTS;
		} else {
			return null;
		}
	}
	
	public FetchType getTypeInRequest(HttpServletRequest request) throws IllegalArgumentException {
		String type = request.getParameter(AutocompleteKeys.REQUEST_TYPE);
		if (type == null) {
			throw new IllegalArgumentException("Required arg missing: " + AutocompleteKeys.REQUEST_TYPE);
		} else if (type.equals(FetchType.CLASSES.getSingular())) {
			return FetchType.CLASSES;
		} else if (type.equals(FetchType.PROPERTIES.getSingular())) {
			return FetchType.PROPERTIES;
		} else {
			throw new IllegalArgumentException("Unrecognized value given. " + AutocompleteKeys.REQUEST_ENDPOINT + ": " + type);
		}
	}
	
	private JSONObject getJson(HttpServletRequest request, HttpServletResponse response) throws JSONException, ClassNotFoundException, FileNotFoundException, SQLException, IOException, ParseException {
		FetchType type = getTypeInRequest(request);
		JSONObject resultObject = new JSONObject();
		FetchMethod methodInRequest = getMethodInRequest(request);
		String endpoint = request.getParameter(AutocompleteKeys.REQUEST_ENDPOINT); //can be null, in that case retrieve both methods
		String partialProperty = request.getParameter(AutocompleteKeys.REQUEST_QUERY); //can be null, in that case retrieve both methods
		int maxResults = Integer.parseInt(request.getParameter(AutocompleteKeys.REQUEST_MAX_RESULTS));
		
		DbHelper dbHelper = new DbHelper(new File(getServletContext().getRealPath("/")));
		HashMultimap<String, String> map = dbHelper.getAutocompletions(endpoint, partialProperty, maxResults, type, methodInRequest);
		
		JSONArray queryResultsResults = new JSONArray();
		JSONArray queryAnalysisResults = new JSONArray();
		for (String uri: map.keySet()) {
			for (String method: map.get(uri)) {
				if (method.equals(FetchMethod.QUERY_ANALYSIS.get())) {
					queryAnalysisResults.put(uri);
				} else {
					queryResultsResults.put(uri);
				}
			}
		}
		
		int resultSize = queryResultsResults.length() + queryAnalysisResults.length();
		if (methodInRequest == null || methodInRequest == FetchMethod.QUERY_RESULTS) {
			String status = null;
			String statusMoreInfo = null;
			if (queryResultsResults.length() == 0) {
				if (!dbHelper.autocompletionFetchingEnabled(endpoint, type, FetchMethod.QUERY_RESULTS)) {
					status = "disabled";
					statusMoreInfo = "YASGUI won't try to query for " + type.getPlural() + " for this endpoint. This setting is stored by the YASGUI manager.";
				} else {
					int timeout = 5;
					if (dbHelper.lastFetchesFailed(endpoint, type, AutocompletionsInfo.MAX_RETRIES)) {
						status = "<span style='color:red;font-weight:bold;'>failed</span>";
						statusMoreInfo = "the last " + AutocompletionsInfo.MAX_RETRIES + " attempts to query for " + type.getPlural() + " failed. YASGUI will not attempt to fetch any more " + type.getPlural() + " from the dataset in the future (to avoid unnecessary load on this endpoint)";
					} else if (dbHelper.stillFetching(endpoint, type, timeout)) {
						status = "still fetching " + type.getPlural() + ". Try again in " + timeout + " minutes";
					} else {
						AutocompletionFetcher fetcher = null;
						if (type == FetchType.PROPERTIES) {
							fetcher = new PropertiesFetcher(new File(getServletContext().getRealPath("/")), endpoint);
						} else {
							fetcher = new ClassesFetcher(new File(getServletContext().getRealPath("/")), endpoint);
						}
						try {
							fetcher.fetch();
						} catch (Exception e) {
							status = "<span style='color:red;font-weight:bold;'>failed</span>";
							statusMoreInfo = "Exception message: " + e.getMessage();
						}
						map = dbHelper.getAutocompletions(endpoint, partialProperty, maxResults, type, FetchMethod.QUERY_RESULTS);
						for (String uri: map.keySet()) {
							for (String method: map.get(uri)) {
								if (method.equals(FetchMethod.QUERY_RESULTS.get())) {
									queryResultsResults.put(uri);
								}
							}
							
						}
					}
				}
			}
			JSONObject queryResultsMethodObject = new JSONObject();
			if (status != null) {
				JSONObject statusObj = new JSONObject();
				statusObj.put(AutocompleteKeys.RESPONSE_STATUS_SUBJECT, status);
				statusObj.put(AutocompleteKeys.RESPONSE_STATUS_TEXT, statusMoreInfo);
				queryResultsMethodObject.put(AutocompleteKeys.RESPONSE_STATUS, statusObj);
			}
			int totalSize = queryResultsResults.length();
			if (resultSize == maxResults) {
				//there are probably more results than the maximum we have retrieved
				totalSize = dbHelper.getAutcompletionCount(endpoint, partialProperty, FetchType.PROPERTIES, FetchMethod.QUERY_RESULTS);
			}
			queryResultsMethodObject.put(AutocompleteKeys.RESPONSE_RESULTS, queryResultsResults);
			queryResultsMethodObject.put(AutocompleteKeys.RESPONSE_RESULT_SIZE, totalSize);
			resultObject.put(AutocompleteKeys.RESPONSE_METHOD_QUERY_RESULTS, queryResultsMethodObject);
		}
		
		if (methodInRequest == null || methodInRequest == FetchMethod.QUERY_ANALYSIS) {
			String status = null;
			if (queryAnalysisResults.length() == 0 && !dbHelper.autocompletionFetchingEnabled(endpoint, FetchType.PROPERTIES, FetchMethod.QUERY_ANALYSIS)) {
				status = "disabled";
			}
			
			JSONObject queryAnalysisMethodObject = new JSONObject();
			if (status != null) {
				queryAnalysisMethodObject.put(AutocompleteKeys.RESPONSE_STATUS, status);
			}
			int totalSize = queryAnalysisResults.length();
			if (resultSize == maxResults) {
				//there are probably more results than the maximum we have retrieved
				totalSize = dbHelper.getAutcompletionCount(endpoint, partialProperty, FetchType.PROPERTIES, FetchMethod.QUERY_ANALYSIS);
			}
			queryAnalysisMethodObject.put(AutocompleteKeys.RESPONSE_RESULTS, queryAnalysisResults);
			queryAnalysisMethodObject.put(AutocompleteKeys.RESPONSE_RESULT_SIZE, totalSize);
			resultObject.put(AutocompleteKeys.RESPONSE_METHOD_QUERY_ANALYSIS, queryAnalysisMethodObject);
		}
		return resultObject;
	}

	
}
