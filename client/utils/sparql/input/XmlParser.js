(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.parsers = this.Yasgui.parsers || {};
	
	var XmlParser = function(xml, actualResponseString) {
		var metaInfo = $.grep(Yasgui.sparql.acceptHeaders.select, function( element, index ) {
			  return element.extension == "json";
		}).get(0);
		var getVariables = function() {
			var vars = [];
			xml.find("head").children().each(function(key, value){
				vars.push($(value).attr("name"));
			});
			return vars;
		};
		
		var getBindings = function() {
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
			var result = null;
			var booleanEl = xml.find("boolean");
			if (booleanEl.get().length > 0) {
				if (booleanEl.get()[0].innerHTML.toLowerCase() == "true") {
					result = true;
				} else {
					result = false;
				}
			}
			return result;
		};
		var getResponse = function() {
			return actualResponseString;
		};
		
		var getCmMode = function() {
			return "xml";
		};
		return {
			getVariables: getVariables,
			getBindings: getBindings,
			getBoolean: getBoolean,
			getResponse: getResponse,
			getCmMode: getCmMode,
			getMetaInfo: metaInfo
		};
	};
	
	Yasgui.parsers.XmlParser = XmlParser;
})(this);