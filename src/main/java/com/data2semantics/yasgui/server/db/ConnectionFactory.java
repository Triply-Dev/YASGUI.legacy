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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.server.fetchers.ConfigFetcher;
import com.data2semantics.yasgui.shared.SettingKeys;

public class ConnectionFactory  {
	
	public static Connection getConnection(File configDir) throws JSONException, ClassNotFoundException, SQLException,
	FileNotFoundException, IOException, ParseException {
		JSONObject config = ConfigFetcher.getJsonObject(configDir);
		Connection connect = null;
		try {
			connect = connect(config.getString(SettingKeys.MYSQL_HOST) + "/" + config.getString(SettingKeys.MYSQL_DB), config.getString(SettingKeys.MYSQL_USERNAME),
					config.getString(SettingKeys.MYSQL_PASSWORD));
		} catch (SQLException e) {
			//db probably doesnt exist.
			//connect without db selector, create db, and create new connector
			connect = connect(config.getString(SettingKeys.MYSQL_HOST), config.getString(SettingKeys.MYSQL_USERNAME),
					config.getString(SettingKeys.MYSQL_PASSWORD));
			connect = updateDatabase(connect, config, configDir);
			
		}
		return connect;
	}
	

	private static Connection connect(String host, String username, String password) throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://" + host;
		return DriverManager.getConnection(url, username, password);

	}

	private static Connection updateDatabase(Connection connect, JSONObject config, File configDir) throws FileNotFoundException, IOException, SQLException, JSONException, ClassNotFoundException {
		String dbName = config.getString(SettingKeys.MYSQL_DB);
		if (!databaseExists(connect, dbName)) {
			createDatabase(connect, dbName);
			connect.close();
			connect = connect(config.getString(SettingKeys.MYSQL_HOST) + "/" + config.getString(SettingKeys.MYSQL_DB), config.getString(SettingKeys.MYSQL_USERNAME),
					config.getString(SettingKeys.MYSQL_PASSWORD));
			
			System.out.println("connected to " + config.getString(SettingKeys.MYSQL_HOST) + "/" + config.getString(SettingKeys.MYSQL_DB));
			
			ScriptRunner runner = new ScriptRunner(connect, false, true);
			String filename = "create.sql";
			FileInputStream fileStream = new FileInputStream(configDir.getAbsolutePath() + "/" + ConfigFetcher.CONFIG_DIR + filename);
			runner.runScript(new BufferedReader(new InputStreamReader(fileStream, "UTF-8")));
		}
		return connect;
	}
	
	private static void createDatabase(Connection connect, String dbName) throws SQLException {
		Statement statement = connect.createStatement();
		statement.executeUpdate("CREATE DATABASE `" + dbName + "` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci");
		statement.close();
	}

	private static boolean databaseExists(Connection connect, String dbName) throws SQLException {
		Statement statement = connect.createStatement();
		ResultSet resultSet = statement.executeQuery("SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA WHERE SCHEMA_NAME = '"
				+ dbName + "'");
		boolean exists = resultSet.next();
		statement.close();
		resultSet.close();
		return exists;
	}

}