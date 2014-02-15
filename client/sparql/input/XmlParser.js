(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.parsers = this.Yasgui.parsers || {};
	
	var XmlParser = function(xml, actualResponseString) {
		console.log("xml parser", actualResponseString);
		var getVariables = function() {
			var vars = [];
			xml.find("head").children().each(function(key, value){
				vars.push($(value).attr("name"));
			});
			return vars;
		};
		
		var getBindings = function() {
			console.log(xml.find("bindings"));
			var querySolutions = [];
//			console.log(xml.find("results").children());
			xml.find("results").children().each(function (qsKey, value){
//				console.log(value);
				var querySolution = {};
				$(value).children().each(function(bindingKey, binding){
					var variable = $(binding).attr("name");
					
					var bindingInfo  = $(binding).children().first();
					var type = bindingInfo[0].nodeName;
					var value = bindingInfo[0].innerHTML;
					
					console.log(bindingInfo);
					querySolution[variable] = {
						"type": type,
						"value": value
					};
					if ($(bindingInfo).attr("datatype")) {
						querySolution[variable]["datatype"] = type.attr("datatype");
					}
				});
				querySolutions.push(querySolution);
				
			});
			return querySolutions;
		};
		
		var getBoolean = function() {
			console.log("todo: parse boolean" )
//			console.log(actualResponseString);
//			var boolean = xml.find("boolean");
//			console.log("boolean");
//			if (boolean) {
//				console.log(boolean);
//				console.log(boolean.innerHTML);
//			}
//			if ("boolean" in json) {
//				return json.boolean;
//			} else {
//				return null;
//			}
		};
		var getResponse = function() {
			return actualResponseString;
		};
		
		var getContentType = function() {
			console.log("todo: getting content type from parser");
			return "xml";
		};
		return {
			getVariables: getVariables,
			getBindings: getBindings,
			getBoolean: getBoolean,
			getResponse: getResponse,
			getContentType: getContentType
		};
	};
	
	Yasgui.parsers.XmlParser = XmlParser;
})(this);