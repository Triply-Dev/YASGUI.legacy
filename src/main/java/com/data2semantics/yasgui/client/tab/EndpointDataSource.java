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
package com.data2semantics.yasgui.client.tab;

import java.util.ArrayList;
import java.util.Set;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.shared.Endpoints;
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
	
	@SuppressWarnings("unused")
	private View view;
	private ArrayList<ListGridRecord> records = new ArrayList<ListGridRecord>();
	public EndpointDataSource(View view) {
		this.view = view;
		DataSourceTextField endpointUri = new DataSourceTextField(Endpoints.KEY_ENDPOINT, "User ID");
		DataSourceTextField description = new DataSourceTextField(Endpoints.KEY_DESCRIPTION, "User ID");
		DataSourceTextField title = new DataSourceTextField(Endpoints.KEY_TITLE, "User ID");
		setFields(endpointUri, description, title);
		
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
}
