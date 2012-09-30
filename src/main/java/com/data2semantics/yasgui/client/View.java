package com.data2semantics.yasgui.client;

import java.util.logging.Logger;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.helpers.ZIndexes;
import com.data2semantics.yasgui.client.settings.Settings;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.data2semantics.yasgui.client.tab.EndpointDataSource;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.data2semantics.yasgui.client.tab.results.ResultContainer;
import com.data2semantics.yasgui.shared.Endpoints;
import com.data2semantics.yasgui.shared.exceptions.SettingsException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.URL;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Positioning;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

public class View extends VLayout {
	private Logger logger = Logger.getLogger("");
	private YasguiServiceAsync remoteService = YasguiServiceAsync.Util.getInstance();
	private ImgButton queryButton;
	private Img queryLoading;
	private EndpointDataSource endpointDataSource;
	public static String DEFAULT_LOADING_MESSAGE = "Loading...";
	private static int QUERY_BUTTON_POS_TOP = 5;
	private static int QUERY_BUTTON_POS_LEFT = 5;
	private Label loading;
	private QueryTabs queryTabs;
	
	private Settings settings = new Settings();
	
	public View() {
		endpointDataSource = new EndpointDataSource(this);
		settings = LocalStorageHelper.getSettingsFromCookie();
		JsMethods.setTabBarProperties(QueryTabs.INDENT_TABS);
		JsMethods.declareCallableViewMethods(this);
		JsMethods.setProxyUriInVar(GWT.getModuleBaseURL() + "sparql");
		initLoadingWidget();
		initEndpointDataSource(false);
		setWidth100();
		setHeight100();
		
		addQueryButton();

		//Setting margins on tabset messes up layout. Therefore use spacer
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setHeight(30);
		addMember(spacer);
		
		setAutocompletePrefixes(false);
		queryTabs = new QueryTabs(this);
		addMember(queryTabs);
		addMember(new Footer(this));
	}
	
