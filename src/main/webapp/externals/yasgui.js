var corsEnabled = {};
var proxy;
function sparqlQueryJson(queryStr, endpoint, callback) {
	var ajaxData = {
		query : queryStr,
		format: 'application/sparql-results+json' //some endpoints use the format parameter to set accept header
	};
	var uri;
	if (corsEnabled[endpoint]) {
		onLoadingStart("Executing Query");
		console.log("query directly");
		uri = endpoint;
	} else {
		onLoadingStart("Executing Query (proxy)");
		console.log("query via servlet");
		//query via proxy
		ajaxData['endpoint'] = endpoint;
		uri = proxy;
	}
	$.ajax({
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
		success : function(data) {
			onLoadingFinish();
			callback(data);
		},
		error : function(jqXHR, textStatus, errorThrown) {
			onLoadingFinish();
			onError("Error " + jqXHR.status + ": " + errorThrown);
		}
	});
};

function checkCorsEnabled(endpoint) {
	//Start off assuming it is not cors enabled
	corsEnabled[endpoint] = false;
	$.ajax({
		url : endpoint,
		method : 'get',
		beforeSend : function (xhr) {
//			$("#result").html("Trying to perform a cross-origin request to " + targetSite);
	    },
		complete : function(xhr) {
			if(xhr.status != 0) { // CORS-enabled site
				corsEnabled[endpoint] = true;
			} else { 
				corsEnabled[endpoint] = false;
			}
		}
	});
}