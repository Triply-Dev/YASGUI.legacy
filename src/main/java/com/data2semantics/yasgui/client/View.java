package com.data2semantics.yasgui.client;

import java.util.logging.Logger;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.settings.Settings;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.data2semantics.yasgui.client.tab.results.ResultGrid;
import com.data2semantics.yasgui.shared.exceptions.SettingsException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Positioning;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

public class View extends VLayout {
	private Logger logger = Logger.getLogger("");
	private YasguiServiceAsync remoteService = YasguiServiceAsync.Util.getInstance();
	private ImgButton queryButton;
	private Img queryLoading;
	public static String DEFAULT_LOADING_MESSAGE = "Loading...";
	private static int QUERY_BUTTON_POS_TOP = 5;
	private static int QUERY_BUTTON_POS_LEFT = 5;
	private Label loading;
	private QueryTabs queryTabs;
	
	private Settings settings = new Settings();
	
	public View() {
		settings = Helper.getSettingsFromCookie();
		JsMethods.setTabBarProperties(QueryTabs.INDENT_TABS);
		JsMethods.declareCallableViewMethods(this);
		JsMethods.setProxyUriInVar(GWT.getModuleBaseURL() + "sparql");
		initLoadingWidget();
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
				JsMethods.queryJson(getSelectedTabSettings().getQueryString(), getSelectedTabSettings().getEndpoint());
				
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
		final Window window = new Window();
		window.setAutoSize(true);
		window.setMinWidth(400);
		window.setShowMinimizeButton(false);
		window.setIsModal(true);
		window.setShowModalMask(true);
		window.setAutoCenter(true);
		window.addCloseClickHandler(new CloseClickHandler() {
			public void onCloseClick(CloseClickEvent event) {
				window.destroy();
			}
		});
		window.setShowTitle(false);
		
		Label label = new Label(error);
		label.setMargin(4);
		label.setHeight100();
		window.addItem(label);
		window.draw();
	}

	/**
	 * Show the error window for a trowable. Prints the complete stack trace
	 * @param throwable
	 */
	public void onError(Throwable throwable) {
		String st = throwable.getClass().getName() + ": " + throwable.getMessage();
		for (StackTraceElement ste : throwable.getStackTrace()) {
			st += "\n" + ste.toString();
		}
		onError(st);
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
	public void drawResultsInTable(String jsonResult) {
		//Create grid and fill with data immediately. 
		//Will have cell alignment issues (bug smartgwt i guess) when initiating resultgrid before query, and filling afterwards
		QueryTab tab = getSelectedTab();
		ResultGrid queryTable = new ResultGrid(this, tab, jsonResult);
		tab.addQueryResult(queryTable);
	}
	
	/**
	 * Clear current query result table 
	 * Keep this method in the view object, so that it is easily callable from js
	 */
	public void resetQueryResult() {
		getSelectedTab().resetQueryResult();
	}
	
	/**
	 * Get query string from text area, set it in settings, and store in cookie
	 */
	public void storeQueryInCookie() {
		String query = getSelectedTab().getQueryTextArea().getQuery();
		getSelectedTabSettings().setQueryString(query);
		Helper.storeSettingsInCookie(getSettings());
	}
	
	/**
	 * Load prefixes from the server, and stores in javascript. Used for autocompletion of prefixes
	 * 
	 * @param forceUpdate
	 */
	public void setAutocompletePrefixes(boolean forceUpdate) {
		onLoadingStart("Fetching prefixes");
		// get prefixes from server
		getRemoteService().fetchPrefixes(forceUpdate, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				onError(caught.getMessage());
			}

			public void onSuccess(String prefixes) {
				JsMethods.setAutocompletePrefixes(prefixes);
				onLoadingFinish();
			}
		});

	}
	
	public QueryTabs getTabs() {
		return queryTabs;
	}

}
