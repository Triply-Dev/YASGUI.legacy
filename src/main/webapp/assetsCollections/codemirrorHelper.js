CodeMirror.commands.executeQuery = function(cm) {
	executeQuery();
};
//Make sure deleteLine also -removes- the line
CodeMirror.commands.deleteLines = function(cm) {
	var startLine = cm.getCursor(true).line;
	var endLine = cm.getCursor(false).line;
	var min = Math.min(startLine, endLine);
	var max = Math.max(startLine, endLine);
	for ( var i = min; i <= max; i++) {
		//Do not remove i, because line counter changes after deleting 1 line. 
		//Therefore, keep on deleting the minimum of the selection
		cm.removeLine(min);
	}
	var cursor = cm.getCursor(true);
	if (cursor.line + 1 <= cm.lineCount() ) {
		cursor.line++;
		cursor.ch = 0;
		cm.setCursor(cursor);
	}
};
CodeMirror.commands.commentLines = function(cm) {
	var startLine = cm.getCursor(true).line;
	var endLine = cm.getCursor(false).line;
	var min = Math.min(startLine, endLine);
	var max = Math.max(startLine, endLine);

	//if all lines start with #, remove this char. Otherwise add this char
	var linesAreCommented = true;
	for ( var i = min; i <= max; i++) {
		var line = cm.getLine(i);
		if (line.length == 0 || line.substring(0, 1) != "#") {
			linesAreCommented = false;
			break;
		}
	}
	for ( var i = min; i <= max; i++) {
		if (linesAreCommented) {
			//lines are commented, so remove comments
			cm.replaceRange("", {
				line : i,
				ch : 0
			}, {
				line : i,
				ch : 1
			});
		} else {
			//Not all lines are commented, so add comments
			cm.replaceRange("#", {
				line : i,
				ch : 0
			});
		}

	}
};

CodeMirror.commands.copyLineUp = function(cm) {
	copyLinesBelow(cm);
};
CodeMirror.commands.copyLineDown = function(cm) {
	copyLinesBelow(cm);
	//Make sure cursor goes one down (we are copying downwards)
	var cursor = cm.getCursor();
	cursor.line++;
	cm.setCursor(cursor);
};
CodeMirror.commands.doAutoFormat = function(cm) {
	if (cm.somethingSelected()) {
		var to = {line: cm.getCursor(false).line, ch: cm.getSelection().length};
		cm.autoFormatRange(cm.getCursor(true), to);
	} else {
		var totalLines = cm.lineCount();
		var totalChars = cm.getTextArea().value.length;
		cm.autoFormatRange({line:0, ch:0}, {line:totalLines, ch:totalChars});
	}
	
};
CodeMirror.commands.autoComplete = function(cm) {
	if (cm.somethingSelected()) {
		//do nothing
	} else {
		CodeMirror.showHint(cm, CodeMirror.doPredicateAutocompleteRequest, {async: true,closeCharacters: /(?=a)b/});
	}
	
};
CodeMirror.commands.indentTab = function(cm) {
	var indentSpaces = Array(cm.getOption("indentUnit") + 1).join(" ");
	if (cm.somethingSelected()) {
		for (var i = cm.getCursor(true).line; i <= cm.getCursor(false).line; i++) {
			cm.replaceRange(indentSpaces, {
				line : i,
				ch : 0
			});
		}
	} else {
	    cm.replaceSelection(indentSpaces, "end", "+input");
	}
	
};
CodeMirror.commands.unindentTab = function(cm) {
	for (var i = cm.getCursor(true).line; i <= cm.getCursor(false).line; i++) {
		var line = cm.getLine(i);
		if (/^\t/.test(line)) {
			console.log("tab!");
			line = line.replace(/^\t(.*)/, "$1");
		} else if (/^ /.test(line)) {
			var re = new RegExp("^ {1," + cm.getOption("indentUnit") + "}(.*)","");
			line = line.replace(re, "$1");
		}
		cm.setLine(i, line);
	}
	
};
function copyLinesBelow(cm) {
	var cursor = cm.getCursor();
	var lineCount = cm.lineCount();
	//First create new empty line at end of text
	cm.replaceRange("\n", {
		line : lineCount - 1,
		ch : cm.getLine(lineCount - 1).length
	});
	//Copy all lines to their next line
	for ( var i = lineCount; i > cursor.line; i--) {
		cm.setLine(i, cm.getLine(i - 1));
	}
}
var clearError = function() {};
var prevQueryValid = true;
function checkSyntax(cm, updateQueryButton) {
	var queryValid = true;
	if (clearError != null) {
		clearError();
		clearError = null;
	}
	;
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
		showPlayButton((queryValid? "1": "0"));
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
}

/**
 * loop through line. return true if we have a line break. return false if we find a non-ws token
 */
function nextTokenIsLinebreak(cm, lineNumber, charNumber) {
	if (charNumber == undefined)
		charNumber = 1;
	var token = cm.getTokenAt({
		line : lineNumber,
		ch : charNumber
	});
	if (token.end < charNumber) {
		//hmm, can't find a new token on this line: we've reached the line break
		return true;
	}
	if (token == null || token == undefined || token.end < charNumber || token.type != "sp-ws") {
		return false;
	} else {
		if (/\r?\n|\r/.test(token.string)) {
			//line break!
			return true;
		} else {
			//just a space. get next token
			return nextTokenIsLinebreak(cm, lineNumber, token.end + 1);
		}
	}
	
	
}


CodeMirror.extendMode("sparql11", {
	autoFormatLineBreaks: function (text, start, end) {
//		text = text.substring(start, end).replace(/\r?\n|\r/g, " ");
		text = text.substring(start, end);
		breakAfterArray = [
		    ["sp-keyword", "sp-ws", "sp-prefixed", "sp-ws", "sp-uri"], //i.e. prefix declaration
		    ["sp-keyword", "sp-ws", "sp-uri"]//i.e. base
		];
		breakAfterCharacters = ["{", ".", ";"];
		breakBeforeCharacters = ["}"];
		getBreakType = function(stringVal, type) {
			for (var i = 0; i < breakAfterArray.length; i++) {
				if (stackTrace.equals(breakAfterArray[i])) {
					return 1;
				}
			}
			for (var i = 0; i < breakAfterCharacters.length; i++) {
				if (stringVal == breakAfterCharacters[i]) {
					return 1;
				}
			}
			for (var i = 0; i < breakBeforeCharacters.length; i++) {
				//don't want to issue 'breakbefore' AND 'breakafter', so check current line
				if ($.trim(currentLine) != '' && stringVal == breakBeforeCharacters[i]) {
					return -1;
				}
			}
			return 0;
		};
		var formattedQuery = "";
		var currentLine = "";
		var stackTrace = [];
		CodeMirror.runMode(text, "sparql11", function(stringVal, type) {
			stackTrace.push(type);
			var breakType = getBreakType(stringVal, type);
			if (breakType != 0) {
				if (breakType == 1) {
					formattedQuery += stringVal + "\n";
					currentLine = "";
				} else {//(-1)
					formattedQuery += "\n" + stringVal;
					currentLine = stringVal;
				}
				stackTrace = [];
			} else {
				currentLine += stringVal;
				formattedQuery += stringVal;
			}
			if (stackTrace.length == 1 && stackTrace[0] == "sp-ws") stackTrace = [];
		});
		return $.trim(formattedQuery.replace(/\n\s*\n/g, '\n'));
	}
  });
