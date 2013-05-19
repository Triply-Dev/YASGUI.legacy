package com.data2semantics.yasgui.server.db;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.shared.SettingKeys;
import com.data2semantics.yasgui.shared.UserDetails;

public class DbConnection {
	private Connection connect = null;
	private Statement statement = null;
	private ResultSet resultSet = null;
	private static String DB_NAME = "YASGUI";

	public DbConnection(JSONObject config) throws JSONException, ClassNotFoundException, SQLException,
			FileNotFoundException, IOException {
		try {
			connect(config.getString(SettingKeys.MYSQL_HOST) + "/" + DB_NAME, config.getString(SettingKeys.MYSQL_USERNAME),
					config.getString(SettingKeys.MYSQL_PASSWORD));
		} catch (SQLException e) {
			//db probably doesnt exist.
			//connect without db selector, create db, and create new connector
			connect(config.getString(SettingKeys.MYSQL_HOST), config.getString(SettingKeys.MYSQL_USERNAME),
					config.getString(SettingKeys.MYSQL_PASSWORD));
			updateDatabase();
			connect(config.getString(SettingKeys.MYSQL_HOST) + "/" + DB_NAME, config.getString(SettingKeys.MYSQL_USERNAME),
					config.getString(SettingKeys.MYSQL_PASSWORD));
		}
		
		
	}

	private void connect(String host, String username, String password) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://" + host;
		connect = DriverManager.getConnection(url, username, password);

	}

	private void updateDatabase() throws FileNotFoundException, IOException, SQLException {
		if (!databaseExists(DB_NAME)) {
			ScriptRunner runner = new ScriptRunner(connect, false, true);
			String filename = "create.sql";
			InputStream fileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
			if (fileStream == null) {
				throw new FileNotFoundException("Could not find resource for " + filename);
			}
			runner.runScript(new BufferedReader(new InputStreamReader(fileStream, "UTF-8")));
		}
	}

	public void updateUser() {

	}

	public void updateUserSession() {

	}

	public void readDataBase() throws Exception {
		try {
			// Statements allow to issue SQL queries to the database
			statement = connect.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement.executeQuery("select * from YASGUI.Queries");
			writeResultSet(resultSet);

			//
			//
			// // PreparedStatements can use variables and are more efficient
			// preparedStatement = connect
			// .prepareStatement("insert into  FEEDBACK.COMMENTS values (default, ?, ?, ?, ? , ?, ?)");
			// //
			// "myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
			// // Parameters start with 1
			// preparedStatement.setString(1, "Test");
			// preparedStatement.setString(2, "TestEmail");
			// preparedStatement.setString(3, "TestWebpage");
			// preparedStatement.setDate(4, new java.sql.Date(2009, 12, 11));
			// preparedStatement.setString(5, "TestSummary");
			// preparedStatement.setString(6, "TestComment");
			// preparedStatement.executeUpdate();
			//
			// preparedStatement = connect
			// .prepareStatement("SELECT myuser, webpage, datum, summery, COMMENTS from FEEDBACK.COMMENTS");
			// resultSet = preparedStatement.executeQuery();
			// writeResultSet(resultSet);
			//
			// // Remove again the insert comment
			// preparedStatement = connect
			// .prepareStatement("delete from FEEDBACK.COMMENTS where myuser= ? ; ");
			// preparedStatement.setString(1, "Test");
			// preparedStatement.executeUpdate();
			//
			// resultSet = statement
			// .executeQuery("select * from FEEDBACK.COMMENTS");
			// writeMetaData(resultSet);

		} catch (Exception e) {
			throw e;
		} finally {
			close();
		}

	}

	private boolean databaseExists(String DbName) throws SQLException {
		statement = connect.createStatement();
		resultSet = statement.executeQuery("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '"
				+ DB_NAME + "'");
		return resultSet.next();
	}

	private void writeResultSet(ResultSet resultSet) throws SQLException {
		// ResultSet is initially before the first data set
		while (resultSet.next()) {
			// It is possible to get the columns via name
			// also possible to get the columns via the column number
			// which starts at 1
			// e.g. resultSet.getSTring(2);
			System.out.println(resultSet.getString(1));
			System.out.println(resultSet.getString(2));
		}
	}

	// You need to close the resultSet
	public void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {

		}
	}

	public void storeUserInfo(UserDetails userDetails) throws SQLException {
		// check if user is already stored
		PreparedStatement preparedStatement = connect.prepareStatement("SELECT UniqueId FROM Users WHERE UniqueId = ?");
		preparedStatement.setString(1, userDetails.getUniqueId());
		
		ResultSet result = preparedStatement.executeQuery();
		if (result.next()) {
			PreparedStatement update = connect.prepareStatement("UPDATE Users SET FirstName = ?, LastName = ?, OpenId = ? WHERE UniqueId = ?");
			update.setString(1, userDetails.getFirstName());
			update.setString(2, userDetails.getLastName());
			update.setString(3, userDetails.getOpenId());
			update.setString(4, userDetails.getUniqueId());
			update.executeUpdate();
			update.close();
		} else {
			// insert
			PreparedStatement insert = connect.prepareStatement("INSERT into Users  (OpenId, UniqueId, FirstName, LastName) values (?, ?, ?, ?)");
			insert.setString(1, userDetails.getOpenId());
			insert.setString(2, userDetails.getUniqueId());
			insert.setString(3, userDetails.getFirstName());
			insert.setString(4, userDetails.getLastName());
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

		return userDetails;
	}

	// public void storeUniqueId(String id) throws SQLException {
	// PreparedStatement preparedStatement =
	// connect.prepareStatement("INSERT into  FEEDBACK.COMMENTS values (default, ?, ?, ?, ? , ?, ?)");
	// // Parameters start with 1
	// preparedStatement.setString(1, "Test");
	// }

}