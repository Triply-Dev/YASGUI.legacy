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

	function scriptHint(editor, prefixes, getToken) {
		// Find the token at the cursor
		var cur = editor.getCursor(), token = getToken(editor, cur), tprop = token;

		//First token of line needs to be PREFIX
		if (getToken(editor, {line : cur.line,ch : 1}).string != "PREFIX") return;
		
		if (token.string == "PREFIX") {
			//Cursor is immediately after prefix. Move it one item to the right, and set token as empty string
			editor.replaceRange(" ", {line : cur.line,ch : token.end}, {line : cur.line,ch : token.end + 1});
			token = tprop = {
					start : cur.ch + 1,
					end : cur.ch + 1,
					string : "",
					state : token.state
				};
		}
		
		//If this is a whitespace, and token is just after PREFIX, proceed using empty string as token
		if (/\s*/.test(token.string) && getToken(editor, {line : cur.line,ch : tprop.start}).string == "PREFIX") {
			token = tprop = {
				start : cur.ch,
				end : cur.ch,
				string : "",
				state : token.state
			};
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
		return scriptHint(editor, prefixes, function(e, cur) {
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
