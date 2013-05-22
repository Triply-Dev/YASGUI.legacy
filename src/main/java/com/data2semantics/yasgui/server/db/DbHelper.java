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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.server.openid.HttpCookies;
import com.data2semantics.yasgui.server.openid.OpenIdServlet;
import com.data2semantics.yasgui.shared.Bookmark;
import com.data2semantics.yasgui.shared.UserDetails;
import com.data2semantics.yasgui.shared.exceptions.OpenIdException;

public class DbHelper {
	private JSONObject config;
	private Connection connect;
	private HttpServletRequest request;
	
	
	public DbHelper(JSONObject config, HttpServletRequest request) throws ClassNotFoundException, FileNotFoundException, JSONException, SQLException, IOException {
		this.connect = ConnectionFactory.getConnection(config);
		this.config = config;
		this.request = request;
	}
	public DbHelper(JSONObject config) throws ClassNotFoundException, FileNotFoundException, JSONException, SQLException, IOException {
		this(config, null);
	}

	public void updateUser() {

	}

	public void updateUserSession() {

	}



	public void storeUserInfo(UserDetails userDetails) throws SQLException {
		// check if user is already stored
		PreparedStatement preparedStatement = connect.prepareStatement("SELECT UniqueId FROM Users WHERE OpenId = ?");
		preparedStatement.setString(1, userDetails.getOpenId());
		
		ResultSet result = preparedStatement.executeQuery();
		boolean exists = result.next();
		result.close();
		preparedStatement.close();
		if (exists) {
			PreparedStatement update = connect.prepareStatement("UPDATE Users SET " +
					"FirstName = ?, " +
					"LastName = ?, " +
					"UniqueId = ? ," +
					"FullName = ? ," +
					"NickName = ? ," +
					"Email = ? ," +
					"LastLogin = default " + 
					"WHERE OpenId = ?");
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
			// insert
			PreparedStatement insert = connect.prepareStatement("INSERT into Users  " +
					"(OpenId, UniqueId, FirstName, LastName, FullName, NickName, Email, LastLogin) " +
					"values (?, ?, ?, ?, ?, ?, ?, default)");
			insert.setString(1, userDetails.getOpenId());
			insert.setString(2, userDetails.getUniqueId());
			insert.setString(3, userDetails.getFirstName());
			insert.setString(4, userDetails.getLastName());
			insert.setString(5, userDetails.getFullName());
			insert.setString(6, userDetails.getNickName());
			insert.setString(7, userDetails.getEmail());
			insert.executeUpdate();
			insert.close();
		}
		preparedStatement.close();
	}

	public UserDetails getUserDetails(UserDetails userDetails) throws SQLException {
		PreparedStatement preparedStatement = connect
				.prepareStatement("SELECT Id, FirstName, LastName FROM Users WHERE UniqueId = ?");
		preparedStatement.setString(1, userDetails.getUniqueId());
		ResultSet result = preparedStatement.executeQuery();
		if (result.next()) {
			userDetails.setFirstName(result.getString("FirstName"));
			userDetails.setLastName(result.getString("LastName"));
			userDetails.setUserId(result.getInt("Id"));
		}
		result.close();
		return userDetails;
	}

//	public void addBookmark(Bookmark bookmark) throws SQLException {
//		int userId = getUserId(HttpCookies.getCookieValue(request, OpenIdServlet.uniqueIdCookieName));
//		
//		PreparedStatement insert = connect.prepareStatement("INSERT into Bookmarks  " +
//				"(Id, UserId, Title, Endpoint, Query) " +
//				"values (default, ?, ?, ?, ?)");
//		insert.setInt(1, userId);
//		insert.setString(2, bookmark.getTitle());
//		insert.setString(3, bookmark.getEndpoint());
//		insert.setString(4, bookmark.getQuery());
//		insert.executeUpdate();
//		insert.close();
//	}
	
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
		return bookmarks.toArray(new Bookmark[bookmarks.size()]);
	}


	public void close() {
		try {
			connect.close();
		} catch (Exception e) {
			//do nothing
		}
	}
	
	public int getUserId(String uniqueId) throws SQLException {
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
	public void clearBookmarks() throws SQLException {
		int userId = getUserId(HttpCookies.getCookieValue(request, OpenIdServlet.uniqueIdCookieName));
		PreparedStatement preparedStatement = connect.prepareStatement("DELETE FROM Bookmarks WHERE UserId = ?");
		preparedStatement.setInt(1, userId);
		preparedStatement.execute();
		
	}
	public void addBookmarks(Bookmark... bookmarks) throws SQLException {
		int userId = getUserId(HttpCookies.getCookieValue(request, OpenIdServlet.uniqueIdCookieName));
        PreparedStatement insert = connect.prepareStatement("INSERT into Bookmarks  " +
				"(Id, UserId, Title, Endpoint, Query) " +
				"values (default, ?, ?, ?, ?)");
        int i = 0;
        for (Bookmark bookmark: bookmarks) {
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