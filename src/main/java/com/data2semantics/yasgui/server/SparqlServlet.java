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
		String contentType = "application/sparql-results+json";
		if (query != null && query.length() > 0 && endpoint != null && endpoint.length() > 0) {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(endpoint);
			PrintWriter out = response.getWriter();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			nameValuePairs.add(new BasicNameValuePair("query", query));
			// Some endpoints (dbpedia?) use separate parameter for accept content type
			nameValuePairs.add(new BasicNameValuePair("format", contentType));

			post.setHeader("Accept", contentType);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			HttpResponse endpointResponse = client.execute(post);
			int endpointStatusCode = endpointResponse.getStatusLine().getStatusCode();
			if (endpointStatusCode > 400) {
				//only return this statuscode when it is an error. Redirection codes (e.g. 302) are allowed I guess
				response.setStatus(endpointStatusCode);
			} else {
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
