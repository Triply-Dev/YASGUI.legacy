/*******************************************************************************
 * Copyright (c)  2012 Laurens Rietveld
 * 
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.data2semantics.yasgui.client.settings;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;
import com.data2semantics.yasgui.shared.Output;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;

public class TabSettings extends JSONObject {

	/**
	 * KEYS
	 */
	private static String ENDPOINT = "endpoint";
	private static String QUERY_STRING = "queryFormat";
	private static String TAB_TITLE = "tabTitle";
	private static String OUTPUT_FORMAT = "outputFormat";
	private static String CONTENT_TYPE = "contentType";
	private static String EXTRA_QUERY_ARGS = "extraArgs";

	/**
	 * DEFAULTS
	 */
	private static String DEFAULT_QUERY = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
			+ "SELECT * {?sub ?pred ?obj} LIMIT 10";

	private static String DEFAULT_ENDPOINT = "http://dbpedia.org/sparql";
	private static String DEFAULT_TAB_TITLE = "Query";
	private static String DEFAULT_CONTENT_TYPE = "application/sparql-results+xml";
	
	public TabSettings() {
		setEndpoint(DEFAULT_ENDPOINT);
		setQueryString(DEFAULT_QUERY);
		setTabTitle(DEFAULT_TAB_TITLE);
		setContentType(DEFAULT_CONTENT_TYPE);
		setOutputFormat(Output.OUTPUT_TABLE);
	}

	public TabSettings(JSONObject jsonObject) {
		Set<String> keys = jsonObject.keySet();
		for (String key : keys) {
			put(key, jsonObject.get(key));
		}

	}

	public String getEndpoint() {
		return get(ENDPOINT).isString().stringValue();
	}

	public void setEndpoint(String endpoint) {
		put(ENDPOINT, new JSONString(endpoint));
	}

	public String getQueryString() {
		return get(QUERY_STRING).isString().stringValue();
	}

	public void setQueryString(String queryString) {
		put(QUERY_STRING, new JSONString(queryString));
	}

	public void setTabTitle(String tabTitle) {
		put(TAB_TITLE, new JSONString(tabTitle));
	}

	public String getTabTitle() {
		return get(TAB_TITLE).isString().stringValue();
	}
	public void setOutputFormat(String outputFormat) {
		put(OUTPUT_FORMAT, new JSONString(outputFormat));
	}
	
	public String getOutputFormat() {
		return get(OUTPUT_FORMAT).isString().stringValue();
	}
	
	public void setContentType(String contentType) {
		put(CONTENT_TYPE, new JSONString(contentType));
	}
	public String getContentType() {
		String contentType = "";
		if (containsKey(CONTENT_TYPE)) {
			 contentType = get(CONTENT_TYPE).isString().stringValue();
		}
		return contentType;
	}
	
	public HashMap<String, String> getQueryArgs(String contentType) {
		HashMap<String, String> args = new HashMap<String, String>();
		if (containsKey(EXTRA_QUERY_ARGS)) {
			JSONArray argsArray = get(EXTRA_QUERY_ARGS).isArray();
			if (argsArray != null) {
				for (int i = 0; i < argsArray.size(); i++) {
					JSONObject argObject = argsArray.get(i).isObject();
					String key = argObject.get("key").isString().stringValue();
					String value = argObject.get("value").isString().stringValue();
					args.put(key, value);
				}
			}
		}
		return args;
	}
	public void addQueryArg(String key, String value) {
		JSONArray argsArray;
		if (!containsKey(EXTRA_QUERY_ARGS)) {
			argsArray = new JSONArray();
		} else {
			argsArray = get(EXTRA_QUERY_ARGS).isArray();
		}
		JSONObject argObject = new JSONObject();
		argObject.put("key", new JSONString(key));
		argObject.put("value", new JSONString(value));
		argsArray.set(argsArray.size(), argObject);
		put(EXTRA_QUERY_ARGS, argsArray);
	}
	public void resetAndaddQueryArgs(HashMap<String, String> args) {
		JSONArray argsArray = new JSONArray();
		for (Entry<String, String> arg: args.entrySet()) {
			JSONObject argObject = new JSONObject();
			argObject.put("key", new JSONString(arg.getKey()));
			argObject.put("value", new JSONString(arg.getValue()));
			argsArray.set(argsArray.size(), argObject);
		}
		put(EXTRA_QUERY_ARGS, argsArray);

	}

	public TabSettings clone() {
		//GWT and cloning is difficult. Use the simple solution: serialize to json, and parse into new settings object
		return new TabSettings(JSONParser.parseStrict(this.toString()).isObject());
	}
}
