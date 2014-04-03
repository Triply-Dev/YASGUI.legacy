
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
	var fixedTabContent = $("<div class='fixedTabContent'></div>").appendTo(mainTabContent);
	//fixed
	var queryHeader = new Yasgui.objs.QueryHeader(fixedTabContent, tabSettings);
	
	
	var codemirror = new Yasgui.widgets.QueryCodemirror(fixedTabContent, tabSettings, codemirrorChangeCallback);
	
	//this div is scrollable. the query header and 	query text areay is fixed
	var scrollableSubContent = $("<div class='scrollableTabContent'></div>");
	mainTabContent.append(scrollableSubContent);
	var results = new Yasgui.objs.QueryResults(scrollableSubContent, tabSettings);
	if (tabSettings.results) results.drawContent(tabSettings.results);
	codemirrorChangeCallback(); //init query types
	
	
	var positionElements = function() {
		fixedTabContent.css("top", ($("#tabs").outerHeight(true) + 12 ) + "px");
		queryHeader.positionElement();
//		console.log(mainTabContent.find(".CodeMirror").outerHeight(true));
		scrollableSubContent.css("margin-top", ($("#tabs").outerHeight(true) + 12 + fixedTabContent.outerHeight(true)) + "px"); 
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
