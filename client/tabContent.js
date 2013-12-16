
var TabContent = function(tabSettings) {
	var init = function() {
//		initQueryCodemirror();
	};
	var queryHeader = new Yasgui.objs.QueryHeader(tabSettings);
	var codemirror = new Yasgui.widgets.QueryCodemirror(tabSettings);
	var results = new Yasgui.objs.QueryResults(tabSettings);
	init();
	
	
	return {
		cm: codemirror,
		queryHeader: queryHeader,
		results: results
	};
};
Yasgui.objs.TabContent = TabContent;
