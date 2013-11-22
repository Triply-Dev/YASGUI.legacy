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

import java.util.Map;
import java.util.Set;

import com.data2semantics.yasgui.client.helpers.JsonHelper;
import com.data2semantics.yasgui.shared.SettingKeys;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;

public class EnabledFeatures extends JsonHelper {
	private Settings settings;
	public EnabledFeatures(Settings settings) {
		this.settings = settings;
	}
	public EnabledFeatures(Settings settings, JSONObject jsonObject) {
		this.settings = settings;
		update(jsonObject);
	}
	
	public void update(JSONObject jsonObject) {
		Set<String> keys = jsonObject.keySet();
		for (String key : keys) {
			put(key, jsonObject.get(key));
		}
	}
	
	public boolean endpointSelectionEnabled() {
		//for backwards compatability, support the option 'singleEndpointMode' as well
		if (settings.containsKey(SettingKeys.SINGLE_ENDPOINT_MODE)) {
			boolean singleEndpointMode = settings.get(SettingKeys.SINGLE_ENDPOINT_MODE).isBoolean().booleanValue();
			//remove this key in settings, and convert to this object
			settings.put(SettingKeys.SINGLE_ENDPOINT_MODE, null);
			//now set in this object
			put(SettingKeys.ENABLED_ENDPOINT_SELECTION, JSONBoolean.getInstance(!singleEndpointMode));
		}
		return getBoolean(SettingKeys.ENABLED_ENDPOINT_SELECTION, true);
	}
	public boolean acceptHeadersEnabled() {
		return getBoolean(SettingKeys.ENABLED_ACCEPT_HEADERS, true);
	}
	public boolean propertyAutocompletionEnabled() {
		return getBoolean(SettingKeys.ENABLED_AUTOCOMPLETION_PROPS, true);
	}
	public boolean defaultGraphsSpecificationEnabled() {
		return getBoolean(SettingKeys.ENABLED_DEFAULT_GRAPHS, true);
	}
	public boolean namedGraphsSpecificationEnabled() {
		return getBoolean(SettingKeys.ENABLED_NAMED_GRAPHS, true);
	}
	public boolean queryParametersEnabled() {
		return getBoolean(SettingKeys.ENABLED_QUERY_PARAMS, true);
	}
	public boolean requestParametersEnabled() {
		return getBoolean(SettingKeys.ENABLED_REQUEST_METHOD, true);
	}
	public boolean offlineCachingEnabled() {
		return getBoolean(SettingKeys.ENABLED_OFFLINE_CACHING, true);
	}
	public Map<String, Boolean> getEnabledPropertyCompletionMethods() {
		return getMap(SettingKeys.ENABLED_PROPERTY_COMPLETION_METHODS, null);
	}
	public Map<String, Boolean> getEnabledClassCompletionMethods() {
		return getMap(SettingKeys.ENABLED_CLASS_COMPLETION_METHODS, null);
	}
	
}
