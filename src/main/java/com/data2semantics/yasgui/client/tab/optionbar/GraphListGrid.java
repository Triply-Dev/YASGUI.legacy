package com.data2semantics.yasgui.client.tab.optionbar;

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
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.settings.Imgs;

public class GraphListGrid extends ListGrid {
	public static enum GraphArgType{NAMED_GRAPH, DEFAULT_GRAPH};
	private GraphArgType graphArgType;
	private static String KEY_GRAPH = "graph";
	private View view;
	public GraphListGrid(final View view, GraphArgType graphArgType) {
		this.graphArgType = graphArgType;
		this.view = view;
		setHeight100();
		setWidth100();
		setAutoFitData(Autofit.VERTICAL);
		setWrapCells(true);
		setCanResizeFields(true);
		setAlwaysShowEditors(true);
		setCanRemoveRecords(true);
		setFields(new ListGridField(KEY_GRAPH, "Graph"));
		setGraphs();
		setRemoveIcon(Imgs.CROSS.get());
	}
	
	
	public void setGraphsInSettings() {
		saveAllEdits();
		ArrayList<String> graphs = new ArrayList<String>();
		getTotalRows();
		
		Record[] gridRecords = getRecords();
		for (Record record: gridRecords) {
			if (record != null && record.getAttribute(KEY_GRAPH) != null) {
				graphs.add(record.getAttribute(KEY_GRAPH));
			}
		}
		if (graphArgType == GraphArgType.NAMED_GRAPH) {
			view.getSelectedTabSettings().setNamedGraphs(graphs);
		} else {
			view.getSelectedTabSettings().setDefaultGraphs(graphs);
		}
		LocalStorageHelper.storeSettingsInCookie(view.getSettings());
	}
	
	
	private void setGraphs() {
		ArrayList<String> graphs;
		if (graphArgType == GraphArgType.NAMED_GRAPH) {
			graphs = view.getSelectedTabSettings().getNamedGraphs();
		} else {
			graphs = view.getSelectedTabSettings().getDefaultGraphs();
		}
		ArrayList<ListGridRecord> records = new ArrayList<ListGridRecord>();
		for (String graph: graphs) {
			ListGridRecord record = new ListGridRecord();
			record.setAttribute(KEY_GRAPH, graph);
			records.add(record);
		}
		setRecords(records.toArray(new ListGridRecord[records.size()]));
	}
	
}
