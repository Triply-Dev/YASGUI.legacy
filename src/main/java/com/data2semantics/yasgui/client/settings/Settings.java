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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import com.data2semantics.yasgui.client.helpers.JsonHelper;
import com.data2semantics.yasgui.shared.SettingKeys;
import com.data2semantics.yasgui.shared.exceptions.SettingsException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class Settings extends JsonHelper {
	private ArrayList<TabSettings> tabArray = new ArrayList<TabSettings>();
	private Defaults defaults;

	
	
	/**
	 * DEFAULTS
	 */
	public static int DEFAULT_SELECTED_TAB = 0;
	
	
	public void addToSettings(String jsonString) throws IOException {
		JSONValue jsonVal = JSONParser.parseStrict(jsonString);
		if (jsonVal != null) {
			JSONObject jsonObject = jsonVal.isObject();
			if (jsonObject != null) {
				addToSettings(jsonObject);
			} else {
				throw new IOException("Unable to convert json value to json object");
			}
		} else {
			throw new IOException("Unable to parse json settings string");
		}
	}
	
	public void addToSettings(JSONObject jsonObject) {
		Set<String> keys = jsonObject.keySet();
		for (String key : keys) {
			if (key.equals(SettingKeys.TAB_SETTINGS)) {
				JSONArray jsonArray = jsonObject.get(key).isArray();
				for (int i = 0; i < jsonArray.size(); i++) {
					//Add as TabSettings to tab arraylist
					tabArray.add(new TabSettings(this, jsonArray.get(i).isObject()));
				}
			} else if (key.equals(SettingKeys.DEFAULTS)) {
				defaults = new Defaults(jsonObject.get(key).isObject());
			} else {
				put(key, jsonObject.get(key));
			}
		}
	}
	
	public void initDefaultTab() {
		this.setSelectedTabNumber(DEFAULT_SELECTED_TAB);
		addTabSettings(new TabSettings(this, defaults));
	}
	
	public boolean inSingleEndpointMode() {
		boolean singleEndpointMode = false;
		if (containsKey(SettingKeys.SINGLE_ENDPOINT_MODE)) {
			singleEndpointMode = get(SettingKeys.SINGLE_ENDPOINT_MODE).isBoolean().booleanValue();
		}
		return singleEndpointMode;
	}

	
	public void setSelectedTabNumber(int selectedTabNumber) {
		put(SettingKeys.SELECTED_TAB_NUMBER, new JSONNumber(selectedTabNumber));
	}
	
	public void addTabSettings(TabSettings tabSettings) {
		tabArray.add(tabSettings);
	}
	
	public ArrayList<TabSettings> getTabArray() {
		return tabArray;
	}
	
	
	public int getSelectedTabNumber() {
		int selectedTab = (int)get(SettingKeys.SELECTED_TAB_NUMBER).isNumber().doubleValue();
		if (selectedTab >= tabArray.size()) {
			//Something is wrong, tab does not exist. take last tab
			selectedTab = tabArray.size()-1;
		}
		return selectedTab;
	}
	
	public void removeTabSettings(int index) {
		tabArray.remove(index);
	}
	
	public TabSettings getSelectedTabSettings() throws SettingsException {
		if (getSelectedTabNumber() >= 0) {
			return tabArray.get(getSelectedTabNumber());
		} else {
			return new TabSettings(this, getDefaults());
		}
	}
	
	public Defaults getDefaults() {
		return defaults;
	}
	
	public String getGoogleAnalyticsId() {
		String id = getString(SettingKeys.GOOGLE_ANALYTICS_ID);
		return id;
	}
	
	public boolean useGoogleAnalytics() {
		String analyticsId = getGoogleAnalyticsId();
		return (analyticsId != null && analyticsId.length() > 0 && getTrackingConsent());
	}
	
	public void setTrackingConsent(boolean consent) {
		put(SettingKeys.TRACKING_CONSENT, JSONBoolean.getInstance(consent));
	}
	
	public boolean getTrackingConsent() {
		boolean consent = true;
		if (containsKey(SettingKeys.TRACKING_CONSENT)) {
			consent = get(SettingKeys.TRACKING_CONSENT).isBoolean().booleanValue();
		}
		return consent;
	}
	
	public void setTrackingQueryConsent(boolean consent) {
		put(SettingKeys.TRACKING_QUERIES_CONSENT, JSONBoolean.getInstance(consent));
	}
	
	public boolean getTrackingQueryConsent() {
		boolean consent = true;
		if (containsKey(SettingKeys.TRACKING_QUERIES_CONSENT)) {
			consent = get(SettingKeys.TRACKING_QUERIES_CONSENT).isBoolean().booleanValue();
		}
		return consent;
	}
	
	public boolean cookieConsentAnswered() {
		return (containsKey(SettingKeys.TRACKING_CONSENT) && containsKey(SettingKeys.TRACKING_QUERIES_CONSENT));
	}
	
	public String getBitlyUsername() {
		String username = getString(SettingKeys.BITLY_USERNAME);
		return username;
	}
	
	
	/**
	 * Returns JSON representation of this object
	 */
	public String toString() {
		//First add TabSettings to the jsonobject
		JSONArray jsonArray = new JSONArray();
		for(int i = 0; i < tabArray.size(); i++) {
			jsonArray.set(i, (JSONObject)tabArray.get(i));
		}
		put(SettingKeys.TAB_SETTINGS, jsonArray);
		
		return super.toString();
	}
	
	
}
