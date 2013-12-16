(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.ResultsCodemirror = function(container, resultsParser) {
		var contentType = resultsParser.getContentType();
		cmMode = "javascript";
		if (contentType.contains("json")) {
			cmMode = {
				name: "javascript",
				json: true
			};
		}
		CodeMirror(container.get()[0], {
			mode : cmMode,
			theme: "yasgui",
			lineNumbers : true,
			highlightSelectionMatches: {showToken: /\w/},
			matchBrackets : true,
			readOnly: true,
			fixedGutter: true,
			hideVScroll: true,
			value: resultsParser.getResponse()
		});
		
		//Append another classname to the codemirror div, so we can set width and height via css
//		if (qInput.nextSibling != null && qInput.nextSibling.className == "CodeMirror") {
//			qInput.nextSibling.className = "CodeMirror resultCm";
//			scrollElement = qInput.nextSibling.getElementsByClassName("CodeMirror-scroll");
//			//use jquery for this (a bit easier). for this element, find scroll class, and append another class
//			$("#"+elementId).next().find($(".CodeMirror-scroll")).addClass("resultScrollCm");
//		}
		
	};
}).call(this);
