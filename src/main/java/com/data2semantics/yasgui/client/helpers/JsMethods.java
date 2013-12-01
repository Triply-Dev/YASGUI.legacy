package com.data2semantics.yasgui.client.helpers;

import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.data2semantics.yasgui.client.tab.QueryTabs;
import com.data2semantics.yasgui.client.tab.results.input.dlv.DlvWrapper;

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

public class JsMethods {
	
	public static void initJs() {
		setTabBarProperties(QueryTabs.INDENT_TABBAR_START, QueryTabs.INDENT_TABBAR_END);
		setQtipDefaults(ZIndexes.HELP_TOOLTIPS);
		setAppcacheCallbacks();
	}
	
	
	
	/**
	 * Unset a tabmirror object. Used when closing a tab
	 * 
	 * @param queryInputId Id of the text area of this codemirror instance
	 */
	public static native void destroyCodeMirrorQueryInput(String queryInputId) /*-{
			if ($wnd.sparqlHighlight[queryInputId] != null) { 
				$wnd.sparqlHighlight[queryInputId] = null;
			}
	}-*/;
	public static native void setAppcacheCallbacks() /*-{
		// Fired after the first cache of the manifest.
		$wnd.window.applicationCache.addEventListener('cached', $wnd.appcacheFetchSuccesfull, false);

		// Checking for an update. Always the first event fired in the sequence.
		$wnd.window.applicationCache.addEventListener('checking', $wnd.appcacheFechting, false);
		
		// An update was found. The browser is fetching resources.
		$wnd.window.applicationCache.addEventListener('downloading', $wnd.appcacheFechting, false);
		
		// The manifest returns 404 or 410, the download failed,
		// or the manifest changed while the download was in progress.
		$wnd.window.applicationCache.addEventListener('error', $wnd.appcacheFetchFailed, false);
		
		// Fired after the first download of the manifest.
		$wnd.window.applicationCache.addEventListener('noupdate', $wnd.appcacheFetchSuccesfull, false);
		
		// Fired if the manifest file returns a 404 or 410.
		// This results in the application cache being deleted.
		$wnd.window.applicationCache.addEventListener('obsolete', $wnd.appcacheFetchFailed, false);
		
		// Fired when the manifest resources have been newly redownloaded.
		$wnd.window.applicationCache.addEventListener('updateready', $wnd.appcacheFetchSuccesfull, false);
	}-*/;
	
	/**
	 * Unset a tabmirror object. Used when closing a tab
	 * 
	 * @param queryInputId Id of the text area of this codemirror instance
	 */
	public static native void destroyCodeMirrorQueryResponse(String queryInputId) /*-{
			if ($wnd.sparqlResponseHighlight[queryInputId] != null) { 
				$wnd.sparqlResponseHighlight[queryInputId] = null;
			}
	}-*/;
	
	/**
	 * Initialize and atatch codemirror to a text area
	 * 
	 * @param queryInputId Id of text area to attach codemirror to
	 */
	public static native void initializeQueryCodemirror(String elementId) /*-{
		$wnd.initializeQueryCodemirror(elementId);
	}-*/;
	
	/**
	 * Initialize and atatch codemirror to a text area
	 * 
	 * @param queryInputId Id of text area to attach codemirror to
	 */
	public static native void initializeQueryBookmarkCodemirror(String elementId) /*-{
		$wnd.initializeQueryBookmarkCodemirror(elementId);
	}-*/;
	
	/**
	 * Initialize and atatch codemirror to a text area used for displaying json/xml results of query
	 * 
	 * @param queryInputId Id of text area to attach codemirror to
	 */
	public static native void initializeQueryResponseCodemirror(String elementId, String mode) /*-{
		$wnd.initializeQueryResponseCodemirror(elementId, mode);
	}-*/;
	
	public static native void resizeQueryInput(String queryInputId, int width, int height) /*-{
		if ($wnd.sparqlHighlight[queryInputId] != null) {
			$wnd.sparqlHighlight[queryInputId].setSize(width + "px", height + "px");
		}
	}-*/;


	
	
	public static native void setAutocompletePrefixes(String prefixes) /*-{
		$wnd.prefixes = eval(prefixes);
	}-*/;
	
