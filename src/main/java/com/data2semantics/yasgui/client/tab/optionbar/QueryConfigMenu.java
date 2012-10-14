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
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.helpers.TooltipProperties;
import com.data2semantics.yasgui.client.helpers.properties.TooltipText;
import com.data2semantics.yasgui.client.helpers.properties.ZIndexes;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
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
	private MenuItem post;
	private MenuItem get;
	private static int WINDOW_HEIGHT = 200;
	private static int WINDOW_WIDTH = 400;
	private ParametersListGrid paramListGrid;
	public static String CONTENT_TYPE_JSON = "application/sparql-results+json";
	public static String CONTENT_TYPE_XML = "application/sparql-results+xml";
	public static String REQUEST_POST = "POST";
	public static String REQUEST_GET = "GET";

	public QueryConfigMenu(final View view) {
		this.view = view;
		setIcon("icons/diagona/bolt.png");
		
		mainMenu.setItems(getQueryParamMenuItem(), getAcceptHeaderMenuItem(), getRequestMethodMenuItem());
		setMenu(mainMenu);
		setTitle("Configure request");
		setCanFocus(false);
		addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				showMenu();
			}
			
		});
	}

	private MenuItem getRequestMethodMenuItem() {
		MenuItem acceptHeaders = new MenuItem("Request Method");

		Menu acceptHeadersSubMenu = new Menu();
		post = new MenuItem("POST");
		get = new MenuItem("GET");
		
		post.setCheckIfCondition(new MenuItemIfFunction(){
			@Override
			public boolean execute(Canvas target, Menu menu, MenuItem item) {
				return view.getSelectedTabSettings().getRequestMethod().equals(REQUEST_POST);
			}});
		get.setCheckIfCondition(new MenuItemIfFunction(){
			@Override
			public boolean execute(Canvas target, Menu menu, MenuItem item) {
				return view.getSelectedTabSettings().getRequestMethod().equals(REQUEST_GET);
			}});
		
		post.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				view.getSelectedTabSettings().setRequestMethod(REQUEST_POST);
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
			}
		});
		get.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				view.getSelectedTabSettings().setRequestMethod(REQUEST_GET);
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
			}
		});
		acceptHeadersSubMenu.setItems(post, get);
		acceptHeaders.setSubmenu(acceptHeadersSubMenu);
		return acceptHeaders;
	}

	private MenuItem getAcceptHeaderMenuItem() {
		MenuItem acceptHeaders = new MenuItem("Query accept headers");

		Menu acceptHeadersSubMenu = new Menu();
		post = new MenuItem("JSON");
		get = new MenuItem("XML");
		
		post.setCheckIfCondition(new MenuItemIfFunction(){
			@Override
			public boolean execute(Canvas target, Menu menu, MenuItem item) {
				return view.getSelectedTabSettings().getContentType().equals(CONTENT_TYPE_JSON);
			}});
		get.setCheckIfCondition(new MenuItemIfFunction(){
			@Override
			public boolean execute(Canvas target, Menu menu, MenuItem item) {
				return view.getSelectedTabSettings().getContentType().equals(CONTENT_TYPE_XML);
			}});
		
		post.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				view.getSelectedTabSettings().setContentType(CONTENT_TYPE_JSON);
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
			}
		});
		get.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				view.getSelectedTabSettings().setContentType(CONTENT_TYPE_XML);
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
			}
		});
		acceptHeadersSubMenu.setItems(get, post);
		acceptHeaders.setSubmenu(acceptHeadersSubMenu);
		return acceptHeaders;
	}

	private MenuItem getQueryParamMenuItem() {
		MenuItem queryParam = new MenuItem("Add query parameters");
		queryParam.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(MenuItemClickEvent event) {
				drawParamListGrid();
			}
		});
		return queryParam;
	}
	private void drawParamListGrid() {
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

	public void showTooltips() {
		TooltipProperties tProp = new TooltipProperties();
		tProp.setId(getDOM().getId());
		tProp.setContent(TooltipText.QUERY_CONFIG_MENU);
		tProp.setMy(TooltipProperties.POS_BOTTOM_CENTER);
		tProp.setAt(TooltipProperties.POS_TOP_CENTER);
		tProp.setYOffset(8);
		Helper.drawTooltip(tProp);
		
	}
}
