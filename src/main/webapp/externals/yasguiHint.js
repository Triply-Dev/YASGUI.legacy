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
	
	function getPrefixAutocompletions(editor, getToken) {
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
		
		//not at end of line
		if (editor.getLine(cur.line).length > cur.ch) return;
		
		//we shouldnt be at the uri part the prefix declaration
		if ($.inArray("PNAME_NS", token.state.possibleCurrent) == -1) return; 
		
		//First token of line needs to be PREFIX,
		//there should be no trailing text (otherwise, text is wrongly inserted in between)
		firstToken = getNextNonWsToken(editor, cur.line);
		if (firstToken == null || firstToken.string.toUpperCase() != "PREFIX") return;
		
		
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
			list : getPrefixSuggestions(token),
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
	
	
	function getPropertiesAutocompletions(editor, getToken) {
		if (properties[getCurrentEndpoint()] == null || properties[getCurrentEndpoint()].length == 0) return; //we don't have anything to autocomplete..
		var cur = editor.getCursor(), token = getToken(editor, cur);
		if ($.inArray("a", token.state.possibleCurrent) >= 0) {
			//ok, so we are in a position where we can add properties
			
			if (
				//we are either writing a uri, or we've already typed the prefix part, and want to specify the stuff after the colon
				(token.string.contains(":") !== -1 || token.string.startsWith("<")) 
				//the cursor is at the end of the string
				&& token.end == cur.ch
				//we already have something filled in
				&& token.className != "sp-ws") {
				
				//this is the point where we can get the autocompletions
				//if the user is still typing the actual prefix (i.e. before the ':'), we want the prefix autocompletion stuff, not this one
				var suggestions = getPropertiesSuggestions(editor, getToken);
				if (suggestions != null) {
					return suggestions;
				}
			}
		}
		return;
	}

	function getAllAutoCompletions(editor, getToken) {
		autocompletions = getPrefixAutocompletions(editor, getToken);
		if (autocompletions == null) {
			//ok, so current cursor should not show prefix autocompletions. Try our properties autocompletions
			autocompletions = getPropertiesAutocompletions(editor, getToken);
		}
		
		return autocompletions;
		
	}

	CodeMirror.allAutoCompletions = function(editor) {
		return getAllAutoCompletions(editor, function(e, cur) {
			return e.getTokenAt(cur);
		});
	};
	
	CodeMirror.prefixHint = function(editor) {
		return getPrefixAutocompletions(editor, function(e, cur) {
			return e.getTokenAt(cur);
		});
	};
	
	function getPrefixSuggestions(token) {
		//the keywords should contain the prefixes
		//Start: end of string being typed
		var found = [], start = token.string;
		function maybeAdd(str) {
			if (str.indexOf(start) == 0 && !arrayContains(found, str))
				found.push(str + "\n"); //append linebreak! Otherwise we stay on the same line, and after adding the popup with autocompletions is still firing..
		}
		forEach(prefixes, maybeAdd);
		return found;
	}
	
	
	function getPropertiesSuggestions(editor, getToken) {
		getCompleteToken = function(token, cur) {
			//we cannot use token.string alone (e.g. http://bla results in 2 tokens: http: and //bla)
			var prevToken = getToken(editor, {line : cur.line,ch : token.start});
			if (prevToken.className != "sp-ws") {
				token.start = prevToken.start;
				cur.ch = prevToken.start;
				token.string = prevToken.string + token.string;
				return getCompleteToken(token, cur);//recursively, might have multiple tokens which it should include
			} else {
				return token;
			}
		};
		var cur = editor.getCursor(), token = getToken(editor, cur);
		
		token = getCompleteToken(token, editor.getCursor());
		var uriStart = getUriFromPrefix(editor, token);
		var found = [];
		var queryPrefixes = getPrefixesFromQuery(editor);
		
		
		for ( var i = 0, e = properties[getCurrentEndpoint()].length; i < e; ++i) {
			if (found.length > 500) break; //otherwise autocomplete box might become huge
			str = properties[getCurrentEndpoint()][i];
			if (uriStart.startsWith("<")) {
				str = "<" + str + ">";
			}
			if (str.indexOf(uriStart) == 0 && !arrayContains(found, str)) {
				//great, we've found a hit!
				if (!token.string.startsWith("<")) {
					//we need to get the suggested string back to prefixed form
					var prefix = token.string.substring(0, token.string.indexOf(":") + 1);
					if (queryPrefixes[prefix] != null) {
						str = str.substring(queryPrefixes[prefix].length);
						str = prefix + str;
					}
				} 
				
				//check that the current string isnt the one we want to autocomplete
				if (token.string != str) {
					found.push(str);
				}
			}
		}
		if (found.length > 0) {
			return {
				list : found,
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
		return;
	}
})();

/**
 * whenever the current token is prefixed, find currently defined prefixes from query, and return the complete uri
 * @param editor
 * @param tokenString
 * @returns
 */
function getUriFromPrefix(editor, token) {
	var tokenString = token.string;
	var uri = tokenString;
	var queryPrefixes = getPrefixesFromQuery(editor);
	if (!tokenString.startsWith("<") && tokenString.contains(":")) {
		for (var prefix in queryPrefixes) {
		    if (queryPrefixes.hasOwnProperty(prefix)) {
		        if (tokenString.startsWith(prefix)) {
		        	uri = queryPrefixes[prefix];
		        	uri += tokenString.substring(prefix.length);
		        }
		    }
		}
	}
	return uri;
}


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
		var colonIndex = token.string.indexOf(":");
		if (colonIndex !== -1) {
			//check first token isnt PREFIX, and previous token isnt a '<' (i.e. we are in a uri)
			var firstTokenString = getNextNonWsToken(cm, cur.line).string.toUpperCase();
			var previousToken = cm.getTokenAt({line: cur.line, ch: token.start});//needs to be null (beginning of line), or whitespace
			if (firstTokenString != "PREFIX" && (previousToken.className == "sp-ws" || previousToken.className == null)) {
				//check whether it isnt defined already (saves us from looping through the array)
				var currentPrefix = token.string.substring(0, colonIndex+1);
				var queryPrefixes = getPrefixesFromQuery(cm);
				if (queryPrefixes[currentPrefix] == null) {
					//ok, so it isnt added yet!
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
 * Get defined prefixes from query as array, in format {"prefix:" "uri"}
 * @param cm
 * @returns {Array}
 */
function getPrefixesFromQuery(cm) {
	var queryPrefixes = {};
	var numLines = cm.lineCount();
	for (var i = 0; i < numLines; i++) {
		var firstToken = getNextNonWsToken(cm, i);
		if (firstToken != null && firstToken.string.toUpperCase() == "PREFIX") {
			var prefix = getNextNonWsToken(cm, i, firstToken.end + 1);
			var uri = getNextNonWsToken(cm, i, prefix.end + 1);
			if (prefix != null && prefix.string.length > 0 && uri != null && uri.string.length > 0) {
				uriString = uri.string;
				if (uriString.startsWith("<")) uriString = uriString.substring(1);
				if (uriString.endsWith(">")) uriString = uriString.substring(0, uriString.length-1);
				queryPrefixes[prefix.string] = uriString;
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
 * 
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
	};
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
};
if (typeof String.prototype.contains != 'function') {
	String.prototype.contains = function (str){
		return this.indexOf(":") >= 0;
	};
};



