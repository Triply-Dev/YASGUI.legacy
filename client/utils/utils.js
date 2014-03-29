/**
 * Generic utils
 */
(function(){

	// a convenience function for parsing string namespaces and 
	// automatically generating nested namespaces
	this.extend = function( ns, ns_string ) {
	    var parts = ns_string.split("."),
	        parent = ns,
	        pl;

	    pl = parts.length;

	    for ( var i = 0; i < pl; i++ ) {
	        // create a property if it doesn't exist
	        if ( typeof parent[parts[i]] === "undefined" ) {
	            parent[parts[i]] = {};
	        }

	        parent = parent[parts[i]];
	    }

	    return parent;
	};
	
	this.getWindowLocation = function() {
		var location;
		
		if (this.parent == null || this.parent == this) {
			location = this.location.href;
		} else {
			//when external iframe parent is from different domain, we cannot access the location
			//try to use referrer instead.
			location = this.document.referrer;
		}
		return location;
	};
	this.getHostFromUrl = function(url) {
		if (url != null) {
			var matches = url.match(/^https?\:\/\/([^\/:?#]+)(?:[\/:?#]|$)/i);
			return domain = matches && matches[1];
		}
	};
	this.getWindowLocationHost = function() {
		var host = this.getHostFromUrl(this.getWindowLocation());
		return (host != null? host: "");
	};
	this.dateDiffInDays = function(date1, date2) {
		var msPerDay = 1000 * 60 * 60 * 24;
		// Discard the time and time-zone information.
		var utc1 = Date.UTC(date1.getFullYear(), date1.getMonth(), date1.getDate());
		var utc2 = Date.UTC(date2.getFullYear(), date2.getMonth(), date2.getDate());
		
		return Math.floor((utc2 - utc1) / msPerDay);
	};
	
	
	this.dynamicSort = function(property) {
	    var sortOrder = 1;
	    if(property[0] === "-") {
	        sortOrder = -1;
	        property = property.substr(1);
	    }
	    return function (a,b) {
	        var result = (a[property] < b[property]) ? -1 : (a[property] > b[property]) ? 1 : 0;
	        return result * sortOrder;
	    };
	};

	this.dynamicSortMultiple = function() {
	    /*
	     * save the arguments object as it will be overwritten
	     * note that arguments object is an array-like object
	     * consisting of the names of the properties to sort by
	     */
	    var props = arguments;
	    return function (obj1, obj2) {
	        var i = 0, result = 0, numberOfProperties = props.length;
	        /* try getting a different result from 0 (equal)
	         * as long as we have extra properties to compare
	         */
	        while(result === 0 && i < numberOfProperties) {
	            result = dynamicSort(props[i])(obj1, obj2);
	            i++;
	        }
	        return result;
	    };
	};
	this.dismissOnOutsideClick = function(selector, callback) {
		var eventIdentifier = "mousedown.";
		if (typeof selector == "string") {
			eventIdentifier += selector.substring(1);
		} else {
			eventIdentifier += Math.random().toString(36).slice(2);
		}
		$(document).on(eventIdentifier, function(event) {
		    if(!$(event.target).parents().andSelf().is(selector)) {
		    		callback();
		    		//remove this listener to avoid garbage
		    		$(document).off(eventIdentifier);
			}
		});
	};
	
	/**
	 * IE does not always support deleting an entry from jsonobj. use this workaround
	 */
	this.deleteKey = function(jsonObj, key) {
		jsonObj[key] = null;
		try {
			delete jsonObj[key];
		} catch(e){}
	};
	
	this.sendToParent = function(obj){
		 // var parent_url = decodeURIComponent( document.location.hash.replace( /^#/, '' ) );
		 // $.postMessage(obj,parent_url,parent);
		};

}).call(this);
