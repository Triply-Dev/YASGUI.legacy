package com.data2semantics.yasgui.client;

import java.util.ArrayList;
import java.util.Set;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class EndpointDataSource extends DataSource {
	/**
	 * Implemented as client-only datasource. 
	 * Datasource supports dynamic fetching from server, but the results we are dealing with are not that large
	 * Additionally, this would require multiple sparql queries being executed where one would suffice
	 */
	
	private View view;
	private ArrayList<ListGridRecord> records = new ArrayList<ListGridRecord>();
	public EndpointDataSource(View view) {
		this.view = view;
		DataSourceTextField userId = new DataSourceTextField("endpointUri", "User ID", 128, true);
		userId.setPrimaryKey(true);

		setFields(userId);
		
		//init with empty record
		ListGridRecord record = new ListGridRecord();
		setCacheData(record);
		
		setCacheAllData(true);
		setClientOnly(true);
	}
	
	public void addEndpointsFromJson(String jsonString) throws Exception {
		JSONValue jsonVal = JSONParser.parseStrict(jsonString);
		if (jsonVal != null) {
			JSONArray endpoints = jsonVal.isArray();
			if (endpoints != null) {
				for (int i = 0; i < endpoints.size(); i++) {
					JSONValue endpointVal = endpoints.get(i);
					if (endpointVal != null) {
						JSONObject endpoint = endpointVal.isObject();
						if (endpoint != null) {
							addEndpointFromJson(endpoint);
						}
					}
				}
			}
		}
		setCacheData(records.toArray(new ListGridRecord[records.size()]));
	}
	
	private void addEndpointFromJson(JSONObject endpoint) throws Exception {
		ListGridRecord record = new ListGridRecord();
		Set<String> keys = endpoint.keySet();
		for (String key: keys) {
			JSONValue value = endpoint.get(key);
			if (value != null) {
				JSONString stringJsonVal = value.isString();
				if (stringJsonVal != null) {
					record.setAttribute(key, stringJsonVal.stringValue());
				}
			}
		}
		
		records.add(record);
	}
	@SuppressWarnings("unused")
	private View getView() {
		return this.view;
	}
}