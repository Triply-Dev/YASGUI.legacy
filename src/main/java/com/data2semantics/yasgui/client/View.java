package com.data2semantics.yasgui.client;

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

import java.io.IOException;
import java.util.logging.Logger;

import com.data2semantics.yasgui.client.helpers.AppcacheHelper;
import com.data2semantics.yasgui.client.helpers.CallableJsMethods;
import com.data2semantics.yasgui.client.helpers.ChangelogHelper;
import com.data2semantics.yasgui.client.helpers.ErrorHelper;
import com.data2semantics.yasgui.client.helpers.GoogleAnalytics;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.HistoryHelper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.openid.OpenId;
import com.data2semantics.yasgui.client.services.OpenIdServiceAsync;
import com.data2semantics.yasgui.client.services.YasguiServiceAsync;
import com.data2semantics.yasgui.client.settings.EnabledFeatures;
import com.data2semantics.yasgui.client.settings.Settings;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.data2semantics.yasgui.client.tab.QueryTabs;
import com.data2semantics.yasgui.client.tab.optionbar.endpoints.EndpointDataSource;
import com.data2semantics.yasgui.shared.Endpoints;
import com.data2semantics.yasgui.shared.SettingKeys;
import com.data2semantics.yasgui.shared.StaticConfig;
import com.data2semantics.yasgui.shared.autocompletions.AutocompletionsInfo;
import com.data2semantics.yasgui.shared.exceptions.ElementIdException;
import com.data2semantics.yasgui.shared.exceptions.SettingsException;
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

public class View extends VLayout implements RpcElement {
	private static int TOP_SPACING = 34;
	private Logger logger = Logger.getLogger(View.class.getName());
	private YasguiServiceAsync remoteService = YasguiServiceAsync.Util.getInstance();
	private OpenIdServiceAsync openIdService = OpenIdServiceAsync.Util.getInstance();
	private EndpointDataSource endpointDataSource;
	private QueryTabs queryTabs;
	private ViewElements viewElements;
	private Settings settings;
	private CallableJsMethods jsEvents;
	private OpenId openId;
	private HistoryHelper historyHelper = new HistoryHelper(this);
	private ConnectivityHelper connHelper;
	private ChangelogHelper changelogHelper;
	private ErrorHelper errorHelper;
	private AppcacheHelper appcacheHelper;
	private AutocompletionsInfo autocompletionsInfo = null;
	
	public View() {
		boolean newUser = false;
		
		if (LocalStorageHelper.newUser())
			newUser = true;
		errorHelper = new ErrorHelper(this);
		retrieveSettings();
		if (!Helper.isCrawler() && !Helper.isSeleniumVisitor() && JsMethods.offlineSupported() && getSettings().getEnabledFeatures().offlineCachingEnabled()) {
			this.appcacheHelper = new AppcacheHelper(this);
		}
		setViewLayout();
		
		viewElements = new ViewElements(this);
		jsEvents = new CallableJsMethods(this);
		if (getSettings().isDbSet()) {
			openId = new OpenId(this);
		}

		if (getSettings().useGoogleAnalytics()) {
			GoogleAnalytics.init(getSettings().getGoogleAnalyticsId());
		}
		setOverflow(Overflow.HIDDEN);
		connHelper = new ConnectivityHelper(this);
		if (getEnabledFeatures().endpointSelectionEnabled()) {
			initEndpointDataSource(false);
		}
		setAutocompletePrefixes(false);

		JsMethods.initJs();

		queryTabs = new QueryTabs(this);
		addMember(queryTabs);

		if (settings.useGoogleAnalytics() && !settings.cookieConsentAnswered() && !Helper.isCrawler()) {
			getElements().askCookieConsent();
		}

		changelogHelper = new ChangelogHelper(this);
		processVersionChanges();
		processUrlParameters(newUser);

		getHistory().replaceHistoryState();
		
		if (getSettings().isDbSet() && getEnabledFeatures().getEnabledClassCompletionMethods().size() > 0 || getEnabledFeatures().getEnabledPropertyCompletionMethods().size() > 0) {
			retrieveAutocompletionsInfo();
		}
		
		
		
		connHelper.checkOnlineStatus();
	}

	public void retrieveAutocompletionsInfo() {
		new GwtCallbackWrapper<AutocompletionsInfo>(this) {
			public void onCall(AsyncCallback<AutocompletionsInfo> callback) {
				getRemoteService().getAutocompletionsInfo(callback);
			}

			protected void onFailure(Throwable throwable) {
				getErrorHelper().onError(throwable);
			}

			protected void onSuccess(AutocompletionsInfo autocompletionsInfo) {
				view.setAutocompletionsInfo(autocompletionsInfo);
			}

		}.call();
	}
	
