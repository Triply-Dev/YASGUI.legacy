
var QueryHeader = function(parent, tabSettings) {
	var queryHeader = $("<ul class='queryHeader'></ul>");
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
	init();
	
	return {
		get: function(){return queryHeader;},
		positionElement: positionElement
//		cm: codemirror,
//		check: checkSyntax
	};
};
Yasgui.objs.QueryHeader = QueryHeader;
