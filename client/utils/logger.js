(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.objs = this.Yasgui.objs || {};
	this.Yasgui.objs.Logger = function() {
		var debug = false;
		
		var checkLoggingPossible = function() {
			if (debug) return true;
			if (document.URL.contains("localhost") || !Yasgui.settings.logging) {
				return false;
			}
			if (Yasgui.settings.constants.logging.type == "googleAnalytics" && Yasgui.settings.constants.logging.trackingId.length > 0) {
				return true;
			}
			return false;
		};
		var loggingPossible = checkLoggingPossible();
	
		var initGoogleAnalytics = function(code) {
		    var _gaq = window._gaq || [];
		    _gaq.push(['_setAccount', code]);
		    _gaq.push(['_trackPageview']);
		    (function() {
		        var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
		        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
		        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
		        console.log(s);
		    })();
		};
		
		var init = function() {
			if (!loggingPossible) return;
			if (Yasgui.settings.constants.logging.type == "googleAnalytics") initGoogleAnalytics(Yasgui.settings.constants.logging.trackingId);
		};
		
		var isQueryLoggingEnabled = function() {
			return Yasgui.settings.loggingEnabled !== "no" && Yasgui.settings.loggingEnabled !== "partial";
		};
		
		var trackEvent = function(category, action, optLabel, optValue) {
			if (!loggingPossible || Yasgui.settings.loggingEnabled == "no") return;
			if (Yasgui.settings.constants.logging.type == "googleAnalytics") {
				_gaq.push(['_trackEvent',category, action, optLabel, optValue]);
			}
		};
		init();
		
		return {
			queryLoggingEnabled: isQueryLoggingEnabled,
			track: trackEvent
		};
		
	};
}).call(this);

