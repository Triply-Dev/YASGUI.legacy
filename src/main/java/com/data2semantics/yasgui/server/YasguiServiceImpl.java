package com.data2semantics.yasgui.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.data2semantics.yasgui.client.YasguiService;
import com.data2semantics.yasgui.shared.Output;
import com.data2semantics.yasgui.shared.Settings;
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

	public ResultSetContainer queryGetObject(Settings settings) throws IllegalArgumentException, SparqlRuntimeException {
		ResultSetContainer resultSetContainer = new ResultSetContainer();
		ResultSet resultSet = SparqlService.query(settings.getEndpoint(), settings.getQueryString());
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

	public String fetchPrefixes(boolean forceUpdate) throws IllegalArgumentException {
		JSONArray prefixes = new JSONArray();
		try {
			URI uri = new URI("http://prefix.cc/popular/all.file.json");
			BufferedReader reader = new BufferedReader(new InputStreamReader(uri.toURL().openStream()));
			JSONTokener tokener = new JSONTokener(reader);
			JSONObject jsonObject = new JSONObject(tokener);

			// Get list of keys, and sort them
			@SuppressWarnings("unchecked")
			Iterator<String> keys = jsonObject.keys();
			ArrayList<String> keysList = new ArrayList<String>();
			while (keys.hasNext()) {
				keysList.add(keys.next());
			}
			Collections.sort(keysList);

			// Build new JSONArray (JSONArray is sorted, JSONObject is not)
			// using this keylist
			for (String key : keysList) {
				if (!jsonObject.isNull(key)) {
					prefixes.put(key + ": <" + jsonObject.getString(key) + ">\n");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prefixes.toString();
	}
}
