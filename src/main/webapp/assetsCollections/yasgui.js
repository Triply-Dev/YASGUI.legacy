var corsEnabled = {};
var fetchingCors = {};
var proxy;
var sparqlHighlightHeight = {};
var sparqlHighlight = {};
var sparqlResponseHighlight = {};
var prefixes = [];
var properties = {};
var queryRequest;

//These two variables are ugly workaround with which we can distinguish in our callback between a history state changed issued
//by the browser, and issued by our code
var historyCallbackEnabled = false;
var ignoreNextHistoryPush = false;

/*
 * CORS ajax calls and firefox are not a good match: firefox is buggy in this respect.
 * Use patch below to be able to get the content type
 * http://bugs.jquery.com/ticket/10338
 */
var _super = $.ajaxSettings.xhr;
$
		.ajaxSetup({
			xhr : function() {
				var xhr = _super();
				var getAllResponseHeaders = xhr.getAllResponseHeaders;

				xhr.getAllResponseHeaders = function() {
					var allHeaders = getAllResponseHeaders.call(xhr);
					if (allHeaders) {
						return allHeaders;
					}
					allHeaders = "";
					$(
							[ "Cache-Control", "Content-Language",
									"Content-Type", "Expires", "Last-Modified",
									"Pragma" ])
							.each(
									function(i, header_name) {
										if (xhr.getResponseHeader(header_name)) {
											allHeaders += header_name
													+ ": "
													+ xhr
															.getResponseHeader(header_name)
													+ "\n";
										}
									});
					return allHeaders;
				};
				return xhr;
			}
		});

var corsNotification = "For information on CORS, and how to access your endpoint, visit the <a href=\"http://laurensrietveld.nl/yasgui/help.html\" target=\"_blank\">YASGUI help page</a> for more information.";

function sparqlQueryJson(tabId, queryStr, endpoint, acceptHeader,
		argsJsonString, requestMethod, callback) {
	
	var ajaxData = jQuery.parseJSON(argsJsonString);
	ajaxData.push({name: "query", value: queryStr});
	var uri;
	onQueryStart();
	
	if (corsEnabled[endpoint]) {
		uri = endpoint;
	} else {
		if (!isOnline()) {
			//cors disabled and not online: problem!
			var errorString = "YASGUI is current not connected to the YASGUI server. " +
				"This mean you can only access endpoints on your own computer (e.g. localhost), which are <a href=\"http://enable-cors.org/\" target=\"_blank\">CORS enabled</a>.<br>" +
				"The endpoint you try to access is either not running on your computer, or not CORS-enabled.<br>" +
				corsNotification;
	
			onQueryError(tabId, errorString);
			return;
		}
		
		//query via proxy
		ajaxData.push({name: "endpoint", value: endpoint});
		ajaxData.push({name: "requestMethod", value: requestMethod});
		requestMethod = "POST"; //we stil want to use POST to access the servlet itself
		uri = proxy;
	}
	queryRequest = $
			.ajax({
				url : uri,
				type : requestMethod,
				headers : {
					Accept : acceptHeader
				},
				dataType : 'text',//get as text, let gwt parse it to gwt json object. Want to retrieve json though, so use header setting above
				data : ajaxData,
				beforeSend : function(xhr) {
					//nothing
				},
				success : function(data, textStatus, jqXHR) {
					onQueryFinish();
					callback(tabId, data, jqXHR.getResponseHeader('Content-Type'));
				},
				error : function(jqXHR, textStatus, errorThrown) {
					if (textStatus != "abort") {
						//if user cancels query, textStatus will be 'abort'. No need to show error window then
						onQueryFinish();
						clearQueryResult();
						var errorMsg;
						if (jqXHR.status == 0 && errorThrown.length == 0) {
							checkIsOnline();
							errorMsg = "Error querying endpoint: empty response returned";
						} else {
							errorMsg = "Error querying endpoint: "
									+ jqXHR.status + " - " + errorThrown;
						}
						
						
						if (!inDebugMode() && endpointSelectionEnabled() && corsEnabled[endpoint] == false && endpoint.match(/https*:\/\/(localhost|127).*/) != null) {
							//we were trying to access a local endpoint via the proxy: this won't work...
							errorMsg += "<br><br>A possible reason for this error (next to an incorrect endpoint URL) is that you tried to send a query to an endpoint installed on your computer.<br>" +
									"This only works when the endpoint is <a href=\"http://enable-cors.org/\" target=\"_blank\">CORS enabled</a>.<br>" +
									corsNotification;
						}
						onQueryError(tabId, errorMsg);
					}
				},
			});
};

