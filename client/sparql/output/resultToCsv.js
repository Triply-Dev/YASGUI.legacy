(function(){

	
	this.resultToCsv = function(result) {
		var quote = "\"";
		var delimiter = ",";
		var lineBreak= "\n";
		
		var variables = result.getVariables();
		var querySolutions= result.getBindings();
		
		csvString = "";

		var get = function() {
			createHeader();
			createBody();
			return csvString;
		};
		
		var createHeader = function() {
			for (var i = 0; i < variables.length; i++) {
				addValueToString(variables[i]);
			}
			csvString += lineBreak;
		};
		
		var createBody = function() {
			for (var i = 0; i < querySolutions.length; i++) {
				addQuerySolutionToString(querySolutions[i]);
				csvString += lineBreak;
			}
		};
		
		var addQuerySolutionToString = function(querySolution) {
			for (var i = 0; i < variables.length; i++) {
				var variable = variables[i];
				if(querySolution.hasOwnProperty(variable)){
					addValueToString(querySolution[variable]["value"]);
				} else {
					addValueToString("");
				}
			}
		};
		var addValueToString = function(value) {
			//Quotes in the string need to be escaped
			value.replace(quote, quote + quote);
			if (needToQuoteString(value)) {
				value = quote + value + quote;
			}
			csvString += " " + value + " " + delimiter;
		};
		
		var needToQuoteString = function(value) {
			//quote when it contains whitespace or the delimiter
			var needQuoting = false;
			if (value.match("[\\w|"+ delimiter + "|" + quote + "]")) {
				needQuoting = true;
			}
			return needQuoting;
		};
		
		return get();
	};
	
	
}).call(this);
