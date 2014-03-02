
var Settings = function() {
	
	var fetchSettings = function() {
		//we have 3 sources for our settings: 
		//1. The local storage: this one is interesting as it contains all user-changed settings
		//2. Our client-side default config: interesting, as this one contains the 'allowedFeatures' setting, which might have changed after the previous user session
		//3. Our server-side config: this one contains sensitive data. However, an abstraction of this (e.g. 'do we have a bitly api key?') is send asynchronously to this object
		
		Meteor.call("getSettings", function(errorMsg, result){
			if (result == undefined) {
				console.log(errorMsg);
			} else {
				mergeServersideSettings(result);
			}
			
		});
		

		
		var settings = Yasgui.storage.get("settings") || clientSettings;//take localstorage settings as main config. If not found, then use our clientConfig
		
		//We want to overwrite the allowedFeatures! Our server config (i.e. the clientConfig file), might have changed after the previous user session
		settings.allowedFeatures = clientSettings.allowedFeatures;
		
		
		//TODO: improve this (changes in clientsettings files do not get loaded when settings are retrieved from local storage)
		settings.defaultBrowsingTemplate = clientSettings.defaultBrowsingTemplate;
		
		//if 'enabledFeatures' does not exist, then copy the allowedFeatures. 
		//all enabledFeatures is, is a user-changeable version of the 'allowedFeatures'
		if (settings.enabledFeatures == undefined) settings.enabledFeatures = settings.allowedFeatures;
		
		if (settings.tabs == undefined) {
			settings.tabs = [];
			settings.tabs.push(jQuery.extend(true, {}, settings.defaultTabSettings));//we want to CLONE, not just store the pointer
		}
		return settings;
	};
	
	
	var mergeServersideSettings = function(serverSettings) {
		config = $.extend(settings, serverSettings);
		store();
	};
	var store = function() {
		Yasgui.storage.set("settings", settings);
	};
	var getSelectedTab = function() {
		if (settings.selectedTabKey != undefined && settings.selectedTabKey >= 0 && settings.selectedTabKey < settings.tabs.length) {
			return settings.tabs[settings.selectedTabKey];
		} else {
			return null;
		}
	};
	
	var settings = fetchSettings();
	store();
	settings.store = store;
	settings.getSelectedTab = getSelectedTab;
	return settings;
};
Yasgui.objs.Settings = Settings;
