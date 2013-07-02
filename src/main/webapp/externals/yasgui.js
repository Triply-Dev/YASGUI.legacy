var corsEnabled = {};
var proxy;
var sparqlHighlightHeight = {};
var sparqlHighlight = {};
var sparqlResponseHighlight = {};
var prefixes;
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
function sparqlQueryJson(tabId, queryStr, endpoint, acceptHeader,
		argsJsonString, requestMethod, callback) {
	var ajaxData = {
		query : queryStr,
	};
	var uri;
	onQueryStart();
	
	
	if (corsEnabled[endpoint]) {
		uri = endpoint;
	} else {
		if (!inSingleEndpointMode() && corsEnabled[endpoint] == false && endpoint.match(/https*:\/\/(localhost|127).*/) != null) {
			//we are trying to access a local endpoint via the proxy: this won't work...
			var errorString = "You are trying to send a query to an endpoint installed on your local computer.<br>" +
					"This only works when the endpoint is <a href=\"http://enable-cors.org/\" target=\"_blank\">CORS enabled</a> or when the endpoint is accessible on the same port as YASGUI (i.e. port 80).<br>" +
					"Documentation on how to enable CORS for some endpoints:<ul>" +
					"<li><a href=\"http://4store.org/trac/wiki/SparqlServer\" target=\"_blank\">4store</a></li>" +
					"<li><a href=\"http://www.openlinksw.com/dataspace/doc/dav/wiki/Main/VirtTipsAndTricksGuideCORSSetup\" target=\"_blank\">virtuoso</a></li>" +
					"<li>OpenRDF Sesame: not possible yet (see <a href=\"https://openrdf.atlassian.net/browse/SES-1757\" target=\"_blank\">this issue</a>)" +
					"</ul>" +
					"Instead, you can also configure the endpoint to run via port 80 (the same as YASGUI)";
			
			onQueryError(tabId, errorString);
			return;
		}
		//query via proxy
		ajaxData['endpoint'] = endpoint;
		ajaxData['requestMethod'] = requestMethod;
		requestMethod = "POST"; //we stil want to use POST to access the servlet itself
		uri = proxy;
	}
	args = jQuery.parseJSON(argsJsonString);
	if (args != null) {
		for ( var key in args) {
			if (args.hasOwnProperty(key)) {
				ajaxData[key] = args[key];
			}
		}
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
							errorMsg = "Error querying endpoint: empty response returned";
						} else {
							errorMsg = "Error querying endpoint: "
									+ jqXHR.status + " - " + errorThrown;
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
	if (corsEnabled[endpoint] == null) {
		$.ajax({
			url : endpoint,
			method : 'get',
			complete : function(xhr) {
				if (xhr.status != 0) { // CORS-enabled site
					corsEnabled[endpoint] = true;
				} else {
					corsEnabled[endpoint] = false;
				}
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
			&& (e.ctrlKey || code == e.metaKey) //ctrl or apple cmd key
		) {
		executeQuery();
	}

});

