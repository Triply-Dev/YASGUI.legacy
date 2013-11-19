package com.data2semantics.yasgui.server.db;

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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.server.Helper;
import com.data2semantics.yasgui.server.fetchers.AutocompletionFetcher.FetchMethod;
import com.data2semantics.yasgui.server.fetchers.AutocompletionFetcher.FetchStatus;
import com.data2semantics.yasgui.server.fetchers.AutocompletionFetcher.FetchType;
import com.data2semantics.yasgui.server.fetchers.ConfigFetcher;
import com.data2semantics.yasgui.server.fetchers.PropertiesFetcher;
import com.data2semantics.yasgui.server.openid.HttpCookies;
import com.data2semantics.yasgui.server.openid.OpenIdServlet;
import com.data2semantics.yasgui.shared.Bookmark;
import com.data2semantics.yasgui.shared.UserDetails;
import com.data2semantics.yasgui.shared.exceptions.OpenIdException;
import com.data2semantics.yasgui.shared.exceptions.PossiblyNeedPaging;
import com.google.common.collect.HashMultimap;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;

public class DbHelper {
	private JSONObject config;
	private Connection connect;
	private HttpServletRequest request;

	public DbHelper(File configDir, HttpServletRequest request) throws ClassNotFoundException, FileNotFoundException, JSONException, SQLException, IOException,
			ParseException {
		this.config = ConfigFetcher.getJsonObjectFromPath(configDir);
		this.connect = ConnectionFactory.getConnection(configDir);
		this.request = request;
	}

	public DbHelper(File configDir) throws ClassNotFoundException, FileNotFoundException, JSONException, SQLException, IOException, ParseException {
		this(configDir, null);
	}

	/**
	 * Store user info (either update or insert)
	 * 
	 * @param userDetails
	 * @throws SQLException
	 */
	public void storeUserInfo(UserDetails userDetails) throws SQLException {
		// check if user is already stored
		PreparedStatement preparedStatement = connect.prepareStatement("SELECT UniqueId FROM Users WHERE OpenId = ?");
		preparedStatement.setString(1, userDetails.getOpenId());

		ResultSet result = preparedStatement.executeQuery();
		boolean exists = result.next();
		result.close();
		preparedStatement.close();
		if (exists) {
			PreparedStatement update = connect.prepareStatement("UPDATE Users SET " + "FirstName = ?, " + "LastName = ?, " + "UniqueId = ? ,"
					+ "FullName = ? ," + "NickName = ? ," + "Email = ? ," + "LastLogin = default " + "WHERE OpenId = ?");
			update.setString(1, userDetails.getFirstName());
			update.setString(2, userDetails.getLastName());
			update.setString(3, userDetails.getUniqueId());
			update.setString(4, userDetails.getFullName());
			update.setString(5, userDetails.getNickName());
			update.setString(6, userDetails.getEmail());
			update.setString(7, userDetails.getOpenId());
			update.executeUpdate();
			update.close();
		} else {
			// new user, so insert
			PreparedStatement insert = connect.prepareStatement("INSERT into Users  "
					+ "(OpenId, UniqueId, FirstName, LastName, FullName, NickName, Email, LastLogin) " + "values (?, ?, ?, ?, ?, ?, ?, default)",
					Statement.RETURN_GENERATED_KEYS);
			insert.setString(1, userDetails.getOpenId());
			insert.setString(2, userDetails.getUniqueId());
			insert.setString(3, userDetails.getFirstName());
			insert.setString(4, userDetails.getLastName());
			insert.setString(5, userDetails.getFullName());
			insert.setString(6, userDetails.getNickName());
			insert.setString(7, userDetails.getEmail());
			insert.executeUpdate();
			ResultSet rs = insert.getGeneratedKeys();
			int userId = -1;
			if (rs.next()) {
				userId = rs.getInt(1);
			}
			insert.close();

			// so we have a new user, store default bookmarks as well
			storeDefaultBookmarks(userId);
		}
		preparedStatement.close();
	}

