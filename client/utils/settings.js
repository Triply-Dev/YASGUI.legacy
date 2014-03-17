(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.objs = this.Yasgui.objs || {};
	this.Yasgui.objs.Settings = function() {
		var settings;
		
		var addUnknownValuesToSettings = function(settingsToAdd) {
			
		};
		var fetchSettings = function() {
			var newUser = Yasgui.storage.get("settings") == undefined;
			//we have 6 sources for our settings: 
			
			//3. Our server-side config: this one contains sensitive data. Always overwrite settings object with this config
			//4. Query arguments passed via the url. Check whether this is a new visitor. If it is, use these args to change our only tab. Otherwise, create new tab using these args
			//5. The settings passed via the url (i.e. 'jsonSettings'). Overwrite settings object
			//6. Settings passed via a parent callback function. Deal with this the same as 5
			
			
	
			/**
			 * 1.
			 * The local storage: this one is interesting as it contains all user-changed settings
			 */
			settings = Yasgui.storage.get("settings") || clientSettings;
			
			/**
			 * 2.
			 * Our client-side default config: interesting, as this one contains the 'allowedFeatures' setting, 
			 * which might have changed after the previous user session. 
			 */
			//Overwrite settings from 1 with 2.
			settings = $.extend(true, {}, clientSettings, settings);
			//Add 'defaultSettings' to root settings object (except the default tab settings, as we use this manually later on in this class)
			var defaultSettings = $.extend(true, {}, settings.defaultSettings);//make clone. we don't want to remove tabSettings from original object
			deleteKey(defaultSettings, "tabSettings");
			settings = $.extend(true, {}, defaultSettings, settings);
			
			/**
			 * 3.
			 * Our server-side config: this one contains sensitive data. Always overwrite settings object with this config
			 */
			Meteor.call("getSettings", function(errorMsg, result){
				if (result == undefined) {
					console.log(errorMsg);
				} else {
					$.extend(true, settings, result);
					store();
				}
				
			});
			
			/**
			 * 4.
			 * Query arguments passed via the url. If this is a new visitor, use these args to change our only tab. Otherwise, create new tab using these args
			 */
			var settingsFromUrl = getTabSettingsFromUrl();
			if (settings.tabs == undefined) {
				settings.tabs = [];
				settings.tabs.push($.extend(true, {}, settings.defaults.tabSettings, settingsFromUrl));//we want to CLONE, not just store the pointer
				settings.selectedTabKey = 0;
			} else if (!$.isEmptyObject(settingsFromUrl)){
				console.log(settingsFromUrl);
				settings.tabs.push($.extend(true, {}, settings.defaults.tabSettings, settingsFromUrl));
				settings.selectedTabKey = settings.tabs.length - 1;
			}
			
			/**
			 * 5.
			 * The settings passed via the url (i.e. 'jsonSettings'). Overwrite settings object
			 */
			
			/**
			 * 6.
			 * Settings passed via a parent callback function. Deal with this the same as 5
			 */
			
			
			/**
			 * Some post processing stuff
			 */
			//if 'enabledFeatures' does not exist, then copy the allowedFeatures. 
			//all enabledFeatures is, is a user-changeable version of the 'allowedFeatures'
			if (settings.enabledFeatures == undefined) {
				settings.enabledFeatures = settings.allowedFeatures;
			} else {
				//should we've added a new features, we want this one incorporated as well
				settings.enabledFeatures = $.merge(true, {}, settings.allowedFeatures, settings.enabledFeatures);
			}
			
		};
		
		var getTabSettingsFromUrl = function() {
			var urlSettings = {};
			var urlParamString = (window.location.search.length > 1? window.location.search.substring(1): null);
			
			if (urlParamString) {
				var urlSettings = $.deparam(urlParamString);
				if (!urlSettings["query"]) {
					//ah, my bad, these args are not url settings after all!
					urlSettings = {};
				}
			}
			return urlSettings;
		};
		
		var store = function() {
			Yasgui.storage.set("settings", settings);
		};
		var getCurrentTab = function() {
			if (settings.selectedTabKey != undefined && settings.selectedTabKey >= 0 && settings.selectedTabKey < settings.tabs.length) {
				return settings.tabs[settings.selectedTabKey];
			} else {
				return null;
			}
		};
		
		var getTabById = function(id) {
			for (var i = 0; i < settings.tabs.length; i++) {
				if (settings.tabs[i].id == id) return settings.tabs[i];
			}
		};
		
		fetchSettings();
		store();
		settings.store = store;
		settings.getCurrentTab = getCurrentTab;
		settings.getTabById = getTabById;
		return settings;
	};
}).call(this);


