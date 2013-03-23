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

public class CkanFetcher {
	
	private static String ENDPOINT_CKAN = "http://semantic.ckan.net/sparql";
	
	public static String fetch() throws JSONException, IOException {
		JSONArray endpoints = new JSONArray();
		ResultSet resultSet = queryCkan();
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
			throw new IOException("No endpoints retrieved from Ckan");
		}
		return endpoints.toString();
	}
	
	public static ResultSet queryCkan() {
		return SparqlService.query(ENDPOINT_CKAN, getCkanQuery());
	}
	

	
	private static String getCkanQuery() {
		return "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" 
				+ "		PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
				+ "		PREFIX dcat: <http://www.w3.org/ns/dcat#>\n" 
				+ "		PREFIX dcterms: <http://purl.org/dc/terms/>\n" + "\n"
				+ "		SELECT DISTINCT ?"	+ Endpoints.KEY_DATASETURI	+ " ?" + Endpoints.KEY_TITLE + " ?"	+ Endpoints.KEY_DESCRIPTION + " ?" + Endpoints.KEY_ENDPOINT	+ "  {\n"
				+ "		  ?" + Endpoints.KEY_DATASETURI	+ " dcat:distribution ?distribution.\n"
				+ "		?distribution dcterms:format ?format.\n"
				+ "		?format rdf:value 'api/sparql'.\n"
				+ "		?distribution dcat:accessURL ?"	+ Endpoints.KEY_ENDPOINT + ".\n"
				+ "		?" + Endpoints.KEY_DATASETURI + " dcterms:title ?" + Endpoints.KEY_TITLE + ";\n"
				+ "			dcterms:description ?" + Endpoints.KEY_DESCRIPTION + ".\n" +
				"		} ORDER BY ?" + Endpoints.KEY_TITLE;
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(CkanFetcher.fetch());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
