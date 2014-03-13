(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.BookmarkQuery = function(parent, tabSettings) {
		var menuButton;
		var menu;
		var drawButton = function() {
			$("<button class='bookmarkButton'></button>").appendTo(parent).button({
			    icons: {
			        primary: "bookmarkIcon"
			      },
			      text: false
			}).on("click", bookmarkQuery);
			parent.append(menuButton);
		};
		
		var bookmarkQuery = function() {
			console.log("bookmark query");
			
		};
		
		drawButton();
	};
}).call(this);
