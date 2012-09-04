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
//			var punct = getTerminals().punct.toLowerCase();
//			var keywords = getTerminals().keywords.toLowerCase();
			var punct = getTerminals().punct;
			var keywords = getTerminals().keywords;
			if (prevToken.string.match(punct) || prevToken.string.match(keywords)) {
				token.start = prevToken.start
				cur.ch = prevToken.start;
				token.string = prevToken.string + token.string;
				return includePreviousTokens(token, cur);//recursively, might have multiple tokens which it should include
			} else {
				return token;
			}
		}
		
		//First token of line needs to be PREFIX
		if (getToken(editor, {line : cur.line,ch : 1}).string != "PREFIX") return;
		
		if (token.string == "PREFIX") {
		
			//Cursor is immediately after prefix. Move it one item to the right, and set token as empty string
			editor.replaceRange(" ", {line : cur.line,ch : token.end}, {line : cur.line,ch : token.end + 1});
			token = {
					start : cur.ch + 1,
					end : cur.ch + 1,
					string : "",
					state : token.state
				};
		}
		
		//If this is a whitespace, and token is just after PREFIX, proceed using empty string as token
		if (/\s*/.test(token.string) && getToken(editor, {line : cur.line,ch : token.start}).string == "PREFIX") {
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
				found.push(str);
		}
		forEach(keywords, maybeAdd);
		return found;
	}
})();
