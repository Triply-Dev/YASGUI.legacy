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

import com.data2semantics.yasgui.client.ConnectivityHelper.ConnCallback;
import com.data2semantics.yasgui.client.configmenu.Compatibilities;
import com.data2semantics.yasgui.client.configmenu.ConfigMenu;
import com.data2semantics.yasgui.client.helpers.GoogleAnalytics;
import com.data2semantics.yasgui.client.helpers.GoogleAnalyticsEvent;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.helpers.SparqlQuery;
import com.data2semantics.yasgui.client.helpers.TooltipProperties;
import com.data2semantics.yasgui.client.settings.ExternalLinks;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.settings.TooltipText;
import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.data2semantics.yasgui.client.tab.optionbar.LinkCreator;
import com.data2semantics.yasgui.shared.exceptions.ElementIdException;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.IconMenuButton;

public class ViewElements implements RpcElement {
	private static int TOOLTIP_VERSION_MENU_CONFIG = 7;
	private View view;
	private ImgButton queryButton;
	private ImgButton queryLoading;
	private LinkCreator linkCreator;
	public IconMenuButton configButton;
	public static String CONFIG_MENU_LABEL = "Configure YASGUI";
	private static int QUERY_BUTTON_POS_TOP = 5;
	private static int QUERY_BUTTON_POS_RIGHT = 2;
	private static int QUERY_BUTTON_HEIGHT = 48;
	private static int QUERY_BUTTON_WIDTH = 48;
	private static int CONSENT_WINDOW_HEIGHT = 130;
	private static int CONSENT_WINDOW_WIDTH = 750;
	private static int CONSENT_BUTTON_HEIGHT = 40;
	private static int CONSENT_BUTTON_WIDTH = 175;
	private Window consentWindow;
	private HLayout offlineNotification;
	private ConfigMenu configMenu;
	
	public ViewElements(View view) {
		this.view = view;
		addQueryButton();
		addLogo();
		drawConfigMenu();
		checkOpera();
		initOfflineNotification();
	}
	

	
	/**
	 * Add Query button. Position absolute, as it hovers slightly over the tabbar. Also adds a loading icon on the same place
	 */
	public void addQueryButton() {
		queryLoading = getQueryIcon(Imgs.LOADING.get(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				cancelQuery();
			}
		});
		queryLoading.setTooltip("cancel query");
		queryLoading.hide();
		
		if (queryLoading.isDrawn()) {
			queryLoading.redraw();
		} else {
			queryLoading.draw();
		}
		
