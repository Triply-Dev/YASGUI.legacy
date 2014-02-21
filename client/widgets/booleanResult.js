(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.BooleanResult = function(container, booleanResult) {
		var draw = function() {
			var innerHtml = "";
			if (booleanResult) {
				innerHtml = "<img src='" + Yasgui.constants.imgs.checkMark.get() + "'><strong>true</strong>";
			} else {
				innerHtml = "<img src='" + Yasgui.constants.imgs.cross.get() + "'><strong>false</strong>";
			}
			$(container).html( '<div class="booleanResult">' + innerHtml + '</div>' );
			
			
		};
		draw();
		
	};
}).call(this);
