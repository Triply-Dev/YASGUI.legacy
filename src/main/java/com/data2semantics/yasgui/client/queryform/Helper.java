package com.data2semantics.yasgui.client.queryform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.data2semantics.yasgui.client.JsMethods;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.shared.Settings;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Cookies;

public class Helper {
	private static String COOKIE_SETTINGS = "yasgui_settings";
	public static String implode(ArrayList<String> arrayList, String glue) {
		String result = "";
		for (String stringItem: arrayList) {
			if (result.length() > 0) {
				result += glue;
			}
			result += stringItem;
		}
		return result;
	}
	
	public static String getHashMapAsJson(HashMap<String, String> hashMap) {
		JSONObject json = new JSONObject();
		
		for (Entry<String, String> entry : hashMap.entrySet()) {
			json.put(entry.getKey(), new JSONString(entry.getValue()));
		}

		return json.toString();
	}
	
	public static Settings getSettingsFromJsonString(String jsonString) {
		Settings settings = new Settings();
		JSONValue value = JSONParser.parseStrict(jsonString);
        JSONObject jsonObject = value.isObject();
        if (jsonObject != null) {
        	Set<String> keys = jsonObject.keySet();
        	Iterator<String> iterator = keys.iterator();
        	while(iterator.hasNext()) {
        		String key = iterator.next();
        		JSONString jsonStringValue = jsonObject.get(key).isString();
        		if (jsonStringValue != null) {
        			settings.setSettingDirectly(key, jsonStringValue.stringValue());
        		}
        	}
        }

		return settings;
	}
	
	/**
	 * Get settings from js elements
	 * 
	 * @return Settings object
	 */
	public static Settings getSettings() {
		Settings settings = new Settings();
		JsMethods.saveCodeMirror();
		settings.setQueryString(JsMethods.getValueUsingId(View.QUERY_INPUT_ID));
		settings.setEndpoint(JsMethods.getValueUsingName(View.ENDPOINT_INPUT_NAME));
		settings.setOutputFormat(JsMethods.getValueUsingId(ToolBar.QUERY_FORMAT_SELECTOR_ID));
		return settings;
	}
	
	public static void getAndStoreSettingsInCookie() {
		Settings settings = getSettings();
		Cookies.removeCookie(COOKIE_SETTINGS);
		Cookies.setCookie(COOKIE_SETTINGS, Helper.getHashMapAsJson(settings.getSettingsHashMap()));
	}
	
	public static void storeSettingsInCookie(Settings settings) {
		Cookies.removeCookie(COOKIE_SETTINGS);
		Cookies.setCookie(COOKIE_SETTINGS, Helper.getHashMapAsJson(settings.getSettingsHashMap()));
	}
	
	public static Settings getSettingsFromCookie() {
		Settings settings = new Settings();
		String jsonString = Cookies.getCookie(COOKIE_SETTINGS);
		if (jsonString != null && jsonString.length() > 0) {
			settings = Helper.getSettingsFromJsonString(jsonString);
		}
		return settings;
	}
	
}
