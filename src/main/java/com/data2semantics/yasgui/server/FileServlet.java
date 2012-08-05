package com.data2semantics.yasgui.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.data2semantics.yasgui.shared.Output;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class FileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// Use "request" to read incoming HTTP headers (e.g. cookies)
		// and HTML form data (e.g. data the user entered and submitted)
		String format = request.getParameter("format");
		String query = request.getParameter("query");
		String endpoint = request.getParameter("endpoint");
		
		if (format != null && format.length() > 0 && query != null && query.length() > 0 && endpoint != null && endpoint.length() > 0) {
			ResultSet resultSet = QueryService.query(endpoint, query);
			OutputStream out = response.getOutputStream();
			if (format.equals(Output.OUTPUT_CSV)) {
				ResultSetFormatter.outputAsCSV(out, resultSet);
				response.setContentType("text/csv");
			} else if (format.equals(Output.OUTPUT_JSON)) {
				ResultSetFormatter.outputAsJSON(out, resultSet);
				response.setContentType("application/json");
			} else if (format.equals(Output.OUTPUT_XML)) {
				ResultSetFormatter.outputAsXML(out, resultSet);
				response.setContentType("application/sparql-results+xml");
			}
		} else {
			PrintWriter out = response.getWriter();
			response.setContentType("text/html");
			out.println("this is First servlet Example " + format + endpoint + query);
		}
	}
}
