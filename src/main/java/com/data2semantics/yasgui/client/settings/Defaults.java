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

import java.util.Set;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

public class Defaults extends JSONObject {

	public Defaults(JSONObject jsonObject) {
		Set<String> keys = jsonObject.keySet();
		for (String key : keys) {
			put(key, jsonObject.get(key));
		}
	}
	public String getDefaultEndpoint() {
		String endpoint = null;
		if (containsKey(TabSettings.ENDPOINT)) {
			endpoint = get(TabSettings.ENDPOINT).isString().stringValue();
		}
		return endpoint;
	}
	

	public String getDefaultQueryString() {
		String queryString = null;
		if (containsKey(TabSettings.QUERY_STRING)) {
			queryString = get(TabSettings.QUERY_STRING).isString().stringValue();
		}
		return queryString;
	}

	public String getDefaultTabTitle() {
		String title = null;
		if (containsKey(TabSettings.TAB_TITLE)) {
			title = get(TabSettings.TAB_TITLE).isString().stringValue();
		}
		return title;
	}
	
	public String getDefaultOutputFormat() {
		String format = null;
		if (containsKey(TabSettings.OUTPUT_FORMAT)) {
			format = get(TabSettings.OUTPUT_FORMAT).isString().stringValue();
		}
		return format;
	}
	
	public String getDefaultSelectContentType() {
		String contentType = "";
		if (containsKey(TabSettings.CONTENT_TYPE_SELECT)) {
			 contentType = get(TabSettings.CONTENT_TYPE_SELECT).isString().stringValue();
		}
		return contentType;
	}
	
	
	public String getDefaultConstructContentType() {
		String contentType = "";
		if (containsKey(TabSettings.CONTENT_TYPE_CONSTRUCT)) {
			contentType = get(TabSettings.CONTENT_TYPE_CONSTRUCT).isString().stringValue();
		}
		return contentType;
	}
	
	public String getDefaultRequestMethod() {
		String requestMethod = "";
		if (containsKey(TabSettings.REQUEST_METHOD)) {
			requestMethod = get(TabSettings.REQUEST_METHOD).isString().stringValue();
		}
		return requestMethod;
	}
	
	public JSONArray getDefaultQueryArgs() {
		JSONArray args = null;
		if (containsKey(TabSettings.EXTRA_QUERY_ARGS)) {
			args = get(TabSettings.EXTRA_QUERY_ARGS).isArray();
		}
		return args;
	}
	
}
