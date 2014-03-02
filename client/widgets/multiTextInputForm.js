(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	/**
	 * 
	 * @param config {
	 *  allowDel: boolean
	 *  allowNew: boolean
	 *  
	 *  cols: int
	 *  values: array[array]
	 *  requiredCols: array
	 * }
	 * @returns
	 */
	this.Yasgui.widgets.MultiTextInputForm = function(config) {
		var cols = config.cols || 1;
		var requiredCols = config.requiredCols || [];
		var mainElement = $("<div class='multiTextInputDiv'></div>");
		var values = config.values || [];
		var tableEl = $("<table class='multiTextInputTable'></table>");
		var addRow = function(rowArray) {
			rowArray = rowArray || [];
			var rowEl = $("<tr></tr>");
			
			for (var  colIt = 0; colIt < cols; colIt++) {
				var val = rowArray[colIt] || "";
				rowEl.append($("<td></td>").append($("<input type='text'>").val(val)));
			}
			if (config.allowDel) {
				var button = $("<button></button>").click(function(){
					deleteRow(this);
				}).width(18).height(18).button({
					icons : {
						primary : "delTableRow"
					},
					text : false
				});
				rowEl.append($("<td class='iconCell'></td>").append(button));
			}
			tableEl.append(rowEl);
		};
		
		var deleteRow = function(el) {
			$(el).closest("tr").remove();
		};
		var drawTable = function() {
			if (config.headers) {
				var rowEl = $("<tr></tr>");
				for (var colIt = 0; colIt < config.headers.length; colIt++) {
					rowEl.append($("<th></th>").append(config.headers[colIt]));
					
				}
				if (config.allowNew) {
					var button = $("<button></button>").click(function(){
						addRow();
					}).width(18).height(18).button({
						icons : {
							primary : "addTableRow"
						},
						text : false
					});
					rowEl.append($("<td class='iconCell'></td>").append(button));
				}
				tableEl.append(rowEl);
			}
			if (values.length == 0) values.push([]);//always start with one row
			for (var rowIt = 0; rowIt < values.length; rowIt++) {
				addRow(values[rowIt]);
			}
			mainElement.append(tableEl);
		};
		
		var drawIntro = function() {
			if (config.intro) {
				mainElement.append($("<div class='multiTextInputIntro'></div>").html(config.intro));
			}
		};
		
		var getValues = function() {
			var values = [];
			mainElement.find("tr").each(function(rowKey, row){
				var rowVals = [];
				var colIt = 0;
				var addRow = true;
				$(row).find("input").each(function(colKey, cell) {
					if ($.inArray(colIt, requiredCols) !== -1) {
						//we need to check whether this value is filled in!
						if (!$(cell).val() || $(cell).val().length == 0) {
							addRow = false;
							return false;
						}
					}
					rowVals.push($(cell).val());
					colIt++;
				});
				if (addRow && rowVals.length > 0) {
					values.push(rowVals);
				}
			});
			return values;
		};
		
		drawIntro();
		drawTable();
		return {
			getElement: function(){return mainElement;},
			getValues: getValues
		}
	};
}).call(this);
