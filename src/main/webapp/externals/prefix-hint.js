(function() {
	function forEach(arr, f) {
		for ( var i = 0, e = arr.length; i < e; ++i)
			f(arr[i]);
	}

	function arrayContains(arr, item) {
		if (!Array.prototype.indexOf) {
			var i = arr.length;
			while (i--) {
				if (arr[i] === item) {
					return true;
				}
			}
			return false;
		}
		return arr.indexOf(item) != -1;
	}

	function prefixHint(editor, prefixes, getToken) {

		// Find the token at the cursor
		var cur = editor.getCursor(), token = getToken(editor, cur);
		
		includePreviousTokens = function(token, cur) {
			var prevToken = getToken(editor, {line : cur.line,ch : token.start});
			if (prevToken.className == "sp-punct" || prevToken.className == "sp-keyword") {
				token.start = prevToken.start;
				cur.ch = prevToken.start;
				token.string = prevToken.string + token.string;
				return includePreviousTokens(token, cur);//recursively, might have multiple tokens which it should include
			} else {
				return token;
			}
		};
		
		//First token of line needs to be PREFIX, and there should be no trailing text (otherwise, text is wrongly inserted in between)
		firstToken = getNextNonWsToken(editor, cur.line);
		if (firstToken == null || firstToken.string.toUpperCase() != "PREFIX") return;
		if (editor.getLine(cur.line).length > cur.ch) return;
		//Dont show prefixes when - cursor is on first page, or there is text previously
		
		
		//If this is a whitespace, and token is just after PREFIX, proceed using empty string as token
		if (/\s*/.test(token.string) && getToken(editor, {line : cur.line,ch : token.start}).string.toUpperCase() == "PREFIX") {
			token = {
				start : cur.ch,
				end : cur.ch,
				string : "",
				state : token.state
			};
		} else {
			//We know we are in a PREFIX line. Now check whether the string starts with a punct or keyword
			//Good example is 'a', which is a valid punct in our grammar. 
			//This is parsed as separate token which messes up the token for autocompletion (the part after 'a' is used as separate token)
			//If previous token is in keywords or keywords, prepend this token to current token
			token = includePreviousTokens(token, cur);
		}
		
		return {
			list : getCompletions(token, prefixes),
			from : {
				line : cur.line,
				ch : token.start
			},
			to : {
				line : cur.line,
				ch : token.end
			}
		};
	}

	CodeMirror.prefixHint = function(editor) {
		return prefixHint(editor, prefixes, function(e, cur) {
			return e.getTokenAt(cur);
		});
	};
	
	function getCompletions(token, keywords) {
		//the keywords should contain the prefixes
		//Start: end of string being typed
		var found = [], start = token.string;
		function maybeAdd(str) {
			if (str.indexOf(start) == 0 && !arrayContains(found, str))
				found.push(str + "\n"); //append linebreak! Otherwise we stay on the same line, and after adding the popup with autocompletions is still firing..
		}
		forEach(keywords, maybeAdd);
		return found;
	}
})();


function getNextNonWsToken(cm, lineNumber, charNumber) {
	if (charNumber == undefined) charNumber = 1;
	var token = cm.getTokenAt({line : lineNumber,ch : charNumber});
	if (token == null || token == undefined || token.end < charNumber) {
		return null;
	}
	if (token.className == "sp-ws") {
		return getNextNonWsToken(cm, lineNumber, token.end+1);
	}
	return token;
}

/**
 * Check whether typed prefix is declared. If not, automatically add declaration using list from prefix.cc
 * @param cm
 */
function appendPrefixIfNeeded(cm) {
	var cur = cm.getCursor();
	var token = cm.getTokenAt(cur);
	if (token.className == "sp-prefixed") {
//		if (token.string.endsWith(":")) {
		var colonIndex = token.string.indexOf(":");
		if (colonIndex !== -1) {
			//check first token isnt PREFIX, and previous token isnt a '<' (i.e. we are in a uri)
			var firstTokenString = getNextNonWsToken(cm, cur.line).string.toUpperCase();
			var previousToken = cm.getTokenAt({line: cur.line, ch: token.start});//needs to be null (beginning of line), or whitespace
			if (firstTokenString != "PREFIX" && (previousToken.className == "sp-ws" || previousToken.className == null)) {
				//check whether it isnt defined already (saves us from looping through the array)
				var currentPrefix = token.string.substring(0, colonIndex+1);
				var queryPrefixes = getPrefixesFromQuery(cm);
				if (queryPrefixes.length == 0 || $.inArray(currentPrefix, queryPrefixes) == -1) {
					for (var i = 0; i < prefixes.length; i++) {
						var prefix = prefixes[i].substring(0, currentPrefix.length);
						if (prefix == currentPrefix) {
							appendToPrefixes(cm, prefixes[i]);
							break;
						}
					}
				}
			}
		}
	}
}


/**
 * Get defined prefixes from query as array, in format ["rdf:", "rdfs:"]
 * @param cm
 * @returns {Array}
 */
function getPrefixesFromQuery(cm) {
	var queryPrefixes = [];
	var numLines = cm.lineCount();
	for (var i = 0; i < numLines; i++) {
		var firstToken = getNextNonWsToken(cm, i);
		if (firstToken != null && firstToken.string == "PREFIX") {
			var prefix = getNextNonWsToken(cm, i, firstToken.end + 1);
			if (prefix.string != ":") {
				queryPrefixes.push(prefix.string);
			}
		}
	}
	return queryPrefixes;
}

/**
 * Append prefix declaration to list of prefixes in query window.
 * @param cm
 * @param prefix
 */
function appendToPrefixes(cm, prefix) {
	var lastPrefix = null;
	var lastPrefixLine = 0;
	var numLines = cm.lineCount();
	for (var i = 0; i < numLines; i++) {
		var firstToken = getNextNonWsToken(cm, i);
		if (firstToken != null && (firstToken.string == "PREFIX" || firstToken.string == "BASE")) {
			lastPrefix = firstToken;
			lastPrefixLine = i;
		}
	}

	if (lastPrefix == null) {
		cm.replaceRange("PREFIX " + prefix + "\n", {line: 0, ch:0});
	} else {
		var previousIndent = getIndentFromLine(cm, lastPrefixLine);
		cm.replaceRange("\n" + previousIndent + "PREFIX " + prefix, {line: lastPrefixLine});
	}
	
}

/**
 * Get the used indentation for a certain line
 * @param cm
 * @param line
 * @param charNumber
 * @returns
 */
function getIndentFromLine(cm, line, charNumber) {
	if (charNumber == undefined) charNumber = 1;
	var token = cm.getTokenAt({line : line,ch : charNumber});
	if (token == null || token == undefined || token.className != "sp-ws") {
		return "";
	} else {
		return token.string + getIndentFromLine(cm, line, token.end+1);
	}
}


function updateBookmarkCmHeight(queryInputId) {
	cmHeight = sparqlHighlight[queryInputId].getWrapperElement().offsetHeight;
	if (sparqlHighlightHeight[queryInputId]) {
		if (cmHeight != sparqlHighlightHeight[queryInputId]) {
			sparqlHighlightHeight[queryInputId] = cmHeight;
			adjustBookmarkQueryInputForContent(cmHeight);
		}
	} else {
		sparqlHighlightHeight[queryInputId] = cmHeight;
	}
}


if (typeof String.prototype.startsWith != 'function') {
	  String.prototype.startsWith = function (str){
	    return this.slice(0, str.length) == str;
	  };
}
if (typeof String.prototype.endsWith != 'function') {
	  String.prototype.endsWith = function (str){
	    return this.slice(-str.length) == str;
	  };
}


