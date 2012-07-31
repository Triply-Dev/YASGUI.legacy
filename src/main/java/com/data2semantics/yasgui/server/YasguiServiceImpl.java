package com.data2semantics.yasgui.server;

import com.data2semantics.yasgui.client.YasguiService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class YasguiServiceImpl extends RemoteServiceServlet implements YasguiService {

	public String greetServer(String input) throws IllegalArgumentException {
		return "Hello";
	}

	public String query(String endpoint, String queryString) throws IllegalArgumentException {
		String queryResult = "sdfsdf";
		try {
			QueryService query = new QueryService(endpoint, queryString);
			query.execute();
			//query.getResultsAsString();
			//queryResult = query.getResultsAsString();
		} catch (Exception e) {
			queryResult = e.getMessage();
		}
		return queryResult;
	}

	

}
