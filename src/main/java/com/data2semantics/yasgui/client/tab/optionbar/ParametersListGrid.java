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
package com.data2semantics.yasgui.client.tab.optionbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;

public class ParametersListGrid extends ListGrid {
	private static String KEY_KEY = "key";
	private static String KEY_VALUE = "value";
	private View view;
	public ParametersListGrid(final View view) {
		this.view = view;
		setHeight100();
		setWidth100();
		setAutoFitData(Autofit.VERTICAL);
		setWrapCells(true);
		setCanResizeFields(true);
		setAlwaysShowEditors(true);
		setCanRemoveRecords(true);
		setParamFields();
		setParamData();
	}
	
	
	public void setArgsInSettings() {
		saveAllEdits();
		HashMap<String, String> args = new HashMap<String, String>();
		getTotalRows();
		
		Record[] gridRecords = getRecords();
		for (Record record: gridRecords) {
			if (record != null && record.getAttribute(KEY_KEY) != null && record.getAttribute(KEY_VALUE) != null) {
				args.put(record.getAttribute(KEY_KEY), record.getAttribute(KEY_VALUE));
			}
		}
		view.getSelectedTabSettings().resetAndaddQueryArgs(args);
		LocalStorageHelper.storeSettingsInCookie(view.getSettings());
	}
	
	private void setParamFields() {
		ListGridField keyField = new ListGridField(KEY_KEY, "?key");
//		keyField.setAlign(Alignment.CENTER);
		ListGridField valueField = new ListGridField(KEY_VALUE, "=value");
//		valueField.setAlign(Alignment.CENTER);
		setFields(keyField, valueField);
	}
	
	private void setParamData() {
		HashMap<String, String> args = view.getSelectedTabSettings().getQueryArgs();
		ArrayList<ListGridRecord> records = new ArrayList<ListGridRecord>();
		for (Entry<String, String> arg: args.entrySet()) {
			ListGridRecord record = new ListGridRecord();
			record.setAttribute(KEY_KEY, arg.getKey());
			record.setAttribute(KEY_VALUE, arg.getValue());
			records.add(record);
		}
		setRecords(records.toArray(new ListGridRecord[records.size()]));
	}
	
}
