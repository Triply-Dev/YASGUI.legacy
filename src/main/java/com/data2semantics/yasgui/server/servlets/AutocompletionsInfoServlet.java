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

import com.data2semantics.yasgui.shared.autocompletions.AutocompletionConfigCols;
import com.data2semantics.yasgui.shared.autocompletions.FetchType;
import com.smartgwt.client.rpc.RPCResponse;

public class AutocompletionsInfoServlet extends HttpServlet {

	private static final long serialVersionUID = -5195940814280357118L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			checkRequest(request);
		} catch (IllegalArgumentException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
		}
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", new Date().getTime());
		response.setContentType("application/json");
		try {
			PrintWriter out = response.getWriter();
			out.println(getJson(request, response).toString());
			out.close();
		} catch(Exception e) {
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

	private JSONObject getJson(HttpServletRequest request, HttpServletResponse response) throws JSONException {
		JSONObject resultObject = new JSONObject();
		
		JSONObject jsonResponse = new JSONObject();
		jsonResponse.put("status", RPCResponse.STATUS_SUCCESS);
		jsonResponse.put("startRow", 0);
		jsonResponse.put("endRow", 1);
		jsonResponse.put("totalRows", 1);
		
		
//		  {
//			     "response": {
//			        "status": 0,
//			        "startRow": 0,
//			        "endRow": 76,
//			        "totalRows": 546,
//			        "data": [
//			            {"field1": "value", "field2": "value"},
//			            {"field1": "value", "field2": "value"},
//			            ... 76 total records ...
//			        ]
//			     }
//			  }
		JSONArray dataArray = new JSONArray();
		JSONObject dataObj = new JSONObject();
		dataObj.put(AutocompletionConfigCols.TYPE.getKey(), FetchType.PROPERTIES.getSingular());
		dataObj.put(AutocompletionConfigCols.ENDPOINT.getKey(), "http:example");
		dataObj.put(AutocompletionConfigCols.METHOD_QUERY.getKey(), 11);
		dataObj.put(AutocompletionConfigCols.METHOD_QUERY_RESULTS.getKey(), 22);
		dataArray.put(dataObj);
		jsonResponse.put("data", dataObj);
		resultObject.put("response", jsonResponse);
		return resultObject;
	}

	
}
