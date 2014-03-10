(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.YasguiConfigMenu = function(parent, tabSettings) {
		var menuButton;
		var menu;
		var drawButton = function() {
			menuButton = $('<img class="yasguiConfigButton"></img>').attr("src",Yasgui.constants.imgs.gear.get()).on("click", drawMenu);
			parent.append(menuButton);
		};
		
		var drawWindow = function(contentFunction) {
			menu.hide();
			var windowContent = $("<div></div>");
			var contentObj = contentFunction(windowContent);
			new Yasgui.widgets.dialog({
				id: "configWindowContent",
				title: "Configure YASGUI",
				width: 800,
				height: 500,
				content: windowContent,
				onClose: function(){contentObj.store();}
			});
		};
		
		var drawMenu = function() {
			if (menu == undefined) {
				menu = $("<ul id='yasguiConfigMenu'></ul>");
				$("<li><a href='#'>Advanced Options</a></li>").appendTo(menu).on("click", function(){drawWindow(Yasgui.widgets.AdvancedConfiguration);});
				$("<li><a href='#'>Manage Caches</a></li>").appendTo(menu).on("click", function(){drawWindow(Yasgui.widgets.Cache);});
				$("<li><a href='#'>Help</a></li>").appendTo(menu).on("click", function(){drawWindow(Yasgui.widgets.Help);});
				$("<li><a href='#'>About</a></li>").appendTo(menu).on("click", function(){drawWindow(Yasgui.widgets.About);});
				menu.zIndex(Yasgui.constants.zIndexes.popupDialogues).appendTo($("body")).menu();
			}
			menu.show().position({
		        of: menuButton,
		        my: "right-5 top",
		        at: "right bottom",
		        collision: "none"
			});
			dismissOnOutsideClick(menu, function(){menu.hide();});
		};
		
		drawButton();
	};
}).call(this);
