(function() {
	
	
	
	this.PrefixFetcher = function() {
		
		var fetchFromPrefixCcAndStore = function() {
			return JSON.parse(HTTP.get("http://prefix.cc/popular/all.file.json").content);
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
		
		
		return {
			fetch: fetchFromPrefixCcAndStore
		};
	};
})(this);