/**
 * Perform check to see if endpoint is cors enabled. Use this to decide where to send a query: via the proxy or directly
 * 
 * @param endpoint
 */
function checkCorsEnabled(endpoint) {
	//Only perform check if it hasnt been done already
	if (corsEnabled[endpoint] == null && (fetchingCors[endpoint] == null || fetchingCors[endpoint] == false)) {
		fetchingCors[endpoint] = true;
		$.ajax({
			url : endpoint,
			method : 'get',
			complete : function(xhr) {
				if (xhr.status != 0) { // CORS-enabled site
					corsEnabled[endpoint] = true;
				} else {
					corsEnabled[endpoint] = false;
				}
				fetchingCors[endpoint] = false;
			}
		});
	}
}
$(document).keydown(function(e) {
	var code = (e.keyCode ? e.keyCode : e.which);
	if (code == 27) {//escape key
		cancelQuery();
	}
	if (
			(code == 10 || code == 13) //enter
			&& (e.ctrlKey || e.metaKey) //ctrl or apple cmd key
		) {
		executeQuery();
	}

});

function updateBookmarkCmHeight(elementId) {
	cmHeight = sparqlHighlight[elementId].getWrapperElement().offsetHeight;
	if (sparqlHighlightHeight[elementId]) {
		if (cmHeight != sparqlHighlightHeight[elementId]) {
			sparqlHighlightHeight[elementId] = cmHeight;
			adjustBookmarkQueryInputForContent(cmHeight);
		}
	} else {
		sparqlHighlightHeight[elementId] = cmHeight;
	}
}

function initializeQueryCodemirror(elementId) {
	var qInput = document.getElementById(elementId);
	if (qInput) {
		if (sparqlHighlight[elementId] == null) { 
			//Only add if it hasnt been drawn yet
			sparqlHighlight[elementId] = CodeMirror.fromTextArea(qInput, {
				mode : "sparql11",
				theme: "yasgui",
				highlightSelectionMatches: {showToken: /\w/},
				tabMode : "indent",
				lineNumbers : true,
				gutters: ["gutterErrorBar","CodeMirror-linenumbers" ],
				matchBrackets : true,
				fixedGutter: true,
				extraKeys : {
					"Ctrl-D" : "deleteLines",
					"Ctrl-K" : "deleteLines",
					"Cmd-D" : "deleteLines",
					"Cmd-K" : "deleteLines",
					"Ctrl-/" : "commentLines",
					"Cmd-/" : "commentLines",
					"Ctrl-Alt-Down" : "copyLineDown",
					"Ctrl-Alt-Up" : "copyLineUp",
					"Cmd-Alt-Down" : "copyLineDown",
					"Cmd-Alt-Up" : "copyLineUp",
					"Shift-Ctrl-F": "doAutoFormat",
					"Shift-Cmd-F": "doAutoFormat"
				}
			});
			
			sparqlHighlight[elementId].on("change", function(cm, change){
				checkSyntax(cm, true);
				setQueryType(cm.getStateAfter().queryType);
				height = sparqlHighlight[elementId].getWrapperElement().offsetHeight;
				if (sparqlHighlightHeight[elementId]) {
					if (height != sparqlHighlightHeight[elementId]) {
						sparqlHighlightHeight[elementId] = height;
						adjustQueryInputForContent();
					}
				} else {
					sparqlHighlightHeight[elementId] = height;
				}
				CodeMirror.showHint(cm, CodeMirror.allAutoCompletions, {closeCharacters: /(?=a)b/});
				appendPrefixIfNeeded(cm);
			});
			sparqlHighlight[elementId].on("gutterClick", function(cm, change) {
				saveTabTitle();
			});
			sparqlHighlight[elementId].on("focus", function(cm, change) {
				saveTabTitle();
			});
			sparqlHighlight[elementId].on("blur", function(cm, change) {
				storeQueryInCookie();
			});
			//init query type
			setQueryType(sparqlHighlight[elementId].getStateAfter().queryType);
			
			
			//Append another classname to the codemirror div, so we can set width and height via css
			if (qInput.nextSibling != null && qInput.nextSibling.className == "CodeMirror") {
				qInput.nextSibling.className = "CodeMirror queryCm";
				scrollElement = qInput.nextSibling.getElementsByClassName("CodeMirror-scroll");
				//use jquery for this (a bit easier). for this element, find scroll class, and append another class
				$("#"+elementId).next().find($(".CodeMirror-scroll")).addClass("queryScrollCm");
			}
		}
	} else {
		onError("no text area for query input id: " + elementId);
	}
}

