(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.stringutils = {};
	if (typeof String.prototype.startsWith != 'function') {
		String.prototype.startsWith = function(str) {
			return this.slice(0, str.length) == str;
		};
	}
	if (typeof String.prototype.endsWith != 'function') {
		String.prototype.endsWith = function(str) {
			return this.slice(-str.length) == str;
		};
	};
	if (typeof String.prototype.contains != 'function') {
		String.prototype.contains = function(str) {
			return this.indexOf(str) >= 0;
		};
	};
	
	this.Yasgui.stringutils.getHtmlAsText = function(htmlString) {
		var cleanedString = htmlString;
		if (htmlString.trim().startsWith("<")) {
			cleanedString = $('<i>').html(htmlString).text();
		}
		
		return cleanedString;
	};
	this.Yasgui.stringutils.getUncommentedSparqlQuery = function(origQuery) {
		var cleanedQuery = "";
		CodeMirror.runMode(origQuery, "sparql11", function(stringVal, className) {
			if (className != "sp-comment") {
				cleanedQuery += stringVal;
			}
		});
		return cleanedQuery;
	};
	
	
}).call(this);
