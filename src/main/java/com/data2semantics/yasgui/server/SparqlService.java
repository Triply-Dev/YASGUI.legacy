/*******************************************************************************
 * Copyright (c)  2012 Laurens Rietveld
 * 
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
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
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class SparqlService {

	public static ResultSet query(String endpoint, String queryString) throws SparqlException {
		return query(endpoint, queryString, new HashMap<String,String>());
	}
	
	@SuppressWarnings("rawtypes")
	public static ResultSet query(String endpoint, String queryString, HashMap<String, String> args) throws SparqlException {
		ResultSet results;
		try {
			Query query = QueryFactory.create(queryString);
			QueryEngineHTTP queryExecution = new QueryEngineHTTP(endpoint, query);
			Iterator iterator = args.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry) iterator.next();
				String key = (String) entry.getKey();
				String val = (String)entry.getValue();
				queryExecution.addParam(key, val);
			}
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
		if (format.equals(Output.OUTPUT_RAW_RESPONSE)) {
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
