package com.data2semantics.yasgui.server.fetchers;

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

import org.json.JSONException;

import com.data2semantics.yasgui.server.SparqlService;
import com.data2semantics.yasgui.server.db.DbHelper;
import com.data2semantics.yasgui.shared.exceptions.PossiblyNeedPaging;
import com.hp.hpl.jena.query.ResultSet;


public class PropertiesFetcher {
	private static String METHOD = "property";
	private DbHelper dbHelper;
	private String endpoint;
	public PropertiesFetcher(File configDir, String endpoint) throws ClassNotFoundException, FileNotFoundException, JSONException, SQLException, IOException, ParseException {
		dbHelper = new DbHelper(configDir);
		this.endpoint = endpoint;
	}
	public void fetch() throws IOException, SQLException {
		
		try {
			System.out.println("fetching regular properties for endpoint " + endpoint);
			doRegularFetch();
		} catch (PossiblyNeedPaging ep) {
			try {
				System.out.println("fetching paged properties for endpoint " + endpoint);
				doPagingFetch(ep.getQueryCount());
			} catch (Exception e) {
				dbHelper.setPropertyLogStatus(endpoint, "fetching", e.getMessage(), true);
			}
		} catch (SQLException e) {
			dbHelper.setPropertyLogStatus(endpoint, "fetching", e.getMessage());
			throw e;
		}
		
	}
	
	private void doRegularFetch() throws PossiblyNeedPaging, SQLException {
		dbHelper.setPropertyLogStatus(endpoint, "fetching");
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
				"SELECT DISTINCT ?property WHERE{\n" + 
				"  ?property a rdf:Property\n" + 
				"}";
		System.out.println("exec query");
		ResultSet resultSet = SparqlService.query(endpoint, query);
		//ok. so we know this paging query returns results (otherwise would have thrown an exception). 
		//first clear our properties table of previous results
		dbHelper.clearProperties(endpoint, "property");
		
		
		System.out.println("finished exec query");
		dbHelper.storeProperties(endpoint, METHOD, resultSet);
		dbHelper.setPropertyLogStatus(endpoint, "successful");
	}
	
	private String getPaginationQuery(int iterator, int count) {
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
				"SELECT DISTINCT ?property WHERE{\n" + 
				"  ?property a rdf:Property\n" + 
				"} ORDER BY ?property ";
		query += "LIMIT " + count;
		query += " OFFSET " + (iterator * count);
		return query;
	}
	
	private void doPagingFetch(int count) throws SQLException {
		dbHelper.setPropertyLogStatus(endpoint, "fetching", null, true);
		int iterator = 0;
		boolean needPaging = true;
		while (needPaging) {
			String query = getPaginationQuery(iterator, count);
			ResultSet resultSet = SparqlService.query(endpoint, query);
			if (iterator == 0) {
				//ok. so we know this paging query returns results (otherwise would have thrown an exception). 
				//first clear our properties table of previous results
				dbHelper.clearProperties(endpoint, "property");
			}
			needPaging = false;
			try {
				dbHelper.storeProperties(endpoint, METHOD, resultSet);
			} catch (PossiblyNeedPaging e) {
				iterator++;
				needPaging = true;
			}
		}
	}
	
	public static boolean doubtfullResultSet(int count) {
		return (count > 0 && count % 100 == 0);
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, JSONException, SQLException, ParseException  {
		PropertiesFetcher fetcher = new PropertiesFetcher(new File("src/main/webapp/"), "http://dbpedia.org/sparql");
		fetcher.fetch();
	}
}
