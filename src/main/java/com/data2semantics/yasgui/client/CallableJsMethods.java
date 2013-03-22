package com.data2semantics.yasgui.client;

import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.tab.QueryTab;

public class CallableJsMethods {
	private View view;
	public CallableJsMethods(View view) {
		this.view = view;
		declareCallableViewMethods(this);
	}
	
	public void cancelQuery() {
		view.getElements().cancelQuery();
	}
	
	public void storeSettings() {
		LocalStorageHelper.storeSettingsInCookie(view.getSettings());
	}
	
	public void setQueryType(String queryType) {
		view.getSelectedTab().setQueryType(queryType);
	}
	public void adjustQueryInputForContent() {
		view.getSelectedTab().getQueryTextArea().adjustForContent(true);
	}
	
	/**
	 * Get query string from text area, set it in settings, and store in cookie
	 */
	public void storeQueryInCookie() {
		String query = view.getSelectedTab().getQueryTextArea().getQuery();
		view.getSelectedTabSettings().setQueryString(query);
		LocalStorageHelper.storeSettingsInCookie(view.getSettings());
	}
	
	/**
	 * Clear current query result table 
	 * Keep this method in the view object, so that it is easily callable from js
	 */
	public void resetQueryResult() {
		view.getSelectedTab().getResultContainer().reset();
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
		QueryTab tab = (QueryTab)view.getTabs().getTab(tabId);
		view.getTabs().selectTab(tabId);
		if (tab == null) {
			view.getElements().onError("No tab to draw results in");
		}
		tab.getResultContainer().processResult(resultString, contentType);
	}

	public void onError(String error) {
		view.getElements().onError(error);
	}
	
	
	public void onError(Throwable e) {
		view.getElements().onError(e);
	}
	
	public void onLoadingStart(String message) {
		view.getElements().onLoadingStart(message);
	}
	
	public void onLoadingFinish() {
		view.getElements().onLoadingFinish();
	}
	
	public void onQueryStart() {
		view.getElements().onQueryStart();
	}
	
	public void onQueryFinish() {
		view.getElements().onQueryFinish();
	}
	
	public void onQueryError(String message) {
		view.getElements().onQueryError(message);
	}
	
	public void showPlayButton(String queryValid) {
		view.getElements().showPlayButton(queryValid);
	}
	
	public void saveTabTitle() {
		view.getTabs().saveTabTitle();
	}
	
	/**
	 * Add view methods to JS, use this for situations where a non-static GWT method needs to be called
	 * 
	 * @param view
	 */
	public static native void declareCallableViewMethods(CallableJsMethods viewJs) /*-{
		var view = view; 
		$wnd.drawResults = function(tabId, resultString, contentType) {
			viewJs.@com.data2semantics.yasgui.client.CallableJsMethods::drawResults(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(tabId, resultString, contentType);
		}
		$wnd.onError = function(errorMsg) {
			viewJs.@com.data2semantics.yasgui.client.CallableJsMethods::onError(Ljava/lang/String;)(errorMsg);
		}
		$wnd.onLoadingStart = function(message) {
			viewJs.@com.data2semantics.yasgui.client.CallableJsMethods::onLoadingStart(Ljava/lang/String;)(message);
		}
		$wnd.onLoadingFinish = function() {
			viewJs.@com.data2semantics.yasgui.client.CallableJsMethods::onLoadingFinish()();
		}
		$wnd.onQueryStart = function() {
			viewJs.@com.data2semantics.yasgui.client.CallableJsMethods::onQueryStart()();
		}
		$wnd.onQueryFinish = function() {
			viewJs.@com.data2semantics.yasgui.client.CallableJsMethods::onQueryFinish()();
		}
		$wnd.clearQueryResult = function() {
			viewJs.@com.data2semantics.yasgui.client.CallableJsMethods::resetQueryResult()();
		}
		$wnd.storeQueryInCookie = function() {
			viewJs.@com.data2semantics.yasgui.client.CallableJsMethods::storeQueryInCookie()();
		}
		$wnd.onQueryError = function(errorMsg) {
			viewJs.@com.data2semantics.yasgui.client.CallableJsMethods::onQueryError(Ljava/lang/String;)(errorMsg);
		}
		$wnd.showPlayButton = function(queryValid) {
			viewJs.@com.data2semantics.yasgui.client.CallableJsMethods::showPlayButton(Ljava/lang/String;)(queryValid);
		}
		$wnd.adjustQueryInputForContent = function() {
			viewJs.@com.data2semantics.yasgui.client.CallableJsMethods::adjustQueryInputForContent()();
		}
		$wnd.setQueryType = function(queryType) {
			viewJs.@com.data2semantics.yasgui.client.CallableJsMethods::setQueryType(Ljava/lang/String;)(queryType);
		}
		$wnd.storeSettings = function() {
			viewJs.@com.data2semantics.yasgui.client.CallableJsMethods::storeSettings()();
		}
		$wnd.cancelQuery = function() {
			viewJs.@com.data2semantics.yasgui.client.CallableJsMethods::cancelQuery()();
		}
		$wnd.saveTabTitle = function() {
			viewJs.@com.data2semantics.yasgui.client.CallableJsMethods::saveTabTitle()();
		}
	}-*/;
}
