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

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.helpers.TooltipProperties;
import com.data2semantics.yasgui.client.settings.EnabledFeatures;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.settings.TooltipText;
import com.data2semantics.yasgui.client.settings.ZIndexes;
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
	private MenuItem selectJson = new MenuItem("JSON");
	private MenuItem selectXml = new MenuItem("XML");
	private MenuItem selectCsv = new MenuItem("CSV");
	private MenuItem selectTsv = new MenuItem("TSV");
	private MenuItem constructTurtle = new MenuItem("Turtle");
	private MenuItem constructXml = new MenuItem("RDF/XML");
	private MenuItem constructCsv = new MenuItem("CSV");
	private MenuItem constructTsv = new MenuItem("TSV");
	private MenuItem post;
	private MenuItem get;
	private Window graphWindow;
	private Window queryArgWindow;
	private static int QUERY_ARG_WINDOW_HEIGHT = 200;
	private static int QUERY_ARG_WINDOW_WIDTH = 400;
	private static int GRAPH_ARG_WINDOW_HEIGHT = 200;
	private static int GRAPH_ARG_WINDOW_WIDTH = 300;
	private ParametersListGrid paramListGrid;
	private GraphListGrid graphListGrid;
	public static String CONTENT_TYPE_SELECT_JSON = "application/sparql-results+json";
	public static String CONTENT_TYPE_SELECT_XML = "application/sparql-results+xml";
	public static String CONTENT_TYPE_SELECT_CSV = "text/csv";
	public static String CONTENT_TYPE_SELECT_TSV = "text/tab-separated-values";
	public static String CONTENT_TYPE_CONSTRUCT_TURTLE = "text/turtle";
	public static String CONTENT_TYPE_CONSTRUCT_XML = "application/rdf+xml";
	public static String REQUEST_POST = "POST";
	public static String REQUEST_GET = "GET";

	public QueryConfigMenu(final View view) throws IllegalStateException {
		this.view = view;
		fillMenu();
		
		setTitle("Configure request");
		setCanFocus(false);
		addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				showMenu();
			}
		});
	}
	
	private void fillMenu() throws IllegalStateException {
		Menu mainMenu = new Menu();
		ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
		EnabledFeatures enabledFeatures = view.getEnabledFeatures();
		if (enabledFeatures.queryParametersEnabled()) menuItems.add(getQueryParamMenuItem());
		if (enabledFeatures.namedGraphsSpecificationEnabled()) menuItems.add(getNamedGraphMenuItem());
		if (enabledFeatures.defaultGraphsSpecificationEnabled()) menuItems.add(getDefaultGraphMenuItem());
		if (enabledFeatures.acceptHeadersEnabled()) menuItems.add(getAcceptHeaderMenuItem());
		if (enabledFeatures.requestParametersEnabled()) menuItems.add(getRequestMethodMenuItem());
		
		if (menuItems.size() == 0) {
			throw new IllegalStateException("No items to fill query config menu");
		}
		mainMenu.setItems(menuItems.toArray(new MenuItem[menuItems.size()]));
		setMenu(mainMenu);
	}
	
	private MenuItem getNamedGraphMenuItem() {
		MenuItem namedGraphMenuItem = new MenuItem("Specify Named Graphs");
		namedGraphMenuItem.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				drawGraphWindow(GraphListGrid.GraphArgType.NAMED_GRAPH);
			}
		});
		namedGraphMenuItem.setCheckIfCondition(new MenuItemIfFunction(){
			@Override
			public boolean execute(Canvas target, Menu menu, MenuItem item) {
				return (view.getSelectedTabSettings().getNamedGraphs().size() > 0);
			}});
		return namedGraphMenuItem;
	}
	private MenuItem getDefaultGraphMenuItem() {
		MenuItem defaultGraphMenuItem = new MenuItem("Specify Default Graphs");
		defaultGraphMenuItem.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				drawGraphWindow(GraphListGrid.GraphArgType.DEFAULT_GRAPH);
			}
		});
		defaultGraphMenuItem.setCheckIfCondition(new MenuItemIfFunction(){
			@Override
			public boolean execute(Canvas target, Menu menu, MenuItem item) {
				return (view.getSelectedTabSettings().getDefaultGraphs().size() > 0);
			}});
		return defaultGraphMenuItem;
	}
	
	
	private void drawGraphWindow(GraphListGrid.GraphArgType graphArgType) {
		graphWindow = new Window();
		graphWindow.setZIndex(ZIndexes.MODAL_WINDOWS);
		String graphArgTypeString = (graphArgType == GraphListGrid.GraphArgType.NAMED_GRAPH? "named" : "default");
		graphWindow.setTitle("Specify " + graphArgTypeString + " graphs");
		graphWindow.setIsModal(true);
		graphWindow.setDismissOnOutsideClick(true);
		graphWindow.setWidth(GRAPH_ARG_WINDOW_WIDTH);
		graphWindow.setHeight(GRAPH_ARG_WINDOW_HEIGHT);
		graphWindow.setShowMinimizeButton(false);
		graphWindow.setAutoCenter(true);
		graphWindow.addCloseClickHandler(new CloseClickHandler(){
			@Override
			public void onCloseClick(CloseClickEvent event) {
				graphListGrid.setGraphsInSettings();
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
				if (graphWindow != null) {
					graphWindow.destroy();
				}
			}});
		graphListGrid = new GraphListGrid(view, graphArgType);
		VLayout layout = new VLayout();
		layout.setAlign(Alignment.CENTER);
		layout.setWidth100();
		layout.setHeight100();
	    IButton addButton = new IButton("Add " + graphArgTypeString + " graph");  
	    addButton.setWidth(160);  
	    addButton.setIcon(Imgs.ADD.get());
	    addButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				graphListGrid.startEditingNew();
				
			}});
		layout.addMember(addButton);
		
		layout.addMember(graphListGrid);
		graphWindow.addItem(layout);
		graphWindow.draw();
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
		MenuItem queryHeaders = new MenuItem("SELECT");
		queryHeaders.setSubmenu(getQueryAcceptHeadersSubMenu());
		MenuItem constructHeaders = new MenuItem("Graph");
		constructHeaders.setSubmenu(getConstructAcceptHeadersSubMenu());
		headersMenu.setItems(queryHeaders, constructHeaders);
		
		
		headersMenuItem.setSubmenu(headersMenu);
		return headersMenuItem;
	}
	
	private Menu getQueryAcceptHeadersSubMenu() {
		Menu acceptHeadersSubMenu = new Menu();

		selectJson.setCheckIfCondition(getContentTypeCheckIfCondition(false, CONTENT_TYPE_SELECT_JSON));
		selectXml.setCheckIfCondition(getContentTypeCheckIfCondition(false, CONTENT_TYPE_SELECT_XML));
		selectCsv.setCheckIfCondition(getContentTypeCheckIfCondition(false, CONTENT_TYPE_SELECT_CSV));
		selectTsv.setCheckIfCondition(getContentTypeCheckIfCondition(false, CONTENT_TYPE_SELECT_TSV));
		
		selectJson.addClickHandler(getContentTypeClickHandler(false, CONTENT_TYPE_SELECT_JSON));
		selectXml.addClickHandler(getContentTypeClickHandler(false, CONTENT_TYPE_SELECT_XML));
		selectCsv.addClickHandler(getContentTypeClickHandler(false, CONTENT_TYPE_SELECT_CSV));
		selectTsv.addClickHandler(getContentTypeClickHandler(false, CONTENT_TYPE_SELECT_TSV));

		acceptHeadersSubMenu.setItems(selectXml, selectJson, selectCsv, selectTsv);
		return acceptHeadersSubMenu;
	}
	private Menu getConstructAcceptHeadersSubMenu() {
		Menu acceptHeadersSubMenu = new Menu();
		
		constructTurtle.setCheckIfCondition(getContentTypeCheckIfCondition(true, CONTENT_TYPE_CONSTRUCT_TURTLE));
		constructXml.setCheckIfCondition(getContentTypeCheckIfCondition(true, CONTENT_TYPE_CONSTRUCT_XML));
		constructCsv.setCheckIfCondition(getContentTypeCheckIfCondition(true, CONTENT_TYPE_SELECT_CSV));
		constructTsv.setCheckIfCondition(getContentTypeCheckIfCondition(true, CONTENT_TYPE_SELECT_TSV));
		
		constructTurtle.addClickHandler(getContentTypeClickHandler(true, CONTENT_TYPE_CONSTRUCT_TURTLE));
		constructXml.addClickHandler(getContentTypeClickHandler(true, CONTENT_TYPE_CONSTRUCT_XML));
		constructCsv.addClickHandler(getContentTypeClickHandler(true, CONTENT_TYPE_SELECT_CSV));
		constructTsv.addClickHandler(getContentTypeClickHandler(true, CONTENT_TYPE_SELECT_TSV));
		
		acceptHeadersSubMenu.setItems(constructTurtle, constructXml, constructCsv, constructTsv);
		return acceptHeadersSubMenu;
	}
	
	private ClickHandler getContentTypeClickHandler(final boolean isConstruct, final String contentType) {
		return new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				if (isConstruct) {
					view.getSelectedTabSettings().setConstructContentType(contentType);
				} else {
					view.getSelectedTabSettings().setSelectContentType(contentType);
				}
				view.getSelectedTabSettings().setConstructContentType(contentType);
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
				view.getSelectedTab().adaptInterfaceToQueryType();
			}
		};
	}
	
	private MenuItemIfFunction getContentTypeCheckIfCondition(final boolean isConstruct, final String contentType) {
		return new MenuItemIfFunction(){
			@Override
			public boolean execute(Canvas target, Menu menu, MenuItem item) {
				if (isConstruct) {
					return view.getSelectedTabSettings().getConstructContentType().equals(contentType);
				} else {
					return view.getSelectedTabSettings().getSelectContentType().equals(contentType);
				}
				
			}
		};
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
				return (view.getSelectedTabSettings().getCustomQueryArgs().size() > 0);
			}});
		return queryParam;
	}
	private void drawParamListGrid() {
		queryArgWindow = new Window();
		queryArgWindow.setZIndex(ZIndexes.MODAL_WINDOWS);
		queryArgWindow.setTitle("Specify Query Arguments");
		queryArgWindow.setIsModal(true);
		queryArgWindow.setDismissOnOutsideClick(true);
		queryArgWindow.setWidth(QUERY_ARG_WINDOW_WIDTH);
		queryArgWindow.setHeight(QUERY_ARG_WINDOW_HEIGHT);
		queryArgWindow.setShowMinimizeButton(false);
		queryArgWindow.setAutoCenter(true);
		queryArgWindow.addCloseClickHandler(new CloseClickHandler(){

			@Override
			public void onCloseClick(CloseClickEvent event) {
				paramListGrid.setArgsInSettings();
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
				queryArgWindow.destroy();
				
			}});
		paramListGrid = new ParametersListGrid(view);
		VLayout layout = new VLayout();
		layout.setAlign(Alignment.CENTER);
		layout.setWidth100();
		layout.setHeight100();
	    IButton addButton = new IButton("Add Parameter");  
	    addButton.setWidth(120);  
	    addButton.setIcon(Imgs.ADD.get());
	    addButton.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				paramListGrid.startEditingNew();
				
			}});
		layout.addMember(addButton);
		
		layout.addMember(paramListGrid);
		queryArgWindow.addItem(layout);
		queryArgWindow.draw();
	}

	public void showTooltips(int fromVersionId) {
		if (view.getEnabledFeatures().endpointSelectionEnabled() && fromVersionId < TOOLTIP_VERSION_QUERY_CONFIG) {
			TooltipProperties tProp = new TooltipProperties();
			tProp.setId(getDOM().getId());
			tProp.setContent(TooltipText.QUERY_CONFIG_MENU);
			tProp.setMy(TooltipProperties.POS_TOP_CENTER);
			tProp.setAt(TooltipProperties.POS_BOTTOM_CENTER);
			tProp.setYOffset(0);
			tProp.setXOffset(20);
			Helper.drawTooltip(tProp);
		}
		
	}
}