	public static native void setAutocompleteProperties(String endpoint, String properties) /*-{
		var propertiesArray =  eval(properties);
		var T = new $wnd.Trie();
	    var i;
	    for(i = 0; i < propertiesArray.length; i++) {
	        T.insert(propertiesArray[i]);
	    }
		$wnd.properties[endpoint] = T;
	}-*/;
	public static native boolean propertiesRetrieved(String endpoint) /*-{
		return $wnd.properties[endpoint] != null && $wnd.properties[endpoint].length > 0;
	}-*/;
	

	/**
	 * Query an endpoint. Asynchronous. A callback function (drawResultsInTable) is used to process query result.
	 * 
	 * @param queryString
	 * @param endpoint
	 */
	public static native void query(String tabId, String queryString, String endpoint, String acceptHeader, String argsJsonString, String requestMethod) /*-{
		$wnd.sparqlQueryJson(tabId, queryString, endpoint, acceptHeader, argsJsonString, requestMethod, function(tabId, jsonResult, contentType) {$wnd.drawResults(tabId, jsonResult, contentType);});
	}-*/;
	
	
	public static native String getDefaultSettings() /*-{
		return $wnd.defaults;
	}-*/;
	
	
	/**
	 * Let codemirror save its content to the textarea it is attached to
	 * 
	 * @param id Id of codemirror instance
	 */
	public static native void saveCodeMirror(String id) /*-{
		$wnd.sparqlHighlight[id].save();
	}-*/;
	
	/**
	 * Set content of codemirror
	 * 
	 * @param id Id of codemirror instance
	 */
	public static native void setCodemirrorContent(String id, String content) /*-{
		$wnd.sparqlHighlight[id].setValue(content);
	}-*/;
	
	/**
	 * Get value of element by Id
	 * 
	 * @param id
	 * @return
	 */
	public static native String getValueUsingId(String id) /*-{
		result = "";
		if ($doc.getElementById(id)) {
			if ($doc.getElementById(id).value) {
				result = $doc.getElementById(id).value;
			}
		}
		return result;
	}-*/;
	
	/**
	 * Takes value of first found element using this name
	 * 
	 * @param name of element to search for
	 * @return String value, empty if name not found
	 */
	public static native String getValueUsingName(String name) /*-{
		result = "";
		var elements = $doc.getElementsByName(name);
		for (var i=0, iLen=elements.length; i<iLen; i++) {
		  result = elements[i].value;
		  break;
		}
		return result;
	}-*/;
	
	/**
	 * Check if a site is cors enabled. Stores results in a javascript array (global)
	 * 
	 * @param endpointUri
	 * @return
	 */
	public static native String checkCorsEnabled(String endpointUri) /*-{
		$wnd.checkCorsEnabled(endpointUri);
	}-*/;
	
	
	/**
	 * Set the proper z-index for the q-tip tooltips
	 * 
	 * @param z-index
	 */
	public static native void setQtipDefaults(int zIndex) /*-{
		$wnd.$.fn.qtip.zindex = zIndex;
	}-*/;
	
	/**
	 * Cannot set all properties of tab bar after initialization. Therefore use this method to set properties beforehand
	 * @param startMargin
	 */
	public static native void setTabBarProperties(int startMargin, int endMargin) /*-{
		$wnd.isc.TabBar.addProperties({layoutStartMargin:startMargin, layoutEndMargin:endMargin});
	}-*/;
	
	/*
	 * Calls codemirror the check the querytype of one of our codemirror instances
	 * @param queryInputId
	 * @return
	 */
	public static native String getQueryType(String queryInputId) /*-{
		var instance = $wnd.sparqlHighlight[queryInputId];
		var lineCount = instance.lineCount();
		return instance.getTokenAt({line : lineCount - 1, ch: instance.getLine(lineCount-1).length-1}).state.queryType;
	}-*/;
	
	public static native boolean elementExists(String id) /*-{
		if ($doc.getElementById(id)) {
			return true;
		} else {
			return false;
		}
	}-*/;
	
	public static native void cancelQuery() /*-{
		if ($wnd.queryRequest != null && $wnd.queryRequest != undefined) {
			$wnd.queryRequest.abort();
		}
	}-*/;
	
