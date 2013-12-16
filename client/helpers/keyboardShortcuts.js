$(document).keydown(function(e) {
	var code = (e.keyCode ? e.keyCode : e.which);
	if (code == 27) {//escape key
		if (Yasgui && Yasgui.sparql) {
			Yasgui.sparql.cancel();
			Session.set("queryStatus", "query");
		}
	} else if (
			(code == 10 || code == 13) //enter
			&& (e.ctrlKey || e.metaKey) //ctrl or apple cmd key
		) {
		if (Yasgui && Yasgui.sparql) {
			Yasgui.sparql.query();
			e.preventDefault();
		}
		
	}

});