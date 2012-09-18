package com.data2semantics.yasgui.client;

import java.util.ArrayList;

import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class ClientOnlyDataSource extends DataSource {

	private static ClientOnlyDataSource instance = null;
	private ArrayList<ListGridRecord> records = new ArrayList<ListGridRecord>();
	public static ClientOnlyDataSource getInstance() {
		if (instance == null) {
			instance = new ClientOnlyDataSource("users");
		}

		return instance;
	}

	public ClientOnlyDataSource(String id) {
		setID(id);

		DataSourceTextField userId = new DataSourceTextField("endpointUri", "User ID", 128, true);
		userId.setPrimaryKey(true);

		DataSourceTextField fullName = new DataSourceTextField("description", "Full Name", 128, true);

		setFields(userId, fullName);
		
		ListGridRecord record = new ListGridRecord();
		record.setAttribute("endpointUri", "bla1a");
		record.setAttribute("description", "bla1b");
		records.add(record);
		ListGridRecord record2 = new ListGridRecord();
		record2.setAttribute("endpointUri", "bla2a");
		record2.setAttribute("description", "bla2b");
		records.add(record2);
		setCacheData(records.toArray(new ListGridRecord[records.size()]));
		setCacheAllData(true);
		setClientOnly(true);
	}

}