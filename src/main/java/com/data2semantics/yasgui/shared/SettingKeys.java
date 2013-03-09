package com.data2semantics.yasgui.shared;

import java.io.Serializable;

public class SettingKeys implements Serializable {
	private static final long serialVersionUID = 1L;
	
	//these are keys for our settings json object. Implemented as json object, because of easy (de)serializing
	
	//settings object
	public static String SELECTED_TAB_NUMBER = "selectedTabNumber";
	public static String DEFAULTS = "defaults";
	public static String TAB_SETTINGS = "tabSettings";
	public static String SINGLE_ENDPOINT_MODE = "singleEndpointMode";
	public static String GOOGLE_ANALYTICS_ID = "googleAnalyticsId";
	public static String TRACKING_CONSENT = "trackingConsent";
	public static String TRACKING_QUERIES_CONSENT = "trackingQueriesConsent";
	
	
	//tabsettings object
	public static String ENDPOINT = "endpoint";
	public static String QUERY_STRING = "query";
	public static String TAB_TITLE = "tabTitle";
	public static String OUTPUT_FORMAT = "outputFormat";
	public static String CONTENT_TYPE_SELECT = "contentTypeSelect";
	public static String CONTENT_TYPE_CONSTRUCT = "contentTypeConstruct";
	public static String EXTRA_QUERY_ARGS = "extraArgs";
	public static String REQUEST_METHOD = "requestMethod";
	
	//other (not used clientside)
	public static String BITLY_API_KEY = "bitlyApiKey";
	public static String BITLY_USERNAME = "bitlyUsername";
}
