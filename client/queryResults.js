
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
	var updateDownloadIcon = function() {
		//check: do we have results to download?
		if (results == null) {
			downloadLink
			.attr("href", "#")
			.children()
			.attr("src", Yasgui.constants.imgs.getDisabled("download"))
			.addClass("downloadIconDisabled")
			.attr("title", "Nothing to download");
		} else {
			var isTable = (tabSettings.outputFormat == "table");
			targetUrl = null;
			if (isTable) {
				console.log("todo, download as csv");
			} else {
				targetUrl = stringToUrl(results.getResponse(), "text/plain");
			}
			
			var downloadAttrSupported = true;
			if (downloadAttrSupported) {
				downloadLink.attr("download", "bla.csv");;
			} else {
				downloadLink.attr("target", "_blank");
			}
			
			downloadLink
			.attr("href", targetUrl)
			.children()
			.attr("src", (isTable ? Yasgui.constants.imgs.table: Yasgui.constants.imgs.download))
			.removeClass("downloadIconDisabled")
			.attr("title", (isTable? "Download as CSV": "Download query response"));
		}
		
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
		console.log("darwing table");
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
//			updateDownloadIcon();
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
