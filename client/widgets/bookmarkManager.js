(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.BookmarkManager = function(parent, tabSettings) {
		var menuButton;
		var menu;
		var drawButton = function() {
			menuButton = $('<button class="manageBookmarksButton"></button>').button({
				icons: {
			        primary: "bookmarksIcon",
			      },
			      text: false
			}).on("click", drawBookmarkManagerWindow);
			parent.append(menuButton);
		};
		
		var drawBookmarkManagerWindow = function() {
			console.log("draw bookmark manager");
		};
		
		drawButton();
	};
}).call(this);
