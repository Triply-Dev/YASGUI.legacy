package com.data2semantics.yasgui.client;

public class JsMethods {
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
					"Ctrl-Alt-Up" : "copyLineUp"
				},
			});
		}
	}-*/;

	public static native void setAutocompletePrefixes(String prefixes) /*-{
		$wnd.prefixes = eval(prefixes);
	}-*/;

	public static native String getQuery(String queryInputId) /*-{
		query = "";
		$wnd.sparqlHighlight.save();
		if ($doc.getElementById(queryInputId)) {
			if ($doc.getElementById(queryInputId).value) {
				query = $doc.getElementById(queryInputId).value;
			}
		}
		return query;
	}-*/;
}