	/**
	 * get default bookmarks from config file, and insert them for a given user.
	 * Ran after a user has his/her first login
	 * 
	 * @param userId
	 * @throws SQLException
	 */
	private void storeDefaultBookmarks(int userId) throws SQLException {
		if (userId >= 0) {
			ArrayList<Bookmark> bookmarks = Helper.getDefaultBookmarksFromConfig(config);
			PreparedStatement insert = connect.prepareStatement("INSERT into Bookmarks  " + "(Id, UserId, Title, Endpoint, Query) "
					+ "values (default, ?, ?, ?, ?)");
			int i = 0;
			for (Bookmark bookmark : bookmarks) {
				i++;
				insert.setInt(1, userId);
				insert.setString(2, bookmark.getTitle());
				insert.setString(3, bookmark.getEndpoint());
				insert.setString(4, bookmark.getQuery());

				insert.addBatch();
				if ((i + 1) % 1000 == 0) {
					insert.executeBatch(); // Execute every 1000 items
				}
			}
			insert.executeBatch();
			insert.close();
		}
	}

	/**
	 * Get user details, given a certain unique user id string
	 * 
	 * @param userDetails
	 * @return
	 * @throws SQLException
	 * @throws OpenIdException
	 */
	public UserDetails getUserDetails(UserDetails userDetails) throws SQLException, OpenIdException {
		PreparedStatement preparedStatement = connect
				.prepareStatement("SELECT Id, FirstName, LastName, FullName, NickName, Email FROM Users WHERE UniqueId = ?");
		preparedStatement.setString(1, userDetails.getUniqueId());
		ResultSet result = preparedStatement.executeQuery();
		if (result.next()) {
			userDetails.setFirstName(result.getString("FirstName"));
			userDetails.setLastName(result.getString("LastName"));
			userDetails.setUserId(result.getInt("Id"));
			userDetails.setFullName(result.getString("FullName"));
			userDetails.setNickName(result.getString("NickName"));
			userDetails.setEmail(result.getString("Email"));
		} else {
			throw new OpenIdException("User not found in database");
		}
		preparedStatement.close();
		result.close();
		return userDetails;
	}

	/**
	 * get bookmarks for this user (unique user id retrieved from cookie)
	 * 
	 * @return
	 * @throws SQLException
	 */
	public Bookmark[] getBookmarks() throws SQLException {
		int userId = getUserId(HttpCookies.getCookieValue(request, OpenIdServlet.uniqueIdCookieName));
		ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
		PreparedStatement preparedStatement = connect.prepareStatement("SELECT Id, Endpoint, Query, Title FROM Bookmarks WHERE UserId = ?");
		preparedStatement.setInt(1, userId);
		ResultSet result = preparedStatement.executeQuery();
		while (result.next()) {
			Bookmark bookmark = new Bookmark();
			bookmark.setBookmarkId(result.getInt("Id"));
			bookmark.setEndpoint(result.getString("Endpoint"));
			bookmark.setQuery(result.getString("Query"));
			bookmark.setTitle(result.getString("Title"));
			bookmarks.add(bookmark);
		}
		result.close();
		preparedStatement.close();
		return bookmarks.toArray(new Bookmark[bookmarks.size()]);
	}

	/**
	 * handler to close db connection
	 */
	public void close() {
		try {
			connect.close();
		} catch (Exception e) {
			// do nothing
		}
	}

	/**
	 * get user Id given a unique Id string
	 * 
	 * @param uniqueId
	 * @return
	 * @throws SQLException
	 * @throws OpenIdException
	 */
	public int getUserId(String uniqueId) throws SQLException, OpenIdException {
		PreparedStatement preparedStatement = connect.prepareStatement("SELECT Id FROM Users WHERE UniqueId = ?");
		preparedStatement.setString(1, uniqueId);
		int userId = -1;
		ResultSet result = preparedStatement.executeQuery();
		if (result.next()) {
			userId = result.getInt("Id");
			result.close();
			preparedStatement.close();
		} else {
			result.close();
			preparedStatement.close();
			throw new OpenIdException("User does not exist in database");
		}
		return userId;
	}

	/**
	 * Clear a number of bookmarks from the DB
	 * 
	 * @param bookmarkIds
	 * @throws SQLException
	 * @throws OpenIdException
	 */
	public void clearBookmarks(int... bookmarkIds) throws SQLException, OpenIdException {
		if (bookmarkIds.length > 0) {
			int userId = getUserId(HttpCookies.getCookieValue(request, OpenIdServlet.uniqueIdCookieName));
			Statement statement = connect.createStatement();
			String ids = "";
			boolean hasItemBefore = false;
			for (int bookmarkId : bookmarkIds) {
				if (hasItemBefore)
					ids += ", ";
				ids += bookmarkId;
				hasItemBefore = true;
			}
			String query = "DELETE FROM Bookmarks WHERE UserId = " + userId + " AND Id IN (" + ids + ")";
			statement.execute(query);
			statement.close();
		}
	}

