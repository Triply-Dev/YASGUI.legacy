package com.data2semantics.yasgui.client.helpers;

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

import java.util.Date;

import com.data2semantics.yasgui.client.settings.Settings;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class LocalStorageHelper {
	private static String COOKIE_SETTINGS = "settings";
	private static String COOKIE_TOOLTIPS_SHOWN = "tooltipsShown";
	private static String COOKIE_PREFIXES = "prefixes";
	private static String COOKIE_ENDPOINTS = "endpoints";
	private static String COOKIE_VERSION = "version";
	private static String COOKIE_VERSION_ID = "versionId";
	private static String COOKIE_COMPATABILITIES_SHOWN = "compatabilitiesShown";
	
	private static String LOCAL_STORAGE_EXPIRE_SEPARATOR = "_"; //used to separate content and long value containing timestamp of insertion
	private static int PREFIXES_EXPIRE_DAYS = 30;
	private static int ENDPOINTS_EXPIRE_DAYS = 30;
	private static int SETTINGS_EXPIRE_DAYS = 5000;
	private static int VERSION_EXPIRE_DAYS = 5000;
	private static int TOOLTIPS_EXPIRE_DAYS = 5000;
	private static int COMPATABILITIES_SHOWN_EXPIRE_DAYS = 5000;
	private static int UNKNOWN_EXPIRE_DAYS = 30;
	
	
	/**
	 * Get value from local storage using expire date. 
	 * html5 local storage has not expire date functionality as cookies have, so we prefix all values with a time string used for expire calculation
	 * @param key
	 * @param expireDays
	 * @return
	 */
	public static String getFromLocalStorage(String key, int expireDays) {
		String result = null;
		String storageString = getFromLocalStorage(key);
		
		if (storageString != null && storageString.length() > 0) {
			int separatorIndex = storageString.indexOf(LOCAL_STORAGE_EXPIRE_SEPARATOR);
			try {
				long expire = Long.parseLong(storageString.substring(0, separatorIndex));
				Date currentDate = new Date();
				if (currentDate.getTime() - expire < 1000 * 60 * 60 * 24 * PREFIXES_EXPIRE_DAYS) {
					result = storageString.substring(separatorIndex + 1);
					if (result.length() == 0) {
						result = null;
					}
				} else {
					//expired, so remove
					removeLocalStorageKey(key);
				}
			} catch (Exception e) {
				//parsing to long probably went wrong. just empty local storage
				removeLocalStorageKey(key);
			}
		}
		return result;
	}
	
	private static StorageMap getStorageMap() {
		StorageMap storageMap = null;
		Storage html5Storage = Storage.getLocalStorageIfSupported();
		if (html5Storage != null) {
			storageMap = new StorageMap(html5Storage);
		}
		return storageMap;
	}
	
	/**
	 * Get value from local storage for given key. Appends current domain name to key, 
	 * as we want different local storage when yasgui is loaded via iframe. 
	 * 
	 * @param key
	 * @return
	 */
	private static String getFromLocalStorage(String key) {
		StorageMap storageMap = getStorageMap();
		String domain = Helper.getCurrentHost();
		
		if (storageMap.containsKey(domain + "_" + key)) {
			return storageMap.get(domain + "_" + key);
		} else if (storageMap.containsKey(key)) {
			//for backwards compatability (i.e. the time when we didnt use the basedomain as part of the key)
			String value = storageMap.get(key);
			setInLocalStorage(key, value); //settings it again stores it under correct key with domain name
			storageMap.remove(key);//remove old key
			return value;
		}
		return null;
	}
	
	private static void removeLocalStorageKey(String key) {
		Storage html5Storage = Storage.getLocalStorageIfSupported();
		String domain = Helper.getCurrentHost();
		html5Storage.removeItem(domain + "_" + key);
	}
	
	public static void setInLocalStorage(String key, String value) {
		if (Storage.isLocalStorageSupported()) {
			Storage html5Storage = Storage.getLocalStorageIfSupported();
			html5Storage.setItem(Helper.getCurrentHost() + "_" + key, value);
		}
	}
	
	/**
	 * Tries to set prefixes json string in local html5 storage. Will set nothing if html5 is not supported
	 * 
	 * @param value Needs to be non-empty. Will store nothing when string is empty
	 * @oaram addTimeStamp whether to prepend string with timestamp
	 */
	public static void setInLocalStorage(String key, String value, boolean addTimeStamp) {
		if (value.length() > 0) {
			Date currentDate = new Date();
			if (addTimeStamp) {
				value = Long.toString(currentDate.getTime()) + LOCAL_STORAGE_EXPIRE_SEPARATOR + value;
			}
			setInLocalStorage(key, value);
		}
	}
	
	private static Date getExpireDate(int days) {
		Date date = new Date();
		CalendarUtil.addDaysToDate(date, days);
		return date;
	}
	
	public static void setAsCookie(String key, String value, int expireDays) {
		key = Helper.getCurrentHost() + "_" + key;
		Cookies.removeCookie(key);
		Cookies.setCookie(key, value, getExpireDate(expireDays));
	}
	
	public static String getAsCookie(String key) {
		String domain = Helper.getCurrentHost();
		
		
		if (Cookies.getCookie(domain + "_" + key) != null) {
			return Cookies.getCookie(domain + "_" + key);
		} else if (Cookies.getCookie(key) != null) {
			//for backwards compatability (i.e. the time when we didnt use the basedomain as part of the key)
			String value = Cookies.getCookie(key);
			setAsCookie(key, value, UNKNOWN_EXPIRE_DAYS); //now store it under correct key
			Cookies.removeCookie(key);//remove old key
			return value;
		}
		return null;
	}
	
	

	/**
	 * Store settings as json string in cookie. If html5 local storage is possible, use that. 
	 * html5 storage does not send cookie info on every request, which reduces network load
	 * 
	 * @param settings
	 */
	public static void storeSettingsInCookie(Settings settings) {
		if (Storage.isLocalStorageSupported()) {
			setInLocalStorage(COOKIE_SETTINGS, settings.toString(), true);
		} else {
			//We are using a browser which does not support html5
			setAsCookie(COOKIE_SETTINGS, settings.toString(), SETTINGS_EXPIRE_DAYS);
		}
	}
	
	
	/**
	 * Get settings from cookie (or html local storage if supported). Settings is saved as a json string, so need to parse as json object
	 * @return
	 */
	public static String getSettingsStringFromCookie() {
		String jsonString = getFromLocalStorage(COOKIE_SETTINGS, SETTINGS_EXPIRE_DAYS);
		if (jsonString == null) {
			//We are using a browser which does not support html5
			jsonString = getAsCookie(COOKIE_SETTINGS);
		}
		return jsonString;
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
	/**
	 * Return prefixes json array string from local html5 storage. Returns null if not found, or if html5 is not supported
	 * 
	 * @return
	 */
	public static String getEndpointsFromLocalStorage() {
		return getFromLocalStorage(COOKIE_ENDPOINTS, ENDPOINTS_EXPIRE_DAYS);
	}
	
	public static void setEndpoints(String endpoints) {
		setInLocalStorage(COOKIE_ENDPOINTS, endpoints, true);
	}
	
	public static boolean showTooltips() {
		return (getAsCookie(COOKIE_TOOLTIPS_SHOWN) == null);
	}
	
	public static void setTooltipsShown() {
		setAsCookie(COOKIE_TOOLTIPS_SHOWN, "1", TOOLTIPS_EXPIRE_DAYS);
	}
	
	public static void setVersion(String version) {
		setAsCookie(COOKIE_VERSION, version, VERSION_EXPIRE_DAYS);
	}
	
	public static String getVersion() {
		return getAsCookie(COOKIE_VERSION);
	}
	
	public static void setVersionId(int version) {
		setAsCookie(COOKIE_VERSION_ID, Integer.toString(version), VERSION_EXPIRE_DAYS);
	}
	
	public static int getVersionId() {
		int versionId = 0;
		String versionIdString = getAsCookie(COOKIE_VERSION_ID);
		if (versionIdString != null && versionIdString.length() > 0) {
			versionId = Integer.parseInt(versionIdString);
		}
		return versionId;
	}
	
	public static void setCompatabilitiesShown(int versionNumber) {
		setAsCookie(COOKIE_COMPATABILITIES_SHOWN, Integer.toString(versionNumber), COMPATABILITIES_SHOWN_EXPIRE_DAYS);
	}
	
	public static int getCompatabilitiesShownVersionNumber() {
		int versionId = 0;
		String versionIdString = getAsCookie(COOKIE_COMPATABILITIES_SHOWN);
		if (versionIdString != null && versionIdString.length() > 0) {
			versionId = Integer.parseInt(versionIdString);
		}
		return versionId;
	}
	
	public static boolean newUser() {
		return (getAsCookie(COOKIE_VERSION_ID) == null);
	}
	

}