	public static native void showLoadingNoty(String id, String text, String loadingIcon) /*-{
		if ($wnd.$.noty.get(id) == false) {
			$wnd.noty({
				text: "<img style='vertical-align:middle;' src='" + loadingIcon + "' width='20' height='20'>&nbsp;" + text,
				layout: 'topCenter',
				type: 'alert',
				id: id,
				closeWith: ['click'],
			});
		} else {
			$wnd.$.noty.setText(id, text);
		}
	}-*/;
	public static native void closeLoadingNoty(String id) /*-{
		if ($wnd.$.noty.get(id) != false) {
			$wnd.$.noty.get(id).close();
		}
	}-*/;
	
	
	public static native String drawTooltip(String id, String title, String text, String my, String at, int xOffset, int yOffset) /*-{
		if ($wnd.$('#' + id).data("qtip")) {
			//To support multiple tooltips on the same element, remove data, and add next qtip.
			$wnd.$('#' + id).removeData("qtip")
		}
		$wnd.$('#' + id).qtip({
			content: {
				text: text,
				title: title
			},
			show: {
		        ready: true, // Show the tooltip when ready
		        event: false,
		        effect: function() { $wnd.$(this).fadeIn(250); }
		    },
		    position: {
				my: my,
				at: at,
				target: $wnd.$('#' + id), // my target
				adjust: {
					x: xOffset,
					y: yOffset
				}
			},
		    //hide: false,
		    hide: {
				event: 'click unfocus', //hide when anything is clicked: elsewhere (unfocus) or on the element itself (click)
				effect: function() { $wnd.$(this).fadeOut(250); }
			}
		});
	}-*/;

	
	public static native boolean downloadAttributeSupported() /*-{
		supported = false;
		var a = $wnd.document.createElement('a');
		if (typeof a.download != "undefined") {
		    supported = true;
		}
		return supported;
	}-*/;
	
	public static native boolean stringToDownloadSupported() /*-{
		supported = true;
		windowUrl = $wnd.window.URL || $wnd.window.webkitURL || $wnd.window.mozURL || $wnd.window.msURL;
		
		if (windowUrl == undefined || windowUrl == null) {
			supported = false;
		}
		
		if (supported && $wnd.Blob == undefined) {
			supported = false;
		}
		
		//in versions < ff 13, blob constructor is missing. check!
		if (supported) {
			try {
				var blob = new $wnd.Blob(["text"], {type: "text/html"});
			} catch (err) {
				supported = false;
			}
		}
		return supported;
	}-*/;
	
	public static native String stringToUrl(String string, String contentType) /*-{
		url = "";
		windowUrl = $wnd.window.URL || $wnd.window.webkitURL || $wnd.window.mozURL || $wnd.window.msURL;

		if (windowUrl && $wnd.Blob) {
			var blob = new $wnd.Blob([string], {type: contentType});
			url = windowUrl.createObjectURL(blob);
		}
		return url;
	}-*/;

	public static native String getBrowserName() /*-{
		return $wnd.$.browser.name;
	}-*/;
	
	public static native boolean isIeBrowser() /*-{
		return $wnd.$.browser.msie;
	}-*/;
	

	public static native String getBrowserVersionNumber() /*-{
		return $wnd.$.browser.versionNumber.toString();
	}-*/;

	public static native String GetBrowserOs() /*-{
		return $wnd.$.os.name;
	}-*/;
	
	public static native String getUserAgent() /*-{
		return $wnd.navigator.userAgent;
	}-*/;

	public static native void resetHeightSetting(String inputId) /*-{
		$wnd.sparqlHighlightHeight[inputId] = null;
	}-*/;
	public static native void deleteElementsWithPostfixId(String postfix) /*-{
		$wnd.$('textarea[id$="' + postfix + '"]').each(function() {
		  $wnd.$(this).remove();
		});
	}-*/;
	
	public static native void pushHistoryState(String dataString, String stateTitle, String stateUrl) /*-{
		if ($wnd.ignoreNextHistoryPush) {
			$wnd.ignoreNextHistoryPush = false;
		} else {
			$wnd.historyCallbackEnabled = false;
			dataObject = {"data": dataString};
			$wnd.History.pushState(dataObject, stateTitle, stateUrl);
		}
	}-*/;
	
