package com.data2semantics.yasgui.client.settings;

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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsonHelper;
import com.data2semantics.yasgui.shared.Output;
import com.data2semantics.yasgui.shared.SettingKeys;
import com.google.common.collect.HashMultimap;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;

public class TabSettings extends JsonHelper {
	private Defaults defaults;
	private Settings mainSettings;

	public TabSettings(Settings mainSettings) {
		this.defaults = mainSettings.getDefaults();
		this.mainSettings = mainSettings;
		setDefaultsIfUnset();
	}

	public TabSettings(Settings mainSettings, JSONObject jsonObject) {
		this.mainSettings = mainSettings;
		this.defaults = mainSettings.getDefaults();
		Set<String> keys = jsonObject.keySet();
		for (String key : keys) {
			if (key.equals("queryFormat")) {
				//we used to have queryFormat as key for query string (yes, a bug).
				//for backwards compatability, store this as query string
				//can remove this a couple of versions onwards
				put(SettingKeys.QUERY_STRING, jsonObject.get(key));
			} else if (key.equals(SettingKeys.OUTPUT_FORMAT)) {
				//we used to have a valid output format value 'json', which is replace by 'rawResponse'. 
				//for backwards compatability, store it under the new value
				//can remove this in a couple of version onwards
				if (jsonObject.get(SettingKeys.OUTPUT_FORMAT).isString().stringValue().equals("json")) {
					put(SettingKeys.OUTPUT_FORMAT, new JSONString(Output.OUTPUT_RAW_RESPONSE));
				} else {
					put(key, jsonObject.get(key));
				}
			} else {
				put(key, jsonObject.get(key));
			}
		}
		setDefaultsIfUnset();
	}
	
