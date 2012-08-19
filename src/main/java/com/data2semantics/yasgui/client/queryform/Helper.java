package com.data2semantics.yasgui.client.queryform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import com.data2semantics.yasgui.shared.Settings;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class Helper {
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
	
}
