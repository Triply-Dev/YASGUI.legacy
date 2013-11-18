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

import com.data2semantics.yasgui.shared.exceptions.PossiblyNeedPaging;
import com.hp.hpl.jena.query.ResultSet;


public class PropertiesFetcher extends Fetcher {
	private static String METHOD = "property";
	public PropertiesFetcher(File configDir, String endpoint) throws ClassNotFoundException, FileNotFoundException, JSONException, SQLException, IOException, ParseException {
		super(configDir, endpoint);
	}

	protected String getPaginationQuery(int iterator, int count) {
		String query = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
				"SELECT DISTINCT ?" + getSparqlKeyword() + " WHERE{\n" + 
				"  ?" + getSparqlKeyword() + " a rdf:Property\n" + 
				"} ORDER BY ?" + getSparqlKeyword() + " ";
		query += "LIMIT " + count;
		query += " OFFSET " + (iterator * count);
		return query;
	}
	protected String getRegularQuery() {
		return "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
				"SELECT DISTINCT ?" + getSparqlKeyword() + " WHERE{\n" + 
				"  ?" + getSparqlKeyword() + " a rdf:Property\n" + 
				"}";
	}
	
	protected String getSparqlKeyword() {
		return "property";
	}
	protected void setLogStatus(String status) throws SQLException {
		System.out.println(status);
		System.out.println(endpoint);
		dbHelper.setPropertyLogStatus(endpoint, status);
	}
	protected void storeSparqlResultsInDb(ResultSet resultSet) throws PossiblyNeedPaging, SQLException {
		dbHelper.storePropertiesFromQueryResult(endpoint, METHOD, resultSet);
	}
	protected void clearPreviousResultsFromDb() throws SQLException {
		dbHelper.clearProperties(endpoint, "property");
	}

	protected void storeSparqlResultInDb(ResultSet resultSet) throws PossiblyNeedPaging, SQLException {
		dbHelper.storePropertiesFromQueryResult(endpoint, METHOD, resultSet);
	}
	protected void setLogStatus(String status, String message) throws SQLException {
		dbHelper.setPropertyLogStatus(endpoint, status, message);
	}
	protected void setLogStatus(String status, String message, boolean paging) throws SQLException {
		dbHelper.setPropertyLogStatus(endpoint, status, message, true);
	}
	
	public static void main(String[] args) throws IOException, ClassNotFoundException, JSONException, SQLException, ParseException  {
		PropertiesFetcher fetcher = new PropertiesFetcher(new File("src/main/webapp/"), "http://biocyc.bio2rdf.org/sparql");
		fetcher.fetch();
	}
}
