package com.data2semantics.yasgui.client.helpers;

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
	public static native void attachCodeMirrorToQueryInput(String queryInputId) /*-{
		var qInput = $doc.getElementById(queryInputId);
		if (qInput) {
			if ($wnd.sparqlHighlight[queryInputId] == null) { 
				//Only add if it hasnt been drawn yet
				$wnd.sparqlHighlight[queryInputId] = $wnd.CodeMirror.fromTextArea(qInput, {
					mode : "application/x-sparql-query",
					theme: "yasgui",
					highlightSelectionMatches: {showToken: /\w/},
					tabMode : "indent",
					lineNumbers : true,
					gutters: ["gutterErrorBar","CodeMirror-linenumbers" ],
					matchBrackets : true,
					fixedGutter: true,
					extraKeys : {
						"Ctrl-Space" : "autocomplete",
						"Ctrl-D" : "deleteLines",
						"Ctrl-/" : "commentLines",
						"Ctrl-Alt-Down" : "copyLineDown",
						"Ctrl-Alt-Up" : "copyLineUp",
					}
				});
				
				$wnd.sparqlHighlight[queryInputId].on("change", function(cm, change){
					$wnd.checkSyntax(cm, true);
					$wnd.setQueryType(cm.getStateAfter().queryType);
					height = $wnd.sparqlHighlight[queryInputId].getWrapperElement().offsetHeight;
					if ($wnd.sparqlHighlightHeight[queryInputId]) {
						if (height != $wnd.sparqlHighlightHeight[queryInputId]) {
							$wnd.sparqlHighlightHeight[queryInputId] = height;
							$wnd.adjustQueryInputForContent();
						}
					} else {
						$wnd.sparqlHighlightHeight[queryInputId] = height;
					}
					$wnd.CodeMirror.showHint(cm, $wnd.CodeMirror.allAutoCompletions, {closeCharacters: /(?=a)b/});
					$wnd.appendPrefixIfNeeded(cm);
				});
				$wnd.sparqlHighlight[queryInputId].on("gutterClick", function(cm, change) {
					$wnd.saveTabTitle();
				});
				$wnd.sparqlHighlight[queryInputId].on("focus", function(cm, change) {
					$wnd.saveTabTitle();
				});
				$wnd.sparqlHighlight[queryInputId].on("blur", function(cm, change) {
					$wnd.storeQueryInCookie();
				});
				//init query type
				$wnd.setQueryType($wnd.sparqlHighlight[queryInputId].getStateAfter().queryType);
				
				
				//Append another classname to the codemirror div, so we can set width and height via css
				if (qInput.nextSibling != null && qInput.nextSibling.className == "CodeMirror") {
					qInput.nextSibling.className = "CodeMirror queryCm";
					scrollElement = qInput.nextSibling.getElementsByClassName("CodeMirror-scroll");
					//use jquery for this (a bit easier). for this element, find scroll class, and append another class
					$wnd.$("#"+queryInputId).next().find($wnd.$(".CodeMirror-scroll")).addClass("queryScrollCm");
				}
			}
		} else {
			$wnd.onError("no text area for query input id: " + queryInputId);
		}
	}-*/;
	
	/**
	 * Initialize and atatch codemirror to a text area
	 * 
	 * @param queryInputId Id of text area to attach codemirror to
	 */
	public static native void attachCodeMirrorToBookmarkedQuery(String queryInputId) /*-{
		var qInput = $doc.getElementById(queryInputId);
		if (qInput) {
			$wnd.sparqlHighlight[queryInputId] = $wnd.CodeMirror.fromTextArea(qInput, {
				mode : "application/x-sparql-query",
				tabMode : "indent",
				theme: "yasgui",
				highlightSelectionMatches: {showToken: /\w/},
				gutters: ["gutterErrorBar","CodeMirror-linenumbers" ],
				lineNumbers : true,
				matchBrackets : true,
				fixedGutter: true,
				viewportMargin: Infinity,
				extraKeys : {
					"Ctrl-Space" : "autocomplete",
					"Ctrl-D" : "deleteLines",
					"Ctrl-/" : "commentLines",
					"Ctrl-Alt-Down" : "copyLineDown",
					"Ctrl-Alt-Up" : "copyLineUp",
				}
			});
			
			$wnd.sparqlHighlight[queryInputId].on("change", function(cm, change){
				$wnd.checkSyntax(cm, false);
				$wnd.setQueryType(cm.getStateAfter().queryType);
				$wnd.updateBookmarkCmHeight(queryInputId);
				$wnd.CodeMirror.showHint(cm, $wnd.CodeMirror.prefixHint, {closeCharacters: /(?=a)b/});
				$wnd.appendPrefixIfNeeded(cm);
			});
			$wnd.sparqlHighlight[queryInputId].on("blur", function(cm, change) {
				$wnd.updateBookmarkedQuery();
			});
				
			$wnd.updateBookmarkCmHeight(queryInputId);
			//Append another classname to the codemirror div, so we can set width and height via css
			if (qInput.nextSibling != null && qInput.nextSibling.className == "CodeMirror") {
				qInput.nextSibling.className = "CodeMirror bookmarkCm";
				scrollElement = qInput.nextSibling.getElementsByClassName("CodeMirror-scroll");
				//use jquery for this (a bit easier). for this element, find scroll class, and append another class
				$wnd.$("#"+queryInputId).next().find($wnd.$(".CodeMirror-scroll")).addClass("bookmarkScrollCm");
			}
		} else {
			$wnd.onError("no text area for bookmark query input id: " + queryInputId);
		}
	}-*/;
	
	public static native void resizeQueryInput(String queryInputId, int width, int height) /*-{
		if ($wnd.sparqlHighlight[queryInputId] != null) {
			$wnd.sparqlHighlight[queryInputId].setSize(width + "px", height + "px");
		}
	}-*/;

	/**
	 * Initialize and atatch codemirror to a text area used for displaying json/xml results of query
	 * 
	 * @param queryInputId Id of text area to attach codemirror to
	 */
	public static native void attachCodeMirrorToQueryResult(String queryInputId, String mode) /*-{
		var qInput = $doc.getElementById(queryInputId);
		if (qInput) {
			var drawCodeMirror = false;
			if ($wnd.sparqlResponseHighlight[queryInputId] == null) drawCodeMirror = true;
			
			//also check if it isnt drawn yet. Checking for just the javascript object in the sparqlResponseHighlight object is not enough
			//The object can be there, while visually you see the text area. a goof that happens between codemirror and smartgwt I believe (having to do with the way smartgwt loads pages icw resizing pages)
			if (qInput.nextSibling == null) drawCodeMirror = true;

			if (drawCodeMirror) {
				var cmMode = mode;
				if (mode == "json") {
					cmMode = {
						name: "javascript",
						json: true
					};
				}
				$wnd.sparqlResponseHighlight[queryInputId] = $wnd.CodeMirror.fromTextArea($doc.getElementById(queryInputId), {
					mode : cmMode,
					theme: "yasgui",
					lineNumbers : true,
					highlightSelectionMatches: {showToken: /\w/},
					matchBrackets : true,
					readOnly: true,
					fixedGutter: true,
					hideVScroll: true
				});
				
				//Append another classname to the codemirror div, so we can set width and height via css
				if (qInput.nextSibling != null && qInput.nextSibling.className == "CodeMirror") {
					qInput.nextSibling.className = "CodeMirror resultCm";
					scrollElement = qInput.nextSibling.getElementsByClassName("CodeMirror-scroll");
					//use jquery for this (a bit easier). for this element, find scroll class, and append another class
					$wnd.$("#"+queryInputId).next().find($wnd.$(".CodeMirror-scroll")).addClass("resultScrollCm");
				}
			}
		} else {
			$wnd.onError("no text area to create sparql response highlight for. Input id: " + queryInputId);
		}
	}-*/;
	
	
	public static native void setAutocompletePrefixes(String prefixes) /*-{
		$wnd.prefixes = eval(prefixes);
	}-*/;
	
	public static native void setAutocompleteProperties(String endpoint, String properties) /*-{
		var propertiesArray =  eval(properties);
		var T = new Trie();
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
	 * Define the url of the sparql proxy servlet in javascript
	 * 
	 * @param proxy
	 */
	public static native void setProxyUriInVar(String proxy) /*-{
		$wnd.proxy = proxy;
	}-*/;
	
	/**
	 * Set the proper z-index for the q-tip tooltips
	 * 
	 * @param z-index
	 */
	public static native void setQtipZIndex(int zIndex) /*-{
		$wnd.jQuery.fn.qtip.zindex = zIndex;
	}-*/;
	
	/**
	 * Cannot set all properties of tab bar after initialization. Therefore use this method to set properties beforehand
	 * @param margin
	 */
	public static native void setTabBarProperties(int margin) /*-{
		$wnd.isc.TabBar.addProperties({layoutStartMargin:margin});
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
		$wnd.queryRequest.abort();
	}-*/;
	
	
	public static native String drawTooltip(String id, String content, String my, String at, int xOffset, int yOffset) /*-{
		if ($wnd.$('#' + id).data("qtip")) {
			//To support multiple tooltips on the same element, remove data, and add next qtip.
			$wnd.$('#' + id).removeData("qtip")
		}
		$wnd.$('#' + id).qtip({
			content: content, 
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
			},
			style: { 
		  		classes: 'ui-tooltip-tipped',
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

}
