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
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.server.Helper;
import com.data2semantics.yasgui.server.fetchers.ConfigFetcher;
import com.data2semantics.yasgui.server.fetchers.PropertiesFetcher;
import com.data2semantics.yasgui.server.openid.HttpCookies;
import com.data2semantics.yasgui.server.openid.OpenIdServlet;
import com.data2semantics.yasgui.shared.Bookmark;
import com.data2semantics.yasgui.shared.UserDetails;
import com.data2semantics.yasgui.shared.autocompletions.AccessibilityStatus;
import com.data2semantics.yasgui.shared.autocompletions.AutocompletionConfigCols;
import com.data2semantics.yasgui.shared.autocompletions.AutocompletionsInfo;
import com.data2semantics.yasgui.shared.autocompletions.EndpointPrivateFlag;
import com.data2semantics.yasgui.shared.autocompletions.FetchMethod;
import com.data2semantics.yasgui.shared.autocompletions.FetchStatus;
import com.data2semantics.yasgui.shared.autocompletions.FetchType;
import com.data2semantics.yasgui.shared.autocompletions.Util;
import com.data2semantics.yasgui.shared.exceptions.EndpointIdException;
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

			// so we have a new user, store default bookmarks as well
			storeDefaultBookmarks(userId);
		}
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
		} else {
			result.close();
			throw new OpenIdException("User does not exist in database");
		}
		return userId;
	}
	
	public int getUserId() throws SQLException, OpenIdException {
		return getUserId(HttpCookies.getCookieValue(request, OpenIdServlet.uniqueIdCookieName));
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
	}

	public HashMultimap<String, String> getAutocompletions(int endpointId, String partialProperty, int maxResults, FetchType type) throws SQLException {
		return getAutocompletions(endpointId, partialProperty, maxResults, type, null);
	}

	public HashMultimap<String, String> getAutocompletions(int endpointId, String partialProperty, int maxResults, FetchType type, FetchMethod method) throws SQLException {
		HashMultimap<String, String> autocompletions = HashMultimap.create();
		PreparedStatement preparedStatement;
		if (method == null) {
			preparedStatement = connect.prepareStatement("SELECT Uri, Method "
					+ "FROM " + type.getPluralCamelCase() + " AS completions "
					+ " WHERE completions.EndpointId = ? AND completions.Uri LIKE ? LIMIT ?");
			preparedStatement.setInt(3, maxResults);
		} else {
			preparedStatement = connect.prepareStatement("SELECT Uri, Method "
					+ "FROM " + type.getPluralCamelCase() + " AS completions "
					+ " WHERE completions.EndpointId = ? AND completions.Uri LIKE ? AND completions.Method = ? LIMIT ?");
			preparedStatement.setString(3, method.get());
			preparedStatement.setInt(4, maxResults);
		}
		preparedStatement.setInt(1, endpointId);
		preparedStatement.setString(2, partialProperty + "%");
		ResultSet result = preparedStatement.executeQuery();
		while (result.next()) {
			autocompletions.put(result.getString("Uri"), result.getString("Method"));
		}
		result.close();
		return autocompletions;
	}

	public int getAutcompletionCount(int endpointId, String partialProperty, FetchType type, FetchMethod method) throws SQLException {
		int count = 0;
		PreparedStatement preparedStatement;
		if (method == null) {
			preparedStatement = connect.prepareStatement("SELECT COUNT(Uri) AS count "
					+ " FROM " + type.getPluralCamelCase() + " AS completions"
					+ " WHERE completions.EndpointId = ? AND completions.Uri LIKE ?");
		} else {
			preparedStatement = connect.prepareStatement("SELECT COUNT(Uri) AS count "
					+ "FROM " + type.getPluralCamelCase() + " AS completions"
					+ " WHERE completions.EndpointId = ? AND completions.Uri LIKE ? AND completions.Method = ?");
			preparedStatement.setString(3, method.get());
		}
		preparedStatement.setInt(1, endpointId);
		preparedStatement.setString(2, partialProperty + "%");
		ResultSet result = preparedStatement.executeQuery();
		result.next();// only 1 result;
		count = result.getInt("count");
		result.close();
		return count;
	}
	
	public Map<String, Boolean> areAutocompletionsAdded(int endpointId, Set<String> checkUris, FetchType type, FetchMethod method) throws SQLException {
		HashMap<String, Boolean> addedProperties = new HashMap<String, Boolean>();
		PreparedStatement preparedStatement = connect.prepareStatement("SELECT Uri FROM "
					+ type.getPluralCamelCase() + " AS completions "
			 		+ "WHERE completions.EndpointId = ? AND completions.Uri LIKE ? AND completions.Method = ?");
		for (String uri: checkUris) {
			preparedStatement.setInt(1, endpointId);
			preparedStatement.setString(2, uri.trim());
			preparedStatement.setString(3, method.get());
			ResultSet result = preparedStatement.executeQuery();
			addedProperties.put(uri, result.next());
		}
		return addedProperties;
	}

	public void setAutocompletionLog(int endpointId, FetchStatus status, FetchType fetchType, String message, boolean pagination) throws SQLException {
		PreparedStatement ps;
		if (message != null) {
			String sql = "insert into CompletionsLog (EndpointId, Type, Status, Pagination, Message) values (?, ?, ?, ?, ?)";
			ps = connect.prepareStatement(sql);
			ps.setString(5, message);
		} else {
			String sql = "insert into CompletionsLog (EndpointId, Type, Status, Pagination) values (?, ?, ?, ?)";
			ps = connect.prepareStatement(sql);
		}
		ps.setInt(1, endpointId);
		ps.setString(2, fetchType.getSingular());
		ps.setString(3, status.get());
		ps.setBoolean(4, pagination);
		ps.execute();
	}
	
	public void setAutocompletionLog(int endpointId, FetchStatus status, FetchType fetchType, String message) throws SQLException {
		setAutocompletionLog(endpointId, status, fetchType, message, false);
	}
	public void setAutocompletionLog(int endpointId, FetchStatus status, FetchType fetchType) throws SQLException {
		setAutocompletionLog(endpointId, status, fetchType, null);
	}
	
	public void clearPreviousAutocompletionFetches(int endpointId, FetchMethod method, FetchType fetchType) throws SQLException {
		String sql = "DELETE FROM " + fetchType.getPluralCamelCase() + ""
				+ " WHERE Method = ? AND EndpointId = ?";
		PreparedStatement ps = connect.prepareStatement(sql);
		System.out.println(sql);
		ps.setString(1, method.get());
		ps.setInt(2, endpointId);
		ps.execute();
	}
	
	public void storeCompletionFetchesFromQueryResult(int endpointId, FetchType type, FetchMethod method, com.hp.hpl.jena.query.ResultSet resultSet, String sparqlKeyword) throws SQLException, PossiblyNeedPaging {
		storeCompletionFetchesFromQueryResult(endpointId, type, method, resultSet, sparqlKeyword, false);
	}
	public void storeCompletionFetchesFromQueryResult(int endpointId, FetchType type, FetchMethod method, com.hp.hpl.jena.query.ResultSet resultSet, String sparqlKeyword, boolean bypassPaginationCheck) throws SQLException, PossiblyNeedPaging {
		String sql = "insert into " + type.getPluralCamelCase() + " (Uri, EndpointId, Method) values (?, ?, ?)";
		PreparedStatement ps = connect.prepareStatement(sql);

		final int batchSize = 1000;
		int count = 0;
		while (resultSet.hasNext()) {
			QuerySolution querySolution = resultSet.next();
			RDFNode rdfNode = querySolution.get(sparqlKeyword);
			
			ps.setString(1, rdfNode.asResource().getURI().trim());
			ps.setInt(2, endpointId);
			ps.setString(3, method.get());
			ps.addBatch();
			if (++count % batchSize == 0) {
				System.out.println(count + " done");
				ps.executeBatch();
			}
		}
		System.out.println("looping done");
		ps.executeBatch(); // insert remaining records
		if (!bypassPaginationCheck && PropertiesFetcher.doubtfullResultSet(count)) {
			PossiblyNeedPaging pagingException = new PossiblyNeedPaging();
			pagingException.setQueryCount(count);
			throw pagingException;
		}
	}
	
	public void storeAutocompletionsFromQueryAnalysis(int endpointId, FetchType type, FetchMethod method, Set<String> properties) throws SQLException {
		String sql = "insert into " + type.getPluralCamelCase() + " (Uri, EndpointId, Method) values (?, ?, ?)";
		PreparedStatement ps = connect.prepareStatement(sql);

		final int batchSize = 1000;
		int count = 0;
		for (String property: properties) {
			ps.setString(1, property.trim());
			ps.setInt(2, endpointId);
			ps.setString(3, method.get());
			ps.addBatch();
			if (++count % batchSize == 0) {
				ps.executeBatch();
			}
		}
		ps.executeBatch(); // insert remaining records
	}
	public void storeCompletionFetchesFromLocalhost(int endpointId, FetchType type, FetchMethod method, JSONArray completions) throws SQLException, JSONException {
		String sql = "insert into " + type.getPluralCamelCase() + " (Uri, EndpointId, Method) values (?, ?, ?)";
		PreparedStatement ps = connect.prepareStatement(sql);
		
		final int batchSize = 1000;
		int count = 0;
		for (int i = 0; i < completions.length(); i++) {
			ps.setString(1, completions.getString(i).trim());
			ps.setInt(2, endpointId);
			ps.setString(3, method.get());
			ps.addBatch();
			if (++count % batchSize == 0) {
				ps.executeBatch();
			}
		}
		ps.executeBatch(); // insert remaining records
	}
	
	public boolean autocompletionFetchingEnabled(int endpointId, FetchType type, FetchMethod method) throws SQLException {
		String sql = "SELECT * "
				+ "FROM DisabledCompletionEndpoints AS disabledEndpoints "
				+ "WHERE disabledEndpoints.EndpointId = ? AND disabledEndpoints.Method = ? AND disabledEndpoints.Type = ?";
		PreparedStatement ps = connect.prepareStatement(sql);
		ps.setInt(1, endpointId);
		ps.setString(2, method.get());
		ps.setString(3, type.getSingular());
		ResultSet result = ps.executeQuery();
		boolean disabled = result.next();// only 1 result;
		result.close();
		return !disabled;
	}
	
	public HashMultimap<String, FetchMethod> getDisabledEndpointsForCompletionsFetching(FetchType type) throws SQLException {
		String sql = "SELECT DISTINCT endpoints.Endpoint, disabledEndpoints.Method "
				+ "FROM DisabledCompletionEndpoints AS disabledEndpoints, CompletionEndpoints AS endpoints "
				+ "WHERE disabledEndpoints.EndpointId = endpoints.Id AND disabledEndpoints.Type = ?";
		PreparedStatement ps = connect.prepareStatement(sql);
		ps.setString(1, type.getSingular());
		ResultSet result = ps.executeQuery();
		HashMultimap<String, FetchMethod> endpoints = HashMultimap.create();
		while (result.next()) {
			endpoints.put(result.getString("Endpoint"), Util.stringToFetchMethod(result.getString("Method")));
		}
		result.close();
		return endpoints;
	}
	
	/**
	 * ALL last x should not have status succesful
	 * @param endpointId
	 * @param numberOfFetchesToCheck
	 * @return
	 * @throws SQLException
	 */
	public boolean lastFetchesFailed(int endpointId, FetchType type, int numberOfFetchesToCheck) throws SQLException {
		String sql = "SELECT * "
				+ "FROM CompletionsLog AS log "
				+ "WHERE log.EndpointId = ? AND log.Type = ? ORDER BY Time DESC LIMIT ?";
		PreparedStatement ps = connect.prepareStatement(sql);
		ps.setInt(1, endpointId);
//		ps.setString(2, FetchStatus.FETCHING.get());//we don't want the fetching status in there. Only the failed and succeeded ones
		ps.setString(2, type.getSingular());
		ps.setInt(3, numberOfFetchesToCheck);
		ResultSet result = ps.executeQuery();
		int count = 0;
		boolean allFailed = true;
		while (result.next()) {
			count ++;
			if (!result.getString("Status").equals(FetchStatus.FAILED)) {
				allFailed = false;
				break;
			}
		}
		if (count != numberOfFetchesToCheck) {
			allFailed = false;
		}
		result.close();
		return allFailed;
	}
	
	public boolean lastFetchSuccesful(int endpointId, FetchType type) throws SQLException {
		String sql = "SELECT * "
				+ "FROM CompletionsLog AS log "
				+ "WHERE log.EndpointId = ? AND log.Type = ? ORDER BY Time DESC LIMIT 1";
		PreparedStatement ps = connect.prepareStatement(sql);
		ps.setInt(1, endpointId);
		ps.setString(2, type.getSingular());
		ResultSet result = ps.executeQuery();
		boolean lastFetchSuccesful = false;
		if (result.next()) {
			if (result.getString("Status").equals(FetchStatus.SUCCESSFUL.get())) {
				lastFetchSuccesful = true;
			}
		}
		result.close();
		return lastFetchSuccesful;
	}
	
	/**
	 * Check whether we are still fetching properties or classes for an endpoint
	 * @param endpointId
	 * @param fetchType
	 * @param timeFrameSearch The number of minutes which we will check (e.g. only the last 5 minutes?). 
	 * If the status is 'fetching', but was logged 2 months ago, we don't want to show it as 'still fetching'
	 * @return
	 * @throws SQLException
	 */
	public boolean stillFetching(int endpointId, FetchType fetchType, int timeFrameSearch) throws SQLException {
		String sql = "SELECT Status "
				+ "FROM CompletionsLog AS log "
				+ "WHERE log.EndpointId = ? AND TIMESTAMPDIFF(minute,log.Time,NOW()) <= ? AND log.Type = ? ORDER BY log.Time DESC LIMIT 1";
		PreparedStatement ps = connect.prepareStatement(sql);
		ps.setInt(1, endpointId);
		ps.setInt(2, timeFrameSearch);
		ps.setString(3, fetchType.getSingular());
		ResultSet result = ps.executeQuery();
		boolean stillFetching = false;
		while (result.next()) {
			if (result.getString("Status").equals(FetchStatus.FETCHING)) {
				stillFetching = true;
			}
		}
		result.close();
		return stillFetching;
	}
	
	/**
	 * Get the number of times we have a 'failed' status in our logs for all endpoints.
	 * @param type
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Integer> getFailCountForEndpoints(FetchType type) throws SQLException {
		int userId = -1;
		try {
			userId = getUserId();
		} catch (OpenIdException e) {
			//do nothing, just use the default (non-existing) userId
		}
		String sql = "SELECT endpoints.Endpoint, COUNT( endpoints.Endpoint ) AS NumberFails " + 
				"FROM CompletionsLog AS log"
				+ " JOIN CompletionEndpoints AS endpoints on endpoints.Id = log.EndpointId " + 
				"WHERE log.STATUS =  ? "
				+ "AND log.Type = ? " + 
				" AND (endpoints.UserId IS NULL OR endpoints.userId = ?) " +
				"GROUP BY endpoints.Endpoint";
		PreparedStatement ps = connect.prepareStatement(sql);
		ps.setString(1, FetchStatus.FAILED.get());
		ps.setString(2, type.getSingular());
		ps.setInt(3, userId);
		ResultSet result = ps.executeQuery();
		Map<String, Integer> endpoints = new HashMap<String, Integer>();
		while (result.next()) {
			endpoints.put(result.getString("Endpoint"), result.getInt("NumberFails"));
		}
		return endpoints;
	}
	
	/**
	 * List all endpoints for which we have autocompletions
	 * 
	 * @param fetchType
	 * @return
	 * @throws SQLException
	 */
	public HashMultimap<String, FetchMethod> getEndpointsWithAutocompletions(FetchType fetchType) throws SQLException {
		int userId = -1;
		try {
			userId = getUserId();
		} catch (OpenIdException e) {
			//do nothing, just use the default (non-existing) userId
		}
		HashMultimap<String, FetchMethod> endpoints = HashMultimap.create();
		String sql = "SELECT DISTINCT endpoints.Endpoint, completions.Method "
				+ "FROM " + fetchType.getPluralCamelCase() + " AS completions, CompletionEndpoints AS endpoints  "
				+ "WHERE completions.EndpointId = endpoints.Id AND (endpoints.UserId IS NULL OR endpoints.UserId = ?)";
		PreparedStatement ps = connect.prepareStatement(sql);
		ps.setInt(1, userId);
		ResultSet result = ps.executeQuery();
		while (result.next()) {
			endpoints.put(result.getString("Endpoint"), Util.stringToFetchMethod(result.getString("Method")));
		}
		return endpoints;
	}
	
	
	
	public AutocompletionsInfo getAutocompletionInfo() throws SQLException {
		AutocompletionsInfo completionsInfo = new AutocompletionsInfo();
		
		HashMultimap<String, FetchMethod> endpointsWithCompletions = getEndpointsWithAutocompletions(FetchType.PROPERTIES);
		for (String endpoint: endpointsWithCompletions.keySet()) {
			for (FetchMethod method: endpointsWithCompletions.get(endpoint)) {
				completionsInfo.getOrCreateEndpointInfo(endpoint).getPropertyInfo().setHasCompletions(method, true);
			}
		}
		endpointsWithCompletions = getEndpointsWithAutocompletions(FetchType.CLASSES);
		for (String endpoint: endpointsWithCompletions.keySet()) {
			for (FetchMethod method: endpointsWithCompletions.get(endpoint)) {
				completionsInfo.getOrCreateEndpointInfo(endpoint).getClassInfo().setHasCompletions(method, true);
			}
		}
		for (Entry<String, Integer> entry: getFailCountForEndpoints(FetchType.PROPERTIES).entrySet()) {
			completionsInfo.getOrCreateEndpointInfo(entry.getKey()).getPropertyInfo().setFetchFailCount(entry.getValue());
		}
		for (Entry<String, Integer> entry: getFailCountForEndpoints(FetchType.CLASSES).entrySet()) {
			completionsInfo.getOrCreateEndpointInfo(entry.getKey()).getClassInfo().setFetchFailCount(entry.getValue());
		}
		HashMultimap<String, FetchMethod> disabledPropEndpoints = getDisabledEndpointsForCompletionsFetching(FetchType.PROPERTIES);
		for (String endpoint: disabledPropEndpoints.keySet()) {
			for (FetchMethod method: disabledPropEndpoints.get(endpoint)) {
				if (method == FetchMethod.QUERY_ANALYSIS) {
					completionsInfo.getOrCreateEndpointInfo(endpoint).getPropertyInfo().setQueryAnalysisEnabled(false);
				} else if (method == FetchMethod.QUERY_RESULTS) {
					completionsInfo.getOrCreateEndpointInfo(endpoint).getPropertyInfo().setQueryResultsFetchingEnabled(false);
				}
			}
		}
		HashMultimap<String, FetchMethod> disabledClassEndpoints = getDisabledEndpointsForCompletionsFetching(FetchType.CLASSES);
		for (String endpoint: disabledClassEndpoints.keySet()) {
			for (FetchMethod method: disabledClassEndpoints.get(endpoint)) {
				if (method == FetchMethod.QUERY_ANALYSIS) {
					completionsInfo.getOrCreateEndpointInfo(endpoint).getClassInfo().setQueryAnalysisEnabled(false);
				} else if (method == FetchMethod.QUERY_RESULTS) {
					completionsInfo.getOrCreateEndpointInfo(endpoint).getClassInfo().setQueryResultsFetchingEnabled(false);
				}
			}
		}
		return completionsInfo;
	}
	
	
	public JSONArray getPersonalAutocompletionsInfo() throws OpenIdException, SQLException, JSONException {
		int userId = getUserId();
		JSONArray propDataArray = getPersonalAutocompletionsInfo(userId, FetchType.PROPERTIES);
		JSONArray classDataArray = getPersonalAutocompletionsInfo(userId, FetchType.CLASSES);
		//need to concatenate both
		for (int i = 0; i < classDataArray.length(); i++) {
			propDataArray.put(classDataArray.get(i));
		}
		return propDataArray;
	}
	
	private JSONArray getPersonalAutocompletionsInfo(int userId, FetchType type) throws JSONException, OpenIdException, SQLException {
		
		String sql = "SELECT Method, endpoints.Endpoint, COUNT( Method ) AS Count " + 
				"FROM CompletionEndpoints AS endpoints, " + type.getPluralCamelCase() + " " + 
				"WHERE endpoints.UserId = ? " +
				"AND " + type.getPluralCamelCase() + ".EndpointId = endpoints.Id " + 
				"GROUP BY " + type.getPluralCamelCase() + ".Method,  " + type.getPluralCamelCase() + ".EndpointId" +
				"";
		System.out.println(sql);
		PreparedStatement ps = connect.prepareStatement(sql);
		ps.setInt(1, userId);
		ResultSet result = ps.executeQuery();
		HashMap<String, JSONObject> completionsInfo = new HashMap<String, JSONObject>();
		while (result.next()) {
			String endpoint = result.getString("Endpoint");
			if (completionsInfo.containsKey(endpoint)) {
				//only need to add the 'method count' thing (rest is already added to object;
				JSONObject dataObj = completionsInfo.get(endpoint);
				dataObj.put(result.getString("Method"), result.getString("Count"));
			} else {
				JSONObject dataObj = new JSONObject();
				dataObj.put(AutocompletionConfigCols.TYPE.getKey(), type.getSingular());
				dataObj.put(AutocompletionConfigCols.ENDPOINT.getKey(), endpoint);
				dataObj.put(result.getString("Method"), result.getString("Count"));
				completionsInfo.put(endpoint, dataObj);
			}
			
		}
		
		
		JSONArray dataArray = new JSONArray(completionsInfo.values());
		return dataArray;
	}
	/**
	 * Flag the 'accessibility' (i.e. via http from the YASGUI server) status of an endpoint
	 * @param endpointId
	 * @param accessible
	 * @throws SQLException
	 */
	public void setEndpointAccessible(int endpointId, boolean accessible) throws SQLException {
		String sql = "INSERT INTO AccessibleEndpoints (`EndpointId`, `Accessible`) VALUES (?, ?)";
		PreparedStatement ps = connect.prepareStatement(sql);
		ps.setInt(1, endpointId);
		ps.setBoolean(2, accessible);
		ps.executeUpdate();
	}
	
	
	/**
	 * Retrieve the endpoint ID for a given endpoint string. If the endpoint does not exist in db, create a new id
	 * @param endpoint
	 * @return
	 * @throws SQLException
	 */
	public int getEndpointId(String endpoint, EndpointPrivateFlag privateFlag) throws SQLException, EndpointIdException {
		endpoint = endpoint.trim();
		String selectSql = "SELECT Id FROM CompletionEndpoints WHERE Endpoint = ? ";
		boolean addUserId = false;
		switch (privateFlag) {
	        case OWN: 
	        	addUserId = true;
				selectSql += " AND UserId = ?";
				break;
	        case OWN_AND_PUBLIC :
	        	addUserId = true;
	        	selectSql +=  " AND (UserId IS NULL OR UserId = ?)";
	        	break;
	        case PUBLIC:
	        	selectSql += "AND UserId IS NULL";
	        	break;
	        case EVERYTHING:
	        default: //nothing to add
	    }
		PreparedStatement ps = connect.prepareStatement(selectSql);
		ps.setString(1, endpoint);
		
		if (addUserId) {
			try {
				ps.setInt(2, getUserId());
			} catch (OpenIdException e) {
				//user is not logged in. just use -1 (i.e., id which is never used)
				ps.setInt(2, -1);
			}
		}
		ResultSet result = ps.executeQuery();
		int endpointId;
		if (result.next()) {
			endpointId = result.getInt("Id");
		} else {
			throw new EndpointIdException("No endpoint ID found in DB for provided endpoint string " + endpoint);
		}
		return endpointId;
	}
	
	public int generateIdForEndpoint(String endpoint) throws SQLException, EndpointIdException {
		AccessibilityStatus accessibleStatus = Helper.checkEndpointAccessibility(endpoint);
		return generateIdForEndpoint(endpoint, accessibleStatus);
	}
	
	public int generateIdForEndpoint(String endpoint, AccessibilityStatus accessibilityStatus) throws SQLException, EndpointIdException {
		int userId = -1;
		try {
			userId = getUserId();
		} catch (OpenIdException e) {
			//user nog logged in
		}
		String insertSql = "INSERT INTO CompletionEndpoints (Endpoint, UserId) VALUES (?, ?)";
		PreparedStatement psUpdate = connect.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
		psUpdate.setString(1, endpoint);
		if (accessibilityStatus == AccessibilityStatus.ACCESSIBLE) {
			psUpdate.setNull(2, Types.INTEGER);
		} else if (userId >= 0){
			//we need a user id for this one!
			psUpdate.setInt(2, userId);
		} else {
			throw new EndpointIdException("Unable to generate endpoint ID for endpoint " + endpoint + ". Is the user logged in?");
		}
		psUpdate.executeUpdate();
		ResultSet keys = psUpdate.getGeneratedKeys();
		keys.next();
		return keys.getInt(1);
	}
	
	
	public void setEndpointAccessible(int endpointId, AccessibilityStatus accessible) throws SQLException {
		setEndpointAccessible(endpointId, accessible == AccessibilityStatus.ACCESSIBLE);
	}
	
	/**
	 * Checks whether this endpoint is accessible via http from the YASGUI server
	 * 
	 * @param endpointId
	 * @param allowActiveCheck
	 * @return
	 * @throws SQLException
	 */
	public AccessibilityStatus isEndpointAccessible(int endpointId) throws SQLException {
		String sql = "SELECT `Accessible` "
				+ "FROM AccessibleEndpoints  "
				+ "WHERE EndpointId = ? ORDER BY Time LIMIT 1 ";
		PreparedStatement ps = connect.prepareStatement(sql);
		ps.setInt(1, endpointId);
		ResultSet result = ps.executeQuery();
		AccessibilityStatus accessibleStatus = AccessibilityStatus.UNCHECKED;
		if (result.next()) {
			if (result.getBoolean("Accessible")) {
				accessibleStatus = AccessibilityStatus.ACCESSIBLE;
			} else {
				accessibleStatus = AccessibilityStatus.INACCESSIBLE;
			}
		}
		return accessibleStatus;
	}


	public static void main(String[] args) throws ClassNotFoundException, FileNotFoundException, JSONException, SQLException, IOException, ParseException {
		DbHelper dbHelper = new DbHelper(new File("src/main/webapp/"));
		if (dbHelper.lastFetchSuccesful(12, FetchType.PROPERTIES)) {
			System.out.println("accessible");
		} else {
			System.out.println("not accessible");
		}
//		dbHelper.setEndpointAccessible("httpsdf", true);
		System.out.println("" + dbHelper.getEndpointId("htddtpdsdf",EndpointPrivateFlag.EVERYTHING));
//		AutocompletionsInfo info = dbHelper.getAutocompletionInfo();
//		System.out.println(info.toString());
//		for (String endpoint: dbHelper.getDisabledEndpointsForCompletionsFetching(FetchType.CLASSES, FetchMethod.QUERY_ANALYSIS)) {
//			System.out.println(endpoint);
//		}
//		System.out.println((dbHelper.lastFetchesFailed("http://dbpedia.org/sparql", FetchType.PROPERTIES, 5)? "still fetching":"done fetching"));
	}




}