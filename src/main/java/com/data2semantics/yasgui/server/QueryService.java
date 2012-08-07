package com.data2semantics.yasgui.server;

import com.data2semantics.yasgui.shared.SparqlRuntimeException;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.ResultSet;

public class QueryService {

	
	public static ResultSet query(String endpoint, String queryString) throws SparqlRuntimeException {
		ResultSet results;
		try {
			Query query = QueryFactory.create(queryString);
			QueryExecution queryExecution = QueryExecutionFactory.sparqlService(endpoint, query);
			results = queryExecution.execSelect();
		} catch (QueryParseException e) {
			throw new SparqlRuntimeException(e.getMessage(), e);
		}
		return results;
	}
}
