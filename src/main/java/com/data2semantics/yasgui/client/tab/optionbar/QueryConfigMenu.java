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


import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.helpers.properties.ZIndexes;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
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
	private static int WINDOW_HEIGHT = 300;
	private static int WINDOW_WIDTH = 500;
	private ParametersListGrid paramListGrid;
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
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
			}
		});
		xml.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				view.getSelectedTabSettings().setContentType(CONTENT_TYPE_XML);
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
			}
		});
		acceptHeadersSubMenu.setItems(xml, json);
		acceptHeaders.setSubmenu(acceptHeadersSubMenu);
		return acceptHeaders;
	}

	private MenuItem getQueryParamMenuItem() {
		MenuItem queryParam = new MenuItem("Add query parameters");
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
				window.addCloseClickHandler(new CloseClickHandler(){

					@Override
					public void onCloseClick(CloseClickEvent event) {
						paramListGrid.setArgsInSettings();
						LocalStorageHelper.storeSettingsInCookie(view.getSettings());
						window.destroy();
						
					}});
				paramListGrid = new ParametersListGrid(view);
				VLayout layout = new VLayout();
				layout.setAlign(Alignment.CENTER);
				layout.setWidth100();
				layout.setHeight100();
			    IButton addButton = new IButton("Add Parameter");  
			    addButton.setWidth(120);  
			    addButton.setIcon("icons/fugue/plus-button.png");
			    addButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler(){
					@Override
					public void onClick(ClickEvent event) {
						paramListGrid.startEditingNew();
						
					}});
				layout.addMember(addButton);
				
				layout.addMember(paramListGrid);
				window.addItem(layout);
				window.draw();
			}
		});
		return queryParam;
	}

}
