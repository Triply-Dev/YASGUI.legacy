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

import java.io.IOException;
import java.util.logging.Logger;
import com.data2semantics.yasgui.client.helpers.GoogleAnalytics;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.helpers.properties.ZIndexes;
import com.data2semantics.yasgui.client.settings.Settings;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.data2semantics.yasgui.client.tab.optionbar.EndpointDataSource;
import com.data2semantics.yasgui.shared.Endpoints;
import com.data2semantics.yasgui.shared.StaticConfig;
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
import com.google.gwt.user.client.Window;
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
	
	public View() {
		boolean newUser = false;
		if (LocalStorageHelper.newUser()) newUser = true;
		
		
		setViewLayout();
		
		retrieveSettings();
		
		processVersionChanges();
		GoogleAnalytics.init(GoogleAnalytics.UID);
		setOverflow(Overflow.HIDDEN);
		
		
		if (!settings.inSingleEndpointMode()) {
			endpointDataSource = new EndpointDataSource(this);
		}
		setAutocompletePrefixes(false);
		viewElements = new ViewElements(this);
		initJs();

		
		queryTabs = new QueryTabs(this);
		addMember(queryTabs);
		
		footer = new Footer(this);
		addMember(footer);
		
		getElements().checkHtml5();
		
		processUrlParameters(newUser);
	}
	
	private void setViewLayout() {
		setWidth100();
		setHeight100();
		//Setting margins on tabset messes up layout. Therefore use spacer
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setHeight(30);
		addMember(spacer);	
	}
	
	private void processUrlParameters(boolean newUser) {
		String query = Window.Location.getParameter(TabSettings.QUERY_STRING);
		String endpoint = Window.Location.getParameter(TabSettings.ENDPOINT);
		if (query != null && endpoint != null && query.length() > 0 && endpoint.length() > 0) {
			if (newUser) {
				//If we are a new user, just show the results in the same query tab. Otherwise, show in a new query tab
				//to do this, we simple remove the only open tab, and re-open a new one
				QueryTab selectedTab = getSelectedTab();
				if (selectedTab != null) {
					queryTabs.removeTab(selectedTab, false);
				}
			}
			
			TabSettings tabSettings = new TabSettings(getSettings());
			tabSettings.setValuesFromUrlParams();
			
			getSettings().addTabSettings(tabSettings);
			getTabs().addTab(tabSettings, true);
			LocalStorageHelper.storeSettingsInCookie(getSettings());
		}
	}
	
	private void retrieveSettings()  {
		try {
			String installationSettings = JsMethods.getInstallationSettings();
			if (installationSettings == null || installationSettings.length() == 0) {
				throw new IOException("Failed to load default settings from javascript.");
			}
			 
			//First create settings object with the proper default values
			//need default values when creating settings objects, as not all values might be filled in our cache and stuff
			settings.addToSettings(installationSettings);
			
			String settingsString = LocalStorageHelper.getSettingsStringFromCookie();
			if (settingsString != null && settingsString.length() > 0) {
				settings.addToSettings(settingsString);
				
				//add installation settings again. The settings retrieved from cookie might have stale default values
				settings.addToSettings(installationSettings);
			} else {
				//no options in cache. we already have default values and settings object
				//now initialize a tab with default values
				settings.initDefaultTab();
			}
		} catch (IOException e) {
			onError(e);
		}
	}
	
	private void processVersionChanges() {
		final int versionId = LocalStorageHelper.getVersionId();
		if (versionId < StaticConfig.VERSION_ID) {
			LocalStorageHelper.setVersion(StaticConfig.VERSION);
			LocalStorageHelper.setVersionId(StaticConfig.VERSION_ID);
			Scheduler.get().scheduleDeferred(new Command() {
				public void execute() {
					showTooltips(versionId);
					LocalStorageHelper.setTooltipsShown();
				}
			});
		}
	}
	
	public void showTooltips(int fromVersionId) {
		try {
			footer.showTooltips(fromVersionId);
			queryTabs.showTooltips(fromVersionId);
			getSelectedTab().showTooltips(fromVersionId);
		} catch (ElementIdException e) {
			onError(e);
		}
	}
	
	private void initJs() {
		JsMethods.setTabBarProperties(QueryTabs.INDENT_TABS);
		JsMethods.declareCallableViewMethods(this, viewElements);
		JsMethods.setProxyUriInVar(GWT.getModuleBaseURL() + "sparql");
		JsMethods.setQtipZIndex(ZIndexes.HELP_TOOLTIPS);
	}
	public void showPlayButton(java.lang.Boolean queryValid) {
		
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
		TabSettings tabSettings = null;
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
	 * Draw json or xml results
	 * Keep this method in the view object, so that it is easily callable from js
	 * 
	 * @param tabId Tab id to draw results in. 
	 * Pass this, so when you switch tabs just after clicking the query button, the results still gets drawn in the proper tab
	 * @param resultString
	 * @param contentType Content type of query result
	 */
	public void drawResults(String tabId, String resultString, String contentType) {
		QueryTab tab = (QueryTab)queryTabs.getTab(tabId);
		if (tab == null) {
			onError("No tab to draw results in");
		}
		tab.getResultContainer().processResult(resultString, contentType);
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
			viewElements.onLoadingStart("Fetching prefixes");
			getRemoteService().fetchPrefixes(forceUpdate, new AsyncCallback<String>() {
				public void onFailure(Throwable caught) {
					onError(caught.getMessage());
				}
	
				public void onSuccess(String prefixes) {
					LocalStorageHelper.setPrefixes(prefixes);
					JsMethods.setAutocompletePrefixes(prefixes);
					viewElements.onLoadingFinish();
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
			viewElements.onLoadingStart("Fetching endpoint data");
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
					viewElements.onLoadingFinish();
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
	
	public void setQueryType(String queryType) {
		getSelectedTab().setQueryType(queryType);
	}
	
	/**
	 * @see ViewElements#onError(String)
	 */
	public void onError(String error) {
		getElements().onError(error);
	}
	
	
	/**
	 * @see ViewElements#onError()Throwable
	 */
	public void onError(Throwable e) {
		getElements().onError(e);
	}
	
	public void adjustQueryInputForContent() {
		getSelectedTab().getQueryTextArea().adjustForContent(true);
	}
}
