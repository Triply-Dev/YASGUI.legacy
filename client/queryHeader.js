
var QueryHeader = function(parent, tabSettings) {
	var queryHeader = $("<ul class='queryHeader'></ul>");
	var queryIcon;
	parent.append(queryHeader);
	var init = function() {
		
//		drawQueryIcon();
		Yasgui.widgets.QueryIcon(addListItem(), tabSettings);
		Yasgui.widgets.EndpointComboBox(addListItem(), tabSettings);
		Yasgui.widgets.RequestConfigMenu(addListItem(), tabSettings);
//		appendEndpointSelector();
	};
	
	var addListItem = function() {
		var listItem = $("<li></li>");
		queryHeader.append(listItem);
		return listItem;
	};
	var positionElement = function(){
		//position header itself
		queryHeader.css("top", ( $("#tabs").outerHeight(true)) + "px");
		//redraw background div, otherwise the query content will appear under our query header
		$("#queryHeaderBackground").height($("#tabs").outerHeight(true) + queryHeader.outerHeight(true));
	};
	var drawQueryIcon = function() {
//	 ".selector" ).button( "option", "icons", { primary: "ui-icon-gear", secondary: "ui-icon-triangle-1-s" } );

		var icon = $( "<button>query</button>" );
		addListItem().append(icon);
		icon.button({
		      icons: {
		        primary: "queryIcon"
		      },
		      text: false
		    });
//		queryIcon = $("<li><a class='queryIcon' href='#'><img src='" + Yasgui.constants.imgs.play.get() + "'></a></li>");
//		queryHeader.append(queryIcon);
//		
//		Deps.autorun(function() {
//			var queryStatus = Session.get("queryStatus");
//			if (queryStatus == "busy") {
//				queryIcon.off("click").click(function(){
//					Session.set("queryStatus", "query");
//					Yasgui.sparql.cancel();
//					return false;
//				}).children().attr("src",  Yasgui.constants.imgs.loading.get());
//			} else if (queryStatus == "error") {
//				queryIcon.off("click").click(function(){
//					console.log("query");
//					Yasgui.sparql.query();
//					return false;
//				}).children().attr("src",  Yasgui.constants.imgs.play.get());
//		//		Session.set("oldest", oldest.name);
//			} else if (queryStatus == undefined || queryStatus == "query") {
//				queryIcon.off("click").click(function(){
//					Yasgui.sparql.query();
//					return false;
//				}).children().attr("src",  Yasgui.constants.imgs.play.get());
//			} else {
//				console.log("unrecognized query status in session: " + queryStatus);
//			}
//		});
	};
	init();
	
	return {
		get: function(){return queryHeader;},
		positionElement: positionElement
//		cm: codemirror,
//		check: checkSyntax
	};
};
Yasgui.objs.QueryHeader = QueryHeader;
