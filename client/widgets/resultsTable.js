(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.ResultsTable = function(container, resultsParser) {
		var table;
		var getVariablesAsCols = function() {
			var cols = [];
			cols.push({"sTitle": ""});//row numbers
			var sparqlVars = resultsParser.getVariables();
			for (var i = 0; i < sparqlVars.length; i++) {
				cols.push({"sTitle": sparqlVars[i]});
			}
			return cols;
		};
		
		var getFormattedValueFromBinding = function(binding) {
			var value = null;
			if (binding.type == "uri") {
				value = "<a class='snorqlLink' href='#'>" + binding.value + "</a>";
			} else {
				value = "<span class='regularValue'>" + binding.value + "</span>";
			}
			return value;
		};
		var getRows = function() {
			var rows = [];
			var bindings = resultsParser.getBindings();
			var vars = resultsParser.getVariables();
			for (var rowId = 0; rowId < bindings.length; rowId++) {
				var row = [];
				row.push("");//row numbers
				var binding = bindings[rowId];
				for (var colId = 0; colId < vars.length; colId++) {
					var sparqlVar = vars[colId];
					if (sparqlVar in binding) {
						row.push(getFormattedValueFromBinding(binding[sparqlVar]));
					} else {
						row.push("");
					}
				}
				rows.push(row);
			}
			return rows;
		};
		
		var getExternalLinkElement = function() {
			var element = $("#externalLink");
			if (element.length == 0) {
				element = $("<img id='externalLink' src='" + Yasgui.constants.imgs.externalLink.get() + "'></img>")
					.on("click", function(){
						window.open($(this).parent().text());
					});
			}
			return element;
		};
		
		var executeSnorqlQuery = function(uri) {
			var newQuery = Yasgui.settings.defaults.tabularBrowsingTemplate;
			newQuery = newQuery.replace(/<URI>/g, "<" + uri + ">");
			Yasgui.settings.getCurrentTab().query = newQuery;
			Yasgui.tabs.getCurrentTab().cm.reloadFromSettings();
			Yasgui.sparql.query();
		};
		var addEvents = function() {
			table.delegate(".snorqlLink", "click", function() {
				executeSnorqlQuery(this.innerHTML);
				return false;
			});
			
			
			table.delegate("td",'mouseenter', function(event) {
				var extLinkElement = getExternalLinkElement();
				$(this).append(extLinkElement);
				extLinkElement.css("top", ($(this).height() - extLinkElement.height() / 2)); 
				extLinkElement.show();
			}).delegate("td",'mouseleave', function(event) {
				getExternalLinkElement().hide();
			});
		};
		var draw = function() {
			table = $('<table cellpadding="0" cellspacing="0" border="0" class="resultsTable"></table>');
			$(container).html( table );
			table.dataTable( {
		    	"iDisplayLength": 50,
		    	"aLengthMenu": [[10, 50, 100, 1000, -1], [10, 50, 100, 1000, "All"]],
		    	"bLengthChange": true,
		    	"sPaginationType": "full_numbers",
		        "aaData": getRows(),
		        "aoColumns": getVariablesAsCols(),
		        "fnDrawCallback": function ( oSettings ) {
		        	console.log("draw callback");
					for ( var i = 0; i < oSettings.aiDisplay.length; i++) {
						$('td:eq(0)',oSettings.aoData[oSettings.aiDisplay[i]].nTr).html(i + 1);
					}
				},
				"aoColumnDefs": [
					{ "sWidth": "12px", "bSortable": false, "aTargets": [ 0 ] }
				],
		    } ); 
			addEvents();
		};
		draw();
		
	};
}).call(this);
