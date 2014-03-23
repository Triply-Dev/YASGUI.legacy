(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.ConsentWindow = function() {
		var consentMessage = "<p>We track user actions (including used endpoints and queries). " +
				"This data is solely used for research purposes and to get insight into how users use the site. <strong>We would appreciate your consent!</strong></p>";
		var dialog;
		var drawWindow = function() {
			var buttonRow = $("<div class='consentButtonRow'></div>")
				.append($("<button>Yes, allow</button>").button( {
						icons : {
							primary : "checkIcon"
						}
					}).on("click", function(){
						Yasgui.settings.trackingUsage = "yes";
						Yasgui.settings.store();
						dialog.close();
				}))
				.append($("<button>Yes, but no queries/endpoints </button>").button({
						icons : {
							primary : "checkCrossIcon"
						}
					}).on("click", function(){
						Yasgui.settings.trackingUsage = "partial";
						Yasgui.settings.store();
						dialog.close();
				}))
				.append($("<button>No, disable tracking</button>").button({
						icons : {
							primary : "crossIcon"
						}
					}).on("click", function(){
						Yasgui.settings.trackingUsage = "no";
						Yasgui.settings.store();
						dialog.close();
				}))
				.append($("<button>Ask me later</button>").button().on("click", function(){
					dialog.close();
				}));
			var windowContent = $("<div></div>")
				.append(consentMessage)
				.append(buttonRow);
			dialog = Yasgui.widgets.dialog({
				width: 700,
				minHeight: 100,
				content: windowContent,
				position: {
					my: "center bottom",
					at: "center bottom",
					of: window,
					collision: "none"
				},
				onOpen: function(){
					buttonRow.find("button").blur();
				}
			});
		};
		
		
		drawWindow();
	};
}).call(this);
