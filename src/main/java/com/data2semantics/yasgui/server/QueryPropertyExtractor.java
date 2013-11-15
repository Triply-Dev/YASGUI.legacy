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
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;

import com.data2semantics.yasgui.server.db.DbHelper;
import com.data2semantics.yasgui.server.queryanalysis.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class QueryPropertyExtractor {
	
	private Query query;
	private String endpoint;
	private DbHelper dbHelper;
	private Set<String> properties = new HashSet<String>();
	private Set<String> possibleProperties = new HashSet<String>();
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
		if (properties.size() > 0 || possibleProperties.size() > 0) {
			removeExistingProperties();
			
			checkPossibleProperties();
			if (Helper.checkEndpointAccessibility(endpoint)) {
				storeProperties();
			} else {
				System.out.println("not accessible: " + endpoint);
			}
		}
		
	}
	
	private void checkPossibleProperties() {
		if (debug) System.out.println("checking possible properties via query");
		for (String possibleProperty: possibleProperties) {
			try {
				String sparqlString = "ASK {?sub <" + possibleProperty + "> ?obj}";
				Query query = Query.create(sparqlString);
				
				QueryEngineHTTP qExecution = QueryExecutionFactory.createServiceRequest(endpoint, query);
				if (qExecution.execAsk()) {
					properties.add(possibleProperty);
				}
			} catch (Exception e) {
				//just fail silently. doesnt matter if this one query doesnt get added
			}
		}
	}
	private void removeExistingProperties() throws SQLException {
		if (debug) System.out.println("remove already existing properties");
		Set<String> checkStringSet = new HashSet<String>();
		checkStringSet.addAll(properties);
		checkStringSet.addAll(possibleProperties);
		Map<String, Boolean> arePropertiesAdded = dbHelper.arePropertiesAdded(endpoint, checkStringSet, "lazy");
		for (Entry<String, Boolean> entry: arePropertiesAdded.entrySet()) {
			if (entry.getValue()) {
				//property is already added. delete it!
				if (properties.contains(entry.getKey())) properties.remove(entry.getKey());
				if (possibleProperties.contains(entry.getKey())) possibleProperties.remove(entry.getKey());
			}
		}
	}
	
	
	private void analyzeQuery() {
		if (debug) System.out.println("analyzing query");
		properties = query.getProperties();
		possibleProperties = query.getPossibleProperties();
	}
	
	private void storeProperties() throws SQLException {
		if (debug) System.out.println("storing properties");
		if (properties.size() > 0) {
			dbHelper.storePropertiesFromQueryAnalysis(endpoint, "lazy", properties);
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
		String query = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
				"PREFIX  db:   <http://dbpedia.org/>\n" + 
				"PREFIX  dbo:  <http://dbpedia.org/ontology/>\n" + 
				"PREFIX  dbp:  <http://dbpedia.org/property/>\n" + 
				"PREFIX  dbpedia: <http://dbpedia.org/resource/>\n" + 
				"PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
				"\n" + 
				"SELECT DISTINCT  ?University_Name ?Type\n" + 
				"WHERE\n" + 
				"  {   { ?University_Name dbo:type dbpedia:Private_university .\n" + 
				"        ?University_Name dbo:city|dbo:campus dbpedia:Los_Angeles .\n" + 
				"        dbpedia:Private_university rdfs:label ?Type\n" + 
				"        FILTER langMatches(lang(?Type), \"EN\")\n" + 
				"      }\n" + 
				"    UNION\n" + 
				"      { ?University_Name dbo:type dbpedia:Public_university .\n" + 
				"        ?University_Name dbo:city|dbo:campus dbpedia:Los_Angeles .\n" + 
				"        dbpedia:Public_university rdfs:label ?Type\n" + 
				"        FILTER langMatches(lang(?Type), \"EN\")\n" + 
				"      }\n" + 
				"  }\n" + 
				"LIMIT   10";
		QueryPropertyExtractor.store(dbHelper, query, "http://dbpedia.org/sparql");
	}
}
