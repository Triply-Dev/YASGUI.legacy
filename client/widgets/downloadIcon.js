(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.DownloadIcon = function(parent, tabSettings) {
		var downloadLink = null;
		
		var draw = function() {
			downloadLink = $('<button class="downloadButton"></button>').button({
				icons: {
			        primary: "downloadTableIcon"
			      },
			      text: false
			});
			parent.append(downloadLink);
			updateIcon();
		};
		var stringToUrl = function(string, contentType) {
			url = null;
			windowUrl = window.URL || window.webkitURL || window.mozURL || window.msURL;

			if (windowUrl && Blob) {
				var blob = new Blob([string], {type: contentType});
				url = windowUrl.createObjectURL(blob);
			}
			return url;
		};
		
		var getFilename = function(results, isTable) {
			filename = tabSettings.tabTitle;
			if (isTable) {
				filename += ".csv";
			} else {
				filename += "." + results.getMetaInfo.extension;
			}
			return filename;
		};
		
		var updateIcon = function(results) {
			//check: do we have results to download?
			if (!Yasgui.compatabilities.stringToUrl()) {
				downloadLink.button( "option", "icons", { primary: "downloadTableIcon" })
				.button( "option", "disabled", true )
				.button("refresh")
				.attr("title", "Your browser does not support client-side downloading of files");
				
//				downloadLink
//				.addClass("downloadIconDisabled")
//				.attr("src", Yasgui.constants.imgs.download.getDisabled())
				
			} else if (results == null) {
				downloadLink.button( "option", "icons", { primary: "downloadIcon" }).button( "option", "disabled", true )
				.attr("title", "Nothing to download");
//				downloadLink
//				.addClass("downloadIconDisabled")
//				.attr("src", Yasgui.constants.imgs.download.getDisabled())
//				.attr("title", "Nothing to download");
			} else {
				var isTable = (tabSettings.outputFormat == "table" && results.getBoolean() === null);
				
				downloadLink
					.button( "option", "icons", { primary: (isTable? "downloadTableIcon": "downloadIcon") })
					.button( "option", "disabled", false)
					.button("refresh")
					.attr("title", (isTable? "Download as CSV": "Download query response"))
					.off("click")
					.on("click", function() {
						var targetUrl = null;
						if (isTable) {
							targetUrl = stringToUrl(resultToCsv(results), "text/csv");
						} else {
							targetUrl = stringToUrl(results.getResponse(), "text/plain");
						}
						
						if (Yasgui.compatabilities.downloadAttribute()) {
							var downloadMockLink = $("<a></a>");
							downloadMockLink.attr("href", targetUrl);
							downloadMockLink.attr("download", getFilename(results, isTable));
							downloadMockLink.get(0).click();
						} else {
							window.open(targetUrl);
						}
						
					});
			}
		};
		
		draw();//initialize
		return {
			update: updateIcon
		};
	};
}).call(this);
