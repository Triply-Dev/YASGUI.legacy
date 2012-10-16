var corsEnabled = {};
var proxy;
var sparqlHighlightHeight = {};
var sparqlHighlight = {};
var sparqlResponseHighlight = {};
var prefixes;
var queryRequest;

/*
 * CORS ajax calls and firefox are not a good match: firefox is buggy in this respect.
 * Use patch below to be able to get the content type
 * http://bugs.jquery.com/ticket/10338
 */
var _super = $.ajaxSettings.xhr;
$.ajaxSetup( {
    xhr: function ()
    {
        var xhr = _super();
        var getAllResponseHeaders = xhr.getAllResponseHeaders;

        xhr.getAllResponseHeaders = function ()
        {
            var allHeaders = getAllResponseHeaders.call( xhr );
            if( allHeaders )
            {
                return allHeaders;
            }
            allHeaders = "";
            $( ["Cache-Control", "Content-Language", "Content-Type",
                "Expires", "Last-Modified", "Pragma"] ).each( function ( i, header_name )
                    {
                        if( xhr.getResponseHeader( header_name ) )
                        {
                            allHeaders += header_name + ": " + xhr.getResponseHeader( header_name ) + "\n";
                        }
                    } );
            return allHeaders;
        };
        return xhr;
    }
} );
function sparqlQueryJson(tabId, queryStr, endpoint, acceptHeader, argsJsonString, requestMethod, callback) {
	var ajaxData = {
		query : queryStr,
	};
	var uri;
	onQueryStart();
	if (corsEnabled[endpoint]) {
		uri = endpoint;
	} else {
		//query via proxy
		ajaxData['endpoint'] = endpoint;
		ajaxData['requestMethod'] = requestMethod;
		requestMethod = "POST"; //we stil want to use POST to access the servlet itself
		uri = proxy;
	}
	args = jQuery.parseJSON( argsJsonString );
	if (args != null) {
		for (var key in args) {
		  if (args.hasOwnProperty(key)) {
			  ajaxData[key] = args[key];
		  }
		}
	}
	queryRequest = $.ajax({
		url : uri,
		type : requestMethod,
		headers: { 
	        Accept : acceptHeader
		},
		dataType : 'text',//get as text, let gwt parse it to gwt json object. Want to retrieve json though, so use header setting above
		data: ajaxData,
		beforeSend : function(xhr) {
			//nothing
		},
		success : function(data, textStatus, jqXHR) {
			console.log(tabId);
			onQueryFinish();
			callback(tabId, data, jqXHR.getResponseHeader('Content-Type'));
		},
		error : function(jqXHR, textStatus, errorThrown) {
			if (textStatus != "abort") {
				//if user cancels query, textStatus will be 'abort'. No need to show error window than
				onQueryFinish();
				clearQueryResult();
				onQueryError("Error querying endpoint: " + jqXHR.status + " - " + errorThrown);
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
		//Start off assuming it is not cors enabled
		$.ajax({
			url : endpoint,
			method : 'get',
			complete : function(xhr) {
				if(xhr.status != 0) { // CORS-enabled site
					corsEnabled[endpoint] = true;
				} else {
					corsEnabled[endpoint] = false;
				}
			}
		});
	}
}