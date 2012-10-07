/*******************************************************************************
 * Copyright (c)  2012 Laurens Rietveld
 * 
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.data2semantics.yasgui.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

public class SparqlServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String query = request.getParameter("query");
		String endpoint = request.getParameter("endpoint");
		String accept = "application/sparql-results+json";
		if (query != null && query.length() > 0 && endpoint != null && endpoint.length() > 0) {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(endpoint);
			PrintWriter out = response.getWriter();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("query", query));
			// Some endpoints (dbpedia?) use separate parameter for accept content type
			nameValuePairs.add(new BasicNameValuePair("format", accept));
			
			post.setHeader("Accept", accept);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			HttpResponse endpointResponse = client.execute(post);
			int endpointStatusCode = endpointResponse.getStatusLine().getStatusCode();
			if (endpointStatusCode >= 400) {
				//only return this statuscode when it is an error. Redirection codes (e.g. 302) are allowed I guess
				String reason = endpointResponse.getStatusLine().getReasonPhrase();
				
				//reasonphrase is often not verbose enough, so append content of page (which often contains actual verbose error msg)
				BufferedReader rd = new BufferedReader(new InputStreamReader(endpointResponse.getEntity().getContent()));
				
				String line;
				while ((line = rd.readLine()) != null) {
					reason += "<br/>" + line;
				}
				response.sendError(endpointStatusCode, reason);
				
			} else {
				//Header should be sparql json (we asked for that). It might be something else (sparql xml?)
				//Copy response header to the new response
				Header[] headers = endpointResponse.getHeaders("Content-Type");
				String contentType = accept;
				if (headers.length > 0) {
					//Just get first defined content type
					contentType = headers[0].getValue();
				}
				BufferedReader rd = new BufferedReader(new InputStreamReader(endpointResponse.getEntity().getContent()));
	
				String line;
				while ((line = rd.readLine()) != null) {
					out.println(line);
				}
				response.setContentType(contentType);
				out.close();
			}
		} else {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			String errorMsg = "";
			if (endpoint == null) {
				errorMsg += "No endpoint provided as parameter\n";
			} else {
				errorMsg += "Endpoint: '" + endpoint + "'\n";
			}
			if (query == null) {
				errorMsg += "No sparql provided as parameter\n";
			} else {
				errorMsg += "Sparql query: '" + query + "'\n";
			}
			onError("Invalid query parameters: \n" + errorMsg);
		}
	}

	private void onError(String message) throws IOException {
		System.out.println("Invalid query parameters: \n" + message);
	}
}