function initializeQueryResponseCodemirror(elementId, mode) {
	var qInput = document.getElementById(elementId);
	if (qInput) {
		var drawCodeMirror = false;
		if (sparqlResponseHighlight[elementId] == null) drawCodeMirror = true;
		
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
			sparqlResponseHighlight[elementId] = CodeMirror.fromTextArea(document.getElementById(elementId), {
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
				$("#"+elementId).next().find($(".CodeMirror-scroll")).addClass("resultScrollCm");
			}
		}
	} else {
		onError("no text area to create sparql response highlight for. Input id: " + elementId);
	}
}

function initializeQueryBookmarkCodemirror(elementId) {
	var qInput = document.getElementById(elementId);
	if (qInput) {
		sparqlHighlight[elementId] = CodeMirror.fromTextArea(qInput, {
			mode : "sparql11",
			tabMode : "indent",
			theme: "yasgui",
			highlightSelectionMatches: {showToken: /\w/},
			gutters: ["gutterErrorBar","CodeMirror-linenumbers" ],
			lineNumbers : true,
			matchBrackets : true,
			fixedGutter: true,
			viewportMargin: Infinity,
			extraKeys : {
				"Ctrl-D" : "deleteLines",
				"Ctrl-K" : "deleteLines",
				"Cmd-D" : "deleteLines",
				"Cmd-K" : "deleteLines",
				"Ctrl-/" : "commentLines",
				"Cmd-/" : "commentLines",
				"Ctrl-Alt-Down" : "copyLineDown",
				"Ctrl-Alt-Up" : "copyLineUp",
				"Cmd-Alt-Down" : "copyLineDown",
				"Cmd-Alt-Up" : "copyLineUp",
				"Shift-Ctrl-F": "doAutoFormat",
				"Shift-Cmd-F": "doAutoFormat"
			}
		});
		
		sparqlHighlight[elementId].on("change", function(cm, change){
			checkSyntax(cm, false);
			setQueryType(cm.getStateAfter().queryType);
			updateBookmarkCmHeight(elementId);
			CodeMirror.showHint(cm, CodeMirror.prefixHint, {closeCharacters: /(?=a)b/});
			appendPrefixIfNeeded(cm);
		});
		sparqlHighlight[elementId].on("blur", function(cm, change) {
			updateBookmarkedQuery();
		});
			
		updateBookmarkCmHeight(elementId);
		//Append another classname to the codemirror div, so we can set width and height via css
		if (qInput.nextSibling != null && qInput.nextSibling.className == "CodeMirror") {
			qInput.nextSibling.className = "CodeMirror bookmarkCm";
			scrollElement = qInput.nextSibling.getElementsByClassName("CodeMirror-scroll");
			//use jquery for this (a bit easier). for this element, find scroll class, and append another class
			$("#"+elementId).next().find($(".CodeMirror-scroll")).addClass("bookmarkScrollCm");
		}
	} else {
		onError("no text area for bookmark query input id: " + elementId);
	}
	
}


var latestComplete = -1;
var easing = 100;
function progressbarUpdate(percent, $element) {
	var progressBarWidth = percent * $element.width() / 100;
	var newText = percent + "%&nbsp;";
	if ($element.find('div').html() != newText) {
		if (latestComplete != -1 && latestComplete < (percent-1)) {
			//the previous animation is still busy... Don't want a lag, so lower our easing factor
			easing = Math.round(easing / 2);
		}
		$element.find('div').animate(
			{width: progressBarWidth },
			easing,
			function(){latestComplete=percent;}).html(newText);
	}
}
Array.prototype.equals = function (array) {
    // if the other array is a falsy value, return
    if (!array)
        return false;

    // compare lengths - can save a lot of time
    if (this.length != array.length)
        return false;

    for (var i = 0; i < this.length; i++) {
        // Check if we have nested arrays
        if (this[i] instanceof Array && array[i] instanceof Array) {
            // recurse into the nested arrays
            if (!this[i].compare(array[i]))
                return false;
        }
        else if (this[i] != array[i]) {
            // Warning - two different object instances will never be equal: {x:20} != {x:20}
            return false;
        }
    }
    return true;
};
