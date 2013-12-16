(function() {
	
	
	
	this.PrefixFetcher = function() {
//		var Prefixes = new Meteor.Collection("prefixes");
//		var fs = Npm.require('fs');
//		var cacheFile = Utils.constants.cacheDir + "prefixes.json";
//		return JSON.parse(Assets.getText("serverSettings.json"));
		
		
		var fetchFromPrefixCcAndStore = function() {
			var jsonResult = JSON.parse(HTTP.get("http://prefix.cc/popular/all.file.json").content);
			var needClearing = true;
			var hasResults = false;
			
			for (var key in jsonResult) {
				hasResults = true;
				if (needClearing) {
					Prefixes.remove({});
					needClearing = false;
				}
				Prefixes.insert({prefix: key, uri: jsonResult[key], date: new Date()});
			}
			
			return Prefixes.find({}).count();
		};
		
		
//		var fetch = function(forceUpdate) {
//			var result = null;
//			forceUpdate = (Utils.initDir(Utils.constants.cacheDir) || forceUpdate);
//			if (forceUpdate || Utils.needUpdating(cacheFile, cacheExpireDays)) {
//				result = fetchFromPrefixCcAndStore();
//				//store in cache file
//				fs.writeFile(cacheFile, JSON.stringify(result));
//			};
//			if (result == null) {
//				console.log("loading prefixes from cache");
//				result = JSON.parse(fs.readFile(cacheFile));
//			}
//			console.log(result);
//			return result;
//		};
		return {
			fetch: fetchFromPrefixCcAndStore
		};
	};
})(this);