(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.AdvancedConfiguration = function(parent, tabSettings) {
		
		var draw = function() {
			parent.append($("<div>AdvancedConfiguration</div>"));
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
