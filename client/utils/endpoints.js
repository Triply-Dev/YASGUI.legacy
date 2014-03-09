(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.objs = this.Yasgui.objs || {};
	this.Yasgui.objs.Endpoints = function() {
		var endpoints =  [{
			      value: "http://dbpedia.org",
			      label: "DBPedia!!",
			      descLink: "http://google.com",
			      desc: "descccccccc",
			      isCustomEndpoint: true
			    },
			    {
			      value: "http://jquery",
			      label: "http://dbpedfffffffffffffffffffffdia.org",
			      descLink: "http://google.com",
			      desc: "descccccccc",
			      isCustomEndpoint: false
			    },
			    {
			      value: "http://dbpedia.orgg",
			      label: "Sizzle JS",
			      descLink: "http://google.com",
			      desc: "descccccccc",
			      isCustomEndpoint: false
			    }
			  ];
		
		var sortEndpoints = function() {
			endpoints.sort(dynamicSortMultiple("-isCustomEndpoint", "value"));
		};
		
		var store = function() {
			Yasgui.storage.set("endpoints", endpoints, "month", true);
		};
		var addEndpointIfNeeded = function(endpoint) {
			if (endpoint && endpoint.length > 0 && !endpointInList(endpoint)) {
				
				endpoints.push({
					value: endpoint,
					isCustomEndpoint: true
				});
				sortEndpoints();
				store();
				return true;
			}
			return false;
		};
		var stopEarly = function(index, endpoint) {
			//we know the endpoints are sorted first be 'customEndpoint', and then by value
			//So, if current item from array is !customEndpoint, and value is > parameter, we know we won't find it, and we can quit
			//this helps a little bit in efficiency
			if (!endpoints[index].isCustomEndpoint &&  endpoints[index].value > endpoint) {
				return true;
			} else {
				return false;
			}
		};
		var endpointInList = function(endpoint) {
			if (endpoints) {
				for (var i = 0; i < endpoints.length; i++) {
					if (endpoints[i].value == endpoint) {
						return true;
					}
					if (stopEarly(i, endpoint)) return false;
				}
			}
			return false;
		};
		var deleteEndpoint = function(endpoint) {
			console.log("deleting " + endpoint);
			for (var i = 0; i < endpoints.length; i++) {
				if (endpoints[i].value == endpoint) {
					console.log("found!");
					endpoints.splice(i, 1);
					store();
					return true;
				}
				
				//only delete custom endpoints. As our endpoints are ordered (first custom endpoints), we can just stop when we've reach a regular endpoint
				if (!endpoints[i].isCustomEndpoint) return false;
			}
			return false;
		};
		var loadEndpoints = function() {
			endpoints = Yasgui.storage.get("endpoints");
			if (endpoints == null) {
				endpoints = [];
			}
		};
		
		loadEndpoints();
		
		return {
			endpoints: endpoints,
			deleteEndpoint: deleteEndpoint,
			addEndpointIfNeeded: addEndpointIfNeeded
		};
	};
}).call(this);

   