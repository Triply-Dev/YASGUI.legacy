(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.parsers = this.Yasgui.parsers || {};
	
	var DelimitedParser = function(arrays, actualResponseString) {
		var getVariables = function() {
			if (arrays.length > 0) {
				return arrays[0];
			} else {
				return null;
			}
		};
		
		var guessTypeFromValue = function(value) {
			if (value.startsWith("http")) {
				return "uri";
			} else {
				return "literal";
			}
		};
		var getBindings = function() {
			var result = null;
			if (arrays.length > 1) {
				var variables = getVariables();
				result = [];
				for (var rowId = 1; rowId < arrays.length; rowId++) {
					var row = arrays[rowId];
					var bindings = {};
					
					for (var colId = 0; colId < row.length; colId++) {
						var variable = variables[colId];
						bindings[variable] = {
							value: row[colId],
							type: guessTypeFromValue(row[colId])
						};
					}
					result.push(bindings);
				}
			}
			return result;
		};
		
		var getBoolean = function() {
			var result = null;
			if (arrays.length > 1
					&& arrays[0].length > 0 && arrays[0][0] == "bool"
					&& arrays[1].length > 0 && (arrays[1][0] == "1" || arrays[1][0] == "0")) {
				result = (arrays[1][0] == "1"? true: false);
			}
			return result;
		};
		var getResponse = function() {
			return actualResponseString;
		};
		
		var getCmMode = function() {
			return "json";
		};
		return {
			getVariables: getVariables,
			getBindings: getBindings,
			getBoolean: getBoolean,
			getResponse: getResponse,
			getCmMode: getCmMode
		};
	};
	
	Yasgui.parsers.DelimitedParser = DelimitedParser;
})(this);