	/**
	 * Add settings from url parameters
	 */
	public void setValuesFromUrlParams() {
		Map<String, List<String>> parameters = Window.Location.getParameterMap();
		Iterator<String> iterator = parameters.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			if (key.equals(SettingKeys.QUERY_STRING)) {
				setQueryString(parameters.get(key).get(0));
			} else if (key.equals(SettingKeys.ENDPOINT)) {
				setEndpoint(parameters.get(key).get(0));
			} else if (key.equals(SettingKeys.TAB_TITLE)) {
				setTabTitle(parameters.get(key).get(0));
			} else if (key.equals(SettingKeys.OUTPUT_FORMAT)) {
				setOutputFormat(parameters.get(key).get(0));
			} else if (key.equals(SettingKeys.CONTENT_TYPE_CONSTRUCT)) {
				setConstructContentType(parameters.get(key).get(0));
			} else if (key.equals(SettingKeys.CONTENT_TYPE_SELECT)) {
				setSelectContentType(parameters.get(key).get(0));
			} else if (key.equals(SettingKeys.REQUEST_METHOD)) {
				setRequestMethod(parameters.get(key).get(0));
			} else if (key.equals(SettingKeys.NAMED_GRAPHS)) {
				setNamedGraphs(new ArrayList<String>(parameters.get(key)));
			} else if (key.equals(SettingKeys.DEFAULT_GRAPHS)) {
				setDefaultGraphs(new ArrayList<String>(parameters.get(key)));
			} else if (!key.startsWith("gwt.")) {
				//all other parameter keys are added as extra query arguments 
				//ignore keys starting with gwt though (used for debugging)
				addCustomQueryArg(key, parameters.get(key).get(0));
			}
		}
	}
	
	
	private void setDefaultsIfUnset() {
		if (getEndpoint() == null || getEndpoint().length() == 0) {
			setEndpoint(defaults.getDefaultEndpoint());
		}
		if (getQueryString() == null || getQueryString().length() == 0) {
			setQueryString(defaults.getDefaultQueryString());
		}
		if (getTabTitle() == null || getTabTitle().length() == 0) {
			setTabTitle(defaults.getDefaultTabTitle());
		}
		if (getSelectContentType() == null || getSelectContentType().length() == 0) {
			setSelectContentType(defaults.getDefaultSelectContentType());
		}
		if (getConstructContentType() == null || getConstructContentType().length() == 0) {
			setConstructContentType(defaults.getDefaultConstructContentType());
		}
		if (getOutputFormat() == null || getOutputFormat().length() == 0) {
			setOutputFormat(defaults.getDefaultOutputFormat());
		}
		if (getRequestMethod() == null || getRequestMethod().length() == 0) {
			setRequestMethod(defaults.getDefaultRequestMethod());
		}
		if (getRequestMethod() == null || getRequestMethod().length() == 0) {
			setRequestMethod(defaults.getDefaultRequestMethod());
		}
		if (getCustomQueryArgs().size() == 0) {
			put(SettingKeys.EXTRA_QUERY_ARGS, defaults.getDefaultQueryArgs());
		}
		if (getNamedGraphs().size() == 0) {
			put(SettingKeys.NAMED_GRAPHS, defaults.getDefaultNamedGraphs());
		}
		if (getDefaultGraphs().size() == 0) {
			put(SettingKeys.DEFAULT_GRAPHS, defaults.getDefaultDefaultGraphs());
		}
	}

	public void setRequestMethod(String requestMethod) {
		put(SettingKeys.REQUEST_METHOD, new JSONString(requestMethod));
		
	}

	public String getRequestMethod() {
		return getString(SettingKeys.REQUEST_METHOD, null);
	}

	public String getEndpoint() {
		String endpoint = null;
		if (!mainSettings.getEnabledFeatures().endpointSelectionEnabled()) {
			//do this, because otherwise when config on server changes to single endpoint mode, we don't want old (cached) endpoints in settings still being used
			//instead, we just want to use one: the default
			endpoint = defaults.getDefaultEndpoint();
		} else if (containsKey(SettingKeys.ENDPOINT)) {
			endpoint = getString(SettingKeys.ENDPOINT, null);
		}
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		put(SettingKeys.ENDPOINT, new JSONString(endpoint));
	}

	public String getQueryString() {
		return getString(SettingKeys.QUERY_STRING, null);
	}

	public void setQueryString(String queryString) {
		put(SettingKeys.QUERY_STRING, new JSONString(queryString));
	}

	public void setTabTitle(String tabTitle) {
		put(SettingKeys.TAB_TITLE, new JSONString(tabTitle));
	}

	public String getTabTitle() {
		return getString(SettingKeys.TAB_TITLE, null);
	}
	public void setOutputFormat(String outputFormat) {
		put(SettingKeys.OUTPUT_FORMAT, new JSONString(outputFormat));
	}
	
	public String getOutputFormat() {
		return getString(SettingKeys.OUTPUT_FORMAT, null);
	}
	
	public void setSelectContentType(String contentType) {
		put(SettingKeys.CONTENT_TYPE_SELECT, new JSONString(contentType));
	}
	
	public String getSelectContentType() {
		return getString(SettingKeys.CONTENT_TYPE_SELECT, "");
	}
	public void setConstructContentType(String contentType) {
		put(SettingKeys.CONTENT_TYPE_CONSTRUCT, new JSONString(contentType));
	}
	
	public String getConstructContentType() {
		return getString(SettingKeys.CONTENT_TYPE_CONSTRUCT, "");
	}
	
	public ArrayList<String> getNamedGraphs() {
		ArrayList<String> namedGraphs = new ArrayList<String>();
		if (containsKey(SettingKeys.NAMED_GRAPHS)) {
			namedGraphs = Helper.getJsonAsArrayList(get(SettingKeys.NAMED_GRAPHS));
		}
		return namedGraphs;
	}
	
	public void setNamedGraphs(ArrayList<String> namedGraphs) {
		put(SettingKeys.NAMED_GRAPHS, Helper.getArrayListAsJson(namedGraphs));
	}
	public ArrayList<String> getDefaultGraphs() {
		ArrayList<String> defaultGraphs = new ArrayList<String>();
		if (containsKey(SettingKeys.DEFAULT_GRAPHS)) {
			defaultGraphs = Helper.getJsonAsArrayList(get(SettingKeys.DEFAULT_GRAPHS));
		}
		return defaultGraphs;
	}
	
	public void setDefaultGraphs(ArrayList<String> defaultGraphs) {
		put(SettingKeys.DEFAULT_GRAPHS, Helper.getArrayListAsJson(defaultGraphs));
	}
	
	private JSONObject getSimpleQueryArgObject(String name, String value) {
		JSONObject object = new JSONObject();
		object.put("name", new JSONString(name));
		object.put("value", new JSONString(value));
		return object;
	}
	public String getQueryArgsAsJsonString() {
		return getQueryArgsAsJson().toString();
	}
	public String getQueryArgsAsUrlString() {
		String urlString = "";
		JSONArray argsArray = getQueryArgsAsJson();
		for (int i = 0; i < argsArray.size(); i++) {
			JSONObject argObject = argsArray.get(i).isObject();
			urlString += "&" + argObject.get("name").isString().stringValue() + 
					"=" + URL.encodeQueryString(argObject.get("value").isString().stringValue());
		}
		return urlString;
	}
	
	public JSONArray getQueryArgsAsJson() {
		JSONArray argsArray = new JSONArray();
		HashMultimap<String, String> args = getQueryArgs();
		for (Entry<String, String> arg: args.entries()) {
			argsArray.set(argsArray.size(), getSimpleQueryArgObject(arg.getKey(), arg.getValue()));
		}
		return argsArray;
	}
	
	public HashMultimap<String, String> getQueryArgs() {
		HashMultimap<String, String> args = getCustomQueryArgs();
		for (String defaultGraph: getDefaultGraphs()) {
			args.put("default-graph-uri", defaultGraph);
		}
		for (String namedGraph: getNamedGraphs()) {
			args.put("named-graph-uri", namedGraph);
		}
		return args;
	}
	
	public HashMultimap<String, String> getCustomQueryArgs() {
		HashMultimap<String, String> args = HashMultimap.create();
		
		if (containsKey(SettingKeys.EXTRA_QUERY_ARGS)) {
			JSONArray argsArray = get(SettingKeys.EXTRA_QUERY_ARGS).isArray();
			if (argsArray != null) {
				for (int i = 0; i < argsArray.size(); i++) {
					JSONObject argObject = argsArray.get(i).isObject();
					//for backwards compatability, first try to get key and value from object
					//can remove this in the future
					String key = null;
					String value = null;
					if (argObject.containsKey("key") && argObject.containsKey("value")) {
						key = argObject.get("key").isString().stringValue();
						value = argObject.get("value").isString().stringValue();
					} else {
						Set<String> keySet = argObject.keySet();
						for (String arrayKey: keySet) {
							key = arrayKey;
							value = argObject.get(arrayKey).isString().stringValue();
						}
					}
					if (key != null && value != null) {
						args.put(key, value);
					}
				}
			}
		}
		return args;
	}
	
	public void clearQueryResultsString() {
		put(SettingKeys.QUERY_RESULT_STRING, null);
	}
	public void clearQueryResultsContentType() {
		put(SettingKeys.QUERY_RESULT_CONTENT_TYPE, null);
	}
	
	public void setQueryResultsString(String queryResultsString) {
		put(SettingKeys.QUERY_RESULT_STRING, new JSONString(queryResultsString));
	}
	public String getQueryResultsString() {
		return getString(SettingKeys.QUERY_RESULT_STRING, null);
	}
	public void setQueryResultsContentType(String queryResultsContentType) {
		put(SettingKeys.QUERY_RESULT_CONTENT_TYPE, new JSONString(queryResultsContentType));
	}
	public String getQueryResultsContentType() {
		return getString(SettingKeys.QUERY_RESULT_CONTENT_TYPE, null);
	}
	
	public void addCustomQueryArg(String key, String value) {
		JSONArray argsArray;
		if (!containsKey(SettingKeys.EXTRA_QUERY_ARGS)) {
			argsArray = new JSONArray();
		} else {
			argsArray = get(SettingKeys.EXTRA_QUERY_ARGS).isArray();
		}
		JSONObject argObject = new JSONObject();
		argObject.put("key", new JSONString(key));
		argObject.put("value", new JSONString(value));
		argsArray.set(argsArray.size(), argObject);
		put(SettingKeys.EXTRA_QUERY_ARGS, argsArray);
	}
	public void resetAndaddCustomQueryArgs(HashMultimap<String, String> args) {
		JSONArray argsArray = new JSONArray();
		for (Entry<String, String> arg: args.entries()) {
			JSONObject argObject = new JSONObject();
			argObject.put("key", new JSONString(arg.getKey()));
			argObject.put("value", new JSONString(arg.getValue()));
			argsArray.set(argsArray.size(), argObject);
		}
		put(SettingKeys.EXTRA_QUERY_ARGS, argsArray);

	}
	
	public int compareTo(TabSettings otherSettings) {
		return toString().compareTo(otherSettings.toString());
	}
	
	public TabSettings clone() {
		//GWT and cloning is difficult. Use the simple solution: serialize to json, and parse into new settings object
		return new TabSettings(mainSettings, JSONParser.parseStrict(this.toString()).isObject());
	}
}
