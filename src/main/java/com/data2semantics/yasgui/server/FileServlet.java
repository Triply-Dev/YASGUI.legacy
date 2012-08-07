package com.data2semantics.yasgui.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
			ResultSet resultSet = SparqlService.query(endpoint, query);
			String fileName = "yasgui." + format;
			OutputStream out = response.getOutputStream();
			if (format.equals(Output.OUTPUT_CSV)) {
				response.setContentType("text/csv");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
		        
				ResultSetFormatter.outputAsCSV(out, resultSet);
				
			} else if (format.equals(Output.OUTPUT_JSON)) {
				response.setContentType("application/json");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
				ResultSetFormatter.outputAsJSON(out, resultSet);
				
			} else if (format.equals(Output.OUTPUT_XML)) {
				response.setContentType("application/sparql-results+xml");
				response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
				ResultSetFormatter.outputAsXML(out, resultSet);
				
			}
		} else {
			PrintWriter out = response.getWriter();
			response.setContentType("text/html");
			out.println("this is First servlet Example " + format + endpoint + query);
		}
	}
}
