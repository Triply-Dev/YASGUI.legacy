(function(){

	
	if (typeof String.prototype.startsWith != 'function') {
		String.prototype.startsWith = function(str) {
			return this.slice(0, str.length) == str;
		};
	}
	if (typeof String.prototype.endsWith != 'function') {
		String.prototype.endsWith = function(str) {
			return this.slice(-str.length) == str;
		};
	};
	if (typeof String.prototype.contains != 'function') {
		String.prototype.contains = function(str) {
			return this.indexOf(str) >= 0;
		};
	};
	this.getHtmlAsText = function(htmlString) {
		return $('<i>').html(htmlString).text();
	};
	
	
}).call(this);
