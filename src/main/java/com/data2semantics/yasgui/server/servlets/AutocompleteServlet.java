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

import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.server.autocompletions.AutocompleteResponseCreator;
import com.data2semantics.yasgui.shared.autocompletions.AutocompleteKeys;
import com.data2semantics.yasgui.shared.autocompletions.FetchMethod;
import com.data2semantics.yasgui.shared.autocompletions.FetchType;

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
		FetchMethod method = getMethodInRequest(request);
		String endpoint = request.getParameter(AutocompleteKeys.REQUEST_ENDPOINT); //can be null, in that case retrieve both methods
		String partialCompletion = request.getParameter(AutocompleteKeys.REQUEST_QUERY); //can be null, in that case retrieve both methods
		int maxResults = Integer.parseInt(request.getParameter(AutocompleteKeys.REQUEST_MAX_RESULTS));
		
		return AutocompleteResponseCreator.create(request, response, getServletContext().getRealPath("/"), type, method, endpoint, partialCompletion, maxResults);
	}
}
