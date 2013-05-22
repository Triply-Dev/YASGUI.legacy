package com.data2semantics.yasgui.server.db;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.shared.SettingKeys;

public class ConnectionFactory  {
	private static String DB_NAME = "YASGUI";

	
	
	public static Connection getConnection(JSONObject config) throws JSONException, ClassNotFoundException, SQLException,
	FileNotFoundException, IOException {
		Connection connect = null;
		try {
			connect = connect(config.getString(SettingKeys.MYSQL_HOST) + "/" + DB_NAME, config.getString(SettingKeys.MYSQL_USERNAME),
					config.getString(SettingKeys.MYSQL_PASSWORD));
		} catch (SQLException e) {
			//db probably doesnt exist.
			//connect without db selector, create db, and create new connector
			connect = connect(config.getString(SettingKeys.MYSQL_HOST), config.getString(SettingKeys.MYSQL_USERNAME),
					config.getString(SettingKeys.MYSQL_PASSWORD));
			updateDatabase(connect);
			connect = connect(config.getString(SettingKeys.MYSQL_HOST) + "/" + DB_NAME, config.getString(SettingKeys.MYSQL_USERNAME),
					config.getString(SettingKeys.MYSQL_PASSWORD));
		}
		return connect;
	}
	

	private static Connection connect(String host, String username, String password) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://" + host;
		return DriverManager.getConnection(url, username, password);

	}

	private static void updateDatabase(Connection connect) throws FileNotFoundException, IOException, SQLException {
		if (!databaseExists(connect, DB_NAME)) {
			ScriptRunner runner = new ScriptRunner(connect, false, true);
			String filename = "create.sql";
			InputStream fileStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
			if (fileStream == null) {
				throw new FileNotFoundException("Could not find resource for " + filename);
			}
			runner.runScript(new BufferedReader(new InputStreamReader(fileStream, "UTF-8")));
		}
	}
	

	private static boolean databaseExists(Connection connect, String DbName) throws SQLException {
		Statement statement = connect.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '"
				+ DB_NAME + "'");
		boolean exists = resultSet.next();
		statement.close();
		resultSet.close();
		return exists;
	}

}