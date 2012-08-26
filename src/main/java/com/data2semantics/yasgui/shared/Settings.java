package com.data2semantics.yasgui.shared;

import java.io.Serializable;
import java.util.HashMap;

public class Settings implements Serializable {
	private static final long serialVersionUID = 1L;
	private HashMap<String, String> settings = new HashMap<String, String>();
	public static String ENDPOINT = "endpoint";
	public static String QUERY_STRING = "queryFormat";
	public static String OUTPUT_FORMAT = "outputFormat";
	
	private static String DEFAULT_QUERY = "PREFIX aers: <http://aers.data2semantics.org/resource/> \n" +
			"SELECT * {<http://aers.data2semantics.org/resource/report/5578636> ?f ?g} LIMIT 50";
//	"SELECT * {?d <http://aers.data2semantics.org/vocab/event_date> ?t} LIMIT 10";
	
//	private static String DEFAULT_ENDPOINT = "http://eculture2.cs.vu.nl:5020/sparql/";
	private static String DEFAULT_ENDPOINT = "http://sws.ifi.uio.no/sparql/npd";//cors enabled

	public Settings(){
		setEndpoint(DEFAULT_ENDPOINT);
		setQueryString(DEFAULT_QUERY);
		
	}
	public String getEndpoint() {
		return settings.get(ENDPOINT);
	}

	public void setEndpoint(String endpoint) {
		settings.put(ENDPOINT, endpoint);
	}

	public String getQueryString() {
		return settings.get(QUERY_STRING);
	}

	public void setQueryString(String queryString) {
		settings.put(QUERY_STRING, queryString);
	}

	public String getOutputFormat() {
		return settings.get(OUTPUT_FORMAT);
	}

	public void setOutputFormat(String outputFormat) {
		settings.put(OUTPUT_FORMAT, outputFormat);
	}
	
	public HashMap<String, String> getSettingsHashMap() {
		return this.settings;
	}
	
	/*
	 * Not advised to use this method. Added to easily store settings defined in json object (stores in cookie as string)
	 * Use other setters to make sure the proper value gets set
	 */
	public void setSettingDirectly(String key, String value) {
		settings.put(key, value);
	}
	
	public String toString() {
		return settings.toString();
	}
}