	/**
	 * add a set of bookmarks
	 * 
	 * @param bookmarks
	 * @throws SQLException
	 * @throws OpenIdException
	 */
	public void addBookmarks(Bookmark... bookmarks) throws SQLException, OpenIdException {
		int userId = getUserId(HttpCookies.getCookieValue(request, OpenIdServlet.uniqueIdCookieName));
		PreparedStatement insert = connect.prepareStatement("INSERT into Bookmarks  " + "(Id, UserId, Title, Endpoint, Query) "
				+ "values (default, ?, ?, ?, ?)");
		int i = 0;
		for (Bookmark bookmark : bookmarks) {
			i++;
			insert.setInt(1, userId);
			insert.setString(2, bookmark.getTitle());
			insert.setString(3, bookmark.getEndpoint());
			insert.setString(4, bookmark.getQuery());

			insert.addBatch();
			if ((i + 1) % 1000 == 0) {
				insert.executeBatch(); // Execute every 1000 items
			}
		}
		insert.executeBatch();
		insert.close();

	}

	/**
	 * update a set of bookmarks
	 * 
	 * @param bookmarks
	 * @throws SQLException
	 * @throws OpenIdException
	 */
	public void updateBookmarks(Bookmark... bookmarks) throws SQLException, OpenIdException {
		int userId = getUserId(HttpCookies.getCookieValue(request, OpenIdServlet.uniqueIdCookieName));
		PreparedStatement insert = connect.prepareStatement("UPDATE Bookmarks  " + "SET Title = ?, " + "Endpoint = ?, " + "Query = ? " + "WHERE UserId = ? "
				+ "AND Id = ?");
		int i = 0;
		for (Bookmark bookmark : bookmarks) {
			i++;

			insert.setString(1, bookmark.getTitle());
			insert.setString(2, bookmark.getEndpoint());
			insert.setString(3, bookmark.getQuery());
			insert.setInt(4, userId);
			insert.setInt(5, bookmark.getBookmarkId());
			insert.addBatch();
			if ((i + 1) % 1000 == 0) {
				insert.executeBatch(); // Execute every 1000 items
			}
		}
		insert.executeBatch();
		insert.close();
	}

	public HashMultimap<String, String> getAutocompletions(String endpoint, String partialProperty, int maxResults, FetchType type) throws SQLException {
		return getAutocompletions(endpoint, partialProperty, maxResults, type, null);
	}

	public HashMultimap<String, String> getAutocompletions(String endpoint, String partialProperty, int maxResults, FetchType type, FetchMethod method) throws SQLException {
		HashMultimap<String, String> autocompletions = HashMultimap.create();
		PreparedStatement preparedStatement;
		if (method == null) {
			preparedStatement = connect.prepareStatement("SELECT Uri, Method FROM " + type.getPluralCamelCase() + " WHERE Endpoint = ? AND Uri LIKE ? LIMIT ?");
			preparedStatement.setInt(3, maxResults);
		} else {
			preparedStatement = connect.prepareStatement("SELECT Uri, Method FROM " + type.getPluralCamelCase() + " WHERE Endpoint = ? AND Uri LIKE ? AND Method = ? LIMIT ?");
			preparedStatement.setString(3, method.get());
			preparedStatement.setInt(4, maxResults);
		}
		preparedStatement.setString(1, endpoint);
		preparedStatement.setString(2, partialProperty + "%");
		ResultSet result = preparedStatement.executeQuery();
		while (result.next()) {
			autocompletions.put(result.getString("Uri"), result.getString("Method"));
		}
		preparedStatement.close();
		result.close();
		return autocompletions;
	}

