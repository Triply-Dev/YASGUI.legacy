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

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.settings.Settings;
import com.data2semantics.yasgui.shared.CookieKeys;
import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.datepicker.client.CalendarUtil;

public class LocalStorageHelper {

	
	private static String LOCAL_STORAGE_EXPIRE_SEPARATOR = "_"; //used to separate content and long value containing timestamp of insertion
	private static int PREFIXES_EXPIRE_DAYS = 30;
	private static int ENDPOINTS_EXPIRE_DAYS = 30;
	private static int SETTINGS_EXPIRE_DAYS = 1000;
	private static int VERSION_EXPIRE_DAYS = 1000;
	private static int TOOLTIPS_EXPIRE_DAYS = 1000;
	private static int COMPATABILITIES_SHOWN_EXPIRE_DAYS = 1000;
	private static int PROPERTIES_EXPIRE_DAYS = 360;
	private static int DEFAULT_EXPIRE_DAYS = 1000;
	@SuppressWarnings("unused")
	private static int UNKNOWN_EXPIRE_DAYS = 30;
	private View view;
	
	public LocalStorageHelper(View view) {
		this.view = view;
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
		String storageString = getFromLocalStorage(key);
		if (storageString != null && storageString.length() > 0) {
			int separatorIndex = storageString.indexOf(LOCAL_STORAGE_EXPIRE_SEPARATOR);
			try {
				long expire = Long.parseLong(storageString.substring(0, separatorIndex));
				Date currentDate = new Date();
				if (currentDate.getTime() - expire < 1000 * 60 * 60 * 24 * expireDays) {
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
			storeInLocalStorage(key, value); //settings it again stores it under correct key with domain name
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
	
	private static void storeInLocalStorage(String key, String value) {
		if (Storage.isLocalStorageSupported()) {
			Storage html5Storage = Storage.getLocalStorageIfSupported();
			try {
				html5Storage.setItem(Helper.getCurrentHost() + "_" + key, value);
			} catch (Throwable t) {
				Helper.logExceptionToServer(t);
			}
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
			storeInLocalStorage(key, value);
		}
	}
	
	private static Date getExpireDate(int days) {
		Date date = new Date();
		CalendarUtil.addDaysToDate(date, days);
		return date;
	}
	
	public static String setAsCookie(String key, String value, int expireDays) {
		return setAsCookie(key, value, expireDays, false);
	}
	
	public static String setAsCookie(String key, String value, int expireDays, boolean prependDomain) {
		if (prependDomain) key = Helper.getCurrentHost() + "_" + key;
		Cookies.removeCookie(key);
		Cookies.setCookie(key, value, getExpireDate(expireDays));
		return key;
	}
	
	public static String getAsCookie(String key, boolean prependDomain) {
		if (prependDomain) key = Helper.getCurrentHost() + "_" + key;
		
		return Cookies.getCookie(key);
	}
	
	public static String getAsCookie(String key) {
		return getAsCookie(key, false);
	}

	/**
	 * Store settings as json string in cookie. If html5 local storage is possible, use that. 
	 * html5 storage does not send cookie info on every request, which reduces network load
	 * 
	 * @param settings
	 */
	public static void storeSettingsInCookie(Settings settings) {
		if (Storage.isLocalStorageSupported()) {
			setInLocalStorage(CookieKeys.SETTINGS, settings.toString(), true);
		} else {
			//We are using a browser which does not support html5
			setAsCookie(CookieKeys.SETTINGS, settings.toString(), SETTINGS_EXPIRE_DAYS, true);
		}
	}
	
	
	/**
	 * Get settings from cookie (or html local storage if supported). Settings is saved as a json string, so need to parse as json object
	 * @return
	 */
	public static String getSettingsStringFromCookie() {
		String jsonString = getFromLocalStorage(CookieKeys.SETTINGS, SETTINGS_EXPIRE_DAYS);
		if (jsonString == null) {
			//We are using a browser which does not support html5
			jsonString = getAsCookie(CookieKeys.SETTINGS, true);
		}
		return jsonString;
	}
	public static void clearSettings() {
		String key = Helper.getCurrentHost() + "_" + CookieKeys.SETTINGS;
		if (Storage.isLocalStorageSupported()) {
			Storage html5Storage = Storage.getLocalStorageIfSupported();
			html5Storage.removeItem(key);
		} else {
			//We are using a browser which does not support html5
			Cookies.removeCookie(key);
		}
	}
	
	public static void setPrefixes(String prefixes) {
		setInLocalStorage(CookieKeys.PREFIXES, prefixes, true);
	}
	
	/**
	 * Return prefixes json array string from local html5 storage. Returns null if not found, or if html5 is not supported
	 * 
	 * @return
	 */
	public static String getPrefixesFromLocalStorage() {
		return getFromLocalStorage(CookieKeys.PREFIXES, PREFIXES_EXPIRE_DAYS);
	}
	
	
	public static void setProperties(String endpoint, String properties) {
		setInLocalStorage(CookieKeys.PROPERTIES + "_" + endpoint, properties, true);
	}
	
	
	/**
	 * Return properties json array string from local html5 storage. Returns null if not found, or if html5 is not supported
	 * 
	 * @return
	 */
	public static String getProperties(String endpoint) {
		return getFromLocalStorage(CookieKeys.PROPERTIES + "_" + endpoint, PROPERTIES_EXPIRE_DAYS);
	}
	
	
	/**
	 * Return prefixes json array string from local html5 storage. Returns null if not found, or if html5 is not supported
	 * 
	 * @return
	 */
	public static String getEndpointsFromLocalStorage() {
		return getFromLocalStorage(CookieKeys.ENDPOINTS, ENDPOINTS_EXPIRE_DAYS);
	}
	
	public static void setEndpoints(String endpoints) {
		setInLocalStorage(CookieKeys.ENDPOINTS, endpoints, true);
	}
	
	public static boolean showTooltips() {
		return (getAsCookie(CookieKeys.TOOLTIPS_SHOWN, true) == null);
	}
	
	public static void setTooltipsShown() {
		setAsCookie(CookieKeys.TOOLTIPS_SHOWN, "1", TOOLTIPS_EXPIRE_DAYS, true);
	}
	
	public static void setVersion(String version) {
		setAsCookie(CookieKeys.VERSION, version, VERSION_EXPIRE_DAYS, true);
	}
	
	public static String getVersion() {
		return getAsCookie(CookieKeys.VERSION, true);
	}
	
	public static void setVersionId(int version) {
		setAsCookie(CookieKeys.VERSION_ID, Integer.toString(version), VERSION_EXPIRE_DAYS, true);
	}
	
	public static int getVersionId() {
		int versionId = 0;
		String versionIdString = getAsCookie(CookieKeys.VERSION_ID, true);
		if (versionIdString != null && versionIdString.length() > 0) {
			versionId = Integer.parseInt(versionIdString);
		}
		return versionId;
	}
	
	public static void setCompatabilitiesShown(int versionNumber) {
		setAsCookie(CookieKeys.COMPATABILITIES_SHOWN, Integer.toString(versionNumber), COMPATABILITIES_SHOWN_EXPIRE_DAYS, true);
	}
	
	public static int getCompatabilitiesShownVersionNumber() {
		int versionId = 0;
		String versionIdString = getAsCookie(CookieKeys.COMPATABILITIES_SHOWN, true);
		if (versionIdString != null && versionIdString.length() > 0) {
			versionId = Integer.parseInt(versionIdString);
		}
		return versionId;
	}
	
	public static boolean newUser() {
		return (getAsCookie(CookieKeys.VERSION_ID, true) == null);
	}
	public static String setStrongName() {
		return setAsCookie(CookieKeys.GWT_STRONG_NAME, GWT.getPermutationStrongName(), DEFAULT_EXPIRE_DAYS);
	}
	

}
