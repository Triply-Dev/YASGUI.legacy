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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.shared.AutocompleteKeys;

public class AutocompleteServlet extends HttpServlet {
	private static final long serialVersionUID = -8887854790329786302L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String stringToComplete = request.getParameter(AutocompleteKeys.REQUEST_QUERY);
		String type = (request.getParameter(AutocompleteKeys.REQUEST_TYPE) == null? AutocompleteKeys.TYPE_PROPERTY: request.getParameter(AutocompleteKeys.REQUEST_TYPE));
		if (stringToComplete != null && stringToComplete.length() > 0) {
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", new Date().getTime());
			response.setContentType("application/json");
			
			try {
				PrintWriter out = response.getWriter();
				out.println(getJson(type).toString());
				out.close();
			} catch(JSONException e) {
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			}
			
		} else {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No query (" + AutocompleteKeys.REQUEST_QUERY + ")");
		}
		
	}
	
	private JSONObject getJson(String type) throws JSONException {
		JSONObject resultObject = new JSONObject();
		if (type.equals(AutocompleteKeys.TYPE_CLASS)) {
			
		} else {
			resultObject = getPropertyJson();
		}
		
		return resultObject;
	}
	
	private JSONObject getPropertyJson() throws JSONException {
		JSONObject resultObject = new JSONObject();
		
		JSONObject propertyMethodObject = new JSONObject();
		JSONArray propertyMethodResults = new JSONArray();
		propertyMethodResults.put("http://xmlns.com/foaf/0.1/prop");
		propertyMethodResults.put("http://xmlns.com/foaf/0.1/prop2");
		propertyMethodResults.put("http://xmlns.com/foaf/0.1/prop3");
		propertyMethodObject.put(AutocompleteKeys.RESPONSE_RESULTS, propertyMethodResults);
		propertyMethodObject.put(AutocompleteKeys.RESPONSE_RESULT_SIZE, 3);
		resultObject.put(AutocompleteKeys.RESPONSE_METHOD_PROPERTY, propertyMethodObject);
		
		JSONObject lazyMethodObject = new JSONObject();
		JSONArray lazyMethodResults = new JSONArray();
		lazyMethodResults.put("http://xmlns.com/foaf/0.1/lazy1");
		lazyMethodResults.put("http://xmlns.com/foaf/0.1/lazy2");
		lazyMethodResults.put("http://xmlns.com/foaf/0.1/lazy3");
		lazyMethodResults.put("http://xmlns.com/foaf/0.1/lazy4");
		lazyMethodObject.put(AutocompleteKeys.RESPONSE_RESULTS, lazyMethodResults);
		lazyMethodObject.put(AutocompleteKeys.RESPONSE_RESULT_SIZE, 4);
		resultObject.put(AutocompleteKeys.RESPONSE_METHOD_LAZY, lazyMethodObject);
		return resultObject;
	}


}
