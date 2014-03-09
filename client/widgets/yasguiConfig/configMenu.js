(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.YasguiConfigMenu = function(parent, tabSettings) {
		var menuButton;
		var drawButton = function() {
			menuButton = $('<img class="yasguiConfigButton"></img>').attr("src",Yasgui.constants.imgs.gear.get()).on("click", drawWindow);
			parent.append(menuButton);
		};
		
		var drawWindow = function() {
			new Yasgui.widgets.dialog({
				id: "configWindowContent",
				title: "Configure YASGUI",
				width: 800,
				height: 500,
				content: getConfigTabs()
			});
		};
		
		var getConfigTabs = function() {
			
			var addTabItem = function(tabId, tabTitle, addFunction) {
				tabList.append("<li><a href='#" + tabId + "'>" + tabTitle + "</a></li>");
				addFunction($("<div id='" + tabId + "'></div>").appendTo(tabs), tabSettings);
			};
			var tabs = $("<div class='yasguiConfigTabs'></div>");
			var tabList = $("<ul></ul>").appendTo(tabs);
			
			
//			addTabItem("compatabilitiesList", "Browser Compatabilities", Yasgui.widgets.Compatabilities);
			addTabItem("advancedConfig", "Advanced Options", Yasgui.widgets.AdvancedConfiguration);
			addTabItem("help", "Help", Yasgui.widgets.Help);
			addTabItem("about", "About", Yasgui.widgets.About);
//			addCompatabilies(tabs);
			
			
			return tabs.tabs();
			
		};
		
		drawButton();
	};
}).call(this);
