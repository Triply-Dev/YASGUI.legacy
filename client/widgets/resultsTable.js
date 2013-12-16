(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.ResultsTable = function(container, resultsParser) {
		var getVariablesAsCols = function() {
			var cols = [];
			cols.push({"sTitle": ""});//row numbers
			var sparqlVars = resultsParser.getVariables();
			for (var i = 0; i < sparqlVars.length; i++) {
				cols.push({"sTitle": sparqlVars[i]});
			}
			return cols;
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
						row.push(binding[sparqlVar].value);
					} else {
						row.push("");
					}
				}
				rows.push(row);
			}
			return rows;
		};
		
		var draw = function() {
			var table = $('<table cellpadding="0" cellspacing="0" border="0" class="resultsTable"></table>');
			$(container).html( table );
			table.dataTable( {
		    	"iDisplayLength": 50,
		    	"aLengthMenu": [[10, 50, 100, 1000, -1], [10, 50, 100, 1000, "All"]],
		    	"bLengthChange": true,
		    	"sPaginationType": "full_numbers",
		        "aaData": getRows(),
//		        	[
//		            /* Reduced data set */
//		            [ "Trident", "Internet Explorer 4.0", "Win 95+", 4, "X" ],
//		            [ "Trident", "Internet Explorer 5.0", "Win 95+", 5, "C" ],
//		            [ "Trident", "Internet Explorer 5.5", "Win 95+", 5.5, "A" ],
//		            [ "Trident", "Internet Explorer 6.0", "Win 98+", 6, "A" ],
//		            [ "Trident", "Internet Explorer 7.0", "Win XP SP2+", 7, "A" ],
//		            [ "Gecko", "Firefox 1.5", "Win 98+ / OSX.2+", 1.8, "A" ],
//		            [ "Gecko", "Firefox 2", "Win 98+ / OSX.2+", 1.8, "A" ],
//		            [ "Gecko", "Firefox 3", "Win 2k+ / OSX.3+", 1.9, "A" ],
//		            [ "Webkit", "Safari 1.2", "OSX.3", 125.5, "A" ],
//		            [ "Webkit", "Safari 1.3", "OSX.3", 312.8, "A" ],
//		            [ "Webkit", "Safari 2.0", "OSX.4+", 419.3, "A" ],
//		            [ "Webkit", "Safari 3.0", "OSX.4+", 522.1, "A" ]
//		        ],
		        "aoColumns": getVariablesAsCols(),
//		        	[
//		            { "sTitle": "Engine" },
//		            { "sTitle": "Browser" },
//		            { "sTitle": "Platform" },
//		            { "sTitle": "Version", "sClass": "center" },
//		            { "sTitle": "Grade", "sClass": "center" }
//		        ]
		        "fnDrawCallback": function ( oSettings ) {
					for ( var i = 0; i < oSettings.aiDisplay.length; i++) {
						$('td:eq(0)',oSettings.aoData[oSettings.aiDisplay[i]].nTr).html(i + 1);
					}
				},
				"aoColumnDefs": [
					{ "sWidth": "12px", "bSortable": false, "aTargets": [ 0 ] }
				],
				//"aaSorting": [[ 1, 'asc' ]]
		    } ); 
		};
		draw();
		
	};
}).call(this);
