(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.SearchEndpoints = function(parent, tabSettings) {
		var menuButton;
		var menu;
		var drawButton = function() {
			menuButton = $('<button class="searchEndpointsButton"></button>').button({
				icons: {
			        primary: "searchEndpointsIcon",
			      },
			      text: false
			}).on("click", drawSearchEndpointsWindow);
			parent.append(menuButton);
		};
		
		var drawSearchEndpointsWindow = function() {
			console.log("search endpoints");
		};
		
		drawButton();
	};
}).call(this);
