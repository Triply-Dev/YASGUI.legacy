(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.parsers = this.Yasgui.parsers || {};
	
	var SparqlParserFactory = function(response) {
		var parser = null;
		
		
		var getParserFromContentType = function() {
			var contentType = response.headers["content-type"];
			if (contentType) {
				contentType = contentType.toLowerCase();
				if (contentType.contains("json")) {
					tryJsonParser();
				} else if (contentType.contains("xml")) {
					tryXmlParser();
				} else if (contentType.contains("csv")) {
					doCsvParser();
				} else if (contentType.contains("tab-separated")) {
					doTsvParser();
				}
			}
		};
		
		var doLuckyGuess = function() {
			tryJsonParser();
			if (parser == null) tryXmlParser();
		};
		var doCsvParser = function() {
//			console.log("before parsing", $.csv.toArrays(response.content));
			parser = new Yasgui.parsers.DelimitedParser($.csv.toArrays(response.content, {separator: ","}), response.content);
		};
		var doTsvParser = function() {
			parser = new Yasgui.parsers.DelimitedParser($.csv.toArrays(response.content, {separator: "\t"}), response.content);
		};
		var tryJsonParser = function() {
			try {
				var json = $.parseJSON(response.content);
				parser = new Yasgui.parsers.JsonParser(json, response.content);
			} catch (e) {
			}
		};
		var tryXmlParser = function() {
			try {
				var xml = $.parseXML(response.content);
				parser = new Yasgui.parsers.XmlParser($( xml ), response.content);
			} catch (e) {}
		};
		
		getParserFromContentType();
		if (parser == null) {
			doLuckyGuess();
		}
		
		return parser;
	};
	
	Yasgui.parsers.SparqlParserFactory = SparqlParserFactory;
})(this);