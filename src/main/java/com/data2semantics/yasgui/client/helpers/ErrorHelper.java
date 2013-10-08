package com.data2semantics.yasgui.client.helpers;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.configmenu.ReportIssue;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.google.gwt.http.client.URL;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class ErrorHelper {
	private View view;
	public ErrorHelper(View view) {
		this.view = view;
	}
	

	public void onQueryError(String tabId, String error) {
		if (view.getElements() != null) {
			view.getElements().onQueryFinish();
		}
		QueryTab tab = (QueryTab)view.getTabs().getTab(tabId);
		onQueryError(error, tab.getTabSettings().getEndpoint(), tab.getTabSettings().getQueryString(), tab.getTabSettings().getQueryArgsAsUrlString());
	}
	
	/**
	 * Show the error window for a trowable. Prints the complete stack trace
	 * @param throwable
	 */
	public void onError(Throwable e) {
		onError(e, view.getSettings().bugReportsSupported());
	}
	
	private void onError(final Throwable t, boolean allowBugReport) {
		if (view.getElements() != null) {
			view.getElements().onLoadingFinish();
		}
		if (allowBugReport) {
			if (view.getElements() != null) {
				view.getElements().onLoadingFinish();
			}
			final Window window = getErrorWindow();
			VLayout content = new VLayout();
			content.setWidth100();
			content.addMember(getErrorLabel(getErrorMsg(t)));
			
			IButton debugButton = new IButton("Report this as a bug");
			debugButton.setIcon(Imgs.BUG.get());
			debugButton.setWidth(140);
			debugButton.addClickHandler(new ClickHandler(){
				@Override
				public void onClick(ClickEvent event) {
					window.destroy();
					ReportIssue.report(view, t);
				}});
			content.addMember(debugButton);
			window.addItem(content);
			window.draw();
		} else {
			onError(getErrorMsg(t));
		}
		Helper.logExceptionToServer(t);
	}
	
	private void drawErrorWindow(Canvas content) {
		Window window = getErrorWindow();
		window.addItem(content);
		window.draw();
	}
	private Label getErrorLabel(String error) {
		Label label = new Label(error);
		label.setMargin(4);
		label.setHeight100();
		return label;
	}
	
	/**
	 * Modal popup window to show on error
	 * 
	 * @param error
	 */
	public void onError(String error) {
		if (view.getElements() != null) {
			view.getElements().onLoadingFinish();
		}
		drawErrorWindow(getErrorLabel(error));
	}
	
	private Window getErrorWindow() {
		final Window window = new Window();
		window.setID("errorWindow");
		window.setDismissOnOutsideClick(true);
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
	public void onQueryError(String error, final String endpoint, final String query, final String argsString) {
		final Window window = getErrorWindow();
		window.setWidth(350);
		window.setZIndex(ZIndexes.MODAL_WINDOWS);
		VLayout vLayout = new VLayout();
		vLayout.setWidth100();
		Label label = new Label();
		label.setID("queryErrorMessage");
		label.setContents(error);
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
				String url = endpoint + "?query=" + URL.encodeQueryString(query) + argsString;
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
		window.draw();
	}
	public static String getErrorMsg(Throwable e) {
		String errorMsg;
		if (Helper.inDebugMode()) {
			errorMsg = Helper.getStackTraceAsString(e);
			errorMsg += "\nCaused by:\n" + Helper.getCausesStackTraceAsString(e);
		} else {
			errorMsg = e.getMessage();
		}
		return errorMsg;
	}
}