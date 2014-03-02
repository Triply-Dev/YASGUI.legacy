var PrefixesFetcher = function() {
	var cacheExpireDays = 30;
	var prefixes = new Trie();
	var fetch = function() {
		//first try local storage
		fetchFromLocalStorage();
		
		//then try if we have anything cached in our db (done async)
		//if the things in our dataset are stale, then a force fetch is executed
		if (prefixes.prefixes == 0) fetchFromDb();
	};
	var fetchFromLocalStorage = function() {
		var prefixArray = Yasgui.storage.get("prefixes");
		if (prefixArray != null && prefixArray.length > 0) {
			addArrayToTrie(prefixArray);
		}
	};
	
	var fetchFromDb = function() {
		console.log("fetching prefixes from db");
		Meteor.subscribe("prefixes", function(){
			console.log(Prefixes);
			var prefix = Prefixes.findOne({}, {reactive: false});
			if (prefix != null && dateDiffInDays(new Date(), prefix.date) < cacheExpireDays) {
				var results = Prefixes.find({}).fetch();
				var prefixArray = [];
				for (var i = 0; i < results.length; i++) {
					var completeString = results.prefix + ": <" + results.uri + ">";
					prefixes.insert(completeString);
					prefixArray.push(completeString);
				}
				Yasgui.storage.set("prefixes", prefixArray, "month", true);
			} else {
				//no results, or stale results
				fetchFromPrefixCc();
			}
		});
		
		
	};
	var fetchFromPrefixCc = function() {
		console.log("forcing prefixes update");
		Meteor.call("forcePrefixUpdate", function(errorMsg, result){
			if (result == undefined) {
				console.log(errorMsg);
			} else {
				console.log(result);
				fetchFromDb();
			}
			
		});
	};
	
	var addArrayToTrie = function(prefixArray) {
		for (var i = 0; i < prefixArray.length; i++) {
			prefixes.insert(prefixArray[i]);
		}
	};
	var autocomplete = function(partialString) {
		return prefixes.autoComplete(partialString);
	};
	fetch();
	return {
		complete: autocomplete,
		forceFetch: fetchFromPrefixCc, 
		fetchFromDb: fetchFromDb
	};
};

Yasgui.objs.Prefixes = PrefixesFetcher;
