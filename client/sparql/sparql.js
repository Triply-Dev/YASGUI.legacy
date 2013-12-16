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
//				SELECT_JSON("application/sparql-results+json", "json", ".json"),
//				SELECT_XML("application/sparql-results+xml", "xml", ".xml"),
//				SELECT_CSV("text/csv", null, ".xml"),
//				SELECT_TSV("text/tab-separated-values", null, ".tsv"),
//				CONSTRUCT_TURTLE("text/turtle", "text/turtle", ".ttl"),
//				CONSTRUCT_XML("application/rdf+xml", "xml", ".xml"),
//				CONSTRUCT_CSV("text/csv", null, ".csv"),
//				CONSTRUCT_TSV("text/tab-separated-values", null, ".tsv");
		};
		var getAcceptHeader = function(acceptHeader) {
			return acceptHeader + ",*/*;q=0.9";
		};
		
		
		var query = function(tabSettings) {
			Yasgui.tabs.getCurrentTab().cm.storeInSettings();
			if (tabSettings == undefined) {
				tabSettings = Yasgui.settings.getSelectedTab();
			}
			executionId = Math.random();
			executedQueries[executionId] = true;
			Session.set("queryStatus", "busy");
			Yasgui.tabs[tabSettings.id].results.clearResults();
			var method = "GET";
			var endpoint = tabSettings.endpoint;
			var options = {
				params: {
					query: tabSettings.query
				},
				headers: {
					Accept: "application/sparql-results+json"
				}
			};
			var callback = function(error, result) {
				console.log(executedQueries);
				if (executionId in executedQueries) {
					executedQueries[executionId] = null;
					try {
						delete executedQueries[executionId];
					} catch(e){}
					console.log(result);
					if (error) {
						console.log("error");
						Yasgui.errors.draw(getHtmlAsText(error));
					} else if (result.error) {
						console.log("result error");
						console.log(result.message);
						
						Yasgui.errors.draw(getHtmlAsText(result.message));
					} else {
						var parser = Yasgui.parsers.SparqlParserFactory(result.content);
						Yasgui.tabs[tabSettings.id].results.drawContent(parser);
					}
					Yasgui.tabs[tabSettings.id].cm.check();
				} else {
					console.log("cancelled query!");
				}
			};
			
			try {
				if (false) {
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
			corsEnabled: corsEnabled,
			checkCorsEnabled: checkCorsEnabled,
			query: query,
			cancel: cancelQueries
		};
	};

	this.Yasgui.sparql = new Sparql();

})(this);

