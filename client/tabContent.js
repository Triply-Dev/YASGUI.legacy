
var TabContent = function(tabSettings) {
	var codemirrorChangeCallback = function() {
		var queryType = codemirror.getQueryType();
		if (queryType) {
			queryType = queryType.toLowerCase();
			if (queryType == "construct" || queryType == "describe") {
				results.setTableOutputEnabled(false);
			} else {
				results.setTableOutputEnabled(true);
			}
		}
		
		
	};
	var mainTabContent = $("#" + tabSettings.id);
	
	//fixed
	var queryHeader = new Yasgui.objs.QueryHeader(mainTabContent, tabSettings);
	//this div is scrollable. the query header is fixed
	var scrollableSubContent = $("<div class='scrollableTabContent'></div>");
	mainTabContent.append(scrollableSubContent);
	
	var codemirror = new Yasgui.widgets.QueryCodemirror(scrollableSubContent, tabSettings, codemirrorChangeCallback);
	var results = new Yasgui.objs.QueryResults(scrollableSubContent, tabSettings);
	console.log(tabSettings.results);
	if (tabSettings.results) results.drawContent(tabSettings.results);
	codemirrorChangeCallback(); //init query types
	
	
	var positionElements = function() {
		queryHeader.positionElement();
		scrollableSubContent.css("margin-top", ($("#tabs").outerHeight(true) + queryHeader.get().outerHeight(true)) + "px"); 
	};
	positionElements();
	return {
		cm: codemirror,
		queryHeader: queryHeader,
		results: results,
		positionElements: positionElements
	};
};
Yasgui.objs.TabContent = TabContent;
