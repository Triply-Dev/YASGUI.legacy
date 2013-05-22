package com.data2semantics.yasgui.server.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.server.openid.HttpCookies;
import com.data2semantics.yasgui.server.openid.OpenIdServlet;
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

	public void addBookmark(String title, String endpoint, String query) throws SQLException {
		int userId = getUserId(HttpCookies.getCookieValue(request, OpenIdServlet.uniqueIdCookieName));
		
		PreparedStatement insert = connect.prepareStatement("INSERT into Queries  " +
				"(QueryId, UserId, Title, Endpoint, Query) " +
				"values (default, ?, ?, ?, ?)");
		insert.setInt(1, userId);
		insert.setString(2, title);
		insert.setString(3, endpoint);
		insert.setString(4, query);
		insert.executeUpdate();
		insert.close();
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
}