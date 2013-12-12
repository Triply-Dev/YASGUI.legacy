var shouldWeFetchNotification = {
	id: "shouldFetchNoty",
	content: "YASGUI just tried to <a href='http://laurensrietveld.nl/yasgui/help.html#autocompletionmethods' target='_blank'>extract</a> properties and classes from your query, allowing you to autocomplete such URIs.<br>" +
			"However, the YASGUI server was unsuccessful in reaching your endpoint. This either means the endpoint is installed on your local computer, or running in an intranet.<br>" +
			"To support autocompletions for localhost endpoints, log in to YASGUI and configure localhost autocompletions.",
	draw: function() {
		noty({
			text: this.content,
			layout: 'bottomLeft',
			type: 'alert',
			id: this.id,
			closeWith: ["button"],
			buttons: [
			          {text: 'Login and configure autocompletions', onClick: function($noty) {
			              $noty.close();
			              setUriFetcherNotificationShown();
			              addToLoginStack("drawAutocompletionConfig();");
			              login();
			            },
			          },
			          {text: 'Ask me later', onClick: function($noty) {
			              $noty.close();
			            }
			          },
			          {text: 'Don\'t ask me again', onClick: function($noty) {
			              $noty.close();
			              setUriFetcherNotificationShown();
			            }
			          }
			        ]
		});
	},
};
var loginAndConfigureAutocompletions = function() {
	//first set callback for logging in
	
};

/*
 * CORS ajax calls and firefox are not a good match: firefox is buggy in this respect.
 * Use patch below to be able to get the content type
 * http://bugs.jquery.com/ticket/10338
 */
var _super = $.ajaxSettings.xhr;
$.ajaxSetup({
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

var fetchCompletions = function(endpoint, type) {
	var completions = [];
	var pagedCount = 0;
	var pagedIterator = 0;
	var queries = {
		"property": { 
			simple: function() {
				return "" + 
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
					"SELECT DISTINCT ?property WHERE {?property rdf:type rdf:Property} " +
					"LIMIT 5" +
					"";
			},
			paged: function(iterator, count) {
				return "" +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n " +
					"SELECT DISTINCT ?property WHERE {?property rdf:type rdf:Property}\n " +
					"ORDER BY ?property\n " +
					"LIMIT " + count + "\n " + 
					"OFFSET " + (iterator * count);
					
			}
		},
		"class": {
			simple: function() {
				return "" +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
					"SELECT DISTINCT ?class WHERE {[] rdf:type ?class} " +
					"LIMIT 101" +
					"";
			},
			paged: function(iterator, count) {
				return "" +
					"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n " +
					"SELECT DISTINCT ?class WHERE {[] rdf:type ?class}\n " +
					"ORDER BY ?class\n " +
					"LIMIT " + count + "\n " + 
					"OFFSET " + (iterator * count);
					
			}
		}
	};
	var simpleCallback = function(data) {
		console.log("simple");
		if (needPaging(data)) {
			//hmm, guess we need paging
			pagedCount = getResultsetSize(data);
			console.log("need paging?");
		} else {
			getCompletionsFromResultset(data);
			sendCompletionsToServer();
		}
	};
	var pagedCallback = function(data) {
		console.log("paged callback");
		var resultSetSize = getResultsetSize(data);
		getCompletionsFromResultset(data);
		if (resultSetSize < pagedMaxCount) {
			//we are done! just stop
		} else {
			pagedIterator++;
			fetchPaged(endpoint);
		}
	};
	var sendCompletionsToServer = function() {
		console.log("sending results to server");
		console.log(completions);
	};
	var getCompletionsFromResultset = function(data) {
		$( data ).find("uri").each(function() {
			completions.push($(this).text());
		});
	};
	
	var getResultsetSize = function(data) {
		return $( data ).find("binding[name='" + type + "']").length;
	};
	var needPaging = function(data) {
		console.log(data);
		var size = getResultsetSize(data);
		return (size > 0 && size % 100 == 0);
	};
	
	this.execQuery = function(endpoint, queryStr, callback) {
		
        var acceptHeader = "application/sparql-results+xml";
        var ajaxData = [{name: "query", value: queryStr}];
//        ajaxData.push({name: "query", value: queryStr});
        
        if (!corsEnabled[endpoint]) {
        	console.log("trying to fetch completions from js on a cors disabled endpoint!");
        }
        $.ajax({
            url : endpoint,
            type : "GET",
            headers : {
                    Accept : acceptHeader
            },
//            dataType : 'text',//get as text, let gwt parse it to gwt json object. Want to retrieve json though, so use header setting above
            data : ajaxData,
            beforeSend : function(xhr) {
                    //nothing
            },
            success : function(data, textStatus, jqXHR) {
            	callback(data);
//            	console.log(data);
//                callback(tabId, data, jqXHR.getResponseHeader('Content-Type'));
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
                        
                        console.log(errorMsg);
                    }
            },
        });
	};
	var fetchSimple = function(endpoint) {
		//first try simple
		execQuery(endpoint, queries[type].simple(), simpleCallback);
	};
	
	var fetchPaged = function(endpoint) {
		var query = queries[type].paged(pagedCount, pagedIterator);
		execQuery(endpoint, query, pagedCallback);
	};
	fetchSimple(endpoint);
//	this.execQuery("http://dbpedia.org/sparql", "select * WHERE {?x ?y ?jh} LIMIT 10", function(){console.log("woeiii");});
};

var fetchCompletionsSize = function(endpoint, type) {
	
};
