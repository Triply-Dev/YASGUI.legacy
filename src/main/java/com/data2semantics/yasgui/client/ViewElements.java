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

import com.data2semantics.yasgui.client.helpers.GoogleAnalytics;
import com.data2semantics.yasgui.client.helpers.GoogleAnalyticsEvent;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.helpers.properties.ZIndexes;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.URL;
import com.google.gwt.storage.client.Storage;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Positioning;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class ViewElements {
	private View view;
	private ImgButton queryButton;
	private ImgButton queryLoading;
	public static String DEFAULT_LOADING_MESSAGE = "Loading...";
	private static int QUERY_BUTTON_POS_TOP = 5;
	private static int QUERY_BUTTON_POS_LEFT = 5;
	private Label loading;
	public ViewElements(View view) {
		this.view = view;
		addLogo();
		initLoadingWidget();
		addQueryButton();
	}
	
	/**
	 * Add Query button. Position absolute, as it hovers slightly over the tabbar. Also adds a loading icon on the same place
	 */
	public void addQueryButton() {
		queryButton = new ImgButton();
		queryButton.setSrc("icons/custom/start.png");
		queryButton.setHeight(48);
		queryButton.setShowRollOver(false);
		queryButton.setShowDown(false);
		queryButton.setWidth(48);
		queryButton.setAlign(Alignment.CENTER);
		queryButton.setZIndex(ZIndexes.TAB_CONTROLS);
		queryButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String tabId = view.getSelectedTab().getID();
				String endpoint = view.getSelectedTabSettings().getEndpoint();
				String queryString = view.getSelectedTabSettings().getQueryString();
				
				String contentType;
				if (view.getSelectedTab().getQueryType().equals("CONSTRUCT")) {
					//Change content type automatically for construct queries
					contentType = view.getSelectedTabSettings().getConstructContentType();
				} else {
					contentType = view.getSelectedTabSettings().getSelectContentType();
				}
			
				String argsString = view.getSelectedTabSettings().getQueryArgsAsJsonString();
				String requestMethod = view.getSelectedTabSettings().getRequestMethod();
				
				
				
				JsMethods.queryJson(tabId, queryString, endpoint, contentType, argsString, requestMethod);
				view.checkAndAddEndpointToDs(endpoint);
				GoogleAnalyticsEvent endpointEvent = new GoogleAnalyticsEvent("sparql", "endpoint");
				endpointEvent.setOptLabel(endpoint);
				GoogleAnalyticsEvent queryEvent = new GoogleAnalyticsEvent("sparql", "query");
				queryEvent.setOptLabel(queryString);
				GoogleAnalytics.trackEvents(endpointEvent, queryEvent);
			}
		});
		
		queryButton.setPosition(Positioning.ABSOLUTE);
		queryButton.setTop(QUERY_BUTTON_POS_TOP);
		queryButton.setLeft(QUERY_BUTTON_POS_LEFT);
		queryButton.draw();
		
		queryLoading = new ImgButton();
		queryLoading.setSrc("icons/custom/query_loader.gif");
		queryLoading.setPosition(Positioning.ABSOLUTE);
		queryLoading.setTop(QUERY_BUTTON_POS_TOP);
		queryLoading.setLeft(QUERY_BUTTON_POS_LEFT);
		queryLoading.hide();
		queryLoading.setHeight(48);
		queryLoading.setWidth(48);
		queryLoading.setZIndex(ZIndexes.TAB_CONTROLS);
		queryLoading.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				JsMethods.cancelQuery();
				onQueryFinish();
			}
		});
		queryLoading.setShowRollOver(false);
		queryLoading.setShowDown(false);
		queryLoading.draw();
	}
	
	/**
	 * initialize loading widget in top right corner
	 */
	public void initLoadingWidget() {
		loading = new Label();
		loading.setIcon("loading.gif");
		loading.setBackgroundColor("#f0f0f0");
		loading.setBorder("1px solid #C0C3C7");
		loading.getElement().getStyle().setPosition(Position.FIXED);
		loading.getElement().getStyle().setTop(0, Unit.PX);
		loading.getElement().getStyle().setLeft(50, Unit.PCT);
		loading.getElement().getStyle().setMarginLeft(-25, Unit.PX);
		loading.setHeight(24);
		loading.setAutoWidth();
		loading.setOverflow(Overflow.VISIBLE);
		loading.setWrap(false);
		loading.setAlign(Alignment.CENTER);
		loading.adjustForContent(false);
		loading.setZIndex(ZIndexes.LOADING_WIDGET);
		loading.hide();
		loading.redraw();
	}

	public void showPlayButton(String queryValid) {
		if (queryValid.equals("1")) {
			queryButton.setSrc("icons/custom/start.png");
		} else {
			queryButton.setSrc("icons/custom/start-error.png");
		}
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
	
	public void onQueryStart() {
		queryButton.hide();
		queryLoading.show();
	}
	
	public void onQueryFinish() {
		queryButton.show();
		queryLoading.hide();
	}
	
	public void onQueryError(String error) {
		onQueryFinish();
		onQueryError(error, view.getSelectedTabSettings().getEndpoint(), view.getSelectedTabSettings().getQueryString());
	}
	
	/**
	 * Modal popup window to show on error
	 * 
	 * @param error
	 */
	public void onError(String error) {
		onLoadingFinish();
		Window window = getErrorWindow();
		Label label = new Label(error);
		label.setMargin(4);
		label.setHeight100();
		window.addItem(label);
		window.draw();
	}
	
	private Window getErrorWindow() {
		final Window window = new Window();
		window.setIsModal(true);
		window.setZIndex(ZIndexes.MODAL_WINDOWS);
		window.setAutoSize(true);
		window.setMinWidth(400);
		window.setShowMinimizeButton(false);
		window.setAutoCenter(true);
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
	 * Display error when querying endpoint failed. Has buttons for opening query result page of endpoint itself on new page
	 * 
	 * @param error Html error msg
	 * @param endpoint Used endpoint
	 * @param query Used query
	 */
	public void onQueryError(String error, final String endpoint, final String query) {
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
		buttons.setAlign(Alignment.CENTER);
		Button executeQuery = new Button("Open endpoint in new window");
		executeQuery.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
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
	

	/**
	 * Show the error window for a trowable. Prints the complete stack trace
	 * @param throwable
	 */
	public void onError(Throwable e) {
		onLoadingFinish();
		String stackTraceString = Helper.getStackTraceAsString(e);
		stackTraceString += Helper.getCausesStackTraceAsString(e);
		onError(stackTraceString);
	}
	
	public void checkHtml5() {
		if (LocalStorageHelper.newUser()) {
			LocalStorageHelper.setHtml5Checked();
			boolean html5 = Storage.isSupported();
			if (!html5) {
				onError("Your browser does not support html5. This website will function slower without html5.<br><br> Try browsers such as Chrome 4+, Firefox 4+, Safari 4+ and Internet Explorer 8+ for better performance");
			}
			GoogleAnalyticsEvent event = new GoogleAnalyticsEvent("html5", (html5? "1": "0"));
			GoogleAnalytics.trackEvents(event);
		}
	}
	
	public void addLogo() {
		HTMLFlow html = new HTMLFlow();
		html.setContents("<span style=\"font-family: 'Audiowide'; font-size: 35px;cursor:pointer;\" onclick=\"window.open('" + Footer.GITHUB_LINK +  "')\">YASGUI</span>");
		html.getElement().getStyle().setPosition(Position.ABSOLUTE);
		html.getElement().getStyle().setTop(4, Unit.PX);
		html.getElement().getStyle().setRight(8, Unit.PX);
		html.getElement().getStyle().setCursor(Cursor.POINTER);
		html.getElement().getStyle().setZIndex(ZIndexes.LOGO);
		html.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				com.google.gwt.user.client.Window.open(Footer.GITHUB_LINK, "_blank", "");
			}});
		html.setWidth(150);
		if (html.isDrawn()) {
			html.redraw();
		} else {
			html.draw();
		}
	}
	
}
