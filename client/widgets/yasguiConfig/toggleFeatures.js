(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.ToggleFeatures = function(parent, tabSettings) {
		
		var draw = function() {
			parent.append($("<div>toggleFeatuers</div>"));
		};
		
		draw();
	};
}).call(this);
