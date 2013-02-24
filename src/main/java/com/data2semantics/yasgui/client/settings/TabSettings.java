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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.data2semantics.yasgui.shared.Output;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;

public class TabSettings extends JSONObject {
	private Defaults defaults;
	private Settings mainSettings;
	/**
	 * KEYS
	 */
	public static String ENDPOINT = "endpoint";
	public static String QUERY_STRING = "query";
	public static String TAB_TITLE = "tabTitle";
	public static String OUTPUT_FORMAT = "outputFormat";
	public static String CONTENT_TYPE_SELECT = "contentTypeSelect";
	public static String CONTENT_TYPE_CONSTRUCT = "contentTypeConstruct";
	public static String EXTRA_QUERY_ARGS = "extraArgs";
	public static String REQUEST_METHOD = "requestMethod";

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
				put(QUERY_STRING, jsonObject.get(key));
			} else if (key.equals(OUTPUT_FORMAT)) {
				//we used to have a valid output format value 'json', which is replace by 'rawResponse'. 
				//for backwards compatability, store it under the new value
				//can remove this in a couple of version onwards
				if (jsonObject.get(OUTPUT_FORMAT).isString().stringValue().equals("json")) {
					put(OUTPUT_FORMAT, new JSONString(Output.OUTPUT_RAW_RESPONSE));
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
			if (key.equals(QUERY_STRING)) {
				setQueryString(parameters.get(key).get(0));
			} else if (key.equals(ENDPOINT)) {
				setEndpoint(parameters.get(key).get(0));
			} else if (key.equals(TAB_TITLE)) {
				setTabTitle(parameters.get(key).get(0));
			} else if (key.equals(OUTPUT_FORMAT)) {
				setOutputFormat(parameters.get(key).get(0));
			} else if (key.equals(CONTENT_TYPE_CONSTRUCT)) {
				setConstructContentType(parameters.get(key).get(0));
			} else if (key.equals(CONTENT_TYPE_SELECT)) {
				setSelectContentType(parameters.get(key).get(0));
			} else if (key.equals(REQUEST_METHOD)) {
				setRequestMethod(parameters.get(key).get(0));
			} else if (!key.startsWith("gwt.")) {
				//all other parameter keys are added as extra query arguments 
				//ignore keys starting with gwt though (used for debugging)
				addQueryArg(key, parameters.get(key).get(0));
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
		if (getQueryArgs().size() == 0) {
			put(EXTRA_QUERY_ARGS, defaults.getDefaultQueryArgs());
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
		if (mainSettings.inSingleEndpointMode()) {
			//do this, because otherwise when config on server changes to single endpoint mode, we don't want old (cached) endpoints in settings still being used
			//instead, we just want to use one: the default
			endpoint = defaults.getDefaultEndpoint();
		} else if (containsKey(ENDPOINT)) {
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
		return new TabSettings(mainSettings, JSONParser.parseStrict(this.toString()).isObject());
	}
}
