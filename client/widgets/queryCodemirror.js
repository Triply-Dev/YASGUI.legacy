(function(){
	this.Yasgui = this.Yasgui || {};
	this.Yasgui.widgets = this.Yasgui.widgets || {};
	
	
	this.Yasgui.widgets.QueryCodemirror = function(tabSettings) {
		var codemirror = null;
		var prevQueryValid = false;
		var clearError = null;
		var checkSyntax = function(cm, updateQueryButton) {
			if (cm == undefined && updateQueryButton == undefined) {
				cm = codemirror;
				updateQueryButton = true;
			}
			var queryValid = true;
			if (clearError != null) {
				clearError();
				clearError = null;
			}
			cm.clearGutter("gutterErrorBar");
			var state = null;
			for ( var l = 0; l < cm.lineCount(); ++l) {
				var precise = false;
				if (!prevQueryValid) {
					//we don't want cached information in this case, otherwise the previous error sign might still show up,
					//even though the syntax error might be gone already
					precise = true;
				}
				state = cm.getTokenAt({
					line : l,
					ch : cm.getLine(l).length
				}, precise).state;
				if (state.OK == false) {
					var error = document.createElement('span');
					error.innerHTML = "&rarr;";
					error.className = "gutterError";
					cm.setGutterMarker(l,"gutterErrorBar", error);
					clearError = function() {
						cm.markText({
							line : l,
							ch : state.errorStartPos
						}, {
							line : l,
							ch : state.errorEndPos
						}, "sp-error");
					};
					queryValid = false;
					break;
				}
			}
			prevQueryValid = queryValid;
			if (updateQueryButton) {
				Session.set("queryStatus", (queryValid? "query": "error"));
				if (state != null && state.stack != undefined) {
					var stack = state.stack, len = state.stack.length;
					// Because incremental parser doesn't receive end-of-input
					// it can't clear stack, so we have to check that whatever
					// is left on the stack is nillable
					if (len > 1)
						queryValid = false;
					else if (len == 1) {
						if (stack[0] != "solutionModifier" && stack[0] != "?limitOffsetClauses"
								&& stack[0] != "?offsetClause")
							queryValid = false;
					}
				}
			}
		};
		var init = function() {
			codemirror = CodeMirror(document.getElementById(tabSettings.id), {
				mode : "sparql11",
				theme: "yasgui",
				value: tabSettings.query,
				highlightSelectionMatches: {showToken: /\w/},
				tabMode : "indent",
				lineNumbers : true,
				gutters: ["gutterErrorBar","CodeMirror-linenumbers" ],
				matchBrackets : true,
				fixedGutter: true,
				extraKeys : {
					"Ctrl-Space" : "autoComplete",
					"Cmd-Space" : "autoComplete",
					"Ctrl-D" : Yasgui.Codemirror.deleteLines,
					"Ctrl-K" : Yasgui.Codemirror.deleteLine,
					"Cmd-D" : Yasgui.Codemirror.deleteLine,
					"Cmd-K" : Yasgui.Codemirror.deleteLine,
					"Ctrl-/" : Yasgui.Codemirror.commentLines,
					"Cmd-/" : Yasgui.Codemirror.commentLines,
					"Ctrl-Alt-Down" : Yasgui.Codemirror.copyLineDown,
					"Ctrl-Alt-Up" : Yasgui.Codemirror.copyLineUp,
					"Cmd-Alt-Down" : Yasgui.Codemirror.copyLineDown,
					"Cmd-Alt-Up" : Yasgui.Codemirror.copyLineUp,
					"Shift-Ctrl-F": Yasgui.Codemirror.doAutoFormat,
					"Shift-Cmd-F": Yasgui.Codemirror.doAutoFormat,
					"Tab" : Yasgui.Codemirror.indentTab,
					"Shift-Tab": Yasgui.Codemirror.unindentTab
				}
			});
			
			codemirror.on("change", function(cm, change){
				checkSyntax(cm, true);
//				queryType = cm.getStateAfter().queryType;
				CodeMirror.showHint(cm, Yasgui.Codemirror.prefixHint, {closeCharacters: /(?=a)b/});
				Yasgui.Codemirror.appendPrefixIfNeeded(cm);
			});
			codemirror.on("blur", function(cm, change) {
				storeInSettings();
			});
			//init query type
//			queryType = codemirror.getStateAfter().queryType;
			
			
			//Append another classname to the codemirror div, so we can set width and height via css
//			if (qInput.nextSibling != null && qInput.nextSibling.className == "CodeMirror") {
//				qInput.nextSibling.className = "CodeMirror queryCm";
//				scrollElement = qInput.nextSibling.getElementsByClassName("CodeMirror-scroll");
//				//use jquery for this (a bit easier). for this element, find scroll class, and append another class
//				$("#"+elementId).next().find($(".CodeMirror-scroll")).addClass("queryScrollCm");
//			}
		};
		
		var storeInSettings = function() {
			tabSettings.query = codemirror.getValue();
			Yasgui.settings.store();
		};
		init();
		
		return {
			cm: codemirror,
			check: checkSyntax,
			storeInSettings: storeInSettings
		};
	};
}).call(this);