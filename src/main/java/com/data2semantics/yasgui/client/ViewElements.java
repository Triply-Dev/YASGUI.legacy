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

import java.util.HashMap;
import java.util.Map.Entry;

import com.data2semantics.yasgui.client.helpers.GoogleAnalytics;
import com.data2semantics.yasgui.client.helpers.GoogleAnalyticsEvent;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.helpers.TooltipProperties;
import com.data2semantics.yasgui.client.settings.ExternalLinks;
import com.data2semantics.yasgui.client.settings.Icons;
import com.data2semantics.yasgui.client.settings.TooltipText;
import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.data2semantics.yasgui.client.tab.optionbar.LinkCreator;
import com.data2semantics.yasgui.shared.exceptions.ElementIdException;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.URL;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.IconMenuButton;

public class ViewElements {
	private static int TOOLTIP_VERSION_MENU_CONFIG = 7;
	private View view;
	private Img queryButton;
	private ImgButton queryLoading;
	private LinkCreator linkCreator;
	public IconMenuButton configButton;
	public static String DEFAULT_LOADING_MESSAGE = "Loading...";
	private static int QUERY_BUTTON_POS_TOP = 5;
	private static int QUERY_BUTTON_POS_RIGHT = 2;
	private static int CONSENT_WINDOW_HEIGHT = 130;
	private static int CONSENT_WINDOW_WIDTH = 750;
	private static int CONSENT_BUTTON_HEIGHT = 40;
	private static int CONSENT_BUTTON_WIDTH = 175;
	private Window consentWindow;
	private Label loading;
	
	public ViewElements(View view) {
		this.view = view;
		addQueryButton();
		addLogo();
		initLoadingWidget();
		drawConfigMenu();
	}
	

	
	/**
	 * Add Query button. Position absolute, as it hovers slightly over the tabbar. Also adds a loading icon on the same place
	 */
	public void addQueryButton() {
		queryButton = new ImgButton();
		
		queryButton.setHeight(48);
		queryButton.setWidth(48);
		queryButton.setSrc(Icons.EXECUTE_QUERY);
		queryButton.setShowRollOver(false);
		queryButton.setShowDown(false);
		queryButton.setZIndex(ZIndexes.TAB_CONTROLS);
		queryButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				executeQuery();
			}
		});
		queryButton.getElement().getStyle().setPosition(Position.ABSOLUTE);
		queryButton.getElement().getStyle().setTop(QUERY_BUTTON_POS_TOP, Unit.PX);
		queryButton.getElement().getStyle().setRight(QUERY_BUTTON_POS_RIGHT, Unit.PX);
