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
