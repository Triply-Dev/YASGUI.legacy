package com.data2semantics.yasgui.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONException;

import com.data2semantics.yasgui.server.db.DbHelper;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;

public class QueryPropertyExtractor {
	
	private Query query;
	private String endpoint;
	private DbHelper dbHelper;
	private Set<String> properties = new HashSet<String>();
	public QueryPropertyExtractor(DbHelper dbHelper, String query, String endpoint) {
		this.query = QueryFactory.create(query);
		this.endpoint = endpoint;
	}
	
	public void analyzeAndStore() throws SQLException {
		analyzeQuery();
		storeProperties();
	}
	
	private void analyzeQuery() {
		
	}
	
	private void storeProperties() throws SQLException {
		if (properties.size() > 0) {
			dbHelper.storePropertiesFromQueryAnalysis(endpoint, "lazy", properties);
		}
	}
	
	public static void store(DbHelper dbHelper, String query, String endpoint) throws SQLException {
		QueryPropertyExtractor extractor = new QueryPropertyExtractor(dbHelper, query, endpoint);
		extractor.analyzeAndStore();
		
	}
	public static void main (String[] args) throws ClassNotFoundException, FileNotFoundException, JSONException, SQLException, IOException, ParseException {
		DbHelper dbHelper = new DbHelper(new File("src/main/webapp/"));
		String query = "SELECT * WHERE {?x <http://sdfsdf> ?g}";
		QueryPropertyExtractor.store(dbHelper, query, "http://blaat");
	}
}
