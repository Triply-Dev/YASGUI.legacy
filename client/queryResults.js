
var QueryResults = function(parent, tabSettings) {
	var mainResultsDiv = null;
	var downloadIcon = null;
	var header = null;
	var content = null;
	var results = null;
	var outputSelector = null;
	var init = function() {
		mainResultsDiv = $("<div class='queryResults'></div>");
		parent.append(mainResultsDiv);
		header = $("<div class='queryResultsHeader'></div>");
		mainResultsDiv.append(header);
		content = $("<div class='queryResultsContent'></div>");
		mainResultsDiv.append(content);
		drawHeader();
	};
	
	var drawOutputSelector = function() {
		var tableSelectorId = tabSettings.id + "_tableSelect";
		var rawResponseSelectorId = tabSettings.id + "_responseSelect";

		outputSelector = $(
			'<div class="outputSelector">' +
			'<input id="' + tableSelectorId + '" type="radio"  name="radio" ' + (tabSettings.outputFormat != "rawResponse"? 'checked="checked"': "") + '><label for="' + tableSelectorId + '">Show as table</label>' +
			'<input id="' + rawResponseSelectorId + '" type="radio"  name="radio" ' + (tabSettings.outputFormat == "rawResponse"? 'checked="checked"': "") + '><label for="' + rawResponseSelectorId + '">Show raw response</label>'+
			'</div>'
		);
		header.append(outputSelector);
		outputSelector.buttonset();
		
		$("#" + tableSelectorId).click(function(){
			tabSettings.outputFormat = "table";
			Yasgui.settings.store();
			
			drawContent();
		});
		$("#" + rawResponseSelectorId).click(function(){
			tabSettings.outputFormat = "rawResponse";
			Yasgui.settings.store();
			drawContent();
		});
	};
	
	var drawHeader = function() {
		drawOutputSelector();
		downloadIcon = Yasgui.widgets.DownloadIcon(header, tabSettings);
	};
	var drawTable = function() {
		if (results != null) {
			downloadIcon.update(results);
			content.html("");
			Yasgui.widgets.ResultsTable(content, results);
		}
	};
	var drawRawResponse = function() {
		if (results != null) {
			downloadIcon.update(results);
			content.html("");
			Yasgui.widgets.ResultsCodemirror(content, results);
		}
	};
	var clearResults = function() {
		results = null;
	};
	var drawBooleanResult = function() {
		if (results != null) {
			downloadIcon.update(results);
			content.html("");
			Yasgui.widgets.BooleanResult(content, results.getBoolean());
		}
	};
	
	var drawContent = function(parser) {
		if (parser) results = parser;
		if (results) {
			if (tabSettings.outputFormat == "rawResponse") {
				drawRawResponse();
			} else if (results.getBoolean() !== null) {
				drawBooleanResult();
			} else if (results.getVariables().length > 0){ 
				drawTable();
			} else {
				//if all else fails, just draw the raw response
				drawRawResponse();
			}
		}
		
	};
	var tableOutputEnabled = null;
	var setTableOutputEnabled = function(enabled) {
		if (tableOutputEnabled !== enabled) {
			tableOutputEnabled = enabled;
			outputSelector.buttonset("option", "disabled", !tableOutputEnabled);
		}
	};
	
	init();
	return {
		drawContent: drawContent,
		clearResults: clearResults,
		setTableOutputEnabled: setTableOutputEnabled
	};
};
Yasgui.objs.QueryResults = QueryResults;
