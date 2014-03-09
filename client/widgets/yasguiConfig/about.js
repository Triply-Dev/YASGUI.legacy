(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.About = function(parent, tabSettings) {
		
		var draw = function() {
			parent.append($("<div>about + compatabilities</div>"));
		};
		
		draw();
	};
}).call(this);
