
var TabContent = function(tabSettings) {
	
	var mainTabContent = $("#" + tabSettings.id);
	
	//fixed
	var queryHeader = new Yasgui.objs.QueryHeader(mainTabContent, tabSettings);
	//this div is scrollable. the query header is fixed
	var scrollableSubContent = $("<div class='scrollableTabContent'></div>");
	mainTabContent.append(scrollableSubContent);
	
	var codemirror = new Yasgui.widgets.QueryCodemirror(scrollableSubContent, tabSettings);
	var results = new Yasgui.objs.QueryResults(scrollableSubContent, tabSettings);
	
	
	
	var positionElements = function() {
		queryHeader.positionElement();
//		console.log("settings scrollable content margin-top to " + queryHeader.get().outerHeight());
//		scrollableSubContent.css("margin-top", (queryHeader.get().outerHeight()) + "px"); 
		scrollableSubContent.css("margin-top", ($("#tabs").outerHeight(true) + queryHeader.get().outerHeight(true)) + "px"); 
	};
	positionElements();
	return {
		cm: codemirror,
		queryHeader: queryHeader,
		results: results
	};
};
Yasgui.objs.TabContent = TabContent;
