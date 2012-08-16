package com.data2semantics.yasgui.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import com.data2semantics.yasgui.client.YasguiService;
import com.data2semantics.yasgui.shared.Output;
import com.data2semantics.yasgui.shared.Prefix;
import com.data2semantics.yasgui.shared.Settings;
import com.data2semantics.yasgui.shared.SparqlRuntimeException;
import com.data2semantics.yasgui.shared.rdf.RdfNodeContainer;
import com.data2semantics.yasgui.shared.rdf.ResultSetContainer;
import com.data2semantics.yasgui.shared.rdf.SolutionContainer;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
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


	public String queryGetText(Settings settings) throws IllegalArgumentException, SparqlRuntimeException {
		String format = settings.getOutputFormat();
		String result = "";
		ResultSet resultSet = SparqlService.query(settings.getEndpoint(), settings.getQueryString());
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
	
	public ResultSetContainer queryGetObject(Settings settings) throws IllegalArgumentException, SparqlRuntimeException  {
		ResultSetContainer resultSetContainer = new ResultSetContainer();
		ResultSet resultSet = SparqlService.query(settings.getEndpoint(), settings.getQueryString());
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

	@Override
	public String fetchPrefixes(boolean forceUpdate) throws IllegalArgumentException {
		JSONArray prefixes = new JSONArray();
		DefaultHttpClient httpclient = new DefaultHttpClient();

        HttpGet httpget = new HttpGet("http://prefix.cc/popular/all.json");
		try {
			HttpResponse response = httpclient.execute(httpget);
			HttpEntity entity = response.getEntity();
			
			//Gives back json, but in html... (setting accept header doesnt help). Need to parse it manually. blegh
			String regex = "(.*<pre class=\"source json\">)(.*)(</pre>.*)";
			String updated = Pattern.compile(regex, Pattern.DOTALL | Pattern.MULTILINE).matcher(EntityUtils.toString(entity)).replaceAll("$2");
			
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
		
		prefixes.put("aers: <http://aers.data2semantics.org/resource/>\n");
		prefixes.put("rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n");
		return prefixes.toString();
	}
}
