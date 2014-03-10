(function(){
	this.Yasgui = this.Yasgui || {};
	//keep track, so we are able to cancel queries
	var executedQueries = {};
	var Sparql = function() {
		var corsEnabled = {};
		var checkCorsEnabled = function(endpoint) {
			//Only perform check if it hasnt been done already
			if (corsEnabled[endpoint] == null) {
				$.ajax({
					url : endpoint,
					data: {query: "ASK {?sub ?pred ?obj}"},
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
		};
		
		var acceptHeaders = {
			select: [
				{
					header: "application/sparql-results+json",
					extension: "json",
					name: "JSON",
					cmMode: {
						name: "javascript",
						json: true
					},
				},
				{
					header: "application/sparql-results+xml",
					extension: "xml",
					name: "XML",
					cmMode: "xml"
				},
				{
					header: "text/csv",
					extension: "csv",
					name: "CSV",
					cmMode: "json"
				},
				{
					header: "text/tab-separated-values",
					extension: ".tsv",
					name: "TSV",
					cmMode: "json"
				},
			],
			graph: [
				{
					header: "text/turtle",
					extension: "ttl",
					name: "Turtle",
					cmMode: "turtle"
				},
				{
					header: "application/rdf+xml",
					extension: "xml",
					name: "RDF/XML",
					cmMode: "xml"
				},
				{
					header: "text/csv",
					extension: "csv",
					name: "CSV",
					cmMode: "json"
				},
				{
					header: "text/tab-separated-values",
					extension: "tsv",
					name: "TSV",
					cmMode: "json"
				}
				
			]
	
		};
		var getAcceptHeader = function(tabSettings) {
			var qType = Yasgui.tabs[tabSettings.id].cm.getQueryType();
			var acceptHeader;
			if (qType == "CONSTRUCT" || qType == "DESCRIBE") {
				//Change content type automatically for construct queries
				acceptHeader = tabSettings.contentTypeGraph;
			} else {
				acceptHeader = tabSettings.contentTypeSelect;
			}
			return acceptHeader + ",*/*;q=0.9";
		};
		
		var getHeaders = function(tabSettings) {
			var headers = {
					Accept: getAcceptHeader(tabSettings)
			};
			var customHeaders = tabSettings.headers || {};
			$.extend(headers, customHeaders);
			
			return headers;
			
		};
		
		var getParams = function(tabSettings) {
			var params = [{ name: "query", value: tabSettings.query }];
			var customParams = tabSettings.params || [];
			$.merge(params, customParams);
			if (tabSettings.defaultGraphs) {
				for (var i = 0; i < tabSettings.defaultGraphs.length; i++) {
					params.push({name: "default-graph-uri", value: tabSettings.defaultGraphs[i]});
				}
			}
			if (tabSettings.namedGraphs) {
				for (var i = 0; i < tabSettings.namedGraphs.length; i++) {
					params.push({name: "named-graph-uri", value: tabSettings.namedGraphs[i]});
				}
			}
			return params;
		};
		
		var query = function(tabSettings) {
			var callback = function(error, result) {
				if (executionId in executedQueries) {
					deleteKey(executedQueries, executionId);
					if (error) {
						console.log("error1");
						console.log(error);
						console.log(result);
						onQueryError(error.message);
					} else if (result.error) {
						console.log("result error");
						console.log(result.message);
						
						onQueryError(result.message);
					} else {
						var referencedTabSettings = Yasgui.settings.getTabById(tabSettings.id);
						if (referencedTabSettings) {
							if (result.content.length < 100000) {
								referencedTabSettings.results = result;
							} else {
								referencedTabSettings.results = null;
							}
						}
						
						
						Yasgui.tabs[tabSettings.id].results.drawContent(result);
					}
					Yasgui.tabs[tabSettings.id].cm.check();
				} else {
					console.log("cancelled query!");
				}
			};
			var onQueryError = function(errorMsg) {
				var content = $("<div></div>");
				
				content.append($("<div></div>").html(errorMsg));
				
				var openQuery = $("<button>Open query in new window</button>")
					.on("click", function(){
						window.open(tabSettings.endpoint + "?" + paramsString);
					})
					.button();
				content.append($("<div style='margin-top: 10px;text-align: center; width: 100%'></div>").append(openQuery));
				Yasgui.widgets.errorDialog({
					content: content,
					title: "Error Executing Query"
				});
			};
			
			Yasgui.tabs.getCurrentTab().cm.storeInSettings();
			if (tabSettings == undefined) {
				tabSettings = Yasgui.settings.getSelectedTab();
			}
			executionId = Math.random();
			executedQueries[executionId] = true;
			Session.set("queryStatus", "busy");
			Yasgui.tabs[tabSettings.id].results.clearResults();
			var method = tabSettings.requestMethod;
			var endpoint = tabSettings.endpoint;
			
			
			var options = {
					headers: getHeaders(tabSettings)
				};
			
			var paramsString = $.param(getParams(tabSettings));
			if (method == "GET") {
				options.query = paramsString;
			} else {
				options.content = paramsString;
				options.headers['Content-Type'] = "application/x-www-form-urlencoded";
			}
//			return;
//			console.log(options);
			
			try {
				if (corsEnabled[endpoint]) {
					HTTP.call(method, endpoint, options, callback);
				} else {
					Meteor.call("query", method, endpoint, options, callback);
				}
				
			} catch (e) {
				Yasgui.tabs[tabSettings.id].cm.check();
				console.log("caught error", e);
			}
			
		};
		
		var cancelQueries = function() {
			executedQueries = {};
		};
		return {
			acceptHeaders: acceptHeaders,
			corsEnabled: corsEnabled,
			checkCorsEnabled: checkCorsEnabled,
			query: query,
			cancel: cancelQueries
		};
	};

	this.Yasgui.sparql = new Sparql();

})(this);

