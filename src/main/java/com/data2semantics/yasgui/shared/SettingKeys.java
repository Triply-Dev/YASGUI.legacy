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

public class SettingKeys implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//these are keys for our settings json object. Implemented as json object, because of easy (de)serializing
	
	//settings object
	public static String SELECTED_TAB_NUMBER = "selectedTabNumber";
	public static String DEFAULTS = "defaults";
	public static String ENABLED_FEATURES = "enabledFeatures";
	public static String TAB_SETTINGS = "tabSettings";
	public static String SINGLE_ENDPOINT_MODE = "singleEndpointMode";
	public static String GOOGLE_ANALYTICS_ID = "googleAnalyticsId";
	public static String TRACKING_CONSENT = "trackingConsent";
	public static String TRACKING_QUERIES_CONSENT = "trackingQueriesConsent";
	public static String DEFAULT_BOOKMARKS = "defaultBookmarks";
	public static String BROWSER_TITLE = "browserTitle";
	public static String URI_AS_SNORQL = "uriAsSnorql";
	
	//tabsettings object
	public static String ENDPOINT = "endpoint";
	public static String QUERY_STRING = "query";
	public static String TAB_TITLE = "tabTitle";
	public static String OUTPUT_FORMAT = "outputFormat";
	public static String CONTENT_TYPE_SELECT = "contentTypeSelect";
	public static String CONTENT_TYPE_CONSTRUCT = "contentTypeConstruct";
	public static String EXTRA_QUERY_ARGS = "extraArgs";
	public static String REQUEST_METHOD = "requestMethod";
	public static String NAMED_GRAPHS = "namedGraphs";
	public static String DEFAULT_GRAPHS = "defaultGraphs";
	
	
	//enabled features object
	public static String ENABLED_ENDPOINT_SELECTION = "endpointSelection";
	public static String ENABLED_AUTOCOMPLETION_PROPS = "propertyAutocompletion";
	public static String ENABLED_QUERY_PARAMS = "queryParameters";
	public static String ENABLED_NAMED_GRAPHS = "namedGraphs";
	public static String ENABLED_DEFAULT_GRAPHS = "defaultGraphs";
	public static String ENABLED_ACCEPT_HEADERS = "acceptHeaders";
	public static String ENABLED_REQUEST_METHOD = "requestMethod";
	public static String ENABLED_OFFLINE_CACHING = "offlineCaching";
	
	
	//other (not used clientside) (actually, not accessible from clientside for security reasons)
	public static String BITLY_API_KEY = "bitlyApiKey";
	public static String BITLY_USERNAME = "bitlyUsername";
	public static String MYSQL_USERNAME = "mysqlUsername";
	public static String MYSQL_PASSWORD = "mysqlPassword";
	public static String MYSQL_HOST = "mysqlHost";
	public static String MYSQL_DB = "mysqlDb";
	
	public static String GITHUB_USERNAME = "githubUsername";
	public static String GITHUB_OATH_TOKEN = "githubOathToken";
	public static String GITHUB_REPOSITORY = "githubRepo";
	
	//inferred settings
	public static String DB_SET = "dbSet";
	public static String USE_BITLY = "useBitly";
	public static String BUG_REPORTS_SUPPORTED = "bugReportsSupported";
	
	public static String JSON_SETTINGS_ARGUMENT = "jsonSettings";//passed in url to YASGUI, containing all the json settings
}
