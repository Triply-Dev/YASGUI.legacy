
/**
 * supported functionality:
 * parent -> child:
 * 		- run query
 * 		- get query string
 * 		- get endpoint
 * 
 * child -> parent:
 * 		- send query results
 * 		- send query string
 * 		- send endpoint string
 * 
 * 
 * iframe: iframe obj to use
 * yasguiUrl: url to include in iframe
 * config: {
 * 		yasguiSettings: yasgui settings
 * 		ignoreWarnings: true/false (ignore warnings. possible warning: too large of a url string for IE)
 * }
 */


var yasguiIframeManager = function(iframe, yasguiUrl, config) {
	var callbacks = {};
	var iframeUrl = yasguiUrl;
	
	var startsWith = function(stringToTest, startWith) {
		return stringToTest.slice(0, startWith.length) == startWith;
	};
	var addListener = function() {
		var eventMethod = window.addEventListener ? "addEventListener" : "attachEvent";
		var eventer = window[eventMethod];
		var messageEvent = eventMethod == "attachEvent" ? "onmessage" : "message";

		// Listen to message from child window
		eventer(messageEvent,function(e) {
			if (startsWith(e.data, "queryString_") && callbacks.queryString) {
				callbacks.queryString(decodeURIComponent(e.data.substring("queryString_".length)));
			}
		},false);
	};
	addListener();
	
	
	
	if (config.yasguiSettings) iframeUrl = "?settings="	+ encodeURIComponent(JSON.stringify(config.yasguiSettings));
	if (!config.ignoreWarnings && iframeUrl.length > 2048) alert("The iframe you try to load has more than 2048 characters (specifically, " + iframeUrl.length + " characters). This won't work in some browsers (e.g. Internet Explorer)");
	
	
	iframe.src = iframeUrl;
	
	
	
	
	return {
		sendmsg: function(callback) {
			var msg = "sizing";
			console.log("sending msg: ", msg, iframeUrl);
			iframe.contentWindow.postMessage(msg, iframeUrl);
		},
		execQuery: function(callback){
			if (callback) {
				callbacks.queryResults = callback;
			} else {
				callbacks.queryResults = null;
			}
			iframe.contentWindow.postMessage("execQuery_" + (callback? "1": "0"), iframeUrl);
		},
		getQuery: function(callback) {
			if (callback) {
				callbacks.queryString = callback;
			} else {
				callbacks.queryString = null;
			}
			iframe.contentWindow.postMessage("getQuery", iframeUrl);
		}
		
		
	}
};