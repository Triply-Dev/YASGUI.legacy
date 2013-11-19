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

import org.apache.commons.lang.WordUtils;
import org.json.JSONException;

import com.data2semantics.yasgui.server.SparqlService;
import com.data2semantics.yasgui.server.db.DbHelper;
import com.data2semantics.yasgui.shared.exceptions.PossiblyNeedPaging;
import com.hp.hpl.jena.query.ResultSet;


public abstract class AutocompletionFetcher  {
	public enum FetchStatus {
		SUCCESSFUL("successful"),
		FETCHING("fetching"),
		FAILED("failed");
		private String statusString;

		private FetchStatus(String statusString) {
			this.statusString = statusString;
		}
		public String get() {
			return this.statusString;
		}
	}
	public enum FetchType {
		CLASSES("class", "classes"),
		PROPERTIES("property", "properties");
		private String singular;
		private String plural;

		private FetchType(String singular, String plural) {
			this.singular = singular;
			this.plural = plural;
		}
		public String getSingular() {
			return this.singular;
		}
		public String getSingularCamelCase() {
			return WordUtils.capitalize(this.singular);
		}
		public String getPlural() {
			return this.plural;
		}
		public String getPluralCamelCase() {
			return WordUtils.capitalize(this.plural);
		}
	}
	public enum FetchMethod {
		QUERY_ANALYSIS("query"),
		QUERY_RESULTS("queryResults");
		private String method;
		
		private FetchMethod(String method) {
			this.method = method;
		}
		public String get() {
			return this.method;
		}
	}
	protected DbHelper dbHelper;
	protected String endpoint;
	public AutocompletionFetcher(File configDir, String endpoint) throws ClassNotFoundException, FileNotFoundException, JSONException, SQLException, IOException, ParseException {
		dbHelper = new DbHelper(configDir);
		this.endpoint = endpoint;
	}
	public void fetch() throws IOException, SQLException {
		try {
			System.out.println("fetching " + getSparqlKeyword() + " for endpoint " + endpoint);
			doRegularFetch();
		} catch (PossiblyNeedPaging ep) {
			try {
				System.out.println("fetching paged " + getSparqlKeyword() + " for endpoint " + endpoint);
				doPagingFetch(ep.getQueryCount());
			} catch (Exception e) {
				setLogStatus(FetchStatus.FAILED, e.getMessage(), true);
			}
		} catch (SQLException e) {
			setLogStatus(FetchStatus.FAILED, e.getMessage());
			throw e;
		}
		
	}
	
	private void doRegularFetch() throws PossiblyNeedPaging, SQLException {
		System.out.println("regular fetch");
		setLogStatus(FetchStatus.FETCHING);
		ResultSet resultSet = SparqlService.query(endpoint, getRegularQuery());
		//ok. so we know this paging query returns results (otherwise would have thrown an exception). 
		//first clear our properties table of previous results
		clearPreviousResultsFromDb();
		storeSparqlResultInDb(resultSet);
		setLogStatus(FetchStatus.SUCCESSFUL);
		
	}
	

	private void doPagingFetch(int count) throws SQLException {
		System.out.println("paging fetch (count: " + count + ")");
		setLogStatus(FetchStatus.FETCHING, null, true);
		int iterator = 0;
		boolean needPaging = true;
		while (needPaging) {
			String query = getPaginationQuery(iterator, count);
			ResultSet resultSet = SparqlService.query(endpoint, query);
			if (iterator == 0) {
				//ok. so we know this paging query returns results (otherwise would have thrown an exception). 
				//first clear our table of previous results
				clearPreviousResultsFromDb();
			}
			needPaging = false;
			try {
				storeSparqlResultInDb(resultSet);
			} catch (PossiblyNeedPaging e) {
				iterator++;
				needPaging = true;
			}
		}
	}
	public static boolean doubtfullResultSet(int count) {
		return (count > 0 && count % 100 == 0);
	}
	
	protected void setLogStatus(FetchStatus status) throws SQLException {
		dbHelper.setAutocompletionLog(endpoint, status, getFetchType());
	}
	protected void setLogStatus(FetchStatus status, String message) throws SQLException {
		dbHelper.setAutocompletionLog(endpoint, status, getFetchType(), message);
	}
	protected void setLogStatus(FetchStatus status, String message, boolean paging) throws SQLException {
		dbHelper.setAutocompletionLog(endpoint, status, getFetchType(), message, true);
	}
	
	protected void storeSparqlResultInDb(ResultSet resultSet) throws PossiblyNeedPaging, SQLException {
		dbHelper.storeCompletionFetchesFromQueryResult(endpoint, getFetchType(), getFetchMethod(), resultSet, getSparqlKeyword());
	}
	protected void clearPreviousResultsFromDb() throws SQLException {
		dbHelper.clearPreviousAutocompletionFetches(endpoint, getFetchMethod(), getFetchType());
	}
	
	protected abstract FetchMethod getFetchMethod();
	protected abstract FetchType getFetchType();
	protected abstract String getSparqlKeyword();
	protected abstract String getPaginationQuery(int iterator, int count);
	protected abstract String getRegularQuery();

}
