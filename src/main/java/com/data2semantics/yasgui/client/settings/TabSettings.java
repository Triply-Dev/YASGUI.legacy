package com.data2semantics.yasgui.client.settings;

import java.util.Set;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;

public class TabSettings extends JSONObject {

	/**
	 * HASHMAP KEYS
	 */
	private static String ENDPOINT = "endpoint";
	private static String QUERY_STRING = "queryFormat";
	private static String TAB_TITLE = "tabTitle";

	/**
	 * DEFAULTS
	 */
	private static String DEFAULT_QUERY = "PREFIX aers: <http://aers.data2semantics.org/resource/> \n"
			+ "SELECT * {<http://aers.data2semantics.org/resource/report/5578636> ?f ?g} LIMIT 50";

	private static String DEFAULT_ENDPOINT = "http://sws.ifi.uio.no/sparql/npd";// cors
																				// enabled
	private static String DEFAULT_TAB_TITLE = "Query";

	public TabSettings() {
		setEndpoint(DEFAULT_ENDPOINT);
		setQueryString(DEFAULT_QUERY);
		setTabTitle(DEFAULT_TAB_TITLE);
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

	public TabSettings clone() {
		//GWT and cloning is difficult. Use the simple solution: serialize to json, and parse into new settings object
		return new TabSettings(JSONParser.parseStrict(this.toString()).isObject());
	}
}