	public int getAutcompletionCount(String endpoint, String partialProperty, FetchType type, FetchMethod method) throws SQLException {
		int count = 0;
		PreparedStatement preparedStatement;
		if (method == null) {
			preparedStatement = connect.prepareStatement("SELECT COUNT(Uri) AS count FROM " + type.getPluralCamelCase() + " WHERE Endpoint = ? AND Uri LIKE ?");
		} else {
			preparedStatement = connect.prepareStatement("SELECT COUNT(Uri) AS count FROM " + type.getPluralCamelCase() + " WHERE Endpoint = ? AND Uri LIKE ? AND Method = ?");
			preparedStatement.setString(3, method.get());
		}
		preparedStatement.setString(1, endpoint);
		preparedStatement.setString(2, partialProperty + "%");
		ResultSet result = preparedStatement.executeQuery();
		result.next();// only 1 result;
		count = result.getInt("count");
		preparedStatement.close();
		result.close();
		return count;
	}
	
	public Map<String, Boolean> areAutocompletionsAdded(String endpoint, Set<String> checkUris, FetchType type, FetchMethod method) throws SQLException {
		HashMap<String, Boolean> addedProperties = new HashMap<String, Boolean>();
		PreparedStatement preparedStatement = connect.prepareStatement("SELECT Uri FROM " + type.getPluralCamelCase() + " WHERE Endpoint = ? AND Uri LIKE ? AND Method = ?");
		for (String uri: checkUris) {
			preparedStatement.setString(1, endpoint.trim());
			preparedStatement.setString(2, uri.trim());
			preparedStatement.setString(3, method.get());
			ResultSet result = preparedStatement.executeQuery();
			addedProperties.put(uri, result.next());
		}
		return addedProperties;
	}

	public void setAutocompletionLog(String endpoint, FetchStatus status, FetchType fetchType, String message, boolean pagination) throws SQLException {
		PreparedStatement ps;
		if (message != null) {
			String sql = "insert into Log" + fetchType.getSingularCamelCase() + "Fetcher (Endpoint, Status, Pagination, Message) values (?, ?, ?, ?)";
			ps = connect.prepareStatement(sql);
			ps.setString(4, message);
		} else {
			String sql = "insert into Log" + fetchType.getSingularCamelCase() + "Fetcher (Endpoint, Status, Pagination) values (?, ?, ?)";
			ps = connect.prepareStatement(sql);
		}
		ps.setString(1, endpoint.trim());
		ps.setString(2, status.get());
		ps.setBoolean(3, pagination);
		
		ps.execute();
		ps.close();
	}
	
	public void setAutocompletionLog(String endpoint, FetchStatus status, FetchType fetchType, String message) throws SQLException {
		setAutocompletionLog(endpoint, status, fetchType, message, false);
	}
	public void setAutocompletionLog(String endpoint, FetchStatus status, FetchType fetchType) throws SQLException {
		setAutocompletionLog(endpoint, status, fetchType, null);
	}
	
	public void clearPreviousAutocompletionFetches(String endpoint, FetchMethod method, FetchType fetchType) throws SQLException {
		String sql = "DELETE FROM " + fetchType.getPluralCamelCase() + " WHERE Method = ? AND Endpoint = ?";
		PreparedStatement ps = connect.prepareStatement(sql);
		ps.setString(1, method.get());
		ps.setString(2, endpoint);
		ps.execute();
		ps.close();
	}
	