	protected void setAutocompletionsInfo(AutocompletionsInfo autocompletionsInfo) {
		this.autocompletionsInfo = autocompletionsInfo;
	}
	public AutocompletionsInfo getAutocompletionsInfo() {
		return this.autocompletionsInfo;
	}
	

	private void setViewLayout() {
		setWidth100();
		setHeight100();
		// Setting margins on tabset messes up layout. Therefore use spacer
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setHeight(TOP_SPACING);
		addMember(spacer);
	}

	private void processUrlParameters(boolean newUser) {
		String query = Window.Location.getParameter(SettingKeys.QUERY_STRING);
		String endpoint = Window.Location.getParameter(SettingKeys.ENDPOINT);
		if (query != null && endpoint != null && query.length() > 0 && endpoint.length() > 0) {
			if (newUser) {
				// If we are a new user, just show the results in the same query
				// tab. Otherwise, show in a new query tab
				// to do this, we simple remove the only open tab, and re-open a
				// new one
				QueryTab selectedTab = getSelectedTab();
				if (selectedTab != null) {
					queryTabs.removeTab(selectedTab, false, true);
				}
			}

			TabSettings tabSettings = new TabSettings(getSettings());
			tabSettings.setValuesFromUrlParams();

			getSettings().addTabSettings(tabSettings);
			getTabs().addTab(tabSettings, true);
			LocalStorageHelper.storeSettingsInCookie(getSettings());
		}
	}

	public ChangelogHelper getChangelogHelper() {
		return changelogHelper;
	}

