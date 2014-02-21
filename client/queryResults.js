
var QueryResults = function(parent, tabSettings) {
	var mainResultsDiv = null;
	var downloadIcon = null;
	var header = null;
	var content = null;
	var results = null;
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

		var outputSelector = $(
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
			
			drawTable();
		});
		$("#" + rawResponseSelectorId).click(function(){
			console.log("click");
			tabSettings.outputFormat = "rawResponse";
			Yasgui.settings.store();
			drawRawResponse();
		});
	};
	
//	var drawDownloadLink = function() {
//		downloadLink = $('<a id="downloadLink"><img class="downloadIcon"></a>');
//		header.append(downloadLink);
//		updateDownloadIcon();
//	};
	var drawHeader = function() {
		drawOutputSelector();
		downloadIcon = Yasgui.widgets.DownloadIcon(header, tabSettings);
//		drawDownloadLink();
	};
	var drawTable = function() {
		if (results != null) {
			downloadIcon.update(results);
//			updateDownloadIcon();
			content.html("");
			Yasgui.widgets.ResultsTable(content, results);
		}
	};
	var drawRawResponse = function() {
		console.log("drawing raw response");
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
		results = parser;
		if (parser.getBoolean() !== null) {
			drawBooleanResult();
		} else if (tabSettings.outputFormat == "rawResponse") {
			drawRawResponse();
		} else { 
			drawTable();
		}
		
	};
	
	
	init();
	return {
		drawContent: drawContent,
		clearResults: clearResults
//		check: checkSyntax
	};
};
Yasgui.objs.QueryResults = QueryResults;
