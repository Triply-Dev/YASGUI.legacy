(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.AdvancedConfiguration = function(parent, tabSettings) {
		var content;
		var getCheckBox = function(label, checked, onChange) {
			
			var item = $('<label>' + label + '<input id="checkbox_id" type="checkbox" ' + (checked? "checked": "") + '></label>').on("change", function(){
				console.log("changed");
				onChange();
			});
			
			return item;
		};
		
		var draw = function() {
			content = $("<div></div>");;
			parent.append(content);
			
			content.append(getCheckBox("labelll", true, function(){console.log("callback");}));
		};
		var store = function() {
			console.log("store");
		};
		draw();
		
		return {
			store: store
		};
	};
}).call(this);
