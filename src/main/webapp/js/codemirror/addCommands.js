CodeMirror.commands.autocomplete = function(cm) {
	CodeMirror.simpleHint(cm, CodeMirror.prefixHint);
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
};
CodeMirror.commands.copyLineDown = function(cm) {
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
	//Make sure cursor goes one down (we are copying downwards)
	cursor.line++;
	cm.setCursor(cursor);
};
