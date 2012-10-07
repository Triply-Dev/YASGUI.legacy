package com.data2semantics.yasgui.client;

import com.data2semantics.yasgui.client.helpers.GoogleAnalytics;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.properties.ZIndexes;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.http.client.URL;
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
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class ViewElements {
	private View view;
	private ImgButton queryButton;
	private Img queryLoading;
	public static String DEFAULT_LOADING_MESSAGE = "Loading...";
	private static int QUERY_BUTTON_POS_TOP = 5;
	private static int QUERY_BUTTON_POS_LEFT = 5;
	private Label loading;
	public ViewElements(View view) {
		this.view = view;
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
				JsMethods.queryJson(tabId, view.getSelectedTabSettings().getQueryString(), endpoint);
				view.checkAndAddEndpointToDs(endpoint);
				GoogleAnalytics.trackEvent("interaction", "query", endpoint);
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
	 * initialize loading widget in top right corner
	 */
	public void initLoadingWidget() {
		loading = new Label();
		loading.setIcon("loading.gif");
		loading.setBackgroundColor("#f0f0f0");
		loading.setBorder("1px solid #C0C3C7");
		loading.getElement().getStyle().setPosition(Position.ABSOLUTE);
		loading.getElement().getStyle().setTop(0, Unit.PX);
		loading.getElement().getStyle().setRight(0, Unit.PX);
		loading.setHeight(30);
		loading.setAutoWidth();
		loading.setOverflow(Overflow.VISIBLE);
		loading.setWrap(false);
		loading.setAlign(Alignment.CENTER);
		loading.adjustForContent(false);
		loading.setZIndex(ZIndexes.LOADING_WIDGET);
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
	
	public void onQueryStart() {
		queryButton.hide();
		queryLoading.show();
	}
	
	public void onQueryFinish() {
		queryButton.show();
		queryLoading.hide();
	}
	
	public void onQueryError(String error) {
		onLoadingFinish();
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
	
}
