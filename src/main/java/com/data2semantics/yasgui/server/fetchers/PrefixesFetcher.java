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

	public static String fetch(boolean forceUpdate, File cacheDir) throws URISyntaxException, MalformedURLException, IOException, JSONException {
		String result = "";
		JSONArray prefixes = new JSONArray();
		File file = new File(cacheDir + "/" + CACHE_FILENAME);
		boolean updateFile = forceUpdate;
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
		if (updateFile) {
			URI uri = new URI(PREFIX_CC_URL);
			BufferedReader reader = new BufferedReader(new InputStreamReader(uri.toURL().openStream()));
			JSONTokener tokener = new JSONTokener(reader);
			JSONObject jsonObject = new JSONObject(tokener);

			// Get list of keys, and sort them
			@SuppressWarnings("unchecked")
			Iterator<String> keys = jsonObject.keys();
			ArrayList<String> keysList = new ArrayList<String>();
			while (keys.hasNext()) {
				keysList.add(keys.next());
			}
			Collections.sort(keysList);

			// Build new JSONArray (JSONArray is sorted, JSONObject is not)
			// using this keylist
			for (String key : keysList) {
				if (!jsonObject.isNull(key)) {
					prefixes.put(key + ": <" + jsonObject.getString(key) + ">\n");
				}
			}
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
}
