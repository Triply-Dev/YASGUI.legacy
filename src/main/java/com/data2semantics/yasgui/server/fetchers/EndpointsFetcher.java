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
package com.data2semantics.yasgui.server.fetchers;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.server.Helper;
import com.data2semantics.yasgui.server.SparqlService;
import com.data2semantics.yasgui.shared.Endpoints;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class EndpointsFetcher {
	
	private static String ENDPOINT = "http://labs.mondeca.com/endpoint/ends";
	private static String CACHE_FILENAME = "endpoints.json";
	private static String CACHE_FILENAME_BAK = "endpoints.json.bak";
	private static int CACHE_EXPIRES_DAYS = 14;
	
	public static String fetch(boolean forceUpdate, File cacheDir) throws JSONException, IOException {
		String result = "";
		if (!cacheDir.exists()) {
			cacheDir.mkdir();
			forceUpdate = true;
		}
		File file = new File(cacheDir + "/" + CACHE_FILENAME);
		
		if (forceUpdate || Helper.needUpdating(file, CACHE_EXPIRES_DAYS)) {
			file.createNewFile();
			try {
				result = getEndpointsAsJsonArrayString();
				if (result.length() > 0) {
					Helper.writeFile(file, result);
				}
			} catch (Exception e) {
				//probably endpoints problems with ckan or mondeca... just use our backup
				//pretty ugly, but at least we have something...
				result = Helper.readFile(new File(cacheDir + "/" + CACHE_FILENAME_BAK));
			}
			
		} else {
			try {
				result = Helper.readFile(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	
	/**
	 * Query CKAN for endpoints, add them to json array object, and serialize as string (for caching on server)
	 * 
	 * @return
	 * @throws JSONException
	 */
	private static String getEndpointsAsJsonArrayString() throws JSONException {
		JSONArray endpoints = new JSONArray();
		ResultSet resultSet = SparqlService.query(ENDPOINT, getQuery());
		while (resultSet.hasNext()) {
			JSONObject endpoint = new JSONObject();
			
			QuerySolution querySolution = resultSet.next();
			Iterator<String> varNames = querySolution.varNames();
			while (varNames.hasNext()) {
				String varName = varNames.next();
				endpoint.put(varName, getBindingValueAsString(querySolution.get(varName)));
			}
			endpoints.put(endpoint);
		}
		
		
		return endpoints.toString();
	}
	
	
	private static String getBindingValueAsString(RDFNode node) {
		if (node.isLiteral()) {
			return node.asLiteral().getString();
		} else if (node.isURIResource()) {
			return node.asResource().getURI();
		} else {
			return node.toString();
		}
	}
	
	private static String getQuery() {
		return "PREFIX xsd:<http://www.w3.org/2001/XMLSchema#>\n" + 
				"PREFIX dcterms:<http://purl.org/dc/terms/>\n" + 
				"PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX ends:<http://labs.mondeca.com/vocab/endpointStatus#>\n" + 
				"PREFIX void:<http://rdfs.org/ns/void#>\n" + 
				"\n" + 
				"SELECT DISTINCT ?" + Endpoints.KEY_DATASETURI + " ?"  + Endpoints.KEY_TITLE + " ?" + Endpoints.KEY_DESCRIPTION + " ?" + Endpoints.KEY_ENDPOINT + " \n" + 
				"WHERE{\n" + 
				"	?" + Endpoints.KEY_DATASETURI + " void:sparqlEndpoint ?" + Endpoints.KEY_ENDPOINT + ".\n" + 
				"	?" + Endpoints.KEY_DATASETURI + " ends:status ?status.\n" + 
				"	?status ends:statusIsAvailable \"true\"^^xsd:boolean.\n" + 
				"  	?" + Endpoints.KEY_DATASETURI + " dcterms:title ?" + Endpoints.KEY_DESCRIPTION + ".\n" + 
				"BIND(?" + Endpoints.KEY_DESCRIPTION + " AS ?" + Endpoints.KEY_TITLE + ")" +
				//"  		dcterms:identifier ?" + Endpoints.KEY_TITLE + ".\n" + 
				"} \n";

	}
}
