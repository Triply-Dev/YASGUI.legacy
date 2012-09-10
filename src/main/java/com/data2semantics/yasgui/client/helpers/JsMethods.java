package com.data2semantics.yasgui.client.helpers;

import com.data2semantics.yasgui.client.View;

public class JsMethods {
	
	/**
	 * Unset a tabmirror object. Used when closing a tab
	 * 
	 * @param queryInputId Id of the text area of this codemirror instance
	 */
	public static native void destroyCodeMirror(String queryInputId) /*-{
			if ($wnd.sparqlHighlight[queryInputId] != null) { 
				$wnd.sparqlHighlight[queryInputId] = null;
			}
	}-*/;
	
	/**
	 * Initialize and atatch codemirror to a text area
	 * 
	 * @param queryInputId Id of text area to attach codemirror to
	 */
	public static native void attachCodeMirror(String queryInputId) /*-{
		if ($doc.getElementById(queryInputId)) {
			if ($wnd.sparqlHighlight[queryInputId] == null) { 
				//Only add if it hasnt been drawn yet
				$wnd.sparqlHighlight[queryInputId] = $wnd.CodeMirror.fromTextArea($doc
						.getElementById(queryInputId), {
					mode : "application/x-sparql-query",
					tabMode : "indent",
					lineNumbers : true,
					matchBrackets : true,
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
	
	
	public static native void setAutocompletePrefixes(String prefixes) /*-{
		$wnd.prefixes = eval(prefixes);
	}-*/;

	/**
	 * Query an endpoint. Asynchronous. A callback function (drawResultsInTable) is used to process query result.
	 * 
	 * @param queryString
	 * @param endpoint
	 */
	public static native void queryJson(String queryString, String endpoint) /*-{
		$wnd.sparqlQueryJson(queryString, endpoint, function(jsonResult) {$wnd.drawResultsInTable(jsonResult);});
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
		$wnd.drawResultsInTable = function(jsonResult) {
			view.@com.data2semantics.yasgui.client.View::drawResultsInTable(Ljava/lang/String;)(jsonResult);
		}
		$wnd.onError = function(errorMsg) {
			view.@com.data2semantics.yasgui.client.View::onError(Ljava/lang/String;)(errorMsg);
		}
		$wnd.onLoadingStart = function(message) {
			if (message == undefined) {
				view.@com.data2semantics.yasgui.client.View::onLoadingStart()();
			} else {
				view.@com.data2semantics.yasgui.client.View::onLoadingStart(Ljava/lang/String;)(message);
			}
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

	
}
