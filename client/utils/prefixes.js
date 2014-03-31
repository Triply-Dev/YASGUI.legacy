(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.objs = this.Yasgui.objs || {};
	this.Yasgui.objs.Prefixes = function() {
		var cacheExpireDays = 30;
		var prefixes = new Trie();
		var fetch = function() {
			//first try local storage
			fetchFromLocalStorage();
			
			//then try if we have anything cached in our db (done async)
			//if the things in our dataset are stale, then a force fetch is executed
			if (prefixes.prefixes == 0) fetchFromServer();
		};
		var fetchFromLocalStorage = function() {
			var prefixArray = Yasgui.storage.get("prefixes");
			if (prefixArray != null && prefixArray.length > 0) {
				addArrayToTrie(prefixArray);
			} else {
				fetchFromServer();
			}
		};
		
		var fetchFromServer = function() {
			Meteor.call("fetchPrefixes", function(errorMsg, result){
				if (result == undefined) {
					console.log(errorMsg);
				} else {
					var prefixArray = [];
					for (var prefix in result) {
						var completeString = prefix + ": <" + result[prefix] + ">";
						prefixes.insert(completeString);//the trie we have in memory
						prefixArray.push(completeString);//the array we want to store in localstorage
					}
					Yasgui.storage.set("prefixes", prefixArray, "month", true);
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
			forceFetch: fetchFromServer 
		};
	};
}).call(this);