	public void storeCompletionFetchesFromQueryResult(String endpoint, FetchType type, FetchMethod method, com.hp.hpl.jena.query.ResultSet resultSet, String sparqlKeyword) throws SQLException, PossiblyNeedPaging {
		storeCompletionFetchesFromQueryResult(endpoint, type, method, resultSet, sparqlKeyword, false);
	}
	public void storeCompletionFetchesFromQueryResult(String endpoint, FetchType type, FetchMethod method, com.hp.hpl.jena.query.ResultSet resultSet, String sparqlKeyword, boolean bypassPaginationCheck) throws SQLException, PossiblyNeedPaging {
		String sql = "insert into " + type.getPluralCamelCase() + " (Uri, Endpoint, Method) values (?, ?, ?)";
		PreparedStatement ps = connect.prepareStatement(sql);

		final int batchSize = 1000;
		int count = 0;
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			RDFNode rdfNode = querySolution.get(sparqlKeyword);
			
			ps.setString(1, rdfNode.asResource().getURI().trim());
			ps.setString(2, endpoint.trim());
			ps.setString(3, method.get());
			ps.addBatch();
			if (++count % batchSize == 0) {
				System.out.println(count + " done");
				ps.executeBatch();
			}
		}
		System.out.println("looping done");
		ps.executeBatch(); // insert remaining records
		ps.close();
		if (!bypassPaginationCheck && PropertiesFetcher.doubtfullResultSet(count)) {
			PossiblyNeedPaging pagingException = new PossiblyNeedPaging();
			pagingException.setQueryCount(count);
			throw pagingException;
		}
	}
	
	public void storeAutocompletionsFromQueryAnalysis(String endpoint, FetchType type, FetchMethod method, Set<String> properties) throws SQLException {
		String sql = "insert into " + type.getPluralCamelCase() + " (Uri, Endpoint, Method) values (?, ?, ?)";
		PreparedStatement ps = connect.prepareStatement(sql);

		final int batchSize = 1000;
		int count = 0;
		for (String property: properties) {
			ps.setString(1, property.trim());
			ps.setString(2, endpoint.trim());
			ps.setString(3, method.get());
			ps.addBatch();
			if (++count % batchSize == 0) {
				ps.executeBatch();
			}
		}
		ps.executeBatch(); // insert remaining records
		ps.close();
	}
	
	public boolean autocompletionFetchingEnabled(String endpoint, FetchType type, FetchMethod method) throws SQLException {
		String sql = "SELECT * FROM Disabled" + type.getSingularCamelCase() + "Endpoints WHERE Endpoint = ? AND Method = ?";
		PreparedStatement ps = connect.prepareStatement(sql);
		ps.setString(1, endpoint.trim());
		ps.setString(2, method.get());
		ResultSet result = ps.executeQuery();
		boolean disabled = result.next();// only 1 result;
		ps.close();
		result.close();
		return !disabled;
	}
	
	public ArrayList<String> getDisabledEndpointsForCompletionsFetching(FetchType type) throws SQLException {
		String sql = "SELECT Endpoint FROM Disabled" + type.getSingularCamelCase() + "Endpoints WHERE 1";
		Statement statement = connect.createStatement();
		ResultSet result = statement.executeQuery(sql);
		ArrayList<String> endpoints = new ArrayList<String>();
		while (result.next()) {
			endpoints.add(result.getString("Endpoint"));
		}
		statement.close();
		result.close();
		return endpoints;
	}
	
	/**
	 * ALL last x should not have status succesful
	 * @param endpoint
	 * @param numberOfFetchesToCheck
	 * @return
	 * @throws SQLException
	 */
	public boolean lastFetchesFailed(String endpoint, FetchType type, int numberOfFetchesToCheck) throws SQLException {
		String sql = "SELECT * FROM Log" + type.getSingularCamelCase() + "Fetcher WHERE Endpoint = ? ORDER BY Time DESC LIMIT ?";
		PreparedStatement ps = connect.prepareStatement(sql);
		ps.setString(1, endpoint.trim());
		ps.setInt(2, numberOfFetchesToCheck);
		ResultSet result = ps.executeQuery();
		int count = 0;
		boolean allFailed = true;
		while (result.next()) {
			count ++;
			if (result.getString("Status").equals("successful")) {
				allFailed = false;
			}
		}
		if (count != numberOfFetchesToCheck) {
			allFailed = false;
		}
		ps.close();
		result.close();
		return allFailed;
	}
	public boolean stillFetching(String endpoint, FetchType fetchType, int timeFrameSearch) throws SQLException {
		String sql = "SELECT * FROM Log" + fetchType.getSingularCamelCase() + "Fetcher WHERE Endpoint = ? AND TIMESTAMPDIFF(minute,Time,NOW()) <= ? LIMIT 1";
		PreparedStatement ps = connect.prepareStatement(sql);
		ps.setString(1, endpoint.trim());
		ps.setInt(2, timeFrameSearch);
		ResultSet result = ps.executeQuery();
		
		boolean stillFetching = result.next();
		ps.close();
		result.close();
		return stillFetching;
	}


	public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, JSONException, SQLException, IOException, ParseException {
		DbHelper dbHelper = new DbHelper(new File("src/main/webapp/"));
//		System.out.println((dbHelper.stillFetching("http://dbpedia.org/sparql", 5)? "still fetching":"done fetching"));
	}


}