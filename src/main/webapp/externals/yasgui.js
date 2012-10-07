var corsEnabled = {};
var proxy;
var sparqlHighlight = {};
var sparqlResponseHighlight = {};
var prefixes;
var queryRequest;

function sparqlQueryJson(tabId, queryStr, endpoint, callback) {
	var ajaxData = {
		query : queryStr,
		format: 'application/sparql-results+json' //some endpoints use the format parameter to set accept header
	};
	var uri;
	onQueryStart();
	if (corsEnabled[endpoint]) {
		console.log("query directly");
		uri = endpoint;
	} else {
		//query via proxy
		ajaxData['endpoint'] = endpoint;
		uri = proxy;
	}
	queryRequest = $.ajax({
		url : uri,
		type : 'POST',
		headers: { 
	        Accept : 'application/sparql-results+json'
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