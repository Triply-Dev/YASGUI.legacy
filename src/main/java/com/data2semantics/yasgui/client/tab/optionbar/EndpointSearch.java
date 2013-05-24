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
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.util.StringUtil;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.properties.ZIndexes;
import com.data2semantics.yasgui.client.settings.Icons;
import com.data2semantics.yasgui.shared.Endpoints;
public class EndpointSearch extends VLayout {
	private static int ICON_WIDTH = 20;
	private static int ICON_HEIGHT = 20;
	private static int LAYOUT_WIDTH = 26;
	
	private static int WINDOW_HEIGHT = 600;
	private static int WINDOW_WIDTH = 1000;
	private static int COL_WIDTH_DATASET_TITLE = 150;
	private static int COL_WIDTH_MORE_INFO = 22;
	private ListGrid searchGrid;
	private View view;
	private Window window;
	private ImgButton imgButton = new ImgButton();
	public EndpointSearch(View view) {
		this.view = view;
		LayoutSpacer bottomSpacer = new LayoutSpacer();
		bottomSpacer.setHeight(3);
		LayoutSpacer topSpacer = new LayoutSpacer();
		topSpacer.setHeight100();
		
		
		
		addMembers(topSpacer, addImgButton(), bottomSpacer);
		
		
	}
	
	private HLayout addImgButton() {
		HLayout hLayout = new HLayout();
		hLayout.setWidth(LAYOUT_WIDTH);
		LayoutSpacer rSpacer = new LayoutSpacer();
		rSpacer.setWidth100();
		LayoutSpacer lSpacer = new LayoutSpacer();
		lSpacer.setWidth(3);
		imgButton.setSrc(Icons.SEARCH);
		
		imgButton.setWidth(ICON_WIDTH);
		imgButton.setHeight(ICON_HEIGHT);
		imgButton.setShowDown(false);
		imgButton.setShowRollOver(false);
		
		imgButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				window = new Window();
				window.setZIndex(ZIndexes.MODAL_WINDOWS);
				window.setTitle("Search endpoints");
				window.setIsModal(true);
				window.setDismissOnOutsideClick(true);
				window.setWidth(WINDOW_WIDTH);
				window.setHeight(WINDOW_HEIGHT);
				window.setShowMinimizeButton(false);
				window.setAutoCenter(true);
				window.addItem(getListGridSearchTable());
				window.draw();
				
			}

		});
		hLayout.addMembers(lSpacer, imgButton, rSpacer);
		return hLayout;
	}
	
	/**
	 * get listgrid to search endpoints in
	 * 
	 * @return
	 */
	private ListGrid getListGridSearchTable() {
		searchGrid = new ListGrid();
		searchGrid.setCellFormatter(new CellFormatter(){
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				if (rowNum == 0 && colNum == 0 && Helper.recordIsEmpty(record)) {
					return "Empty";
				}
				String colName = searchGrid.getFieldName(colNum);
				String cellValue = record.getAttribute(colName);
				
				if (cellValue != null) {
					if (colName.equals(Endpoints.KEY_TITLE) || colName.equals(Endpoints.KEY_DESCRIPTION) || colName.equals(Endpoints.KEY_ENDPOINT)) {
						return "<span style='cursor:pointer;'>" + StringUtil.asHTML(cellValue) + "</span>";
					} else if (colName.equals(Endpoints.KEY_DATASETURI) && cellValue.length() > 0) {
						return "<a href=\"" + cellValue + "\" target=\"_blank\"><img src=\"images/icons/fugue/information.png\"/ width=\"16\" height=\"16\"></a>";
					}
				}
                return null;
			}});
		searchGrid.addRecordClickHandler(new RecordClickHandler(){
			@Override
			public void onRecordClick(RecordClickEvent event) {
				view.getSelectedTab().getEndpointInput().setEndpoint(event.getRecord().getAttributeAsString(Endpoints.KEY_ENDPOINT));
				window.destroy();
			}});
		searchGrid.setFixedRecordHeights(false);
		searchGrid.setHeight100();
		searchGrid.setWidth100();
		searchGrid.setFilterButtonPrompt("");
		searchGrid.setAutoFitData(Autofit.VERTICAL);
		searchGrid.setWrapCells(true);
		searchGrid.setShowFilterEditor(true);
		ArrayList<ListGridField> fields = new ArrayList<ListGridField>();
		fields.add(new ListGridField(Endpoints.KEY_TITLE, "Dataset", COL_WIDTH_DATASET_TITLE));
		fields.add(new ListGridField(Endpoints.KEY_ENDPOINT, "Endpoint"));
		fields.add(new ListGridField(Endpoints.KEY_DESCRIPTION, "Description"));
		ListGridField dataUriField = new ListGridField(Endpoints.KEY_DATASETURI, " ", COL_WIDTH_MORE_INFO);
		dataUriField.setFilterEditorType(new SpacerItem());//don't want to be able to filter on datauri
		fields.add(dataUriField);
		searchGrid.setFields(fields.toArray(new ListGridField[fields.size()]));
		searchGrid.setFilterOnKeypress(true);
		searchGrid.setDataSource(view.getEndpointDataSource());
		searchGrid.setWrapCells(true);
		searchGrid.setCanResizeFields(true);
		searchGrid.fetchData();//We are using a client-only datasource. need to manually fetch to fill grid
		return searchGrid;
	}
}
