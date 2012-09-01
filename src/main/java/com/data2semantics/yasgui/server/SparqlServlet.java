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
		if (query != null && query.length() > 0 && endpoint != null && endpoint.length() > 0) {
			HttpClient client = new DefaultHttpClient();
			HttpPost post = new HttpPost(endpoint);
			PrintWriter out = response.getWriter();
			try {

				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
				nameValuePairs.add(new BasicNameValuePair("query", query));

				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse endpointResponse = client.execute(post);
				BufferedReader rd = new BufferedReader(new InputStreamReader(endpointResponse.getEntity().getContent()));

				String line;
				while ((line = rd.readLine()) != null) {
					out.println(line);
					System.out.println(line);
				}
			} catch (IOException e) {
				onError(response, Helper.getExceptionStackTraceAsString(e));
			}
			response.setContentType("application/sparql-results+json");
			out.close();
		} else {
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
			onError(response, "Invalid query parameters: \n" + errorMsg);
		}
	}
	
	private void onError(HttpServletResponse response, String message) throws IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		out.println(message);
		out.close();
	}
}
