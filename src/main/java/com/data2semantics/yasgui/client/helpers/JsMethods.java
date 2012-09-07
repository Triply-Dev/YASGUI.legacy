package com.data2semantics.yasgui.client.helpers;

import com.data2semantics.yasgui.client.View;

public class JsMethods {
	
	public static native void destroyCodeMirror(String queryInputId) /*-{
			if ($wnd.sparqlHighlight[queryInputId] != null) { 
				$wnd.sparqlHighlight[queryInputId] = null;
			}
	}-*/;
	
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
						"Ctrl-S": "storeSettings"
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

	
	public static native void queryJson(String queryString, String endpoint) /*-{
		$wnd.sparqlQueryJson(queryString, endpoint, function(jsonResult) {$wnd.drawResultsInTable(jsonResult);});
	}-*/;
	
	
	public static native void saveCodeMirror(String id) /*-{
		$wnd.sparqlHighlight[id].save();
	}-*/;
	
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
	

	public static native String getQueryResult() /*-{
		return jsonResults;
	}-*/;
	
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
		$wnd.onError = function(jsonResult) {
			view.@com.data2semantics.yasgui.client.View::onError(Ljava/lang/String;)(jsonResult);
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
		$wnd.clearQueryResult = function() {
			view.@com.data2semantics.yasgui.client.View::resetQueryResult()();
		}
		$wnd.storeQueryInCookie = function() {
			view.@com.data2semantics.yasgui.client.View::storeQueryInCookie()();
		}
		
	}-*/;
	
	
	public static native void setProxyUriInVar(String proxy) /*-{
		$wnd.proxy = proxy;
	}-*/;
	public static native void setTabBarProperties(int margin) /*-{
		$wnd.isc.TabBar.addProperties({layoutStartMargin:margin});
	}-*/;
	
}
