package com.data2semantics.yasgui.server;

import com.data2semantics.yasgui.client.YasguiService;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class YasguiServiceImpl extends RemoteServiceServlet implements YasguiService {

	public String greetServer(String input) throws IllegalArgumentException {
		return "Hello";
	}

	public String query(String endpoint, String queryString) throws IllegalArgumentException {
		String queryResult = "";
		try {
			Query query = QueryFactory.create(queryString);
			QueryExecution queryExecution = QueryExecutionFactory.sparqlService(endpoint, query);
			ResultSet resultSet = queryExecution.execSelect();
			queryResult = ResultSetFormatter.asText(resultSet);
		} catch (Exception e) {
			queryResult = e.getMessage();
		}
		return queryResult;
	}

	

}
