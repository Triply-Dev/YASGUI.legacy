package com.data2semantics.yasgui.server;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

public class QueryService {

	private String endpoint;
	private String queryString;
	private ResultSet resultSet;
	public QueryService(String endpoint, String queryString) {
		this.endpoint = endpoint;
		this.queryString = queryString;
	}
	
	public ResultSet execute() {
		resultSet = query(endpoint, queryString);
		return resultSet;
	}


	
	public void printResult() {
		ResultSetFormatter.out(System.out, resultSet);
	}
	
	public String getResultsAsString() {
		return ResultSetFormatter.asText(resultSet);
	}
	
	private ResultSet query(String endpoint, String queryString) {
		Query query = QueryFactory.create(queryString);
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(endpoint, query);
		ResultSet results = queryExecution.execSelect();
		return results;
	}
}
