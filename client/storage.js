var Storage = function() {
	var msDay = 1000 * 60 * 60 * 24;
	var durations = {
		years: msDay * 1000,
		year: msDay * 300,
		month: msDay * 30,
		week: msDay * 7,
		day: msDay
	};
	var prefix = null;
	var getKey = function(key) {
		if (prefix == null) prefix = getWindowLocationHost() + "_";
		return prefix + key;
	};
	
	
	var set = function(key, value, timeout, onlyLocalStorage) {
		var storeMethod = (onlyLocalStorage? amplify.store.localStorage: amplify.store);
		key = getKey(key);
		if (timeout != undefined && durations[timeout] != undefined) {
			storeMethod(key, value, {expires: durations[timeout]});
		} else {
			storeMethod(key, value);
		}
	};
	
	var get = function(key) {
		return amplify.store(getKey(key));
	};
	
	
	return {
		set: set,
		get: get
	};
};
Yasgui.objs.Storage = Storage;