		queryButton = getQueryIcon(Imgs.EXECUTE_QUERY.get(), new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SparqlQuery.exec(view);
			}
		});
		queryButton.setTooltip("execute query");
		if (queryButton.isDrawn()) {
			queryButton.redraw();
		} else {
			queryButton.draw();
		}
	}
	
	
	private ImgButton getQueryIcon(String icon, ClickHandler clickHandler) {
		final ImgButton imgButton = new ImgButton();
		imgButton.setHeight(QUERY_BUTTON_HEIGHT);
		imgButton.setWidth(QUERY_BUTTON_WIDTH);
		imgButton.setSrc(icon);
		imgButton.setShowRollOver(false);
		imgButton.setShowDown(false);
		imgButton.setShowOverCanvas(false);
		imgButton.addClickHandler(clickHandler);
		imgButton.getElement().getStyle().setPosition(Position.ABSOLUTE);
		imgButton.getElement().getStyle().setTop(QUERY_BUTTON_POS_TOP, Unit.PX);
		imgButton.getElement().getStyle().setRight(QUERY_BUTTON_POS_RIGHT, Unit.PX);
		imgButton.setCursor(com.smartgwt.client.types.Cursor.POINTER);
		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				//tricky: we need a z-index larger than the tabset (otherwise button is not clickable),
				//and we need one smaller than the config menu (otherwise the overlay is wrong)
				//therefore set dynamically. we use a scheduled method, as at the time of drawing the 
				//query button, the tabs arent drawn yet
				imgButton.getElement().getStyle().setZIndex(view.getTabs().getZIndex()+1);
			}
		});
		return imgButton;
	}
	
	/**
	 * Cancel query request, en redraw query icon
	 */
	public void cancelQuery() {
		JsMethods.cancelQuery();
		onQueryFinish();
	}
	
	public void showPlayButton(String queryValid) {
		if (queryValid.equals("1")) {
			queryButton.setSrc(Imgs.EXECUTE_QUERY.get());
			queryButton.setTooltip("execute query");
		} else {
			queryButton.setSrc(Imgs.QUERY_ERROR.get());
			queryButton.setTooltip("invalid query, click to execute anyway");
		}
	}
	
	public void onQueryStart() {
		queryButton.hide();
		queryLoading.show();
	}
	
	public void onQueryFinish() {
		queryButton.show();
		queryLoading.hide();
	}
	
	public void addLogo() {
		HTMLFlow html = getYasguiLogo(31, "Show YASGUI page", "mainYasguiLogo");
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
	
	public HTMLFlow getYasguiLogo(int fontSize, String title, String id) {
		HTMLFlow html = new HTMLFlow();
		html.setID(id);
		html.setContents("<span title='" + title + "' style=\"font-family: 'Audiowide'; font-size: " + fontSize + "px;cursor:pointer;\" onclick=\"window.open('" + ExternalLinks.YASGUI_HTML +  "')\">YASGUI</span>");
		html.setWidth(140);
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
        yesButton.setShowRollOver(false);  
        yesButton.setHeight(CONSENT_BUTTON_HEIGHT);
        yesButton.setIcon(Imgs.CHECKMARK.get());
        yesButton.setIconOrientation("left");
        yesButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GoogleAnalyticsEvent consentEvent = new GoogleAnalyticsEvent("consent", "yes");
				GoogleAnalytics.trackEvent(consentEvent);
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
		noQueriesButton.setShowRollOver(false);  
		noQueriesButton.setIcon(Imgs.CHECK_CROSS.get());
		noQueriesButton.setIconOrientation("left");  
		noQueriesButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GoogleAnalyticsEvent consentEvent = new GoogleAnalyticsEvent("consent", "yes/no");
				GoogleAnalytics.trackEvent(consentEvent);
				consentWindow.destroy();
				view.getSettings().setTrackingConsent(true);
				view.getSettings().setTrackingQueryConsent(false);
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
			}});
		noQueriesButton.setShowDownIcon(false);
        
        LayoutSpacer spacer2 = new LayoutSpacer();
        spacer2.setWidth(10);
        
		IButton noButton = new IButton("No, disable tracking");  
		noButton.setShowRollOver(false);  
		noButton.setWidth(CONSENT_BUTTON_WIDTH);
		noButton.setHeight(CONSENT_BUTTON_HEIGHT);
		noButton.setIcon(Imgs.CROSS.get());
		noButton.setIconOrientation("left");  
		noButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GoogleAnalyticsEvent consentEvent = new GoogleAnalyticsEvent("consent", "no");
				GoogleAnalytics.trackEvent(consentEvent);
				consentWindow.destroy();
				view.getSettings().setTrackingConsent(false);
				view.getSettings().setTrackingQueryConsent(false);
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
			}});
		noButton.setShowDownIcon(false); 
		
        LayoutSpacer spacer3 = new LayoutSpacer();
        spacer3.setWidth(10);
		
		IButton askLater = new IButton("Ask me later");  
		askLater.setShowRollOver(false);  
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
		Compatibilities compatibilities = new Compatibilities(view);
		String icon  = "";
		if (!compatibilities.allSupported() && LocalStorageHelper.getCompatibilitiesShownVersionNumber() < Compatibilities.VERSION_NUMBER) {
			icon = Imgs.WARNING.get();
		} else {
			icon = Imgs.TOOLS.get();
		}
		
		String label = CONFIG_MENU_LABEL;
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
		configMenu = new ConfigMenu(view);
		configButton.setMenu(configMenu);
		configButton.setCanFocus(false);
		configButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				configButton.showMenu();
			}
			
		});
		if (configButton.isDrawn()) {
			configButton.bringToFront();
			configButton.redraw();
		} else {
			configButton.bringToFront();
			configButton.draw();
		}
		
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
			tProp.set(TooltipText.CONFIG_MENU);
			tProp.setMy(TooltipProperties.POS_RIGHT_TOP);
			tProp.setAt(TooltipProperties.POS_LEFT_TOP);
			Helper.drawTooltip(tProp);
		}
	}

	public void showTooltips(int fromVersionId) {
		showConfigMenuTooltip(fromVersionId);
	}
	
	private void checkOpera() {
		//the newest opera version (15) uses webkit, works on yasgui, and is identified as 'chrome'.
		//All incompatible opera versions are 0-14
		//therefore, just check whether we have as name 'opera'
		if (JsMethods.getBrowserName().equals("opera")) {
			view.getErrorHelper().onError("You are using an opera browser. Users are known to encounter issues in YASGUI using this browser. <br>" +
					"We recommend you switch to Opera 15+, or to any other modern browser");
		}
	}



	public void disableRpcElements() {
		configMenu.disableRpcElements();
	}
	public void enableRpcElements() {
		configMenu.enableRpcElements();
	}
	
	public void initOfflineNotification() {
		offlineNotification = new HLayout();
		offlineNotification.setWidth(320);
		offlineNotification.setHeight(30);
		offlineNotification.getElement().getStyle().setPosition(Position.ABSOLUTE);
		offlineNotification.getElement().getStyle().setTop(0, Unit.PX);
		offlineNotification.getElement().getStyle().setLeft(150, Unit.PX);
		offlineNotification.setZIndex(ZIndexes.LOADING_WIDGET);
		offlineNotification.setBackgroundColor("#f0f0f0");
		offlineNotification.setBorder("1px solid #C0C3C7");
		Img disconnectedImg = new Img(Imgs.DISCONNECTED.get());
		disconnectedImg.setHeight(30);
		disconnectedImg.setWidth(30);
		
		Label offlineText = new Label();
		offlineText.setHeight(20);
		offlineText.setWidth(250);
		offlineText.setContents("YASGUI server is unreachable. YASGUI will still work on most localhost (CORS-enabled) endpoints");
		offlineText.setLayoutAlign(VerticalAlignment.CENTER);
		Button tryConnectButton = new Button("Try to reconnect");
		tryConnectButton.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				view.getConnHelper().checkOnlineStatus(new ConnCallback() {
					
					@Override
					public void connectedCallback() {
						//do nothing
					}
				});
				
			}});
		tryConnectButton.setLayoutAlign(VerticalAlignment.CENTER);
		
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setWidth(5);
		offlineNotification.setMembers(disconnectedImg, offlineText, tryConnectButton, spacer);
		offlineNotification.hide();
		offlineNotification.redraw();
	}


	public void showOfflineNotification(boolean isOnline) {
		if (isOnline) {
			offlineNotification.hide();
		} else {
			offlineNotification.show();
		}
	}
	public IconMenuButton getConfigMenu() {
		return configButton;
	}
	
}
