package com.data2semantics.yasgui.server;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;

import com.data2semantics.yasgui.shared.Output;
import com.data2semantics.yasgui.shared.exceptions.SparqlException;
import com.data2semantics.yasgui.shared.rdf.RdfNodeContainer;
import com.data2semantics.yasgui.shared.rdf.ResultSetContainer;
import com.data2semantics.yasgui.shared.rdf.SolutionContainer;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QueryParseException;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class SparqlService {

	
	public static ResultSet query(String endpoint, String queryString) throws SparqlException {
		ResultSet results;
		try {
			Query query = QueryFactory.create(queryString);
			QueryExecution queryExecution = QueryExecutionFactory.sparqlService(endpoint, query);
			results = queryExecution.execSelect();
		} catch (QueryParseException e) {
			throw new SparqlException(e.getMessage(), e);
		}
		return results;
	}
	
	public static ResultSetContainer getAsResultSetContainer(ResultSet resultSet) {
		ResultSetContainer resultSetContainer = new ResultSetContainer();
		resultSetContainer.setResultVars((ArrayList<String>) resultSet.getResultVars());
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			Iterator<String> varnames = querySolution.varNames();
			SolutionContainer solutionContainer = new SolutionContainer();
			while (varnames.hasNext()) {
				String varName = varnames.next();
				RDFNode rdfNode = querySolution.get(varName);
				RdfNodeContainer rdfNodeContainer = new RdfNodeContainer();
				rdfNodeContainer.setVarName(varName);
				if (rdfNode.isLiteral()) {
					Literal literal = rdfNode.asLiteral();
					rdfNodeContainer.setIsLiteral(true);
					rdfNodeContainer.setValue(literal.getString());
					rdfNodeContainer.setDatatype(literal.getDatatypeURI());
				} else if (rdfNode.isAnon()) {
					rdfNodeContainer.setIsAnon(true);
					rdfNodeContainer.setValue(rdfNode.asResource().getURI());
				} else {
					// is uri
					rdfNodeContainer.setIsUri(true);
					rdfNodeContainer.setValue(rdfNode.asResource().getURI());

				}

				solutionContainer.addRdfNodeContainer(rdfNodeContainer);
			}
			resultSetContainer.addQuerySolution(solutionContainer);
		}
		return resultSetContainer;
	}
	
	public static String getAsFormattedString(ResultSet resultSet, String format) throws IllegalArgumentException, SparqlException {
		String result = "";
		if (format.equals(Output.OUTPUT_RESPONSE)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ResultSetFormatter.outputAsJSON(baos, resultSet);
			try {
				result = baos.toString("UTF-8");
			} catch (UnsupportedEncodingException e) {
			}
		} else if (format.equals(Output.OUTPUT_CSV)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ResultSetFormatter.outputAsCSV(baos, resultSet);
			try {
				result = baos.toString("UTF-8");
			} catch (UnsupportedEncodingException e) {

			}
		} else {
			throw new IllegalArgumentException("No valid output format given as parameter");
		}
		return result;
	}
}