//		queryButton.getElement().getStyle().setCursor(Cursor.POINTER);
		queryButton.setCursor(com.smartgwt.client.types.Cursor.POINTER);
		
		if (queryButton.isDrawn()) {
			queryButton.redraw();
		} else {
			queryButton.draw();
		}
		

		queryLoading = new ImgButton();
		queryLoading.setSrc(Icons.LOADING);
		queryLoading.hide();
		queryLoading.setHeight(48);
		queryLoading.setWidth(48);
		queryLoading.setZIndex(ZIndexes.TAB_CONTROLS);
		queryLoading.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				cancelQuery();
			}
		});
		queryLoading.setShowRollOver(false);
		queryLoading.setShowDown(false);
		queryLoading.getElement().getStyle().setPosition(Position.ABSOLUTE);
		queryLoading.getElement().getStyle().setTop(QUERY_BUTTON_POS_TOP, Unit.PX);
		queryLoading.getElement().getStyle().setRight(QUERY_BUTTON_POS_RIGHT, Unit.PX);
		queryLoading.getElement().getStyle().setCursor(Cursor.POINTER);
		if (queryLoading.isDrawn()) {
			queryLoading.redraw();
		} else {
			queryLoading.draw();
		}
		
	}
	
	/**
	 * Cancel query request, en redraw query icon
	 */
	public void cancelQuery() {
		JsMethods.cancelQuery();
		onQueryFinish();
	}
	
	/**
	 * execute query
	 */
	public void executeQuery() {
		if (JsMethods.stringToDownloadSupported()) {
			view.getSelectedTab().getDownloadLink().showDisabledIcon();
		}
		//onblur might not always fire (will have to check that). for now, store query in settings before query execution just to be sure
		view.getCallableJsMethods().storeQueryInCookie();
		
		String tabId = view.getSelectedTab().getID();
		String endpoint = view.getSelectedTabSettings().getEndpoint();
		String queryString = view.getSelectedTabSettings().getQueryString();
		
		String mainAcceptHeader;
		if (view.getSelectedTab().getQueryType().equals("CONSTRUCT")) {
			//Change content type automatically for construct queries
			mainAcceptHeader = view.getSelectedTabSettings().getConstructContentType();
		} else {
			mainAcceptHeader = view.getSelectedTabSettings().getSelectContentType();
		}
		String acceptHeaders = Helper.getAcceptHeaders(mainAcceptHeader);
	
		String argsString = view.getSelectedTabSettings().getQueryArgsAsJsonString();
		String requestMethod = view.getSelectedTabSettings().getRequestMethod();
		
		
		
		JsMethods.query(tabId, queryString, endpoint, acceptHeaders, argsString, requestMethod);
		view.checkAndAddEndpointToDs(endpoint);
		if (view.getSettings().useGoogleAnalytics() && view.getSettings().getTrackingQueryConsent()) {
			GoogleAnalyticsEvent endpointEvent = new GoogleAnalyticsEvent("sparql", "endpoint");
			endpointEvent.setOptLabel(endpoint);
			GoogleAnalyticsEvent queryEvent = new GoogleAnalyticsEvent("sparql", "query");
			queryEvent.setOptLabel(queryString);
			GoogleAnalytics.trackEvents(endpointEvent, queryEvent);
		}
	}
	
	/**
	 * initialize loading widget in top right corner
	 */
	public void initLoadingWidget() {
		loading = new Label();
		loading.setIcon(Icons.LOADING);
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
			queryButton.setSrc(Icons.EXECUTE_QUERY);
			queryButton.setShowOverCanvas(true);
		} else {
			queryButton.setSrc(Icons.QUERY_ERROR);
			queryButton.setShowOverCanvas(true);
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
	
	public void onQueryError(String tabId, String error) {
		onQueryFinish();
		QueryTab tab = (QueryTab)view.getTabs().getTab(tabId);
		onQueryError(error, tab.getTabSettings().getEndpoint(), tab.getTabSettings().getQueryString(), tab.getTabSettings().getQueryArgs());
	}
	
	/**
	 * Show the error window for a trowable. Prints the complete stack trace
	 * @param throwable
	 */
	public void onError(Throwable e) {
		String errorMsg;
		
		if (Helper.inDebugMode()) {
			errorMsg = Helper.getStackTraceAsString(e);
			errorMsg += "\nCaused by:\n" + Helper.getCausesStackTraceAsString(e);
		} else {
			errorMsg = e.getMessage();
		}
		
		onError(errorMsg);
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
		window.setIsModal(true);
		window.setDismissOnOutsideClick(true);
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
	 * @param args 
	 */
	public void onQueryError(String error, final String endpoint, final String query, final HashMap<String, String> args) {
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
				for (Entry<String, String> entry : args.entrySet()) {
				    url += "&" + entry.getKey() + "=" + URL.encodeQueryString(entry.getValue());
				}
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

	
	public void addLogo() {
		HTMLFlow html = getLogo(31, "Show YASGUI page");
		html.getElement().getStyle().setPosition(Position.ABSOLUTE);
		html.getElement().getStyle().setTop(-2, Unit.PX);
		html.getElement().getStyle().setLeft(4, Unit.PX);
		html.getElement().getStyle().setCursor(Cursor.POINTER);
		html.getElement().getStyle().setZIndex(ZIndexes.LOGO);
		
		if (html.isDrawn()) {
			html.redraw();
		} else {
			html.draw();
		}
	}
	
	public HTMLFlow getLogo(int fontSize, String title) {
		HTMLFlow html = new HTMLFlow();
		html.setContents("<span title='" + title + "' style=\"font-family: 'Audiowide'; font-size: " + fontSize + "px;cursor:pointer;\" onclick=\"window.open('" + ExternalLinks.YASGUI_HTML +  "')\">YASGUI</span>");
		html.setWidth(100);
		html.setHeight(30);
		return html;
	}

	public void askCookieConsent() {
		consentWindow = new Window();
		int pageWidth = com.google.gwt.user.client.Window.getClientWidth();
		int pageHeight = com.google.gwt.user.client.Window.getClientHeight();
		
		
		consentWindow.setRect((pageWidth / 2) - (CONSENT_WINDOW_WIDTH / 2), pageHeight - CONSENT_WINDOW_HEIGHT, CONSENT_WINDOW_WIDTH, CONSENT_WINDOW_HEIGHT);
		
		VLayout windowCanvas = new VLayout();
		
		HTMLFlow consentMessage = new HTMLFlow();
		consentMessage.setContents("<p style='text-align:center; margin:0px;'>We track user actions (including used endpoints and queries). This data is solely used for research purposes and to get insight into how users use the site. <strong>We would appreciate your consent!</strong></p>");
		
		consentMessage.setMargin(8);
		consentMessage.setWidth(CONSENT_WINDOW_WIDTH - 20);
		consentMessage.setAlign(Alignment.CENTER);
		windowCanvas.addMember(consentMessage);
		
		LayoutSpacer vSpacer = new LayoutSpacer();
		vSpacer.setHeight100();
		windowCanvas.addMember(vSpacer);
		
		HLayout buttons = new HLayout();
		buttons.setAlign(Alignment.CENTER);
		IButton yesButton = new IButton("Yes, allow");  
		yesButton.setWidth(CONSENT_BUTTON_WIDTH);
        yesButton.setShowRollOver(true);  
        yesButton.setHeight(CONSENT_BUTTON_HEIGHT);
        yesButton.setIcon(Icons.CHECKMARK);
        yesButton.setIconOrientation("left");
        yesButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				consentWindow.destroy();
				view.getSettings().setTrackingConsent(true);
				view.getSettings().setTrackingQueryConsent(true);
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
			}});
        yesButton.setShowDownIcon(false);
		
        LayoutSpacer spacer1 = new LayoutSpacer();
        spacer1.setWidth(10);
        
		buttons.setAlign(Alignment.CENTER);
		IButton noQueriesButton = new IButton("Yes, track site usage, but not <br>the queries/endpoints I use");  
		noQueriesButton.setWidth(CONSENT_BUTTON_WIDTH);
		noQueriesButton.setHeight(CONSENT_BUTTON_HEIGHT);
		noQueriesButton.setShowRollOver(true);  
		noQueriesButton.setIcon(Icons.CHECK_CROSS);
		noQueriesButton.setIconOrientation("left");  
		noQueriesButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				consentWindow.destroy();
				view.getSettings().setTrackingConsent(true);
				view.getSettings().setTrackingQueryConsent(false);
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
			}});
		noQueriesButton.setShowDownIcon(false);
        
        LayoutSpacer spacer2 = new LayoutSpacer();
        spacer2.setWidth(10);
        
		IButton noButton = new IButton("No, disable tracking");  
		noButton.setShowRollOver(true);  
		noButton.setWidth(CONSENT_BUTTON_WIDTH);
		noButton.setHeight(CONSENT_BUTTON_HEIGHT);
		noButton.setIcon(Icons.CROSS);
		noButton.setIconOrientation("left");  
		noButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				consentWindow.destroy();
				view.getSettings().setTrackingConsent(false);
				view.getSettings().setTrackingQueryConsent(false);
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
			}});
		noButton.setShowDownIcon(false); 
		
        LayoutSpacer spacer3 = new LayoutSpacer();
        spacer3.setWidth(10);
		
		IButton askLater = new IButton("Ask me later");  
		askLater.setShowRollOver(true);  
		askLater.setWidth(CONSENT_BUTTON_WIDTH - 30);
		askLater.setHeight(CONSENT_BUTTON_HEIGHT);
		askLater.setIconOrientation("left");  
		askLater.setShowDownIcon(false); 
		askLater.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				consentWindow.destroy();
			}});
		
        buttons.addMembers(yesButton, spacer1, noQueriesButton, spacer2, noButton, spacer3, askLater);
        buttons.setMargin(5);
        windowCanvas.addMember(buttons);
		consentWindow.setTitle("Tracking site usage");
		
		consentWindow.addItem(windowCanvas);
		consentWindow.draw();
	}
	
	public void drawOptionsInQueryField() {
		linkCreator = new LinkCreator(view);
		com.google.gwt.user.client.Window.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				linkCreator.changeHorizontalOffset();
			}
			
		});
		linkCreator.changeHorizontalOffset();
		linkCreator.draw();
	}
	
	/**
	 * Draw main YASGUI configuration menu
	 */
	public void drawConfigMenu() {
		Compatabilities compatabilities = new Compatabilities(view);
		String icon  = "";
		if (!compatabilities.allSupported() && LocalStorageHelper.getCompatabilitiesShownVersionNumber() < Compatabilities.VERSION_NUMBER) {
			icon = Icons.WARNING;
		} else {
			icon = Icons.TOOLS;
		}
		
		String label = "Configure YASGUI";
		if (!view.getSettings().isDbSet()) {
			//openid not supported. leave label as-is
		} else if (view.getOpenId() != null && view.getOpenId().isLoggedIn()) {
			label += " (" + view.getOpenId().getDisplayName() + ")";
		} else {
			label += " (not logged in)";
		}
		
		configButton = new IconMenuButton(label);
		configButton.getElement().getStyle().setPosition(Position.ABSOLUTE);
		configButton.getElement().getStyle().setTop(2, Unit.PX);
		configButton.getElement().getStyle().setRight(50, Unit.PX);
		configButton.setIcon(icon);
		configButton.setZIndex(ZIndexes.TAB_CONTROLS);
		configButton.setMenu(new ConfigMenu(view));
		configButton.setCanFocus(false);
		configButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				configButton.showMenu();
			}
			
		});
		configButton.draw();
	}
	
	/**
	 * Redraw configuration menu
	 */
	public void redrawConfigMenu() {
		configButton.destroy();
		configButton = null;
		drawConfigMenu();
	}
	
	private void showConfigMenuTooltip(int fromVersionId) throws ElementIdException {
		if (fromVersionId < TOOLTIP_VERSION_MENU_CONFIG) {
			TooltipProperties tProp = new TooltipProperties();
			tProp.setId(configButton.getDOM().getId());
			tProp.setContent(TooltipText.CONFIG_MENU);
			tProp.setMy(TooltipProperties.POS_RIGHT_TOP);
			tProp.setAt(TooltipProperties.POS_LEFT_TOP);
			Helper.drawTooltip(tProp);
		}
	}

	public void showTooltips(int fromVersionId) {
		showConfigMenuTooltip(fromVersionId);
	}
	
	
	
}
