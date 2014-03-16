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
			}).on("click", drawLinkWindow);
			parent.append(menuButton);
		};
		
		var getShortLink = function(longLink, callback) {
			Meteor.call("shortenUrl", function(errorMsg, response){
				if (response == undefined) {
					console.log(errorMsg);
				} else {
					console.log(response);
					callback(response.content);
				}
				
			});
		}
		var drawLinkWindow = function() {
			
			var linkTabSettings = $.extend({}, tabSettings);
			deleteKey(linkTabSettings, 'results');
			deleteKey(linkTabSettings, 'id');
			
			var windowContent = $("<div></div>");
			var link = window.location.href + "?" +  $.param(linkTabSettings);
			var inputElement = $('<input class="queryLink" type="text">').val(link).on("focus", function(){
			    this.select();
			    return false;
			}).focus();
			windowContent.append(inputElement);
			
			if (self != top ) {
				//we are in an iframe. show notification
				windowContent.append($("<p></p>").html("You are using YASGUI via an external site. Note that this link points to the original YASGUI website: <a href='" + window.location.href + "' target='_blank'>" + window.location.href + "</a>!"));
			}
			var shortenButton = $("<button class='shortenUrl'>Shorten URL</button>").button().on("click", function() {
				console.log("shorten url");
				getShortLink(link, function(shortLink){
					inputElement.val(shortLink).focus();
				});
			}).appendTo(windowContent);
			Yasgui.widgets.dialog({
				content: windowContent,
				hideTitleBar: true,
				width: 400,
				minHeight: null, //overwrite the default of 150px
				position: {
					my: "right top",
					at: "right bottom",
					of: menuButton,
					collission: "none"
					
				}
			});
//			console.log(JSON.stringify(linkTabSettings));
		};
		
		drawButton();
	};
}).call(this);
