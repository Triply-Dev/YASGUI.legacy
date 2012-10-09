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
package com.data2semantics.yasgui.client;

import java.util.logging.Logger;
import com.data2semantics.yasgui.client.helpers.GoogleAnalytics;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.helpers.properties.ZIndexes;
import com.data2semantics.yasgui.client.settings.Settings;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.data2semantics.yasgui.client.tab.EndpointDataSource;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.data2semantics.yasgui.client.tab.results.ResultContainer;
import com.data2semantics.yasgui.shared.Endpoints;
import com.data2semantics.yasgui.shared.exceptions.ElementIdException;
import com.data2semantics.yasgui.shared.exceptions.SettingsException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

public class View extends VLayout {
	private Logger logger = Logger.getLogger("");
	private YasguiServiceAsync remoteService = YasguiServiceAsync.Util.getInstance();
	private EndpointDataSource endpointDataSource;
	private QueryTabs queryTabs;
	private ViewElements viewElements;
	private Footer footer;
	private Settings settings = new Settings();
	public static String VERSION = "12.10"; //also defined in pom.xml
	
	public View() {
		LocalStorageHelper.setVersion(VERSION);
		GoogleAnalytics.init(GoogleAnalytics.UID);
		setOverflow(Overflow.HIDDEN);
		endpointDataSource = new EndpointDataSource(this);
		settings = LocalStorageHelper.getSettingsFromCookie();
		initJs();
		viewElements = new ViewElements(this);
		getElements().initLoadingWidget();
		setWidth100();
		setHeight100();
		
		getElements().addQueryButton();

		//Setting margins on tabset messes up layout. Therefore use spacer
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setHeight(30);
		addMember(spacer);
		
		setAutocompletePrefixes(false);
		queryTabs = new QueryTabs(this);
		addMember(queryTabs);
		
		footer = new Footer(this);
		addMember(footer);
		initEndpointDataSource(false);
		//Schedule this all the way at the end, so we have no problems with absolute positions and undrawn elements
		Scheduler.get().scheduleFinally(new Command() {
			public void execute() {
				if (LocalStorageHelper.showTooltips()) {
					showTooltips();
					LocalStorageHelper.setTooltipsShown();
				}
			}
		});
		getElements().checkHtml5();
	}
	
	public void showTooltips() {
		try {
			footer.showTooltips();
			queryTabs.showTooltips();
			getSelectedTab().showTooltips();
		} catch (ElementIdException e) {
			onError(e);
		}
	}
	
	private void initJs() {
		JsMethods.setTabBarProperties(QueryTabs.INDENT_TABS);
		JsMethods.declareCallableViewMethods(this);
		JsMethods.setProxyUriInVar(GWT.getModuleBaseURL() + "sparql");
		JsMethods.setQtipZIndex(ZIndexes.HELP_TOOLTIPS);
	}
	
	
	public ViewElements getElements() {
		return this.viewElements;
	}

	public YasguiServiceAsync getRemoteService() {
		return remoteService;
	}

	public Logger getLogger() {
		return this.logger;
	}

	public Settings getSettings() {
		return this.settings;
	}
	
	
	/**
	 * This method is used relatively often, so for easier use put it here
	 * 
	 * @return
	 */
	public TabSettings getSelectedTabSettings() {
		TabSettings tabSettings = new TabSettings();
		try {
			tabSettings = getSettings().getSelectedTabSettings();
		} catch (SettingsException e) {
			onError(e.getMessage());
		}
		return tabSettings;
	}

	public QueryTab getSelectedTab() {
		return (QueryTab) queryTabs.getSelectedTab();
	}
	
	/**
	 * Draw jsonresult in nice smartgwt table.
	 * Keep this method in the view object, so that it is easily callable from js
	 * 
	 * @param tabId Tab id to draw results in. 
	 * Pass this, so when you switch tabs just after clicking the query button, the results still gets drawn in the proper tab
	 * @param jsonResult
	 * @param contentType Content type of query result
	 */
	public void drawResultsInTable(String tabId, String jsonResult, String contentType) {
		QueryTab tab = (QueryTab)queryTabs.getTab(tabId);
		if (tab == null) {
			onError("No tab to draw results in");
		}
		int resultFormat;
		if (contentType.contains("json")) {
			resultFormat = ResultContainer.RESULT_FORMAT_JSON;
		} else {
			resultFormat = ResultContainer.RESULT_FORMAT_XML;
		}
		tab.getResultContainer().addQueryResult(jsonResult, resultFormat);
	}
	
	/**
	 * Clear current query result table 
	 * Keep this method in the view object, so that it is easily callable from js
	 */
	public void resetQueryResult() {
		getSelectedTab().getResultContainer().reset();
	}
	
	/**
	 * Get query string from text area, set it in settings, and store in cookie
	 */
	public void storeQueryInCookie() {
		String query = getSelectedTab().getQueryTextArea().getQuery();
		getSelectedTabSettings().setQueryString(query);
		LocalStorageHelper.storeSettingsInCookie(getSettings());
	}
	
