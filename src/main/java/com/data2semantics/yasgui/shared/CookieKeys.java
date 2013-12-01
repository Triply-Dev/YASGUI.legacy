package com.data2semantics.yasgui.shared;

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

import java.io.Serializable;

public class CookieKeys implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public static String SETTINGS = "settings";
	public static String LOGIN_STACK = "loginStack";
	public static String TOOLTIPS_SHOWN = "tooltipsShown";
	public static String PREFIXES = "prefixes";
	public static String PROPERTIES = "properties";
	public static String ENDPOINTS = "endpoints";
	public static String VERSION = "version";
	public static String VERSION_ID = "versionId";
	public static String GWT_STRONG_NAME = "gwtStrongName";
	public static String COMPATIBILITIES_SHOWN = "compatabilitiesShown";
	public static String URI_FETCHER_NOTIFICATION_SHOWN = "uriFetcherNotificationShown";
}
