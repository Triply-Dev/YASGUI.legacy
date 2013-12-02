package com.data2semantics.yasgui.client.configmenu;

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

import java.util.ArrayList;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.shared.autocompletions.AutocompletionConfigCols;
import com.smartgwt.client.data.RestDataSource;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
public class AutocompletionConfigTable extends ListGrid {
	
	private View view;
	public AutocompletionConfigTable(View view) {
		this.view = view;
		setFixedRecordHeights(false);
		setHeight100();
		setWidth100();
		setAutoFitData(Autofit.VERTICAL);
		setWrapCells(true);
		setCanResizeFields(true);
		
		createAndSetFields();
		setGroupStartOpen(GroupStartOpen.ALL);  
        setGroupByField(AutocompletionConfigCols.ENDPOINT.getKey());
		
        initDataSource();
	}
	
	
	
	private void initDataSource() {
		RestDataSource dataSource = new RestDataSource();  
		dataSource.setDataFormat(DSDataFormat.JSON);  
	    dataSource.setDataURL("Yasgui/autocompletionsInfo");
	    setDataSource(dataSource);
	    setAutoFetchData(true);  
	}



	private ListGridField getFieldFromCol(AutocompletionConfigCols col) {
		if (col.getWidth() >= 0) {
			return new ListGridField(col.getKey(), col.getLabel(), col.getWidth());
		} else {
			return new ListGridField(col.getKey(), col.getLabel());
		}
		
	}
	
	private void createAndSetFields() {
		ArrayList<ListGridField> fields = new ArrayList<ListGridField>();
		
		fields.add(getFieldFromCol(AutocompletionConfigCols.ENDPOINT));
		fields.add(getFieldFromCol(AutocompletionConfigCols.TYPE));
		fields.add(getFieldFromCol(AutocompletionConfigCols.METHOD_QUERY));
		fields.add(getFieldFromCol(AutocompletionConfigCols.METHOD_QUERY_RESULTS));
		setFields(fields.toArray(new ListGridField[fields.size()]));
		
	}
}
