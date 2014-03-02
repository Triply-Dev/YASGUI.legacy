(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.RequestConfigMenu = function(parent, tabSettings) {
		var menuButton;
		var menu;
		var drawButton = function() {
			menuButton = $('<button class="configRequestButton">Configure Request</button>');
			menuButton.height(26).button();
			menuButton.on("click", drawMenu);
			parent.append(menuButton);
		};
		
		var destroy = function() {
			if (menu) {
				menu.remove();
				menu = null;
			}
			menuButton.blur();
		};
		
		/**
		 * [{ name: "first", value: "Rick" }] ----> [['first','Rick']]
		 */
		var getNameValuePairsAsArray = function(nameValuePairs) {
			if (!nameValuePairs) nameValuePairs = [];
			var array = [];
			for (var i = 0; i < nameValuePairs.length; i++) {
				var nameValuePair = nameValuePairs[i];
				array.push([nameValuePair.name, nameValuePair.value]);
			}
			return array;
		};
		
		/**
		 * [['first','Rick']] ----> [{ name: "first", value: "Rick" }]
		 */
		var getArrayAsNameValuePairs = function(arrays) {
			if (!arrays) arrays = [];
			var nameValuePairs = [];
			for (var i = 0; i < arrays.length; i++) {
				var array = arrays[i];
				nameValuePairs.push({name: array[0], value: array[1]});
			}
			return nameValuePairs;
		};
		
		
		var editQueryParameters = function() {
			var inputForm = new Yasgui.widgets.MultiTextInputForm({
				allowDel: true,
				allowNew: true,
				maxCols: 2,
				headers: ["key", "value"],
				values: getNameValuePairsAsArray(Yasgui.settings.getSelectedTab().params),
				intro: "Manually specify query parameters below. Use this for those triple stores supporting additional parameters, such as <a href='http://4store.org/' target='_blank'>4-store</a> which allows you to specify a <a href='http://4store.org/trac/wiki/Query' target='_blank'>'soft limit'</a> in your request",
				requiredCols: [0]
				
			});
			Yasgui.widgets.dialog({
				title: "Specify query parameters",
				id: 'editQueryParam',
				width: 600,
				height: 300,
				position: 'right-10 bottom+10',
//				content: "blaat"
				content: inputForm.getElement(),
				onClose: function() {
					Yasgui.settings.getSelectedTab().params = getArrayAsNameValuePairs(inputForm.getValues());
					Yasgui.settings.store();
				}
			});
			destroy();
		};
		var editQueryHeaders = function() {
			console.log("todo: edit query headers");
			destroy();
		};
		var editNamedGraphs = function() {
			console.log("todo: edit named graphs");
			destroy();
		};
		var editDefaultGraphs = function() {
			console.log("todo: edit default graphs");
			destroy();
		};
		var getSpacer = function() {
			return "<span style='margin-left:22px;'></span>";
		}
		var drawMenu = function(event) {
			menu = $("<ul id='requestConfigMenu'></ul>").width(200).zIndex(Yasgui.objs.ZIndexes().tabHeader);
			
			
			menu.append($("<li><a href='#'>Add Query Parameters</li>").on("click", editQueryParameters));
			menu.append($("<li><a href='#'>Add Query Headers</li>").on("click", editQueryHeaders));
			menu.append($("<li><a href='#'>Specify Named Graphs</li>").on("click", editNamedGraphs));
			menu.append($("<li><a href='#'>Specify Default Graphs</li>").on("click", editDefaultGraphs));
			
			/**
			 * Draw accept headers
			 */
			var acceptHeadersSubMenu = $("<ul></ul>").width(100);
			var selectAcceptHeadersSubMenu = $("<ul></ul>").width(100);
			$.each(Yasgui.sparql.acceptHeaders.select, function(index, acceptHeader) {
				var icon = getSpacer();
				if (tabSettings.contentTypeSelect == acceptHeader.header) {
					icon = "<img src='" + Yasgui.constants.imgs.checkMark.get() + "'>";
				}
				selectAcceptHeadersSubMenu.append(
						$("<li><a href='#'>" + icon  + acceptHeader.name + "</a></li>")
							.on("click", function(){
								console.log("setting to ", acceptHeader.header);
								tabSettings.contentTypeSelect = acceptHeader.header;
								destroy();
						})
					);
			});
			acceptHeadersSubMenu.append($("<li><a href'#'>SELECT</a></li>").append(selectAcceptHeadersSubMenu));
			var graphAcceptHeadersSubMenu = $("<ul></ul>").width(100);
			$.each(Yasgui.sparql.acceptHeaders.graph, function(index, acceptHeader) {
				var icon = getSpacer();
				if (tabSettings.contentTypeGraph == acceptHeader.header) {
					icon = "<img src='" + Yasgui.constants.imgs.checkMark.get() + "'>";
				}
				graphAcceptHeadersSubMenu.append($("<li><a href='#'>" + icon + acceptHeader.name + "</a></li>")
						.on("click", function() {
							tabSettings.contentTypeGraph = acceptHeader.header;
							destroy();
						}));
			});
			acceptHeadersSubMenu.append($("<li><a href'#'>Graph</a></li>").append(graphAcceptHeadersSubMenu));
			menu.append($("<li><a href'#'>Accept headers</a></li>").append(acceptHeadersSubMenu));
			
			
			var getIcon = getSpacer();
			var postIcon = getSpacer();
			if (tabSettings.requestMethod == "GET") {
				getIcon = "<img src='" + Yasgui.constants.imgs.checkMark.get() + "'>";
			} else {
				postIcon = "<img src='" + Yasgui.constants.imgs.checkMark.get() + "'>";
			}
			var requestMethodSubMenu = $("<ul></ul>")
				.width(80)
				.append($("<li><a href='#'>" + getIcon + "GET</a></li>").on("click", function(){
					tabSettings.requestMethod = "GET";
					destroy();
				}))
				.append($("<li><a href='#'>" + postIcon + "POST</a></li>").on("click", function(){
					tabSettings.requestMethod = "POST";
					destroy();
				}));
			menu.append($("<li><a href'#'>Request Method</a></li>").append(requestMethodSubMenu));
			
			$("body").append(menu);
			menu.position({
				my: "left top",
				at: "left bottom+10",
				of: menuButton,
				collision: "none",
			}).menu();
			dismissOnOutsideClick('#requestConfigMenu', destroy);
			return false;
		};
		
		drawButton();
	};
}).call(this);
