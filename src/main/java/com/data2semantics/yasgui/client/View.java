package com.data2semantics.yasgui.client;

import java.util.logging.Logger;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.queryform.QueryTab;
import com.data2semantics.yasgui.client.queryform.QueryTabs;
import com.data2semantics.yasgui.client.queryform.ToolBar;
import com.data2semantics.yasgui.client.settings.Settings;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.data2semantics.yasgui.shared.exceptions.SettingsException;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class View extends VLayout {
	private Logger logger = Logger.getLogger("");
	private YasguiServiceAsync remoteService = YasguiServiceAsync.Util.getInstance();
	
	public static String DEFAULT_LOADING_MESSAGE = "Loading...";

	private Label loading;
	private ToolBar toolBar;
	private QueryTabs queryTabs;
	
	private Settings settings = new Settings();
	
	public View() {
		settings = Helper.getSettingsFromCookie();
		JsMethods.setTabBarProperties(QueryTabs.INDENT_TABS);
		JsMethods.declareCallableViewMethods(this);
		JsMethods.setProxyUriInVar(GWT.getModuleBaseURL() + "sparql");
		initLoadingWidget();
		setMargin(10);
		setWidth100();
		setHeight100();
		this.toolBar = new ToolBar(this);
		addMember(this.toolBar);
		setAutocompletePrefixes(false);
		queryTabs = new QueryTabs(this);
		addMember(queryTabs);
	}


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
//		window.setShowCloseButton(false);
//		window.setShowEdges(false);
		
		Label label = new Label(error);
		label.setMargin(4);
		label.setHeight100();
//		label.setWrap(false);
		window.addItem(label);
		window.draw();
	}

	public void onError(Throwable throwable) {
		String st = throwable.getClass().getName() + ": " + throwable.getMessage();
		for (StackTraceElement ste : throwable.getStackTrace()) {
			st += "\n" + ste.toString();
		}
		onError(st);
	}

	private void initLoadingWidget() {
		loading = new Label();
		loading.setIcon("loading.gif");
		loading.setBackgroundColor("#f0f0f0");
		loading.setBorder("1px solid grey");
		loading.getElement().getStyle().setPosition(Position.ABSOLUTE);
		loading.getElement().getStyle().setBottom(0, Unit.PX);
		loading.getElement().getStyle().setLeft(0, Unit.PX);
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
		getSelectedTab().drawResultsInTable(jsonResult);
	}
	
	/**
	 * Clear current query result table 
	 * Keep this method in the view object, so that it is easily callable from js
	 */
	public void resetQueryResult() {
		getSelectedTab().resetQueryResult();
	}
	
	public void storeQueryInCookie() {
		String query = getSelectedTab().getQueryTextArea().getQuery();
		getSelectedTabSettings().setQueryString(query);
		Helper.storeSettingsInCookie(getSettings());
	}
	
	public void setAutocompletePrefixes(boolean forceUpdate) {
		onLoadingStart();
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
