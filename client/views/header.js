Template.header.queryIconSrc = function() {
//	var src = Session.get("queryIcon");
	if (src == undefined) {
		Session.set("queryIcon", Yasgui.constants.imgs.playSquare);
		src = Yasgui.constants.imgs.playSquare;
	}
	return src;
};
Template.header.yasguiHomePage = function() {
	return Yasgui.constants.links.yasguiHome;
};

//run this after dom load, as otherwise Yasgui obj might not exist yet
//Meteor.startup(function(){
//	Session.set("queryStatus", "query");//on page load, reset our query status (there can't be any running queries anyway)
//	Deps.autorun(function() {
//		var queryStatus = Session.get("queryStatus");
//		if (queryStatus == "busy") {
//			$("#queryIcon").off("click").click(function(){
//				Session.set("queryStatus", "query");
//				Yasgui.sparql.cancel();
//				return false;
//			}).children().attr("src",  Yasgui.constants.imgs.loading);
//		} else if (queryStatus == "error") {
//			$("#queryIcon").off("click").click(function(){
//				console.log("query");
//				Yasgui.sparql.query();
//				return false;
//			}).children().attr("src",  Yasgui.constants.imgs.playSquareError);
//	//		Session.set("oldest", oldest.name);
//		} else if (queryStatus == undefined || queryStatus == "query") {
//			$("#queryIcon").off("click").click(function(){
//				Yasgui.sparql.query();
//				return false;
//			}).children().attr("src",  Yasgui.constants.imgs.playSquare);
//		} else {
//			console.log("unrecognized query status in session: " + queryStatus);
//		}
//	});
//});