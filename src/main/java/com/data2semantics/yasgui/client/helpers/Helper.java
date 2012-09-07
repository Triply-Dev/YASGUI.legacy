package com.data2semantics.yasgui.client.helpers;

import java.util.ArrayList;
import java.util.Set;
import com.data2semantics.yasgui.client.queryform.ToolBar;
import com.data2semantics.yasgui.client.settings.Settings;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.user.client.Cookies;

public class Helper {
	private static String COOKIE_SETTINGS = "yasgui_settings";

	public static String implode(ArrayList<String> arrayList, String glue) {
		String result = "";
		for (String stringItem : arrayList) {
			if (result.length() > 0) {
				result += glue;
			}
			result += stringItem;
		}
		return result;
	}

	/**
	 * Get settings from js elements
	 * 
	 * @return Settings object
	 */
	public static Settings getSettings() {
		Settings settings = new Settings();
		// JsMethods.saveCodeMirror();
		// settings.setQueryString(JsMethods.getValueUsingId(QueryTextArea.APPEND_INPUT_ID));
		// settings.setEndpoint(JsMethods.getValueUsingName(EndpointInput.ENDPOINT_INPUT_NAME));
//		settings.setOutputFormat(JsMethods.getValueUsingId(ToolBar.QUERY_FORMAT_SELECTOR_ID));
		return settings;
	}

	public static void storeSettingsInCookie(Settings settings) {
		Cookies.removeCookie(COOKIE_SETTINGS);
		Cookies.setCookie(COOKIE_SETTINGS, settings.toString());
	}

	public static Settings getSettingsFromCookie() {
		Settings settings = new Settings();
		String jsonString = Cookies.getCookie(COOKIE_SETTINGS);
		if (jsonString != null && jsonString.length() > 0) {
			JSONObject jsonObject = JSONParser.parseStrict(jsonString).isObject();
			if (jsonObject == null) {
				// Something went wrong. Just use original 'bare' settings
				// objects
			} else {
				settings = new Settings(jsonObject);
			}
		}
		return settings;
	}
}
