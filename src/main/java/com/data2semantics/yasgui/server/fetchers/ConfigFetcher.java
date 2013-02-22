package com.data2semantics.yasgui.server.fetchers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

/**
 * Servlet implementation class ConfigServlet
 */
public class ConfigFetcher {
	private static String CONFIG_FILE = "/config/config.json";

	public static String getJson(String path) throws FileNotFoundException, IOException {
		String jsonString;
		File configFile = new File( path + CONFIG_FILE);
		if (!configFile.exists()) {
			throw new IOException("Unable to load config file from server. Trying to load: " + configFile.getAbsolutePath());
		} else {
			jsonString = IOUtils.toString(new FileReader(configFile));
			try {
				new JSONObject(jsonString);
			} catch (Exception e) {
				throw new IOException("Unable to parse config file on server", e);
			}
			
		}
		return jsonString;
	}

}
