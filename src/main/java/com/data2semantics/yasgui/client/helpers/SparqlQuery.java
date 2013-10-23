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
import com.data2semantics.yasgui.client.tab.optionbar.endpoints.EndpointInput;
import com.google.common.collect.HashMultimap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class SparqlQuery {

	private static String corsNotification = "For information on CORS, and how to access your endpoint, "
			+ "visit the <a href=\"http://laurensrietveld.nl/yasgui/help.html\" target=\"_blank\">YASGUI help page</a> for more information.";

	
	private View view;
	private String tabId;
	private String endpoint;
	private String queryString;
	private String acceptHeader;

	private HashMultimap<String, String> customQueryArgs;
	private RequestBuilder.Method queryRequestMethod;
	public SparqlQuery(View view) {
		this.view = view;
	}
	
	private void fetchProperties() {
		//onblur might not always fire (will have to check that). for now, store query in settings before query execution just to be sure
		view.getCallableJsMethods().storeQueryInCookie();
		queryString = view.getSelectedTabSettings().getQueryString();
		//the same happens whenever our endpointinput has focus
		EndpointInput endpointInput = view.getSelectedTab().getEndpointInput();
		if (endpointInput != null) {
			endpointInput.storeEndpointInSettings();
		}
		endpoint = view.getSelectedTabSettings().getEndpoint();
		view.checkAndAddEndpointToDs(endpoint);
		
		
		tabId = view.getSelectedTab().getID();
		
		
		if (view.getSelectedTab().getQueryType().equals("CONSTRUCT") || view.getSelectedTab().getQueryType().equals("DESCRIBE")) {
			//Change content type automatically for construct queries
			acceptHeader = view.getSelectedTabSettings().getConstructContentType();
		} else {
			acceptHeader = view.getSelectedTabSettings().getSelectContentType();
		}
		acceptHeader += ",*/*;q=0.9";
	
		customQueryArgs = view.getSelectedTabSettings().getQueryArgs();
		queryRequestMethod = (view.getSelectedTabSettings().getRequestMethod().equals("GET")? RequestBuilder.GET: RequestBuilder.POST);
	}
	
	public void doRequest() {
		if (!view.getConnHelper().isOnline() && !JsMethods.corsEnabled(endpoint)) {
			//cors disabled and not online: problem!
			String errorMsg = "YASGUI is current not connected to the YASGUI server. " +
				"This mean you can only access endpoints on your own computer (e.g. localhost), which are <a href=\"http://enable-cors.org/\" target=\"_blank\">CORS enabled</a>.<br>" +
				"The endpoint you try to access is either not running on your computer, or not CORS-enabled.<br>" +
				corsNotification;
			view.getErrorHelper().onQueryError(errorMsg, endpoint, queryString, customQueryArgs);
			return;
		}
		
		view.getElements().onQueryStart();
		RequestBuilder builder;
		HashMultimap<String, String> queryArgs = customQueryArgs;
		RequestBuilder.Method requestMethod = queryRequestMethod;
		queryArgs.put("query", queryString);
		if (JsMethods.corsEnabled(endpoint)) {
			builder = new RequestBuilder(queryRequestMethod, endpoint + "?" + Helper.getParamsAsString(queryArgs));
			if (queryRequestMethod == RequestBuilder.POST) {
				builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
			}
		} else {
			requestMethod = RequestBuilder.POST;
			queryArgs.put("endpoint", endpoint);
			queryArgs.put("requestMethod", (queryRequestMethod == RequestBuilder.POST? "POST": "GET"));
			builder = new RequestBuilder(RequestBuilder.POST, GWT.getModuleBaseURL() + "sparql"); //send via proxy
			builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
		}
		builder.setHeader("Accept", acceptHeader);
		try {
			final long startTime = System.currentTimeMillis();
			builder.sendRequest((requestMethod == RequestBuilder.POST? Helper.getParamsAsString(queryArgs):null), new RequestCallback() {
				public void onError(Request request, Throwable e) {
					//e.g. a timeout
					queryErrorHandler(e);
				}
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					view.getElements().onQueryFinish();
					if (!response.getStatusText().equals("Abort")) {
						//if user cancels query, textStatus will be 'abort'. No need to show error window then
						if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
							if (view.getSettings().useGoogleAnalytics()) {
								long stopTime = System.currentTimeMillis();
								GoogleAnalytics.trackEvent(new GoogleAnalyticsEvent(endpoint, JsMethods.getUncommentedSparql(queryString), "", (int)(stopTime - startTime)));
							}
							drawResults(response.getText(), response.getHeader("Content-Type"));
						} else {
							queryErrorHandler(response);
							
						}
					}
					

				}
			});
		} catch (RequestException e) {
			queryErrorHandler(e);
		}
	}
	
	private void queryErrorHandler(Response response) {
		if (view.getSettings().useGoogleAnalytics()) {
			GoogleAnalytics.trackEvent(new GoogleAnalyticsEvent(endpoint, JsMethods.getUncommentedSparql(queryString), "", -1));
		}
		view.getElements().onQueryFinish();
		
		//clear query result
		QueryTab tab = (QueryTab)view.getTabs().getTab(tabId);
		view.getTabs().selectTab(tabId);
		if (tab != null) {
			view.getSelectedTab().getResultContainer().reset();
		}
		
		String errorMsg;
		if (response.getStatusCode() == 0 && (response.getStatusText() == null || response.getStatusText().trim().length() == 0)) {
			view.getConnHelper().checkOnlineStatus();
			errorMsg = "Error querying endpoint: empty response returned";
		} else {
			errorMsg = "Error querying endpoint: " + response.getStatusCode() + " - " + response.getStatusText();
		}
		if (!Helper.inDebugMode() && view.getSettings().getEnabledFeatures().endpointSelectionEnabled() && JsMethods.corsEnabled(endpoint) != true && Helper.isLocalhostDomain(endpoint)) {
//			//we were trying to access a local endpoint via the proxy: this won't work...
			errorMsg += "<br><br>A possible reason for this error (next to an incorrect endpoint URL) is that you tried to send a query to an endpoint installed on your computer.<br>" +
					"This only works when the endpoint is <a href=\"http://enable-cors.org/\" target=\"_blank\">CORS enabled</a>.<br>" +
					corsNotification;
			
		}
		view.getErrorHelper().onQueryError(errorMsg, endpoint, queryString, customQueryArgs);
	}
	
	private void queryErrorHandler(Throwable throwable) {
		view.getElements().onQueryFinish();
		
		if (view.getSettings().useGoogleAnalytics()) {
			GoogleAnalytics.trackEvent(new GoogleAnalyticsEvent(endpoint, JsMethods.getUncommentedSparql(queryString), "", -1));
		}
		
		//clear query result
		QueryTab tab = (QueryTab)view.getTabs().getTab(tabId);
		view.getTabs().selectTab(tabId);
		if (tab != null) {
			view.getSelectedTab().getResultContainer().reset();
		}
		
		view.getErrorHelper().onQueryError(throwable.getMessage(), endpoint, queryString, customQueryArgs);
	}


	private void preProcess() {
		//set history checkpoint (do before resetting resultcontainer, as we need this info)
		view.getHistory().setHistoryCheckpoint();
		
		//clear current result container -before- query, not after
		view.getSelectedTab().getResultContainer().reset();
		
		//disable string to download icon
		if (JsMethods.stringToDownloadSupported()) {
			view.getSelectedTab().getDownloadLink().showDisabledIcon();
		}
		
		//onblur might not always fire (will have to check that). for now, store query in settings before query execution just to be sure
		view.getCallableJsMethods().storeQueryInCookie();
		//the same happens whenever our endpointinput has focus
		EndpointInput endpointInput = view.getSelectedTab().getEndpointInput();
		if (endpointInput != null) {
			endpointInput.storeEndpointInSettings();
		}
		view.checkAndAddEndpointToDs(endpoint);
	}
	
	private void drawResults(String resultString, String contentType) {
		QueryTab tab = (QueryTab)view.getTabs().getTab(tabId);
		view.getTabs().selectTab(tabId);
		if (tab == null) {
			view.getErrorHelper().onError("No tab to draw results in");
		}
		tab.getResultContainer().drawResult(endpoint, queryString, resultString, contentType);
	}
	
	public static void exec(View view) {
		SparqlQuery query = new SparqlQuery(view);
		query.fetchProperties();
		query.preProcess();
		query.doRequest();
	}
}
