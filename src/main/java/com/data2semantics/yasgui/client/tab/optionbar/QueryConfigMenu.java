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


import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.helpers.TooltipProperties;
import com.data2semantics.yasgui.client.helpers.properties.TooltipText;
import com.data2semantics.yasgui.client.helpers.properties.ZIndexes;
import com.data2semantics.yasgui.client.settings.Icons;
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
	private static final int TOOLTIP_VERSION_QUERY_CONFIG = 1;
	private View view;
	private Window window;
	private Menu mainMenu = new Menu();
	private MenuItem selectJson;
	private MenuItem selectXml;
	private MenuItem constructXml;
	private MenuItem constructTurtle;
	private MenuItem post;
	private MenuItem get;
	private static int WINDOW_HEIGHT = 200;
	private static int WINDOW_WIDTH = 400;
	private ParametersListGrid paramListGrid;
	public static String CONTENT_TYPE_SELECT_JSON = "application/sparql-results+json";
	public static String CONTENT_TYPE_SELECT_XML = "application/sparql-results+xml";
	public static String CONTENT_TYPE_CONSTRUCT_TURTLE = "text/turtle";
	public static String CONTENT_TYPE_CONSTRUCT_XML = "application/rdf+xml";
	public static String REQUEST_POST = "POST";
	public static String REQUEST_GET = "GET";

	public QueryConfigMenu(final View view) {
		this.view = view;
		setIcon(Icons.TOOLS);
		
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
		MenuItem headersMenuItem = new MenuItem("Accept headers");

		Menu headersMenu = new Menu();
		MenuItem queryHeaders = new MenuItem("Select");
		queryHeaders.setSubmenu(getQueryAcceptHeadersSubMenu());
		MenuItem constructHeaders = new MenuItem("Construct");
		constructHeaders.setSubmenu(getConstructAcceptHeadersSubMenu());
		headersMenu.setItems(queryHeaders, constructHeaders);
		
		
		headersMenuItem.setSubmenu(headersMenu);
		return headersMenuItem;
	}
	
	private Menu getQueryAcceptHeadersSubMenu() {
		Menu acceptHeadersSubMenu = new Menu();
		selectJson = new MenuItem("JSON");
		selectXml = new MenuItem("XML");
		
		selectJson.setCheckIfCondition(new MenuItemIfFunction(){
			@Override
			public boolean execute(Canvas target, Menu menu, MenuItem item) {
				return view.getSelectedTabSettings().getSelectContentType().equals(CONTENT_TYPE_SELECT_JSON);
			}});
		selectXml.setCheckIfCondition(new MenuItemIfFunction(){
			@Override
			public boolean execute(Canvas target, Menu menu, MenuItem item) {
				return view.getSelectedTabSettings().getSelectContentType().equals(CONTENT_TYPE_SELECT_XML);
			}});
		
		selectJson.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				view.getSelectedTabSettings().setSelectContentType(CONTENT_TYPE_SELECT_JSON);
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
			}
		});
		selectXml.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				view.getSelectedTabSettings().setSelectContentType(CONTENT_TYPE_SELECT_XML);
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
			}
		});
		acceptHeadersSubMenu.setItems(selectXml, selectJson);
		return acceptHeadersSubMenu;
	}
	private Menu getConstructAcceptHeadersSubMenu() {
		Menu acceptHeadersSubMenu = new Menu();
		constructTurtle = new MenuItem("Turtle");
		constructXml = new MenuItem("XML");
		
		constructTurtle.setCheckIfCondition(new MenuItemIfFunction(){
			@Override
			public boolean execute(Canvas target, Menu menu, MenuItem item) {
				return view.getSelectedTabSettings().getConstructContentType().equals(CONTENT_TYPE_CONSTRUCT_TURTLE);
			}});
		constructXml.setCheckIfCondition(new MenuItemIfFunction(){
			@Override
			public boolean execute(Canvas target, Menu menu, MenuItem item) {
				return view.getSelectedTabSettings().getConstructContentType().equals(CONTENT_TYPE_CONSTRUCT_XML);
			}});
		
		constructTurtle.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				view.getSelectedTabSettings().setConstructContentType(CONTENT_TYPE_CONSTRUCT_TURTLE);
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
			}
		});
		constructXml.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				view.getSelectedTabSettings().setConstructContentType(CONTENT_TYPE_CONSTRUCT_XML);
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
			}
		});
		acceptHeadersSubMenu.setItems(constructTurtle, constructXml);
		return acceptHeadersSubMenu;
	}

	private MenuItem getQueryParamMenuItem() {
		MenuItem queryParam = new MenuItem("Add query parameters");
		queryParam.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				drawParamListGrid();
			}
		});
		queryParam.setCheckIfCondition(new MenuItemIfFunction(){
			@Override
			public boolean execute(Canvas target, Menu menu, MenuItem item) {
				return (view.getSelectedTabSettings().getQueryArgs().size() > 0);
			}});
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
	    addButton.setIcon(Icons.ADD);
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

	public void showTooltips(int fromVersionId) {
		if (!view.getSettings().inSingleEndpointMode() && fromVersionId < TOOLTIP_VERSION_QUERY_CONFIG) {
			TooltipProperties tProp = new TooltipProperties();
			tProp.setId(getDOM().getId());
			tProp.setContent(TooltipText.QUERY_CONFIG_MENU);
			tProp.setMy(TooltipProperties.POS_LEFT_CENTER);
			tProp.setAt(TooltipProperties.POS_RIGHT_CENTER);
			Helper.drawTooltip(tProp);
		}
		
	}
}
