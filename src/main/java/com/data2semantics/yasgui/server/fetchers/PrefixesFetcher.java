package com.data2semantics.yasgui.server.fetchers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.data2semantics.yasgui.server.Helper;

public class PrefixesFetcher {
	public static String CACHE_FILENAME = "prefixes.json";
	public static String PREFIX_CC_URL = "http://prefix.cc/popular/all.file.json";
	public static int CACHE_EXPIRES_DAYS = 1;//cache expires after 1 day
	public static String fetch(boolean forceUpdate, File cacheDir) throws URISyntaxException, MalformedURLException, IOException, JSONException {
		String result = "";
		File file = new File(cacheDir + "/" + CACHE_FILENAME);
		
		if (needUpdating(forceUpdate, file)) {
			JSONObject jsonObject = getJsonObject();
			JSONArray prefixes = convertToSortedJsonArray(jsonObject);
			
			result = prefixes.toString();
			// Write to file on server
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(result);
			out.close();
		} else {
			try {
				result = Helper.readFile(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return result;
	}
	
	/**
	 * Checks whether we need to update the prefixes. 
	 * @param updateFile
	 * @param file
	 * @return
	 */
	private static boolean needUpdating(boolean updateFile, File file) {
		if (!updateFile) {
			if (!file.exists()) {
				updateFile = true;
			} else {
				Long now = new Date().getTime();
				Long lastModified = file.lastModified();
				if ((now - lastModified) > 1000 * 60 * 60 * 24 * 1) {
					updateFile = true;
				}
			}
		}
		return updateFile;
	}
	
	/**
	 * Get JSON object by fetching the prefix.cc prefix page
	 * @return
	 * @throws JSONException
	 * @throws URISyntaxException
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	private static JSONObject getJsonObject() throws JSONException, URISyntaxException, MalformedURLException, IOException {
		URI uri = new URI(PREFIX_CC_URL);
		BufferedReader reader = new BufferedReader(new InputStreamReader(uri.toURL().openStream()));
		JSONTokener tokener = new JSONTokener(reader);
		return new JSONObject(tokener);
	}
	
	/**
	 * We want a sorted list of prefixes, and we retrieved a json object from prefix.cc. 
	 * JSONObject is unsorted, so convert to JSONArray
	 * @param jsonObject
	 * @return JSONArray
	 * @throws JSONException
	 */
	private static JSONArray convertToSortedJsonArray(JSONObject jsonObject) throws JSONException {
		JSONArray prefixes = new JSONArray();
		// Get list of keys, and sort them
		@SuppressWarnings("unchecked")
		Iterator<String> keys = jsonObject.keys();
		ArrayList<String> keysList = new ArrayList<String>();
		while (keys.hasNext()) {
			keysList.add(keys.next());
		}
		Collections.sort(keysList);

		for (String key : keysList) {
			if (!jsonObject.isNull(key)) {
				prefixes.put(key + ": <" + jsonObject.getString(key) + ">\n");
			}
		}
		return prefixes;
	}
}
