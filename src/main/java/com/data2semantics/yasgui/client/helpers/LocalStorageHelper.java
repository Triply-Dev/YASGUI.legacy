package com.data2semantics.yasgui.client.helpers;

import java.util.Date;

import com.data2semantics.yasgui.client.settings.Settings;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;

public class LocalStorageHelper {
	private static String COOKIE_SETTINGS = "yasgui_settings";
	private static String COOKIE_PREFIXES = "yasgui_prefixes";
	private static String LOCAL_STORAGE_EXPIRE_SEPARATOR = "_"; //used to separate content and long value containing timestamp of insertion
	private static int PREFIXES_EXPIRE_DAYS = 5;
	

	/**
	 * Store settings as json string in cookie. If html5 local storage is possible, use that. 
	 * html5 storage does not send cookie info on every request, which reduces network load
	 * 
	 * @param settings
	 */
	public static void storeSettingsInCookie(Settings settings) {
		Storage html5Storage = Storage.getLocalStorageIfSupported();
		if (html5Storage != null) {
			html5Storage.setItem(COOKIE_SETTINGS, settings.toString());
		} else {
			//We are using a browser which does not support html5
			Cookies.removeCookie(COOKIE_SETTINGS);
			Cookies.setCookie(COOKIE_SETTINGS, settings.toString());
		}
		
	}
	

	

	/**
	 * Get value from local storage using expire date. 
	 * html5 local storage has not expire date functionality as cookies have, so we prefix all values with a time string used for expire calculation
	 * @param key
	 * @param expireDays
	 * @return
	 */
	public static String getFromLocalStorage(String key, int expireDays) {
		String result = null;
		Storage html5Storage = Storage.getLocalStorageIfSupported();
		if (html5Storage != null) {
			String storageString = html5Storage.getItem(key);
			if (storageString != null) {
				int separatorIndex = storageString.indexOf(LOCAL_STORAGE_EXPIRE_SEPARATOR);
				try {
					long expire = Long.parseLong(storageString.substring(0, separatorIndex));
					Date currentDate = new Date();
					if (currentDate.getTime() - expire < 1000 * 60 * 60 * 24 * PREFIXES_EXPIRE_DAYS) {
						result = storageString.substring(separatorIndex + 1);
					} else {
						//expired, so remove
						html5Storage.removeItem(key);
					}
				} catch (Exception e) {
					//parsing to long probably went wrong. just empty local storage
					html5Storage.removeItem(key);
				}
			}
		}
		return result;
	}
	
	/**
	 * Get value from local storage using expire date. 
	 * html5 local storage has not expire date functionality as cookies have, so we prefix all values with a time string used for expire calculation
	 * @param key
	 * @return
	 */
	public static String getFromLocalStorage(String key) {
		Storage html5Storage = Storage.getLocalStorageIfSupported();
		return html5Storage.getItem(key);
	}
	
	/**
	 * Tries to set prefixes json string in local html5 storage. Will set nothing if html5 is not supported
	 * 
	 * @param value
	 */
	public static void setInLocalStorage(String key, String value) {
		if (Storage.isLocalStorageSupported()) {
			Storage html5Storage = Storage.getLocalStorageIfSupported();
			html5Storage.setItem(key, value);
		}
	}
	/**
	 * Tries to set prefixes json string in local html5 storage. Will set nothing if html5 is not supported
	 * 
	 * @param value
	 * @oaram addTimeStamp whether to prepend string with timestamp
	 */
	public static void setInLocalStorage(String key, String value, boolean addTimeStamp) {
		Date currentDate = new Date();
		setInLocalStorage(key, Long.toString(currentDate.getTime()) + LOCAL_STORAGE_EXPIRE_SEPARATOR + value);
	}
	
	
	
	/**
	 * Get settings from cookie (or html local storage if supported). Settings is saved as a json string, so need to parse as json object
	 * @return
	 */
	public static Settings getSettingsFromCookie() {
		Settings settings = new Settings();
		String jsonString = getFromLocalStorage(COOKIE_SETTINGS);
		if (jsonString == null) {
			//We are using a browser which does not support html5
			jsonString = Cookies.getCookie(COOKIE_SETTINGS);
		}
		
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
	
	public static void setPrefixes(String prefixes) {
		setInLocalStorage(COOKIE_PREFIXES, prefixes, true);
	}
	
	/**
	 * Return prefixes json array string from local html5 storage. Returns null if not found, or if html5 is not supported
	 * 
	 * @return
	 */
	public static String getPrefixesFromLocalStorage() {
		return getFromLocalStorage(COOKIE_PREFIXES, PREFIXES_EXPIRE_DAYS);
	}
	

}
