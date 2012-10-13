/*******************************************************************************
 * Copyright (c)  2012 Laurens Rietveld
 * 
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.data2semantics.yasgui.client.helpers;

import com.data2semantics.yasgui.client.View;

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
		if ($doc.getElementById(queryInputId)) {
			if ($wnd.sparqlHighlight[queryInputId] == null) { 
				//Only add if it hasnt been drawn yet
				$wnd.sparqlHighlight[queryInputId] = $wnd.CodeMirror.fromTextArea($doc
						.getElementById(queryInputId), {
					mode : "application/x-sparql-query",
					tabMode : "indent",
					lineNumbers : true,
					matchBrackets : true,
					fixedGutter: true,
					onCursorActivity : function() {
						$wnd.sparqlHighlight[queryInputId]
								.matchHighlight("CodeMirror-matchhighlight");
					},
					onChange : function(cm) {
						$wnd.CodeMirror.simpleHint(cm, $wnd.CodeMirror.prefixHint);
					},
					extraKeys : {
						"Ctrl-Space" : "autocomplete",
						"Ctrl-D" : "deleteLines",
						"Ctrl-/" : "commentLines",
						"Ctrl-Alt-Down" : "copyLineDown",
						"Ctrl-Alt-Up" : "copyLineUp",
					},
					onHighlightComplete : function(cm) {
						$wnd.checkSyntax(cm);
					},
					onBlur: function() {
						$wnd.storeQueryInCookie();
					}
				});
			}
		} else {
			$wnd.onError("no text area for input id: " + queryInputId);
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
				var cmMode;
				if (mode == "json") {
					cmMode = {
						name: "javascript",
						json: true
					};
				} else {
					cmMode = "xml";
				}
				$wnd.sparqlResponseHighlight[queryInputId] = $wnd.CodeMirror.fromTextArea($doc.getElementById(queryInputId), {
					mode : cmMode,
					lineNumbers : true,
					matchBrackets : true,
					readOnly: true,
					fixedGutter: true
				});
				
				//Append another classname to the codemirror div, so we can set width and height via css
				if (qInput.nextSibling != null && qInput.nextSibling.className == "CodeMirror") {
					qInput.nextSibling.className = "CodeMirror resultCm";
					scrollElement = qInput.nextSibling.getElementsByClassName("CodeMirror-scroll");
					//use jquery for this (a bit easier). for this element, find scroll class, and append another class
					$wnd.$("#"+queryInputId).next().find($wnd.$(".CodeMirror-scroll")).addClass("scrollCm");
				}
			}
		} else {
			$wnd.onError("no text area to create sparql response highlight for. Input id: " + queryInputId);
		}
	}-*/;
	
	
	public static native void setAutocompletePrefixes(String prefixes) /*-{
		$wnd.prefixes = eval(prefixes);
	}-*/;

	/**
	 * Query an endpoint. Asynchronous. A callback function (drawResultsInTable) is used to process query result.
	 * 
	 * @param queryString
	 * @param endpoint
	 */
	public static native void queryJson(String tabId, String queryString, String endpoint, String acceptHeader, String argsJsonString) /*-{
		$wnd.sparqlQueryJson(tabId, queryString, endpoint, acceptHeader, argsJsonString, function(tabId, jsonResult, contentType) {$wnd.drawResults(tabId, jsonResult, contentType);});
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
	 * Add view methods to JS, use this for situation where a non-static GWT method needs to be called
	 * 
	 * @param view
	 */
	public static native void declareCallableViewMethods(View view) /*-{
		var view = view; 
		$wnd.drawResults = function(tabId, resultString, contentType) {
			view.@com.data2semantics.yasgui.client.View::drawResults(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(tabId, resultString, contentType);
		}
		$wnd.onError = function(errorMsg) {
			view.@com.data2semantics.yasgui.client.View::onError(Ljava/lang/String;)(errorMsg);
		}
		$wnd.onLoadingStart = function(message) {
			view.@com.data2semantics.yasgui.client.View::onLoadingStart(Ljava/lang/String;)(message);
		}
		$wnd.onLoadingFinish = function() {
			view.@com.data2semantics.yasgui.client.View::onLoadingFinish()();
		}
		$wnd.onQueryStart = function() {
			view.@com.data2semantics.yasgui.client.View::onQueryStart()();
		}
		$wnd.onQueryFinish = function() {
			view.@com.data2semantics.yasgui.client.View::onQueryFinish()();
		}
		$wnd.clearQueryResult = function() {
			view.@com.data2semantics.yasgui.client.View::resetQueryResult()();
		}
		$wnd.storeQueryInCookie = function() {
			view.@com.data2semantics.yasgui.client.View::storeQueryInCookie()();
		}
		$wnd.onQueryError = function(errorMsg) {
			view.@com.data2semantics.yasgui.client.View::onQueryError(Ljava/lang/String;)(errorMsg);
		}
		
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
	
	/*
	 * Calls codemirror the check the querytype of one of our codemirror instances
	 * @param queryInputId
	 * @return
	 */
	public static native String openDownDialogForCsv(String csv) /*-{
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
		  		classes: 'ui-tooltip-tipped'
			}
		});
	}-*/;
	
	
}
