package com.data2semantics.yasgui.client;

public class JsMethods {
	
	/**
	 * This function registers the (static) java methods which should be callable from javascript
	 */
	public static native void exportCallableJavaMethods() /*-{
	    $wnd.storeSettingsInCookie =
	       $entry(@com.data2semantics.yasgui.client.queryform.Helper::getAndStoreSettingsInCookie());
	 }-*/;
	
	public static native void attachCodeMirror(String queryInputId) /*-{
		if ($doc.getElementById(queryInputId)) {
			$wnd.CodeMirror.commands.autocomplete = function(cm) {
				$wnd.CodeMirror.simpleHint(cm, $wnd.CodeMirror.prefixHint);
			}
			$wnd.sparqlHighlight = $wnd.CodeMirror.fromTextArea($doc
					.getElementById(queryInputId), {
				mode : "application/x-sparql-query",
				tabMode : "indent",
				lineNumbers : true,
				matchBrackets : true,
				onCursorActivity : function() {
					$wnd.sparqlHighlight
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
				}
			});
		}
	}-*/;

	public static native void setAutocompletePrefixes(String prefixes) /*-{
		$wnd.prefixes = eval(prefixes);
	}-*/;

	
	public static native void sparqlQueryJson(String queryString, String endpoint, String callbackFunction) /*-{
		sparqlQueryJson(queryString, endpoint, callbackFunction);
	}-*/;
	
	public static native void saveCodeMirror() /*-{
		$wnd.sparqlHighlight.save();
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
		// get the one with value 'yes'
		for (var i=0, iLen=elements.length; i<iLen; i++) {
		  result = elements[i].value;
		  break;
		}
		return result;
	}-*/;
}
