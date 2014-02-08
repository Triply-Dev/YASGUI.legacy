(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.parsers = this.Yasgui.parsers || {};
	
	var SparqlParserFactory = function(response) {
		var parser = null;
		
		var tryJsonParser = function() {
			try {
				var json = $.parseJSON(response);
				parser = new Yasgui.parsers.JsonParser(json, response);
			} catch (e) {
				console.log(e);
			}
		};
		
		var tryXmlParser = function() {
			try {
				var xml = $.parseXML(response);
				parser = new Yasgui.parsers.XmlParser(xml, response);
			} catch (e) {}
		};
		
		tryJsonParser();
		if (parser == null) tryXmlParser();
		return parser;
	};
	
	Yasgui.parsers.SparqlParserFactory = SparqlParserFactory;
})(this);