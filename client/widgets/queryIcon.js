(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.QueryIcon = function(parent, tabSettings) {
		var queryIcon = null;
		
		var draw = function() {
//			 ".selector" ).button( "option", "icons", { primary: "ui-icon-gear", secondary: "ui-icon-triangle-1-s" } );

			queryIcon = $("<button class='queryButton'>query</button>");
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
					}).blur().button( "option", "icons", { primary: "queryIconBusy" } );
				} else if (queryStatus == "error") {
					queryIcon.off("click").click(function(){
						console.log("query");
						Yasgui.sparql.query();
						return false;
					}).blur().button( "option", "icons", { primary: "queryIconInvalid" } );
			//		Session.set("oldest", oldest.name);
				} else if (queryStatus == undefined || queryStatus == "query") {
					queryIcon.off("click").click(function(){
						Yasgui.sparql.query();
						return false;
					}).blur().button( "option", "icons", { primary: "queryIcon" } );
				} else {
					console.log("unrecognized query status in session: " + queryStatus);
				}
			});
			queryIcon.parent().width(queryIcon.width() + 4);
		};
		
		
		var updateIconStatus = function() {
			
		};
		
		draw();// initialize
		return {
			updateIconStatus: updateIconStatus
//			setStatusQuery: setStatusQuery,
//			setStatusInvalid: setStatusInvalid,
//			setStatusLoading: setStatusLoading
		};
	};
}).call(this);