	private void retrieveSettings() {
		try {
			settings = Settings.retrieveSettings();
		} catch (IOException e) {
			getErrorHelper().onError(e);
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
				}
			});
		} else {
			String versionString = LocalStorageHelper.getVersion();
			// the version ID might have stayed the same (no major changes which
			// might have influenced stale caches or needing tooltip popups),
			// but version string might have been upped. Keep track of this
			if (!versionString.equals(StaticConfig.VERSION)) {
				LocalStorageHelper.setVersion(StaticConfig.VERSION);
			}
		}
	}

	public void showTooltips(int fromVersionId) {
		if (!Helper.isCrawler()) {
			try {
				queryTabs.showTooltips(fromVersionId);
				getElements().showTooltips(fromVersionId);
				getSelectedTab().showTooltips(fromVersionId);
			} catch (ElementIdException e) {
				getErrorHelper().onError(e);
			}
		}
	}

	public void showPlayButton(java.lang.Boolean queryValid) {

	}

	public ViewElements getElements() {
		return this.viewElements;
	}

	public YasguiServiceAsync getRemoteService() {
		return remoteService;
	}

	public OpenIdServiceAsync getOpenIdService() {
		return openIdService;
	}

	public Logger getLogger() {
		return this.logger;
	}

	public Settings getSettings() {
		return this.settings;
	}

	public void setSettings(Settings settings) {
		this.settings = settings;
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
			getErrorHelper().onError(e);
		}
		return tabSettings;
	}

	public QueryTab getSelectedTab() {
		return (QueryTab) queryTabs.getSelectedTab();
	}

	/**
	 * Load prefixes from the server, and stores in javascript. Used for
	 * autocompletion of prefixes
	 * 
	 * @param forceUpdate
	 */
	public void setAutocompletePrefixes(final boolean forceUpdate) {
		String prefixes = LocalStorageHelper.getPrefixesFromLocalStorage();
		if (forceUpdate || prefixes == null) {
			// get prefixes from server
			viewElements.onLoadingStart("Fetching prefixes");

			new GwtCallbackWrapper<String>(this) {
				public void onCall(AsyncCallback<String> callback) {
					getRemoteService().fetchPrefixes(forceUpdate, callback);
				}

				protected void onFailure(Throwable throwable) {
					getErrorHelper().onError(throwable);
				}

				protected void onSuccess(String prefixes) {
					LocalStorageHelper.setPrefixes(prefixes);
					JsMethods.setAutocompletePrefixes(prefixes);
					viewElements.onLoadingFinish();
				}

			}.call();
		} else {
			JsMethods.setAutocompletePrefixes(prefixes);
		}
	}

	/**
	 * Initialize datasource containing endpoint info. Used in autocompletion
	 * input, as well as endpoint search grid
	 * 
	 * @param forceUpdate
	 */
	public void initEndpointDataSource(final boolean forceUpdate) {
		endpointDataSource = new EndpointDataSource(this);
		String endpoints = LocalStorageHelper.getEndpointsFromLocalStorage();
		if (!forceUpdate && endpoints != null && endpoints.length() > 0) {
			try {
				endpointDataSource.addEndpointsFromJson(endpoints);
			} catch (Exception e) {
				getErrorHelper().onError(e);
			}
		} else {
			// get endpoint data from server
			viewElements.onLoadingStart("Fetching endpoint data");
			new GwtCallbackWrapper<String>(this) {
				public void onCall(AsyncCallback<String> callback) {
					getRemoteService().fetchEndpoints(forceUpdate, callback);
				}

				protected void onFailure(Throwable throwable) {
					getErrorHelper().onError(throwable);
				}

				protected void onSuccess(String endpoints) {
					if (endpoints.length() > 0) {
						LocalStorageHelper.setEndpoints(endpoints);
						try {
							endpointDataSource.addEndpointsFromJson(endpoints);
						} catch (Exception e) {
							getErrorHelper().onError(e);
						}

					} else {
						getErrorHelper().onError("Failed to retrieve list of endpoints from server");
					}
					viewElements.onLoadingFinish();
				}

			}.call();
		}
	}

	public EndpointDataSource getEndpointDataSource() {
		return this.endpointDataSource;
	}

	public QueryTabs getTabs() {
		return queryTabs;
	}

	/**
	 * For a given endpoint, check whether it is defined in our endpoints
	 * datasource. If it isnt, add it
	 * 
	 * @param endpoint
	 */
	public void checkAndAddEndpointToDs(String endpoint) {
		if (getEnabledFeatures().endpointSelectionEnabled()) {
			Record[] records = endpointDataSource.getCacheData();
			boolean exists = false;
			for (Record record : records) {
				String recordEndpoint = record.getAttribute(Endpoints.KEY_ENDPOINT);
				if (recordEndpoint != null && recordEndpoint.equals(endpoint)) {
					exists = true;
					break;
				}
			}

			if (!exists) {
				// Ok, so endpoint is not in our datasource. let's add it
				ListGridRecord listGridRecord = new ListGridRecord();
				listGridRecord.setAttribute(Endpoints.KEY_ENDPOINT, endpoint);
				Record[] newRecords = new Record[records.length + 1];
				newRecords[0] = listGridRecord;
				System.arraycopy(records, 0, newRecords, 1, records.length);
				endpointDataSource = new EndpointDataSource(this, newRecords);

				if (Storage.isSupported()) {
					// we have html5. add it to local storage as well so we keep
					// it persistent between sessions
					String endpointsJsonString = LocalStorageHelper.getEndpointsFromLocalStorage();
					if (endpointsJsonString == null) {
						// There are no endpoints in our storage.
						// This is kinda strange, but lets create a json array
						// with this new endpoint anyway
						JSONArray jsonArray = new JSONArray();
						JSONObject newEndpointObject = new JSONObject();
						newEndpointObject.put(Endpoints.KEY_ENDPOINT, new JSONString(endpoint));
						jsonArray.set(0, newEndpointObject);
						LocalStorageHelper.setEndpoints(jsonArray.toString());
					} else {
						// Prepend the new endpoint to the array in our json
						// object
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
	}

	/**
	 * get openid object
	 * 
	 * @return
	 */
	public OpenId getOpenId() {
		return this.openId;
	}

	/**
	 * Get java methods callable from JS
	 * 
	 * @return
	 */
	public CallableJsMethods getCallableJsMethods() {
		return jsEvents;

	}

	public HistoryHelper getHistory() {
		return this.historyHelper;
	}

	public EnabledFeatures getEnabledFeatures() {
		return getSettings().getEnabledFeatures();
	}

	public ConnectivityHelper getConnHelper() {
		return this.connHelper;
	}
	public ErrorHelper getErrorHelper() {
		return this.errorHelper;
	}

	public void disableRpcElements() {
		getTabs().disableRpcElements();
		viewElements.disableRpcElements();
	}

	public void enableRpcElements() {
		// this might not have loaded correctly when yasgui was offline on first load
		if (autocompletionsInfo == null) retrieveAutocompletionsInfo();
		getTabs().enableRpcElements();
		viewElements.enableRpcElements();

	}

	public AppcacheHelper getAppcacheHelper() {
		return appcacheHelper;
	}
}
