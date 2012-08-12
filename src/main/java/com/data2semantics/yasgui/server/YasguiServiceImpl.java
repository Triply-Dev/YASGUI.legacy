package com.data2semantics.yasgui.server;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import com.data2semantics.yasgui.client.YasguiService;
import com.data2semantics.yasgui.shared.Output;
import com.data2semantics.yasgui.shared.SparqlRuntimeException;
import com.data2semantics.yasgui.shared.rdf.RdfNodeContainer;
import com.data2semantics.yasgui.shared.rdf.ResultSetContainer;
import com.data2semantics.yasgui.shared.rdf.SolutionContainer;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFNode;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class YasguiServiceImpl extends RemoteServiceServlet implements YasguiService {


	public String queryGetText(String endpoint, String queryString, String format) throws IllegalArgumentException, SparqlRuntimeException {
		String result = "";
		ResultSet resultSet = SparqlService.query(endpoint, queryString);
		if (format.equals(Output.OUTPUT_JSON)) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ResultSetFormatter.outputAsJSON(baos, resultSet);
			try {
				result = baos.toString("UTF-8");
			} catch (UnsupportedEncodingException e) {
				
			}
		} else if (format.equals(Output.OUTPUT_XML)) {
			result = ResultSetFormatter.asXMLString(resultSet);
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
	
	public ResultSetContainer queryGetObject(String endpoint, String queryString) throws IllegalArgumentException, SparqlRuntimeException  {
		ResultSetContainer resultSetContainer = new ResultSetContainer();
		ResultSet resultSet = SparqlService.query(endpoint, queryString);
		resultSetContainer.setResultVars((ArrayList<String>)resultSet.getResultVars());
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
				} else if (rdfNode.isAnon()){
					rdfNodeContainer.setIsAnon(true);
					rdfNodeContainer.setValue(rdfNode.asResource().getURI());
				} else {
					//is uri
					rdfNodeContainer.setIsUri(true);
					rdfNodeContainer.setValue(rdfNode.asResource().getURI());
					
				}
				
				
				solutionContainer.addRdfNodeContainer(rdfNodeContainer);
			}
			resultSetContainer.addQuerySolution(solutionContainer);
		}
		return resultSetContainer;
	}
}
