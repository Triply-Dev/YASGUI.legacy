//runs after all other js files

Yasgui.constants = {};
Yasgui.constants.links = new Yasgui.objs.Links();
Yasgui.constants.imgs = new Yasgui.objs.Imgs();
Yasgui.constants.zIndexes = new Yasgui.objs.ZIndexes();
Yasgui.storage = new Yasgui.objs.Storage();
Yasgui.settings = new Yasgui.objs.Settings();

Yasgui.prefixes = new Yasgui.objs.Prefixes();
Yasgui.endpoints = new Yasgui.objs.Endpoints();
Session.set("queryStatus", "query");//on page load, reset our query status (there can't be any running queries anyway)

//Register handlers
window.onresize = function() {
   Yasgui.tabs.positionElements();
};



//Meteor.startup(function() {
//	Meteor.call("forcePrefixUpdate", function(errorMsg, result){
//		if (result == undefined) {
//			console.log(errorMsg);
//		} else {
//			console.log(result);
//		}
//		
//	});
//});
//HTTP.call("POST", "http://localhost:1337", {
////	  query: "id=foo&id=bar"
//	content: "id=foo&id=bar"
////		]
//	}, function(error, result) {
//	 console.log(result);
//	});