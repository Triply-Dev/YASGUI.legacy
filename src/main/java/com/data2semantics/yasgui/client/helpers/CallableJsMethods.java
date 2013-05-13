package com.data2semantics.yasgui.client.helpers;

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

import com.data2semantics.yasgui.client.View;
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
	
	public void executeQuery() {
		view.getElements().executeQuery();
	}
	
	public void onQueryError(String tabId, String message) {
		view.getElements().onQueryError(tabId, message);
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
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::drawResults(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(tabId, resultString, contentType);
		}
		$wnd.onError = function(errorMsg) {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::onError(Ljava/lang/String;)(errorMsg);
		}
		$wnd.onLoadingStart = function(message) {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::onLoadingStart(Ljava/lang/String;)(message);
		}
		$wnd.onLoadingFinish = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::onLoadingFinish()();
		}
		$wnd.onQueryStart = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::onQueryStart()();
		}
		$wnd.onQueryFinish = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::onQueryFinish()();
		}
		$wnd.clearQueryResult = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::resetQueryResult()();
		}
		$wnd.storeQueryInCookie = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::storeQueryInCookie()();
		}
		$wnd.onQueryError = function(tabId, errorMsg) {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::onQueryError(Ljava/lang/String;Ljava/lang/String;)(tabId, errorMsg);
		}
		$wnd.showPlayButton = function(queryValid) {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::showPlayButton(Ljava/lang/String;)(queryValid);
		}
		$wnd.adjustQueryInputForContent = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::adjustQueryInputForContent()();
		}
		$wnd.setQueryType = function(queryType) {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::setQueryType(Ljava/lang/String;)(queryType);
		}
		$wnd.storeSettings = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::storeSettings()();
		}
		$wnd.cancelQuery = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::cancelQuery()();
		}
		$wnd.executeQuery = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::executeQuery()();
		}
		$wnd.saveTabTitle = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::saveTabTitle()();
		}
	}-*/;
}