	/**
	 * Add Query button. Position absolute, as it hovers slightly over the tabbar. Also adds a loading icon on the same place
	 */
	private void addQueryButton() {
		queryButton = new ImgButton();
		queryButton.setSrc("icons/custom/start.png");
		queryButton.setHeight(48);
		queryButton.setShowRollOver(false);
		queryButton.setShowDown(false);
		queryButton.setWidth(48);
		queryButton.setAlign(Alignment.CENTER);
		queryButton.setZIndex(666666666);
		queryButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String endpoint = getSelectedTabSettings().getEndpoint();
				JsMethods.queryJson(getSelectedTabSettings().getQueryString(), endpoint);
				checkAndAddEndpointToDs(endpoint);
			}
		});
		
		queryButton.setPosition(Positioning.ABSOLUTE);
		queryButton.setTop(QUERY_BUTTON_POS_TOP);
		queryButton.setLeft(QUERY_BUTTON_POS_LEFT);
		queryButton.draw();
		
		queryLoading = new Img();
		queryLoading.setSrc("icons/custom/query_loader.gif");
		queryLoading.setPosition(Positioning.ABSOLUTE);
		queryLoading.setTop(QUERY_BUTTON_POS_TOP);
		queryLoading.setLeft(QUERY_BUTTON_POS_LEFT);
		queryLoading.hide();
		queryLoading.setHeight(48);
		queryLoading.setWidth(48);
		queryLoading.draw();
	}

	
	/**
	 * Modal popup window to show on error
	 * 
	 * @param error
	 */
	public void onError(String error) {
		onLoadingFinish();
		Window window = getErrorWindow();
		window.setZIndex(ZIndexes.MODAL_WINDOWS);
		Label label = new Label(error);
		label.setCanSelectText(true);
		label.setMargin(4);
		label.setHeight100();
		window.addItem(label);
		window.draw();
	}
	
	public void onQueryError(String error) {
		onLoadingFinish();
		final Window window = getErrorWindow();
		window.setZIndex(ZIndexes.MODAL_WINDOWS);
		VLayout vLayout = new VLayout();
		vLayout.setWidth100();
		Label label = new Label(error);
		label.setMargin(4);
		label.setHeight100();
		label.setWidth100();
		vLayout.addMember(label);
		
		HLayout buttons = new HLayout();
		Button executeQuery = new Button("Open endpoint in new window");
		executeQuery.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				String endpoint = getSelectedTabSettings().getEndpoint();
				String query = getSelectedTabSettings().getQueryString();
				String url = endpoint + "?query=" + URL.encodeQueryString(query);
				com.google.gwt.user.client.Window.open(url, "_blank", null);
			}});
		executeQuery.setWidth(200);
		Button closeWindow = new Button("Close window");
		closeWindow.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				window.destroy();
			}});
		
		buttons.addMember(executeQuery);
		buttons.addMember(closeWindow);
		buttons.setWidth100();
		buttons.setLayoutAlign(Alignment.CENTER);
		vLayout.addMember(buttons);
		window.addItem(vLayout);
		window.setWidth(350);
		window.draw();
	}
	
	private Window getErrorWindow() {
		final Window window = new Window();
		window.setIsModal(true);
		window.setZIndex(ZIndexes.MODAL_WINDOWS);
		window.setAutoSize(true);
		window.setMinWidth(400);
		window.setShowMinimizeButton(false);
//		window.setShowModalMask(true);
		window.setAutoCenter(true);
//		window.setCanDrag(false);
		window.setCanDragResize(true);
		window.addCloseClickHandler(new CloseClickHandler() {
			public void onCloseClick(CloseClickEvent event) {
				window.destroy();
			}
		});
		window.setShowTitle(false);
		return window;
	}

	/**
	 * Show the error window for a trowable. Prints the complete stack trace
	 * @param throwable
	 */
	public void onError(Throwable e) {
		
		String stackTraceString = Helper.getStackTraceAsString(e);
		stackTraceString += Helper.getCausesStackTraceAsString(e);
		onError(stackTraceString);
	}

	/**
	 * initialize loading widget on left bottom corner
	 */
	private void initLoadingWidget() {
		loading = new Label();
		loading.setIcon("loading.gif");
		loading.setBackgroundColor("#f0f0f0");
		loading.setBorder("1px solid grey");
		loading.getElement().getStyle().setPosition(Position.ABSOLUTE);
		loading.getElement().getStyle().setTop(0, Unit.PX);
		loading.getElement().getStyle().setRight(0, Unit.PX);
		loading.setHeight(30);
		loading.setAutoWidth();
		loading.setOverflow(Overflow.VISIBLE);
		loading.setWrap(false);
		loading.setAlign(Alignment.CENTER);
		loading.adjustForContent(false);
		loading.setZIndex(999999999);
		loading.hide();
		loading.redraw();
	}

	public void onLoadingStart() {
		onLoadingStart(DEFAULT_LOADING_MESSAGE);
	}
	
	public void onLoadingStart(String message) {
		//Add spaces to end of message, as we have autowidth enabled to this Label
		loading.setContents(message + "&nbsp;&nbsp;");
		loading.show();
	}

	public void onLoadingFinish() {
		loading.hide();
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
	
	public void onQueryStart() {
		queryButton.hide();
		queryLoading.show();
	}
	
	public void onQueryFinish() {
		queryButton.show();
		queryLoading.hide();
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
	 * @param jsonResult
	 */
	public void drawResultsInTable(String jsonResult, String contentType) {
		//Create grid and fill with data immediately. 
		//Will have cell alignment issues (bug smartgwt i guess) when initiating resultgrid before query, and filling afterwards
		QueryTab tab = getSelectedTab();
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
	
	public void initEndpointDataSource(boolean forceUpdate) {
		String endpoints = LocalStorageHelper.getEndpointsFromLocalStorage();
		if (forceUpdate || endpoints == null) {
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
	private void checkAndAddEndpointToDs(String endpoint) {
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

}
