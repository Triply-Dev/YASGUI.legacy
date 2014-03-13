(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.ShareQuery = function(parent, tabSettings) {
		var menuButton;
		var menu;
		var drawButton = function() {
			$("<button class='queryLinkButton'></button>").appendTo(parent).button({
			    icons: {
			        primary: "shareIcon"
			      },
			      text: false
			}).on("click", getQueryLink);
			parent.append(menuButton);
		};
		
		var getQueryLink = function() {
			console.log("get query link");
			
		};
		
		drawButton();
	};
}).call(this);
