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


import java.io.IOException;

import com.data2semantics.yasgui.client.settings.Settings;
import com.data2semantics.yasgui.shared.SettingKeys;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Window;

public class JsonHelper extends JSONObject {

	private static String USER_SETTING_APPENDIX = "_user";
	/**
	 * If we have settings passed as argument (e.g. in an iframe setting, add these to settings object)
	 * @param settings
	 * @return
	 * @throws IOException
	 */
	protected static Settings addUrlArgToSettings(Settings settings) throws IOException {
		//If we have settings passed as argument (e.g. in an iframe setting, add these to settings object)
		String jsonSettings = Window.Location.getParameter(SettingKeys.JSON_SETTINGS_ARGUMENT);
		if (jsonSettings != null && jsonSettings.length() > 0) {
			settings.addToSettings(jsonSettings);
		}
		return settings;
	}
	
	protected boolean getBoolean(String key, boolean defaultVal) {
		return getBoolean(key, defaultVal, false);
	}
	protected boolean getBoolean(String key, boolean defaultVal, boolean userSetting) {
		boolean value = defaultVal;
		if (userSetting) {
			String userKey = key + USER_SETTING_APPENDIX;
			if (containsKey(userKey)) {
				value = getBoolean(userKey, defaultVal);
			} else {
				value = getBoolean(key, defaultVal);
				set(key, value, true);
			}
		} else {
			
			if (containsKey(key) && get(key).isBoolean() != null){
				value = get(key).isBoolean().booleanValue();
			}
			
		}
		return value;
	}
	
	protected void set(String key, boolean val, boolean userSetting) {
		if (userSetting) {
			set(key + USER_SETTING_APPENDIX, val);
		} else {
			put(key, JSONBoolean.getInstance(val));
		}
		
	}
	protected void set(String key, boolean val) {
		set(key, val, false);
	}
	protected void set(String key, String val) {
		put(key, new JSONString(val));
	}
	protected String getString(String key, String defaultVal) {
		String value = defaultVal;
		if (containsKey(key) && get(key).isString() != null && get(key).isString().stringValue().length() > 0) {
			value = get(key).isString().stringValue();
		}
		return value;
	}
	
	protected int getInt(String key, int defaultVal) {
		int value = defaultVal;
		if (containsKey(key) && get(key).isNumber() != null){
			value = (int)get(key).isNumber().doubleValue();
		}
		return value;
	}
}
