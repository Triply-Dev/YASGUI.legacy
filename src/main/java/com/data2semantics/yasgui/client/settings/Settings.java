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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.JsonHelper;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.shared.SettingKeys;
import com.data2semantics.yasgui.shared.exceptions.SettingsException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class Settings extends JsonHelper {
	private ArrayList<TabSettings> tabArray = new ArrayList<TabSettings>();
	private Defaults defaults;
	private EnabledFeatures enabledFeatures = new EnabledFeatures(this); //initialize with empty obj (don't require this obj in json settings file)
	
	/**
	 * DEFAULTS
	 */
	public static int DEFAULT_SELECTED_TAB = 0;
	
	public Settings() {}
	
	public Settings(String jsonString) throws IOException {
		this.addToSettings(jsonString);
	}
	


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
		//we first need to add defaults (if there are any), as these are needed in the TabSettings initialization
		if (jsonObject.containsKey(SettingKeys.DEFAULTS)) {
			if (defaults == null) {
				defaults = new Defaults(jsonObject.get(SettingKeys.DEFAULTS).isObject());
			} else {
				defaults.update(jsonObject.get(SettingKeys.DEFAULTS).isObject());
			}
		}
		for (String key : keys) {
			if (key.equals(SettingKeys.DEFAULTS)) continue; //already added defaults
			if (key.equals(SettingKeys.TAB_SETTINGS)) {
				JSONArray jsonArray = jsonObject.get(key).isArray();
				for (int i = 0; i < jsonArray.size(); i++) {
					//Add as TabSettings to tab arraylist
					tabArray.add(new TabSettings(this, jsonArray.get(i).isObject()));
				}
			} else if (key.equals(SettingKeys.ENABLED_FEATURES)) {
				enabledFeatures.update(jsonObject.get(key).isObject());
			} else {
				put(key, jsonObject.get(key));
			}
		}
	}
	
	public void initDefaultTab() {
		this.setSelectedTabNumber(DEFAULT_SELECTED_TAB);
		addTabSettings(new TabSettings(this, defaults));
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
	
	public JSONArray getTabArrayAsJson() {
		JSONArray jsonArray = new JSONArray();
		for(int i = 0; i < tabArray.size(); i++) {
			jsonArray.set(i, (JSONObject)tabArray.get(i));
		}
		return jsonArray;
	}
	
	
	public int getSelectedTabNumber() {
		int selectedTab = getInt(SettingKeys.SELECTED_TAB_NUMBER, 99999);
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
	
	public EnabledFeatures getEnabledFeatures() {
		return enabledFeatures;
	}
	
	public String getGoogleAnalyticsId() {
		return getString(SettingKeys.GOOGLE_ANALYTICS_ID, null);
	}
	
	public boolean useGoogleAnalytics() {
		String analyticsId = getGoogleAnalyticsId();
		return (analyticsId != null && analyticsId.length() > 0 && !Helper.isSeleniumVisitor() && getTrackingConsent());
	}
	
	public void setTrackingConsent(boolean consent) {
		set(SettingKeys.TRACKING_CONSENT, consent);
	}
	
	public boolean getTrackingConsent() {
		return getBoolean(SettingKeys.TRACKING_CONSENT, true);
	}
	
	public void setTrackingQueryConsent(boolean consent) {
		set(SettingKeys.TRACKING_QUERIES_CONSENT, consent);
	}
	
	public boolean getTrackingQueryConsent() {
		return getBoolean(SettingKeys.TRACKING_QUERIES_CONSENT, true);
	}
	
	public boolean cookieConsentAnswered() {
		return (containsKey(SettingKeys.TRACKING_CONSENT) && containsKey(SettingKeys.TRACKING_QUERIES_CONSENT));
	}
	
	public boolean useBitly() {
		return getBoolean(SettingKeys.USE_BITLY, false);
	}
	
	public boolean isDbSet() {
		return getBoolean(SettingKeys.DB_SET, false);
	}
	
	public boolean bugReportsSupported() {
		return getBoolean(SettingKeys.BUG_REPORTS_SUPPORTED, false);
	}
	
	public boolean useUrlAsSnorql() {
		return getBoolean(SettingKeys.URI_AS_SNORQL, true);
	}
	
	public String getBrowserTitle() {
		return getString(SettingKeys.BROWSER_TITLE, "YASGUI");
	}
	
	public void setBrowserTitle(String title) {
		set(SettingKeys.BROWSER_TITLE, title);
	}
	
	public boolean useOfflineCaching() {
		boolean useOfflineCaching = false;
		if (enabledFeatures.offlineCachingEnabled()) {
			//only check this setting if the enabled features config allows us to!
			useOfflineCaching = getBoolean(SettingKeys.DOWNLOAD_APPCACHE, false, true);
		}
		return useOfflineCaching;
	}
	
	public void setUseOfflineCaching(boolean useOfflineCaching) {
		set(SettingKeys.DOWNLOAD_APPCACHE, useOfflineCaching, true);
	}
	public boolean showAppcacheDownloadNotification() {
		return getBoolean(SettingKeys.SHOW_APPCACHE_DOWNLOAD_NOTIFICATION, true, true);
	}
	public void setShowAppcacheDownloadNotification(boolean show) {
		set(SettingKeys.SHOW_APPCACHE_DOWNLOAD_NOTIFICATION, show, true);
	}
	public void clearQueryResults() {
		for (int i = 0; i < tabArray.size(); i++) {
			//Add as TabSettings to tab arraylist
			JSONObject tabItem = tabArray.get(i).isObject();
			if (tabItem != null) {
				tabItem.put(SettingKeys.QUERY_RESULT_STRING, null);
				tabItem.put(SettingKeys.QUERY_RESULT_CONTENT_TYPE, null);
			}
		}
	}
	public void clearQueryResults(int maxSize) {
		int currentSize = 0;
		for (int i = 0; i < tabArray.size(); i++) {
			//Add as TabSettings to tab arraylist
			JSONObject tabItem = tabArray.get(i).isObject();
			if (tabItem != null) {
				JSONValue queryJsonValue = tabItem.get(SettingKeys.QUERY_RESULT_STRING);
				if (queryJsonValue != null) {
					if (queryJsonValue.isString() != null) {
						currentSize += queryJsonValue.isString().stringValue().length();
					}
				}
			}
		}
		if (currentSize > maxSize) {
			clearQueryResults();
		}
	}
	
	public Map<String, Boolean> getPropertyCompletionMethods() {
		Map<String, Boolean> propertyCompletionMethods = getMap(SettingKeys.ENABLED_PROPERTY_COMPLETION_METHODS, null);
		if (propertyCompletionMethods == null) {
			Map<String, Boolean> enabledPropertyCompletionMethods = enabledFeatures.getEnabledPropertyCompletionMethods();
			propertyCompletionMethods = new HashMap<String, Boolean>();
			//if something is set to 'false' in enabled features, they should not be added to our final hashmap,
			//as null means we won't show this method, false means it is disabled (but selectable), and true means it is enabled
			for (Entry<String, Boolean> entry: enabledPropertyCompletionMethods.entrySet()) {
				if (entry.getValue()) {
					propertyCompletionMethods.put(entry.getKey(), true);
				}
			}
		} else {
			Map<String, Boolean> enabledPropertyCompletionMethods = enabledFeatures.getEnabledPropertyCompletionMethods();
			if (enabledPropertyCompletionMethods != null) {
				//check whether our user setting has methods enabled, where our enabled properties have them disabled
				//occurs in changes where user settings are cached, and site administrator changes the enabled features.
				for (Entry<String, Boolean> entry: enabledPropertyCompletionMethods.entrySet()) {
					if (entry.getValue() == false && propertyCompletionMethods.containsKey(entry.getKey())) {
						propertyCompletionMethods.remove(entry.getKey());
					}
				}
			}
		}
		return propertyCompletionMethods;
	}
	public JSONObject getPropertCompletionMethodsAsJson() {
		return getMapAsObject(getPropertyCompletionMethods());
	}
	

	public void setPropertyCompletionMethods(JSONObject methods) {
		put(SettingKeys.ENABLED_PROPERTY_COMPLETION_METHODS, methods);
	}
	public Map<String, Boolean> getClassCompletionMethods() {
		Map<String, Boolean> classCompletionMethods = getMap(SettingKeys.ENABLED_CLASS_COMPLETION_METHODS, null);
		if (classCompletionMethods == null) {
			Map<String, Boolean> enabledPropertyCompletionMethods = enabledFeatures.getEnabledClassCompletionMethods();
			classCompletionMethods = new HashMap<String, Boolean>();
			//if something is set to 'false' in enabled features, they should not be added to our final hashmap,
			//as null means we won't show this method, false means it is disabled (but selectable), and true means it is enabled
			for (Entry<String, Boolean> entry: enabledPropertyCompletionMethods.entrySet()) {
				if (entry.getValue()) {
					classCompletionMethods.put(entry.getKey(), true);
				}
			}
		} else {
			Map<String, Boolean> enabledClassCompletionMethods = enabledFeatures.getEnabledClassCompletionMethods();
			if (enabledClassCompletionMethods != null) {
				//check whether our user setting has methods enabled, where our enabled properties have them disabled
				//occurs in changes where user settings are cached, and site administrator changes the enabled features.
				for (Entry<String, Boolean> entry: enabledClassCompletionMethods.entrySet()) {
					if (entry.getValue() == false && classCompletionMethods.containsKey(entry.getKey())) {
						classCompletionMethods.remove(entry.getKey());
					}
				}
			}
		}
		return classCompletionMethods;
	}
	public JSONObject getClassCompletionMethodsAsJson() {
		return getMapAsObject(getClassCompletionMethods());
	}
	
	
	public void setClassCompletionMethods(JSONObject methods) {
		put(SettingKeys.ENABLED_CLASS_COMPLETION_METHODS, methods);
	}
	
	public static Settings retrieveSettings() throws IOException {
		Settings settings = new Settings();
		String defaultSettings = JsMethods.getDefaultSettings();
		if (defaultSettings == null || defaultSettings.length() == 0) {
			throw new IOException("Failed to load default settings from javascript.");
		}
		 
		//First create settings object with the proper default values
		//need default values when creating settings objects, as not all values might be filled in our cache and stuff
		settings.addToSettings(defaultSettings);
		settings = addUrlArgToSettings(settings);
		String settingsString = LocalStorageHelper.getSettingsStringFromCookie();
		
		if (settingsString != null && settingsString.length() > 0) {
			settings.addToSettings(settingsString);
			//add installation + url settings again. The settings retrieved from cookie might have stale default values
			settings.addToSettings(defaultSettings);
			settings = addUrlArgToSettings(settings);
		} else {
			//no options in cache. we already have default values and settings object
			//now initialize a tab with default values
			settings.initDefaultTab();
		}
		return settings;
	}
	/**
	 * Returns JSON representation of this object
	 */
	public String toString() {
		put(SettingKeys.TAB_SETTINGS, getTabArrayAsJson());
		put(SettingKeys.DEFAULTS, defaults);
		return super.toString();
	}
	

	public Settings clone() {
		try {
			Settings clonedSettings = new Settings();
			clonedSettings.addToSettings(toString());
			return clonedSettings;
		} catch (IOException e) {
			return null;
		}
	}
}
