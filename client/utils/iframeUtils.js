(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.iframe = this.Yasgui.iframe || {};
	this.Yasgui.iframe = new function() {
		Yasgui.settings.callbacks = Yasgui.settings.callbacks || {};
		//extract parent domain (without any ports) when we are in an iframe
		var parentDomain = (parent !== window? document.referrer.match(/^(https?\:\/\/[^\/:?#]+)(?:[\/:?#]|$)/i)[1]: null);
		
		var addListener = function() {
			//requires child -> parent communication. add listener!
			var eventMethod = window.addEventListener ? "addEventListener" : "attachEvent";
			var eventer = window[eventMethod];
			var messageEvent = eventMethod == "attachEvent" ? "onmessage" : "message";

			// Listen to message from parent window
			eventer(messageEvent,function(e) {
				if (e.origin !== parentDomain) return;//not coming from parent
				console.log("in child!", e);
				if (e.data.startsWith("execQuery_")) {
					if (e.data.substring("execQuery_".length) == "1") {
						Yasgui.settings.callbacks.queryResults = true;
						
					} else {
						Yasgui.settings.callbacks.queryResults = false;
					}
					Yasgui.settings.store();
					Yasgui.sparql.query();
				} else if (e.data == "getQuery") {
					parent.postMessage("queryString_" + encodeURIComponent(Yasgui.settings.getCurrentTab().query), parentDomain);
				}
			},false);
		};
		
		addListener();
	};
}).call(this);