	/**
	 * Load prefixes from the server, and stores in javascript. Used for autocompletion of prefixes
	 * 
	 * @param forceUpdate
	 */
	public void setAutocompletePrefixes(boolean forceUpdate) {
		String prefixes = LocalStorageHelper.getPrefixesFromLocalStorage();
		if (forceUpdate || prefixes == null) {
			// get prefixes from server
			onLoadingStart("Fetching prefixes");
			getRemoteService().fetchPrefixes(forceUpdate, new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					onError(caught.getMessage());
				}
	
				public void onSuccess(String prefixes) {
					LocalStorageHelper.setPrefixes(prefixes);
					JsMethods.setAutocompletePrefixes(prefixes);
					onLoadingFinish();
				}
			});
		} else {
			JsMethods.setAutocompletePrefixes(prefixes);
		}
	}
	
	/**
	 * Initialize datasource containing endpoint info. Used in autocompletion input, as well as endpoint search grid
	 * 
	 * @param forceUpdate
	 */
	public void initEndpointDataSource(boolean forceUpdate) {
		String endpoints = LocalStorageHelper.getEndpointsFromLocalStorage();
		if (forceUpdate || endpoints == null || endpoints.length() == 0) {
			// get endpoint data from server
			onLoadingStart("Fetching endpoint data");
			getRemoteService().fetchEndpoints(forceUpdate, new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					onError(caught);
				}
				public void onSuccess(String endpoints) {
					if (endpoints.length() > 0) {
						LocalStorageHelper.setEndpoints(endpoints);
						try {
							endpointDataSource.addEndpointsFromJson(endpoints);
						} catch (Exception e) {
							onError(e.getMessage());
						}
						
					} else {
						onError("Failed to retrieve list of endpoints from server");
					}
					onLoadingFinish();
				}
			});
		} else {
			try {
				endpointDataSource.addEndpointsFromJson(endpoints);
			} catch (Exception e) {
				onError(e);
			}
		}
	}
	
	public EndpointDataSource getEndpointDataSource() {
		return this.endpointDataSource;
	}
	
	public QueryTabs getTabs() {
		return queryTabs;
	}
	
	/**
	 * For a given endpoint, check whether it is defined in our endpoints datasource.
	 * If it isnt, add it 
	 * 
	 * @param endpoint
	 */
	public void checkAndAddEndpointToDs(String endpoint) {
		Record[] records = endpointDataSource.getCacheData();
		boolean exists = false;
		for (Record record:records) {
			String recordEndpoint = record.getAttribute(Endpoints.KEY_ENDPOINT);
			if (recordEndpoint != null && recordEndpoint.equals(endpoint)) {
				exists = true;
				break;
			}
		}
		
		if (!exists) {
			//Ok, so endpoint is not in our datasource. let's add it
			ListGridRecord listGridRecord = new ListGridRecord();
			listGridRecord.setAttribute(Endpoints.KEY_ENDPOINT, endpoint);
			Record[] newRecords = new Record[records.length+1];
			newRecords[0] = listGridRecord;
			System.arraycopy(records, 0, newRecords, 1, records.length);
			endpointDataSource.setCacheData(newRecords);
			
			
			if (Storage.isSupported()) {
				//we have html5. add it to local storage as well so we keep it persistent between sessions
				String endpointsJsonString = LocalStorageHelper.getEndpointsFromLocalStorage();
				if (endpointsJsonString == null) {
					//There are no endpoints in our storage. 
					//This is kinda strange, but lets create a json array with this new endpoint anyway
					JSONArray jsonArray = new JSONArray();
					JSONObject newEndpointObject = new JSONObject();
					newEndpointObject.put(Endpoints.KEY_ENDPOINT, new JSONString(endpoint));
					jsonArray.set(0, newEndpointObject);
					LocalStorageHelper.setEndpoints(jsonArray.toString());
				} else {
					//Prepend the new endpoint to the array in our json object
					JSONValue jsonVal = JSONParser.parseStrict(endpointsJsonString);
					if (jsonVal != null) {
						JSONArray endpoints = jsonVal.isArray();
						JSONArray newEndpointsArray = new JSONArray();
						JSONObject newEndpointObject = new JSONObject();
						newEndpointObject.put(Endpoints.KEY_ENDPOINT, new JSONString(endpoint));
						newEndpointsArray.set(0, newEndpointObject);
						if (endpoints != null) {
							for (int i = 0; i < endpoints.size(); i++) {
								newEndpointsArray.set(newEndpointsArray.size(), endpoints.get(i));
							}
						}
						LocalStorageHelper.setEndpoints(newEndpointsArray.toString());
					}
				}
			}
		}
	}
	/**
	 * @see ViewElements#onLoadingStart()
	 */
	public void onLoadingStart(String message) {
		getElements().onLoadingStart(message);
	}

	/**
	 * @see ViewElements#onLoadingFinish()
	 */
	public void onLoadingFinish() {
		getElements().onLoadingFinish();
	}
	
	/**
	 * @see ViewElements#onQueryStart()
	 */
	public void onQueryStart() {
		getElements().onQueryStart();
	}
	
	/**
	 * @see ViewElements#onQueryFinish()
	 */
	public void onQueryFinish() {
		getElements().onQueryFinish();
	}
	
	/**
	 * @see ViewElements#onError(String)
	 */
	public void onError(String error) {
		getElements().onError(error);
	}
	
	/**
	 * @see ViewElements#onQueryError(String)
	 */
	public void onQueryError(String error) {
		getElements().onQueryError(error);
	}
	
	/**
	 * @see ViewElements#onError()Throwable
	 */
	public void onError(Throwable e) {
		getElements().onError(e);
	}
}
