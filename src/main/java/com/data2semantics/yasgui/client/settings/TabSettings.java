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

import com.data2semantics.yasgui.client.tab.optionbar.QueryConfigMenu;
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
	private static String QUERY_STRING = "queryFormat";//hmm, should be queryString. leave as is (otherwise current user lose their setting)
	private static String TAB_TITLE = "tabTitle";
	private static String OUTPUT_FORMAT = "outputFormat";
	private static String CONTENT_TYPE_SELECT = "contentTypeSelect";
	private static String CONTENT_TYPE_CONSTRUCT = "contentTypeConstruct";
	private static String EXTRA_QUERY_ARGS = "extraArgs";
	private static String REQUEST_METHOD = "requestMethod";

	/**
	 * DEFAULTS
	 */
	private static String DEFAULT_QUERY = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" + 
			"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
			+ "SELECT * {?sub ?pred ?obj} LIMIT 10\n";

	private static String DEFAULT_ENDPOINT = "http://dbpedia.org/sparql";
	private static String DEFAULT_TAB_TITLE = "Query";
	private static String DEFAULT_CONTENT_TYPE_SELECT = QueryConfigMenu.CONTENT_TYPE_SELECT_XML;
	private static String DEFAULT_CONTENT_TYPE_CONSTRUCT = QueryConfigMenu.CONTENT_TYPE_CONSTRUCT_TURTLE;
	private static String DEFAULT_REQUEST_METHOD = QueryConfigMenu.REQUEST_POST;
	private static String DEFAULT_OUTPUT = Output.OUTPUT_TABLE;
	
	public TabSettings() {
		setDefaultsIfUnset();
	}

	public TabSettings(JSONObject jsonObject) {
		Set<String> keys = jsonObject.keySet();
		for (String key : keys) {
			put(key, jsonObject.get(key));
		}
		setDefaultsIfUnset();
	}
	
	private void setDefaultsIfUnset() {
		if (getEndpoint() == null || getEndpoint().length() == 0) {
			setEndpoint(DEFAULT_ENDPOINT);
		}
		if (getQueryString() == null || getQueryString().length() == 0) {
			setQueryString(DEFAULT_QUERY);
		}
		if (getTabTitle() == null || getTabTitle().length() == 0) {
			setTabTitle(DEFAULT_TAB_TITLE);
		}
		if (getSelectContentType() == null || getSelectContentType().length() == 0) {
			setSelectContentType(DEFAULT_CONTENT_TYPE_SELECT);
		}
		if (getConstructContentType() == null || getConstructContentType().length() == 0) {
			setConstructContentType(DEFAULT_CONTENT_TYPE_CONSTRUCT);
		}
		if (getOutputFormat() == null || getOutputFormat().length() == 0) {
			setOutputFormat(DEFAULT_OUTPUT);
		}
		if (getRequestMethod() == null || getRequestMethod().length() == 0) {
			setRequestMethod(DEFAULT_REQUEST_METHOD);
		}
	}

	public void setRequestMethod(String requestMethod) {
		put(REQUEST_METHOD, new JSONString(requestMethod));
		
	}

	public String getRequestMethod() {
		String requestMethod = null;
		if (containsKey(REQUEST_METHOD)) {
			requestMethod = get(REQUEST_METHOD).isString().stringValue();
		}
		return requestMethod;
	}

	public String getEndpoint() {
		String endpoint = null;
		if (containsKey(ENDPOINT)) {
			endpoint = get(ENDPOINT).isString().stringValue();
		}
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		put(ENDPOINT, new JSONString(endpoint));
	}

	public String getQueryString() {
		String queryString = null;
		if (containsKey(QUERY_STRING)) {
			queryString = get(QUERY_STRING).isString().stringValue();
		}
		return queryString;
	}

	public void setQueryString(String queryString) {
		put(QUERY_STRING, new JSONString(queryString));
	}

	public void setTabTitle(String tabTitle) {
		put(TAB_TITLE, new JSONString(tabTitle));
	}

	public String getTabTitle() {
		String title = null;
		if (containsKey(TAB_TITLE)) {
			title = get(TAB_TITLE).isString().stringValue();
		}
		return title;
	}
	public void setOutputFormat(String outputFormat) {
		put(OUTPUT_FORMAT, new JSONString(outputFormat));
	}
	
	public String getOutputFormat() {
		String format = null;
		if (containsKey(OUTPUT_FORMAT)) {
			format = get(OUTPUT_FORMAT).isString().stringValue();
		}
		return format;
	}
	
	public void setSelectContentType(String contentType) {
		put(CONTENT_TYPE_SELECT, new JSONString(contentType));
	}
	
	public String getSelectContentType() {
		String contentType = "";
		if (containsKey(CONTENT_TYPE_SELECT)) {
			 contentType = get(CONTENT_TYPE_SELECT).isString().stringValue();
		}
		return contentType;
	}
	public void setConstructContentType(String contentType) {
		put(CONTENT_TYPE_CONSTRUCT, new JSONString(contentType));
	}
	
	public String getConstructContentType() {
		String contentType = "";
		if (containsKey(CONTENT_TYPE_CONSTRUCT)) {
			contentType = get(CONTENT_TYPE_CONSTRUCT).isString().stringValue();
		}
		return contentType;
	}
	
	public HashMap<String, String> getQueryArgs() {
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
	
	public String getQueryArgsAsJsonString() {
		HashMap<String, String> args = getQueryArgs();
		JSONObject argsObject = new JSONObject();
		for (Entry<String, String> arg: args.entrySet()) {
			argsObject.put(arg.getKey(), new JSONString(arg.getValue()));
		}
		return argsObject.toString();
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
