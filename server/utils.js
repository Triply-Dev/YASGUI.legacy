(function() {
	this.Utils = {
		constants: {
			cacheDir: "/cache/"
		},
		needUpdating:  function(file, expireDays) {
			var fs = Npm.require('fs');
			var updateFile = false;
			if (!fs.exists(file)) {
				console.log("file does not exist");
				updateFile = true;
			} else {
				if (Utils.dateDiffInDays(new Date(), fs.stats(file).mtime) > expireDays) {
					console.log("expire days exceeded");
					updateFile = true;
				}
			}
			return updateFile;
		},
		dateDiffInDays: function(date1, date2) {
			var msPerDay = 1000 * 60 * 60 * 24;
			// Discard the time and time-zone information.
			var utc1 = Date.UTC(date1.getFullYear(), date1.getMonth(), date1.getDate());
			var utc2 = Date.UTC(date2.getFullYear(), date2.getMonth(), date2.getDate());
			
			return Math.floor((utc2 - utc1) / msPerDay);
		},
		initDir: function(dir) {
			var fs = Npm.require('fs');
			var created = false;
			if (!fs.exists(dir)) {
				fs.mkdir(dir);
				created = true;
			}
			return created;
		}
	};
})(this);