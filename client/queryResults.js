
var QueryResults = function(tabSettings) {
	var mainResultsDiv = null;
	var downloadIcon = null;
	var header = null;
	var content = null;
	var results = null;
	var init = function() {
		mainResultsDiv = $("<div class='queryResults'></div>");
		$("#" + tabSettings.id).append(mainResultsDiv);
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
	var updateDownloadIcon = function() {
		//check: do we have results to download?
		if (results == null) {
			downloadIcon
			.attr("src", Yasgui.constants.imgs.getDisabled("download"))
			.off()
			.addClass("downloadIconDisabled")
			.attr("title", "Nothing to download");
		} else {
			var isTable = (tabSettings.outputFormat == "table");
//			console.log("istable", isTable);
			downloadIcon
			.attr("src", (isTable ? Yasgui.constants.imgs.table: Yasgui.constants.imgs.download))
			.off()
			.click(function() {
				if (isTable) {
					console.log("todo, download as csv");
				} else {
					console.log("todo, download query response");
				}
			})
			.removeClass("downloadIconDisabled")
			.attr("title", (isTable? "Download as CSV": "Download query response"));
		}
		
	};
	var drawDownloadIcon = function() {
		downloadIcon = $('<img class="downloadIcon">');
		header.append(downloadIcon);
		updateDownloadIcon();
	};
	var drawHeader = function() {
		drawOutputSelector();
		drawDownloadIcon();
	};
	var drawTable = function() {
		console.log("darwing table");
		if (results != null) {
			updateDownloadIcon();
			content.html("");
			Yasgui.widgets.ResultsTable(content, results);
		}
	};
	var drawRawResponse = function() {
		console.log("drawing raw response");
		if (results != null) {
			updateDownloadIcon();
			content.html("");
			Yasgui.widgets.ResultsCodemirror(content, results);
		}
	};
	var clearResults = function() {
		results = null;
	};
	
	var drawContent = function(parser) {
		results = parser;
		if (tabSettings.outputFormat == "rawResponse") {
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