	public static native void replaceHistoryState(String dataString, String stateTitle, String stateUrl) /*-{
		dataObject = {"data": dataString};
		$wnd.historyCallbackEnabled = false;
		$wnd.History.replaceState(dataObject, stateTitle, stateUrl);
	}-*/;
	
	public static native HistoryDataObject getHistoryState() /*-{
		return $wnd.History.getState().data; 
	}-*/;
	
	
	public static native void setHistoryStateChangeCallback() /*-{
		$wnd.window.History.Adapter.bind ($wnd.window, 'statechange', function() {
			if ($wnd.historyCallbackEnabled) {
				$wnd.ignoreNextHistoryPush = true;
				$wnd.historyStateChangeCallback();
			} else {
				$wnd.historyCallbackEnabled = true;
			}
		});
	}-*/;
	
	public static native boolean historyApiSupported() /*-{
		return !!($wnd.window.history && $wnd.history.pushState);
	}-*/;
	public static native void logConsole(String message) /*-{
		$wnd.console.log(message);
	}-*/;

	public static native boolean inExternalIframe() /*-{
		//$wnd is actually our parent (GWT) iframe. we want to check whether there is another iframe as parent
		var inExtIframe;
		if ($wnd.parent == null || $wnd.parent == $wnd) {
			inExtIframe = false;
		} else {
			inExtIframe = true;
		}
		return inExtIframe;
	}-*/;
	
	public static native String getLocation() /*-{
		var location;
		//$wnd is actually our parent (GWT) iframe. we want to check whether there is another iframe as parent
		
		if ($wnd.parent == null || $wnd.parent == $wnd) {
			location = $wnd.location.href;
		} else {
			//when external iframe parent is from different domain, we cannot access the location
			//try to use referrer instead.
			location = $wnd.document.referrer;
		}
		return location;
	}-*/;

	
	public static native DlvWrapper getDlv(String csvString, String separator) /*-{
		return $wnd.$.csv.toArrays(csvString, {"separator": separator});
	}-*/;

	public static native String getUncommentedSparql(String sparqlQuery) /*-{
		var cleanedQuery = "";
		$wnd.CodeMirror.runMode(sparqlQuery, "sparql11", function(stringVal, className) {
			if (className != "sp-comment") {
				cleanedQuery += stringVal;
			}
		});
		return cleanedQuery;
	}-*/;
	
	/**
	 * We cannot change the manifest attribute dynamically via clientside. 
	 * Therefore, use this workaround instead, where we can include an iframe (which has a reference to a manifest attribute) dynamically
	 * @param manifestIframe
	 */
	public static native void appendManifestIframe(String manifestIframe) /*-{
		if ($doc.getElementById("manifestIframe") == undefined) {
			var ifrm = document.createElement("iframe"); 
			ifrm.setAttribute("id", "manifestIframe");
			ifrm.setAttribute("src", manifestIframe);
			ifrm.setAttribute("frameborder", "0");
			ifrm.style.width = 0+"px"; 
			ifrm.style.height = 0+"px"; 
			$doc.body.appendChild(ifrm); 
		}
	}-*/;
	
	/**
	 * We cannot change the manifest attribute dynamically via clientside. 
	 * Therefore, use this workaround instead, where we can include an iframe (which has a reference to a manifest attribute) dynamically
	 * @param manifestIframe
	 */
	public static native boolean isDevPageLoaded() /*-{
		var devPageLoaded = false;
		var path = $wnd.location.pathname;
		//replace whitespace:
		path = path.split(' ').join('');
		if (path.substring(path.lastIndexOf('/')+1) == "dev.jsp") {
			devPageLoaded = true;
		}
		return devPageLoaded; 
	}-*/;

	public static native boolean offlineSupported() /*-{
		var supported = true;
		if ($wnd.$.browser.msie || $wnd.applicationCache == undefined || $wnd.applicationCache == null) {
			supported = false;
		}
		return supported;
	}-*/;
	public static native boolean isMac() /*-{
		return $wnd.navigator.platform.toUpperCase().indexOf('MAC')>=0;
	}-*/;
	public static native boolean corsEnabled(String endpoint) /*-{
		return ($wnd.corsEnabled[endpoint] == true);
	}-*/;
}
