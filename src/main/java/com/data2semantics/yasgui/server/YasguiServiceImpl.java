package com.data2semantics.yasgui.server;

import java.util.Iterator;
import com.data2semantics.yasgui.client.YasguiService;
import com.data2semantics.yasgui.shared.RdfNodeContainer;
import com.data2semantics.yasgui.shared.ResultSetContainer;
import com.data2semantics.yasgui.shared.SolutionContainer;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class YasguiServiceImpl extends RemoteServiceServlet implements YasguiService {


	public String queryGetJson(String endpoint, String queryString) {
		ResultSet resultSet = query(endpoint, queryString);
//		ResultSetFormatter.outputAsJSON(resultSet);
//		ResultSetFormatter.outputAsCSV(resultSet);
		return ResultSetFormatter.asText(resultSet);
	}
	
	public String queryGetXml(String endpoint, String queryString) {
		return "";
	}
	
	public ResultSetContainer queryGetObject(String endpoint, String queryString) {
		ResultSetContainer resultSetContainer = new ResultSetContainer();
		ResultSet resultSet = query(endpoint, queryString);
		resultSetContainer.setResultVars(resultSet.getResultVars());
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			Iterator<String> varnames = querySolution.varNames();
			SolutionContainer solutionContainer = new SolutionContainer();
			while (varnames.hasNext()) {
				String varName = varnames.next();
				RDFNode rdfNode = querySolution.get(varName);
				String value = (String) rdfNode.visitWith(new CustomRdfVisitor());
				RdfNodeContainer rdfNodeContainer = new RdfNodeContainer();
				rdfNodeContainer.setValue(value);
				rdfNodeContainer.setVarName(varName);
				solutionContainer.addRdfNodeContainer(rdfNodeContainer);
			}
			resultSetContainer.addQuerySolution(solutionContainer);
		}
		return resultSetContainer;
	}
	
	private ResultSet query(String endpoint, String queryString) throws IllegalArgumentException {
		ResultSet queryResult;
		Query query = QueryFactory.create(queryString);
		QueryExecution queryExecution = QueryExecutionFactory.sparqlService(endpoint, query);
		queryResult = queryExecution.execSelect();
		return queryResult;
	}
	
	

	

}
