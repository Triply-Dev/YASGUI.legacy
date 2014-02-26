(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.QueryIcon = function(parent, tabSettings) {
		var queryIcon = null;
		
		var draw = function() {
//			 ".selector" ).button( "option", "icons", { primary: "ui-icon-gear", secondary: "ui-icon-triangle-1-s" } );

			queryIcon = $("<button>query</button>");
			parent.append(queryIcon);
			queryIcon.button({
				text : false
			});
			Deps.autorun(function() {
				var queryStatus = Session.get("queryStatus");
				if (queryStatus == "busy") {
					queryIcon.off("click").click(function(){
						Session.set("queryStatus", "query");
						Yasgui.sparql.cancel();
						return false;
					}).button( "option", "icons", { primary: "queryIconBusy" } );
				} else if (queryStatus == "error") {
					console.log("errorrrr");
					queryIcon.off("click").click(function(){
						console.log("query");
						Yasgui.sparql.query();
						return false;
					}).button( "option", "icons", { primary: "queryIconInvalid" } );
			//		Session.set("oldest", oldest.name);
				} else if (queryStatus == undefined || queryStatus == "query") {
					queryIcon.off("click").click(function(){
						Yasgui.sparql.query();
						return false;
					}).button( "option", "icons", { primary: "queryIcon" } );
				} else {
					console.log("unrecognized query status in session: " + queryStatus);
				}
			});
		};
		
		
		var updateIconStatus = function() {
			
		};
//		var setStatusQuery = function() {
//			queryIcon.button( "option", "icons", { primary: "queryIcon" } );
//		};
//		
//		var setStatusInvalid = function() {
//			queryIcon.button( "option", "icons", { primary: "queryIconInvalid" } );
//		};
//		
//		var setStatusLoading = function() {
//			queryIcon.button( "option", "icons", { primary: "queryIconLoading" } );
//		};
		
		draw();// initialize
		return {
			updateIconStatus: updateIconStatus
//			setStatusQuery: setStatusQuery,
//			setStatusInvalid: setStatusInvalid,
//			setStatusLoading: setStatusLoading
		};
	};
}).call(this);
