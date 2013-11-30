package com.data2semantics.yasgui.server;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;

import com.data2semantics.yasgui.server.db.DbHelper;
import com.data2semantics.yasgui.server.queryanalysis.Query;
import com.data2semantics.yasgui.shared.autocompletions.FetchMethod;
import com.data2semantics.yasgui.shared.autocompletions.FetchType;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class QueryPropertyExtractor {
	
	private Query query;
	private String endpoint;
	private DbHelper dbHelper;
	private HashMap<FetchType, Set<String>> possibles = new HashMap<FetchType, Set<String>>();
	private HashMap<FetchType, Set<String>> certains = new HashMap<FetchType, Set<String>>();
	
	private boolean debug = false;
	public QueryPropertyExtractor(DbHelper dbHelper, String query, String endpoint, boolean debug) {
		this.query = Query.create(query);
		this.endpoint = endpoint;
		this.dbHelper = dbHelper;
		this.debug = debug;
	}
	public QueryPropertyExtractor(DbHelper dbHelper, String query, String endpoint) {
		this(dbHelper, query, endpoint, false);
	}
	
	public void analyzeAndStore() throws SQLException {
		analyzeQuery();
		removeExistingUris(FetchType.CLASSES);
		removeExistingUris(FetchType.PROPERTIES);
		checkPossibles();
		if (Helper.checkEndpointAccessibility(endpoint)) {
			store();
		} else {
			System.out.println("not accessible: " + endpoint);
		}
		
	}
	
	private void checkPossibles() {
		if (debug) System.out.println("checking possibles via query");
		for (FetchType type: possibles.keySet()) {
			
			for (String possible: possibles.get(type)) {
				String sparqlString = null;
				if (type == FetchType.PROPERTIES) {
					sparqlString = "ASK {?sub <" + possible + "> ?obj}";
				} else {
					//class
					sparqlString = ""
							+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
							+ "ASK {[] rdf:type <" + possible + "> }";
				}
				try {
					Query query = Query.create(sparqlString);
					QueryEngineHTTP qExecution = QueryExecutionFactory.createServiceRequest(endpoint, query);
					if (qExecution.execAsk()) {
						certains.get(type).add(possible);
					}
				} catch (Exception e) {
					//fail silently, don't care!
				}
				
			}
		}
	}
	private void removeExistingUris(FetchType type) throws SQLException {
		Set<String> checkStringSet = new HashSet<String>();
		checkStringSet.addAll(certains.get(type));
		checkStringSet.addAll(possibles.get(type));
		if (checkStringSet.size() > 0) {
			Map<String, Boolean> areCompletionsAdded = dbHelper.areAutocompletionsAdded(endpoint, checkStringSet, type, FetchMethod.QUERY_ANALYSIS);
			for (Entry<String, Boolean> entry: areCompletionsAdded.entrySet()) {
				if (entry.getValue()) {
					if (certains.get(type).contains(entry.getKey())) certains.get(type).remove(entry.getKey());
					if (possibles.get(type).contains(entry.getKey())) possibles.get(type).remove(entry.getKey());
				}
			}
		}
	}
	
	
	private void analyzeQuery() {
		if (debug) System.out.println("analyzing query");
		certains.put(FetchType.PROPERTIES, query.getProperties());
		possibles.put(FetchType.PROPERTIES, query.getPossibleProperties());
		certains.put(FetchType.CLASSES, query.getClasses());
		possibles.put(FetchType.CLASSES, query.getPossibleClasses());
	}
	
	private void store() throws SQLException {
		if (debug) System.out.println("storing URIs");
		for (FetchType type: certains.keySet()) {
			if (certains.get(type).size() > 0) {
				if (debug) {
					System.out.println("storing " + certains.get(type).size() + " " + type.getPlural());
				} else {
					dbHelper.storeAutocompletionsFromQueryAnalysis(endpoint, type, FetchMethod.QUERY_ANALYSIS, certains.get(type));
				}
			}
		}
		
	}
	
	public static void store(DbHelper dbHelper, String query, String endpoint) throws SQLException {
		store(dbHelper, query, endpoint, false);
	}
	public static void store(DbHelper dbHelper, String query, String endpoint, boolean debug) throws SQLException {
		QueryPropertyExtractor extractor = new QueryPropertyExtractor(dbHelper, query, endpoint, debug);
		extractor.analyzeAndStore();
	}
	public static void main (String[] args) throws ClassNotFoundException, FileNotFoundException, JSONException, SQLException, IOException, ParseException {
		DbHelper dbHelper = new DbHelper(new File("src/main/webapp/"));
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
				"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" + 
				"PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" + 
				"\n" + 
				"SELECT * WHERE {\n" + 
				"  <http://blaat> rdfs:subClassOf ?obj\n" + 
				"} LIMIT 10";
		QueryPropertyExtractor.store(dbHelper, query, "http://services.data.gov/sparql", true);
	}
}
