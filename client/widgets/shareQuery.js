(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.ShareQuery = function(parent, tabSettings) {
		var menuButton;
		var drawButton = function() {
			menuButton = $("<button class='queryLinkButton'></button>").appendTo(parent).button({
			    icons: {
			        primary: "shareIcon"
			      },
			      text: false
			}).on("click", getQueryLink);
			parent.append(menuButton);
		};
		
		var getQueryLink = function() {
			var linkTabSettings = $.extend({}, tabSettings);
			deleteKey(linkTabSettings, 'results');
			deleteKey(linkTabSettings, 'id');
			
			var windowContent = $("<div></div>");
			var link = window.location.href + "?" +  $.param(linkTabSettings);
			
			windowContent.append($('<input style="width:100%" type="text" value="'+ link + '" class="class anotherclass" readonly >'));
			Yasgui.widgets.dialog({
				content: windowContent,
				hideTitleBar: true,
				width: "400px",
				position: {
					my: "top right",
					at: "bottom right",
					of: menuButton,
					collission: "none"
					
				}
			});
//			console.log(JSON.stringify(linkTabSettings));
		};
		
		drawButton();
	};
}).call(this);
