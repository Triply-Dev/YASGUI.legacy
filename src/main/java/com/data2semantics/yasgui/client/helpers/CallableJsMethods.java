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
import com.data2semantics.yasgui.client.configmenu.OfflineAvailabilityConfig;
import com.data2semantics.yasgui.client.settings.ExternalLinks;
import com.data2semantics.yasgui.client.tab.optionbar.endpoints.EndpointInput;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

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
	
	public void adjustBookmarkQueryInputForContent(int height) {
		view.getSelectedTab().getBookmarkedQueries().adjustQueryInputForContent(height);
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
	 * detect which query is open (not collapsed), and store it.
	 */
	public void updateBookmarkedQuery() {
		view.getSelectedTab().getBookmarkedQueries().storeCurrentTextArea();
	}
	
	
	public boolean isOnline() {
		return view.getConnHelper().isOnline();
	}
	
	public void checkIsOnline() {
		view.getConnHelper().checkOnlineStatus();
	}
	

	public void onError(String error) {
		view.getErrorHelper().onError(error);
	}
	
	
	public void onError(Throwable e) {
		view.getErrorHelper().onError(e);
	}
	
	public void onLoadingStart(String message) {
		view.getElements().onLoadingStart(message);
	}
	
	public void onLoadingFinish() {
		view.getElements().onLoadingFinish();
	}
	
	public void executeQuery() {
		SparqlQuery.exec(view);
	}
	
	
	public void showPlayButton(String queryValid) {
		view.getElements().showPlayButton(queryValid);
	}
	
	public void saveTabTitle() {
		view.getTabs().saveTabTitle();
	}
	
	public void historyStateChangeCallback() {
		view.getHistory().onHistoryStateChange();
	}
	
	public void queryForResource(String resource) {
		view.getSelectedTab().getQueryTextArea().queryForResource(resource);
	}
	
	public boolean endpointSelectionEnabled() {
		return view.getEnabledFeatures().endpointSelectionEnabled();
	}
	
	public String getCurrentEndpoint() {
		EndpointInput endpointInput = view.getSelectedTab().getEndpointInput();
		if (endpointInput != null) {
			endpointInput.storeEndpointInSettings();
		}
		return view.getSelectedTabSettings().getEndpoint();
	}
	
	public boolean inDebugMode() {
		return Helper.inDebugMode();
	}
	
	public void showOfflineAvailabilitySettings() {
		new OfflineAvailabilityConfig(view);
	}

	public void clearQueryResultsFromSettings() {
		view.getSettings().clearQueryResults();
		LocalStorageHelper.storeSettingsInCookie(view.getSettings());
	}

	public void sendQueryAnalyticsEvent(String endpoint, String queryString, String label, int timing) {
		if (view.getSettings().useGoogleAnalytics()) {
			GoogleAnalyticsEvent queryEvent = new GoogleAnalyticsEvent(endpoint, JsMethods.getUncommentedSparql(queryString), label, timing);
			GoogleAnalytics.trackEvent(queryEvent);
		}
	}
	public String getPropertyCompletionMethods() {
		return view.getSettings().getPropertCompletionMethodsAsJson().toString();
	}
	public String getClassCompletionMethods() {
		return view.getSettings().getClassCompletionMethodsAsJson().toString();
	}
	public String getAutocompletionMoreInfoLink() {
		return ExternalLinks.YASGUI_AUTOCOMPLETE_INFO;
	}
	public String getLovApiLink() {
		return ExternalLinks.LOV_API;
	}
	public void storeCompletionMethodsInSettings(String methodsJsonString, String type) {
		JSONValue jsonVal = JSONParser.parseStrict(methodsJsonString);
		if (jsonVal != null && jsonVal.isObject() != null) {
			if (type.equals("property")) {
				view.getSettings().setPropertyCompletionMethods(jsonVal.isObject());
			} else if (type.equals("class")) {
				view.getSettings().setClassCompletionMethods(jsonVal.isObject());
			}
			LocalStorageHelper.storeSettingsInCookie(view.getSettings());
		}
	}
	public boolean isColdAutocompletionFetch(String endpoint, String type) {
		boolean cold = false;
		if (view.getAutocompletionsInfo() != null) {
			cold = view.getAutocompletionsInfo().coldAutocompletionFetch(endpoint, type);
		}
		return cold;
	}
	public boolean retryAllowed(String endpoint, String type) {
		boolean retryAllowed = true;
		if (view.getAutocompletionsInfo() != null) {
			retryAllowed = view.getAutocompletionsInfo().retryFetchAllowed(endpoint, type);
		}
		return retryAllowed;
	}
	public void logAutocompletionsInfo() {
		if (view.getAutocompletionsInfo() == null) {
			JsMethods.logConsole("null");
		} else {
			JsMethods.logConsole(view.getAutocompletionsInfo().toString());
		}
	}
	public void fetchAutocompletionsInfo() {
		view.retrieveAutocompletionsInfo();
	}
	public boolean isDbSet() {
		return view.getSettings().isDbSet();
	}
	
	/**
	 * Add view methods to JS, use this for situations where a non-static GWT method needs to be called
	 * 
	 * @param view
	 */
	public static native void declareCallableViewMethods(CallableJsMethods viewJs) /*-{
		var view = view; 
		$wnd.onError = function(errorMsg) {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::onError(Ljava/lang/String;)(errorMsg);
		}
		$wnd.onLoadingStart = function(message) {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::onLoadingStart(Ljava/lang/String;)(message);
		}
		$wnd.onLoadingFinish = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::onLoadingFinish()();
		}
		$wnd.storeQueryInCookie = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::storeQueryInCookie()();
		}
		$wnd.updateBookmarkedQuery = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::updateBookmarkedQuery()();
		}
		$wnd.showPlayButton = function(queryValid) {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::showPlayButton(Ljava/lang/String;)(queryValid);
		}
		$wnd.adjustQueryInputForContent = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::adjustQueryInputForContent()();
		}
		$wnd.adjustBookmarkQueryInputForContent = function(height) {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::adjustBookmarkQueryInputForContent(I)(height);
		}
		$wnd.setQueryType = function(queryType) {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::setQueryType(Ljava/lang/String;)(queryType);
		}
		$wnd.queryForResource = function(resource) {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::queryForResource(Ljava/lang/String;)(resource);
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
		$wnd.historyStateChangeCallback = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::historyStateChangeCallback()();
		}
		$wnd.endpointSelectionEnabled = function() {
			return viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::endpointSelectionEnabled()();
		}
		$wnd.getCurrentEndpoint = function() {
			return viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::getCurrentEndpoint()();
		}
		$wnd.inDebugMode = function() {
			return viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::inDebugMode()();
		}
		$wnd.isOnline = function() {
			return viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::isOnline()();
		}
		$wnd.checkIsOnline = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::checkIsOnline()();
		}
		$wnd.showOfflineAvailabilitySettings = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::showOfflineAvailabilitySettings()();
		}
		$wnd.clearQueryResultsFromSettings = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::clearQueryResultsFromSettings()();
		}
		$wnd.sendQueryAnalyticsEvent = function(endpoint, queryString, label, timing) {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::sendQueryAnalyticsEvent(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)(endpoint, queryString, label, timing);
		}
		$wnd.getPropertyCompletionMethods = function() {
			var settingsString = viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::getPropertyCompletionMethods()();
			return $wnd.jQuery.parseJSON(settingsString);
		}
		$wnd.getClassCompletionMethods = function() {
			var settingsString = viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::getClassCompletionMethods()();
			return $wnd.jQuery.parseJSON(settingsString);
		}
		$wnd.getAutocompletionMoreInfoLink = function() {
			return viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::getAutocompletionMoreInfoLink()();
		}
		$wnd.getLovApiLink = function() {
			return viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::getLovApiLink()();
		}
		$wnd.storeCompletionMethodsInSettings = function(methods, type) {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::storeCompletionMethodsInSettings(Ljava/lang/String;Ljava/lang/String;)(methods, type);
		}
		$wnd.coldAutocompletionFetch = function(endpoint, type) {
			return viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::isColdAutocompletionFetch(Ljava/lang/String;Ljava/lang/String;)(endpoint, type);
		}
		$wnd.retryAllowed = function(endpoint, type) {
			return viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::retryAllowed(Ljava/lang/String;Ljava/lang/String;)(endpoint, type);
		}
		$wnd.logAutocompletionsInfo = function() {
			return viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::logAutocompletionsInfo()();
		}
		$wnd.fetchAutocompletionsInfo = function() {
			viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::fetchAutocompletionsInfo()();
		}
		$wnd.isDbSet = function() {
			return viewJs.@com.data2semantics.yasgui.client.helpers.CallableJsMethods::isDbSet()();
		}
		
	}-*/;
}
