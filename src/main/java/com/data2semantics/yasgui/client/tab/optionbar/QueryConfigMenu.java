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

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.properties.ZIndexes;
import com.data2semantics.yasgui.client.tab.ConfigMenu;
import com.data2semantics.yasgui.shared.Endpoints;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.util.StringUtil;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.grid.CellFormatter;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.RecordClickEvent;
import com.smartgwt.client.widgets.grid.events.RecordClickHandler;
import com.smartgwt.client.widgets.menu.IconMenuButton;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemIfFunction;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

public class QueryConfigMenu extends IconMenuButton {
	private View view;
	private Window window;
	private Menu mainMenu = new Menu();
	private MenuItem json;
	private MenuItem xml;
	private ListGrid parametersGrid;
	private static int WINDOW_HEIGHT = 600;
	private static int WINDOW_WIDTH = 1000;
	public static String CONTENT_TYPE_JSON = "application/sparql-results+json";
	public static String CONTENT_TYPE_XML = "application/sparql-results+xml";

	public QueryConfigMenu(final View view) {
		this.view = view;
		setIcon("icons/diagona/bolt.png");
		mainMenu.setItems(getQueryParamMenuItem(), getAcceptHeaderMenuItem());
		setMenu(mainMenu);
		setTitle("Configure request");
	}

	private MenuItem getAcceptHeaderMenuItem() {
		MenuItem acceptHeaders = new MenuItem("Query accept headers");

		Menu acceptHeadersSubMenu = new Menu();
		json = new MenuItem("JSON");
		xml = new MenuItem("XML");
		
		json.setCheckIfCondition(new MenuItemIfFunction(){
			@Override
			public boolean execute(Canvas target, Menu menu, MenuItem item) {
				return view.getSelectedTabSettings().getContentType().equals(CONTENT_TYPE_JSON);
			}});
		xml.setCheckIfCondition(new MenuItemIfFunction(){
			@Override
			public boolean execute(Canvas target, Menu menu, MenuItem item) {
				return view.getSelectedTabSettings().getContentType().equals(CONTENT_TYPE_XML);
			}});
		
		json.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				view.getSelectedTabSettings().setContentType(CONTENT_TYPE_JSON);
			}
		});
		xml.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				view.getSelectedTabSettings().setContentType(CONTENT_TYPE_XML);
			}
		});
		acceptHeadersSubMenu.setItems(xml, json);
		acceptHeaders.setSubmenu(acceptHeadersSubMenu);
		return acceptHeaders;
	}

	private MenuItem getQueryParamMenuItem() {
		MenuItem queryParam = new MenuItem("Add query parameters");
		// prefixUpdate.setIcon("icons/diagona/reload.png");
		queryParam.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(MenuItemClickEvent event) {
				window = new Window();
				window.setZIndex(ZIndexes.MODAL_WINDOWS);
				window.setTitle("Search endpoints");
				window.setIsModal(true);
				window.setDismissOnOutsideClick(true);
				window.setWidth(WINDOW_WIDTH);
				window.setHeight(WINDOW_HEIGHT);
				window.setShowMinimizeButton(false);
				window.setAutoCenter(true);
				window.addItem(getParametersListGrid());
				window.draw();
			}
		});
		return queryParam;
	}

	/**
	 * get listgrid to search endpoints in
	 * 
	 * @return
	 */
	private ListGrid getParametersListGrid() {
		parametersGrid = new ListGrid();
		parametersGrid.setCellFormatter(new CellFormatter() {
			@Override
			public String format(Object value, ListGridRecord record, int rowNum, int colNum) {
				if (rowNum == 0 && colNum == 0 && Helper.recordIsEmpty(record)) {
					return "Empty";
				}
				String colName = parametersGrid.getFieldName(colNum);
				String cellValue = record.getAttribute(colName);

				if (cellValue != null) {
					if (colName.equals(Endpoints.KEY_TITLE) || colName.equals(Endpoints.KEY_DESCRIPTION)) {
						return StringUtil.asHTML(cellValue);
					} else if (colName.equals(Endpoints.KEY_ENDPOINT)) {
						return "<a href=\"" + cellValue + "\" target=\"_blank\">" + cellValue + "</a>";
					} else if (colName.equals(Endpoints.KEY_DATASETURI) && cellValue.length() > 0) {
						return "<a href=\"" + cellValue
								+ "\" target=\"_blank\"><img src=\"images/icons/fugue/information.png\"/ width=\"16\" height=\"16\"></a>";
					}
				}
				return null;
			}
		});
		parametersGrid.addRecordClickHandler(new RecordClickHandler() {
			@Override
			public void onRecordClick(RecordClickEvent event) {
				view.getSelectedTab().getEndpointInput().storeEndpoint(event.getRecord().getAttributeAsString(Endpoints.KEY_ENDPOINT));
				window.destroy();
			}
		});
		parametersGrid.setFixedRecordHeights(false);
		parametersGrid.setHeight(WINDOW_HEIGHT);
		parametersGrid.setWidth100();
		parametersGrid.setFilterButtonPrompt("");
		parametersGrid.setAutoFitData(Autofit.VERTICAL);
		parametersGrid.setWrapCells(true);
		parametersGrid.setShowFilterEditor(true);
		ArrayList<ListGridField> fields = new ArrayList<ListGridField>();
		fields.add(new ListGridField(Endpoints.KEY_TITLE, "Dataset"));
		fields.add(new ListGridField(Endpoints.KEY_ENDPOINT, "Endpoint"));
		fields.add(new ListGridField(Endpoints.KEY_DESCRIPTION, "Description"));
		parametersGrid.setFields(fields.toArray(new ListGridField[fields.size()]));
		parametersGrid.setFilterOnKeypress(true);
		parametersGrid.setDataSource(view.getEndpointDataSource());
		parametersGrid.setWrapCells(true);
		parametersGrid.setCanResizeFields(true);
		parametersGrid.fetchData();// We are using a client-only datasource. need to manually fetch to fill grid
		return parametersGrid;
	}
}
