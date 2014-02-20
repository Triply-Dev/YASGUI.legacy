(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.parsers = this.Yasgui.parsers || {};
	
	var JsonParser = function(json, actualResponseString) {
		
		var getVariables = function() {
			if ("head" in json) {
				return json.head.vars;
			} else {
				return null;
			}
		};
		
		var getBindings = function() {
			if ("results" in json) {
				return json.results.bindings;
			} else {
				return null;
			}
		};
		
		var getBoolean = function() {
			if ("boolean" in json) {
				return json.boolean;
			} else {
				return null;
			}
		};
		var getResponse = function() {
			return actualResponseString;
		};
		
		var getContentType = function() {
			console.log("todo: getting content type from parser");
			return "json";
		};
		return {
			getVariables: getVariables,
			getBindings: getBindings,
			getBoolean: getBoolean,
			getResponse: getResponse,
			getContentType: getContentType
		};
	};
	
	Yasgui.parsers.JsonParser = JsonParser;
})(this);