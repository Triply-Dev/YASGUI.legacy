package com.data2semantics.yasgui.server.fetchers.endpoints;

/*
 * #%L
 * YASGUI
 * %%
 * Copyright (C) 2013 Laurens Rietveld
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.io.IOException;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.data2semantics.yasgui.server.SparqlService;
import com.data2semantics.yasgui.shared.Endpoints;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class MondecaFetcher {
	
	private static String ENDPOINT_MONDECA = "http://labs.mondeca.com/endpoint/ends";
	public static String fetch() throws JSONException, IOException {
		JSONArray endpoints = new JSONArray();
		ResultSet resultSet = SparqlService.query(ENDPOINT_MONDECA, getMondecaQuery());
		while (resultSet.hasNext()) {
			JSONObject endpointObject = new JSONObject();
			QuerySolution querySolution = resultSet.next();
			Iterator<String> varNames = querySolution.varNames();
			while (varNames.hasNext()) {
				String varName = varNames.next();
				endpointObject.put(varName, EndpointsFetcher.getBindingValueAsString(querySolution.get(varName)));
			}
			endpoints.put(endpointObject);
		}
		if (endpoints.length() == 0) {
			throw new IOException("No endpoints retrieved from Mondeca");
		}
		return endpoints.toString();
	}
	
	private static String getMondecaQuery() {
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
	
	public static void main(String[] args) {
		try {
			System.out.println(MondecaFetcher.fetch());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
