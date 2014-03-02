(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.compatabilities = this.Yasgui.comp || {};
	
	
	this.Yasgui.compatabilities.downloadAttribute = function() {
		return ("download" in document.createElement("a"));
	};
	
	
	this.Yasgui.compatabilities.stringToUrl = function() {
		supported = true;
		windowUrl = window.URL || window.webkitURL || window.mozURL || window.msURL;
		
		if (windowUrl == undefined || windowUrl == null) {
			supported = false;
		}
		
		if (supported && Blob == undefined) {
			supported = false;
		}
		
		//in versions < ff 13, blob constructor is missing. check!
		if (supported) {
			try {
				new Blob(["text"], {type: "text/html"});
			} catch (err) {
				supported = false;
			}
		}
		return supported;
	};
}).call(this);
