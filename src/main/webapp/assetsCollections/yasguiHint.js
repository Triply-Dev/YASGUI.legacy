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

	function getPrefixAutocompletions(editor) {
		// Find the token at the cursor
		var cur = editor.getCursor(), token = editor.getTokenAt(cur);

		includePreviousTokens = function(token, cur) {
			var prevToken = editor.getTokenAt({
				line : cur.line,
				ch : token.start
			});
			if (prevToken.className == "sp-punct"
					|| prevToken.className == "sp-keyword") {
				token.start = prevToken.start;
				cur.ch = prevToken.start;
				token.string = prevToken.string + token.string;
				return includePreviousTokens(token, cur);// recursively,
				// might have
				// multiple tokens
				// which it should
				// include
			} else {
				return token;
			}
		};

		// not at end of line
		if (editor.getLine(cur.line).length > cur.ch)
			return;

		if (token.className != "sp-ws") {
			// we want to complete token, e.g. when the prefix starts with an a
			// (treated as a token in itself..)
			// but we to avoid including the PREFIX tag. So when we have just
			// typed a space after the prefix tag, don't get the complete token
			token = getCompleteToken(editor);
		}
		// we shouldnt be at the uri part the prefix declaration
		// also check whether current token isnt 'a' (that makes codemirror
		// thing a namespace is a possiblecurrent
		if (!token.string.startsWith("a")
				&& $.inArray("PNAME_NS", token.state.possibleCurrent) == -1)
			return;

		// First token of line needs to be PREFIX,
		// there should be no trailing text (otherwise, text is wrongly inserted
		// in between)
		firstToken = getNextNonWsToken(editor, cur.line);
		if (firstToken == null || firstToken.string.toUpperCase() != "PREFIX")
			return;

		// If this is a whitespace, and token is just after PREFIX, proceed
		// using empty string as token
		if (/\s*/.test(token.string) && editor.getTokenAt({
			line : cur.line,
			ch : token.start
		}).string.toUpperCase() == "PREFIX") {
			token = {
				start : cur.ch,
				end : cur.ch,
				string : "",
				state : token.state
			};
		} else {
			// We know we are in a PREFIX line. Now check whether the string
			// starts with a punct or keyword
			// Good example is 'a', which is a valid punct in our grammar.
			// This is parsed as separate token which messes up the token for
			// autocompletion (the part after 'a' is used as separate token)
			// If previous token is in keywords or keywords, prepend this token
			// to current token
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

	function getPropertiesAutocompletions(editor) {
		if (properties[getCurrentEndpoint()] == null
				|| properties[getCurrentEndpoint()].length == 0)
			return; // we don't have anything to autocomplete..
		var cur = editor.getCursor(), token = getCompleteToken(editor);
		if ($.inArray("a", token.state.possibleCurrent) >= 0) {
			// ok, so we are in a position where we can add properties
			if (
			// we are either writing a uri, or we've already typed the prefix
			// part, and want to specify the stuff after the colon
			(token.string.contains(":") || token.string.startsWith("<"))
			// the cursor is at the end of the string
			&& token.end == cur.ch
			// we already have something filled in
			&& token.className != "sp-ws") {

				// this is the point where we can get the autocompletions
				// if the user is still typing the actual prefix (i.e. before
				// the ':'), we want the prefix autocompletion stuff, not this
				// one
				var suggestions = getPropertiesSuggestions(editor);
				if (suggestions != null) {
					return suggestions;
				}
			}
		}
		return;
	}

	function getAllAutoCompletions(editor) {
		var autocompletions = getPrefixAutocompletions(editor);
		if (autocompletions == null) {
			// ok, so current cursor should not show prefix autocompletions. Try
			// our properties autocompletions
			autocompletions = getPropertiesAutocompletions(editor);
		}
		return autocompletions;
	}


	CodeMirror.allAutoCompletions = function(editor) {
		return getAllAutoCompletions(editor);
		
	};
	CodeMirror.doPredicateAutocompleteRequest = function(cm, drawCallback) {
		this.maxResults = 50;
		this.methodProperties = {
			"lov" : {
				"color":"#25547B;",
				"abbreviation": "L",
				"description": "<a href='" + getLovApiLink() + "' target='_blank'>LOV API</a>",
				"priority": 3,
			},
			"property": {
				"color":"#502982;",
				"abbreviation": "P",
				"description": "Pre-fetching all properties",
				"priority": 2,
			},
			"lazy": {
				"color":"#BF9C30;",
				"abbreviation": "C",
				"description": "Lazy caching of predicates",
				"priority": 1,
			}
			
		};
		this.resultSizes = {};
		this.fetched = {};
		var predReq = this;
		this.cur = cm.getCursor();
		this.token = getCompleteToken(cm);
		this.tokenPrefix = null;
		this.tokenPrefixUri = null;
		this.results = [];
		this.requestLovAutocompletions = function() {
			predReq.fetched['lov'] = false;
		    var args = {q:predReq.uriStart, page: 1, type: "property"};
		    var url = "";
		    var updateUrl = function() {
		    	url = "http://lov.okfn.org/dataset/lov/api/v2/autocomplete/terms?" + $.param(args);
		    };
		    updateUrl();
		    var increasePage = function(){
		    	args.page++;
		    	updateUrl();
		    };
		    var requestObj = this;
		    this.doLovRequest = function() {
		    	$.get(url, function(data) {
		    		predReq.resultSizes['lov'] = data.total_results;
					for (var i = 0; i < data.results.length; i++) {
						predReq.results.push({
							type: "lov", 
							uri: data.results[i].uri, 
							priority: predReq.methodProperties.lov.priority
						});
				 	}
					var resultsSoFar = data.page_size * data.page;
					if (resultsSoFar < data.total_results && resultsSoFar < predReq.maxResults) {
						increasePage();
						requestObj.doLovRequest();
					} else {
						//request done, draw!
						predReq.fetched['lov'] = true;
						predReq.drawIfNeeded();
					}
				}).fail(function(jqXHR, textStatus, errorThrown) {
					console.log(errorThrown);
				  });
		    };
		    doLovRequest();
		};
		this.requestServletAutocompletions = function(methods) {
			predReq.fetched['servlet'] = false;
			var args = {
				q:predReq.uriStart, 
				max: predReq.maxResults, 
				type: "property",
				endpoint: getCurrentEndpoint()
			};
			if (methods.length == 1) {
				args["method"] = methods[0];
			} else {
				//no need to add methods to args. We want all!
			}
		    var url = "Yasgui/autocomplete?" + $.param(args);
			$.get(url, function(data) {
				if (data.property != undefined) {
					predReq.resultSizes['property'] = data.property.resultSize;
					for (var i = 0; i < data.property.results.length; i++) {
						predReq.results.push({
							type: "property", 
							uri: data.property.results[i],
							priority: predReq.methodProperties.property.priority
						});
					}
				}
				
				if (data.lazy != undefined) {
					predReq.resultSizes['lazy'] = data.lazy.resultSize;
					for (var i = 0; i < data.lazy.results.length; i++) {
						predReq.results.push({
							type: "lazy", 
							uri: data.lazy.results[i],
							priority: predReq.methodProperties.lazy.priority
						});
					}
				}
				predReq.fetched['servlet'] = true;
				predReq.drawIfNeeded();
			}).fail(function(jqXHR, textStatus, errorThrown) {
				console.log(errorThrown);
			});
			
			
			
			
		};
		this.preprocessToken = function() {
			var token = predReq.token;
			if ($.inArray("a", predReq.token.state.possibleCurrent) >= 0) {
				// ok, so we are in a position where we can add properties
				if (
				// we are either writing a uri, or we've already typed the prefix
				// part, and want to specify the stuff after the colon
				(token.string.contains(":") || token.string.startsWith("<"))
				// the cursor is at the end of the string
				&& token.end == cur.ch
				// we already have something filled in
				&& token.className != "sp-ws") {
					token = getCompleteToken(cm);
					if (!token.string.startsWith("<")) {
						predReq.tokenPrefix = token.string.substring(0,
								token.string.indexOf(":") + 1);
						var queryPrefixes = getPrefixesFromQuery(cm);
						if (queryPrefixes[predReq.tokenPrefix] != null) {
							predReq.tokenPrefixUri = queryPrefixes[predReq.tokenPrefix];
						}
					}
					// preprocess string for which to find the autocompletion
					predReq.uriStart = getUriFromPrefix(cm, token);
					if (predReq.uriStart.startsWith("<"))
						predReq.uriStart = predReq.uriStart.substring(1);
					if (predReq.uriStart.endsWith(">"))
						predReq.uriStart = predReq.uriStart.substring(0, predReq.uriStart.length - 1);
				}
			}
		};
		this.drawIfNeeded = function() {
			//only draw when responses from both calls are in!
			var done = true;
			for (request in predReq.fetched) {
				if (!predReq.fetched[request]) {
					done = false;
					break;
				}
			}
			if (done) predReq.draw();
		};
		this.prepareResultsForDrawing = function() {
			//we want to:
			//- make results distinct (a uri might be fetched by several methods)
			//- limit results (make sure the ones with highest priority are included)
			//- sort results alphabetically
			
			//make results distinct. Don't want multiple URIs in there. Use 'priority' for retrieval methods.
			//if a uri is fetched using both the lazy method and the property method, add it as being retrieved as 'lazy'
			//do this by sorting by uri, and then priority. When encountering multiple uris, select the first one
			predReq.results.sort(dynamicSortMultiple("uri", "priority"));
			//increment in reverse, as removing items from the array while looping through it causes problems
			var len = predReq.results.length;
			while (len--) {
			    var result = predReq.results[len];
			    if (predReq.results[len-1] != undefined && predReq.results[len-1].uri == result.uri) {
			    	//the result before this has same uri (and higher priority, as array is sorted). 
			    	//so remove current item
			    	predReq.results.splice(len, 1);
			    }
			}
			
			//Only select x results. So first sort by priority / alphabetically, and then select x
			if (predReq.maxResults < predReq.results.length) {
				predReq.results.sort(dynamicSortMultiple("priority", "uri"));
				predReq.results.splice(predReq.maxResults, predReq.results.length - maxResults);
			}
			//now sort everything alphabetically again
			predReq.results.sort(dynamicSortMultiple("uri"));
		};
		completionMethodChanged = function() {
			var button = $("#completionMethodButton");
			var checkboxElements=document.getElementsByName("propertyCompletions");
			var newEnabledMethods = {};
			for (var i = 0;i < checkboxElements.length; i++) {
				newEnabledMethods[checkboxElements[i].value] = checkboxElements[i].checked;
			}
			var oldEnabledMethods = getPropertyCompletionMethods();
			if (compareObjects(oldEnabledMethods, newEnabledMethods)) {
				//different, show apply button
				if (button != null) {
					button.hide("fast");
				}
			} else {
				//same, hide apply button
				if (button != null) {
					button.show("fast");
				}
				
			}
		};
		storeCompletionMethods = function() {
			var checkboxElements=document.getElementsByName("propertyCompletions");
			var methods = {};
			for (var i = 0;i < checkboxElements.length; i++) {
				methods[checkboxElements[i].value] = checkboxElements[i].checked;
			}
			storeCompletionMethodsInSettings(JSON.stringify(methods));
		};
		this.legendDialogue = {
			legendId: "propertyLegend",
			legendHtml: "placeholder",
			generateHtml: function(dismissOnOutsideClick) {
				var methods = getPropertyCompletionMethods();
				var sortedMethods = [];
				for (var method in methods) {
					//this is a -very- naive quick way to sort. 
					//it -only- makes sure our highest priority item is at the top of the array
					if (sortedMethods.length == 0 || methodProperties[method].priority < methodProperties[sortedMethods[0]].priority) {
						sortedMethods.unshift(method);
					} else {
						sortedMethods.push(method);
					}
				}
				this.legendHtml = 
					"Methods used for fetching autocompletions: (<a href='" + getAutocompletionMoreInfoLink() + " ' target='_blank'>more info</a>):" +
					"<ul id='propertyCompletionsLegend' class='propertyCompletionsLegend'>";
				for (var i = 0; i < sortedMethods.length; i++) {
					var method = sortedMethods[i];
					var methodProps = predReq.methodProperties[method];
					this.legendHtml += 
						"<li id='" + method + "Hint' class='" + method + "Hint propertyCompletionLegend'>" +
							"<span style='background-color:" + methodProps.color + "' class='propertyTypeIconLegend propertyTypeIcon'>" + methodProps.abbreviation + "</span>" +
							"<input onclick='completionMethodChanged();' class='propertyCompletionMethodCheckbox' type='checkbox' name='propertyCompletions' value='" + method + "' " + (methods[method]? "checked":"") + ">" +
							methodProps.description + " (" + (predReq.resultSizes[method] != undefined? predReq.resultSizes[method]: "unknown") + ")" +
						"</li>";
				}
				this.legendHtml += 
					"</ul>" +
					"<button id='completionMethodButton' style='display:none;float: right;' onclick='storeCompletionMethods();$.noty.close(\"" + this.legendId + "\");return false;'>Apply</button>";
			},
			draw: function(dismissOnOutsideClick) {
				this.generateHtml(dismissOnOutsideClick);
				closeWith = (dismissOnOutsideClick? ['button']:[] );
				if ($.noty.get(this.legendId) == false) {
					noty({
						text: this.legendHtml,
						layout: 'bottomLeft',
						type: 'alert',
						id: this.legendId,
						closeWith: closeWith,
					});
				} else {
					$.noty.setText(legendId, legendHtml);
				}
			},
			close: function() {
				$.noty.close(this.legendId);
			},
			update: function(completion) {
				//clear 'selected' class name from 
				$("#propertyCompletionsLegend").find(".propertyCompletionLegendSelected").each (function() {
				    $(this).removeClass("propertyCompletionLegendSelected");
				});
				if (document.getElementById(completion.className) != undefined) {
					document.getElementById(completion.className).className += " " + "propertyCompletionLegendSelected";
				}
				
			}
		};
		this.appendTypeIconsToAutocomplete = function() {
			//class: propertyTypeIcon
			$(".lazyHint.CodeMirror-hint").each(function(){
				$(this).prepend("<span style='background-color:" + predReq.methodProperties.lazy.color + "' class='propertyTypeIcon'>" + predReq.methodProperties.lazy.abbreviation + "</span>");
			});
			$(".propertyHint.CodeMirror-hint").each(function(){
				$(this).prepend("<span style='background-color:" + predReq.methodProperties.property.color + "' class='propertyTypeIcon'>" + predReq.methodProperties.property.abbreviation + "</span>");
			});
			$(".lovHint.CodeMirror-hint").each(function(){
				$(this).prepend("<span style='background-color:" + predReq.methodProperties.lov.color + "' class='propertyTypeIcon'>" + predReq.methodProperties.lov.abbreviation + "</span>");
			});
		};
		this.draw = function() {
			predReq.prepareResultsForDrawing();
			results = predReq.results;
			var found = [];
			if (results.length > 0) {
				// use custom completionhint function, to avoid reaching a loop when the
				// completionhint is the same as the current token
				// regular behaviour would keep changing the codemirror dom, hence
				// constantly calling this callback
				var completionHint = function(cm, data, completion) {
					if (completion.text != cm.getTokenAt(cm.getCursor()).string) {
						cm.replaceRange(completion.text, data.from, data.to);
					}
				};
				
				for ( var i = 0; i < results.length; i++) {
					var suggestedString = results[i].uri;
					if (tokenPrefix != null && tokenPrefixUri != null) {
						// we need to get the suggested string back to prefixed form
						suggestedString = suggestedString
								.substring(tokenPrefixUri.length);
						suggestedString = tokenPrefix + suggestedString;
					} else {
						// it is a regular uri. add '<' and '>' to string
						suggestedString = "<" + suggestedString + ">";
					}
					//different style per autocomplete method
					found.push({
						text : suggestedString,
						displayText: suggestedString,
						hint : completionHint,
						className: results[i].type + "Hint"
					});
				}
			
				if (found.length == 1 && found[0].text == token.string)
					return;// we already have our match
				
				if (found.length > 0) {
					var autocompleteObj =  {
						_handlers: {
							"close": [function(){
								predReq.legendDialogue.close();
							}],
							"select": [function(completion, element) {
								predReq.legendDialogue.update(completion);
							}] 
						},
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
					predReq.legendDialogue.draw();
					drawCallback(autocompleteObj);
					predReq.appendTypeIconsToAutocomplete();
				}
			} else {
				console.log("nothing to draw");
			}
		};
		this.requestAutocompletions = function() {
			var allDisabled = true;
			var methods = getPropertyCompletionMethods();
			var servletMethods = [];
			for (var method in methods) {
				if (methods[method]) {
					allDisabled = false;
					if (method == "lazy") {
						predReq.requestLovAutocompletions();
					} else {
						//both other methods are executed as 1 single request
						servletMethods.push(method);
					}
				}
			}
			if (servletMethods.length > 0) {
				predReq.requestServletAutocompletions(servletMethods);
			}
			if (allDisabled) {
				predReq.legendDialogue.draw(true);
			}
		};
		preprocessToken();
		requestAutocompletions();
	};
	
	
	CodeMirror.prefixHint = function(editor) {
		return getPrefixAutocompletions(editor);
	};

	function getPrefixSuggestions(token) {
		// the keywords should contain the prefixes
		// Start: end of string being typed
		var found = [], start = token.string;
		function maybeAdd(str) {
			if (str.indexOf(start) == 0 && !arrayContains(found, str))
				found.push(str + "\n"); // append linebreak! Otherwise we stay
			// on the same line, and after adding
			// the popup with autocompletions is
			// still firing..
		}
		forEach(prefixes, maybeAdd);
		return found;
	}

	function getCompleteToken(editor, token, cur) {
		if (cur == null) {
			cur = editor.getCursor();
		}
		if (token == null) {
			token = editor.getTokenAt(cur);
		}
		// we cannot use token.string alone (e.g. http://bla results in 2
		// tokens: http: and //bla)

		var prevToken = editor.getTokenAt({
			line : cur.line,
			ch : token.start
		});
		if (prevToken.className != null && prevToken.className != "sp-ws") {
			token.start = prevToken.start;
			token.string = prevToken.string + token.string;
			return getCompleteToken(editor, token, {
				line : cur.line,
				ch : prevToken.start
			});// recursively, might have multiple tokens which it should
			// include
		} else {
			return token;
		}
	}
	;
	function getPropertiesSuggestions(editor) {
		cur = editor.getCursor(), token = editor.getTokenAt(cur);
		var token = getCompleteToken(editor);
		var tokenPrefix = null;
		var tokenPrefixUri = null;
		if (!token.string.startsWith("<")) {
			tokenPrefix = token.string.substring(0,
					token.string.indexOf(":") + 1);
			var queryPrefixes = getPrefixesFromQuery(editor);
			if (queryPrefixes[tokenPrefix] != null) {
				tokenPrefixUri = queryPrefixes[tokenPrefix];
			}
		}

		// preprocess string for which to find the autocompletion
		var uriStart = getUriFromPrefix(editor, token);
		if (uriStart.startsWith("<"))
			uriStart = uriStart.substring(1);
		if (uriStart.endsWith(">"))
			uriStart = uriStart.substring(0, uriStart.length - 1);

		var found = [];

		// use custom completionhint function, to avoid reaching a loop when the
		// completionhint is the same as the current token
		// regular behaviour would keep changing the codemirror dom, hence
		// constantly calling this callback
		var completionHint = function(cm, data, completion) {
			if (completion.text != cm.getTokenAt(editor.getCursor()).string) {
				cm.replaceRange(completion.text, data.from, data.to);
			}
		};
		var suggestionsList = properties[getCurrentEndpoint()].autoComplete(uriStart, 0, 50);
		for ( var i = 0; i < suggestionsList.length; i++) {
			var suggestedString = suggestionsList[i];
			if (tokenPrefix != null && tokenPrefixUri != null) {
				// we need to get the suggested string back to prefixed form
				suggestedString = suggestedString
						.substring(tokenPrefixUri.length);
				suggestedString = tokenPrefix + suggestedString;
			} else {
				// it is a regular uri. add '<' and '>' to string
				suggestedString = "<" + suggestedString + ">";
			}
			found.push({
				text : suggestedString,
				hint : completionHint
			});
		}

		if (found.length == 1 && found[0].text == token.string)
			return;// we already have our match
		
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
 * whenever the current token is prefixed, find currently defined prefixes from
 * query, and return the complete uri
 * 
 * @param editor
 * @param tokenString
 * @returns
 */
function getUriFromPrefix(editor, token) {
	var tokenString = token.string;
	var uri = tokenString;
	var queryPrefixes = getPrefixesFromQuery(editor);
	if (!tokenString.startsWith("<") && tokenString.contains(":")) {
		for ( var prefix in queryPrefixes) {
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
	if (charNumber == undefined)
		charNumber = 1;
	var token = cm.getTokenAt({
		line : lineNumber,
		ch : charNumber
	});
	if (token == null || token == undefined || token.end < charNumber) {
		return null;
	}
	if (token.className == "sp-ws") {
		return getNextNonWsToken(cm, lineNumber, token.end + 1);
	}
	return token;
}

/**
 * Check whether typed prefix is declared. If not, automatically add declaration
 * using list from prefix.cc
 * 
 * @param cm
 */
function appendPrefixIfNeeded(cm) {
	var cur = cm.getCursor();
	var token = cm.getTokenAt(cur);
	if (token.className == "sp-prefixed") {
		var colonIndex = token.string.indexOf(":");
		if (colonIndex !== -1) {
			// check first token isnt PREFIX, and previous token isnt a '<'
			// (i.e. we are in a uri)
			var firstTokenString = getNextNonWsToken(cm, cur.line).string
					.toUpperCase();
			var previousToken = cm.getTokenAt({
				line : cur.line,
				ch : token.start
			});// needs to be null (beginning of line), or whitespace
			if (firstTokenString != "PREFIX"
					&& (previousToken.className == "sp-ws" || previousToken.className == null)) {
				// check whether it isnt defined already (saves us from looping
				// through the array)
				var currentPrefix = token.string.substring(0, colonIndex + 1);
				var queryPrefixes = getPrefixesFromQuery(cm);
				if (queryPrefixes[currentPrefix] == null) {
					// ok, so it isnt added yet!
					for ( var i = 0; i < prefixes.length; i++) {
						var prefix = prefixes[i].substring(0,
								currentPrefix.length);
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
 * 
 * @param cm
 * @returns {Array}
 */
function getPrefixesFromQuery(cm) {
	var queryPrefixes = {};
	var numLines = cm.lineCount();
	for ( var i = 0; i < numLines; i++) {
		var firstToken = getNextNonWsToken(cm, i);
		if (firstToken != null && firstToken.string.toUpperCase() == "PREFIX") {
			var prefix = getNextNonWsToken(cm, i, firstToken.end + 1);
			var uri = getNextNonWsToken(cm, i, prefix.end + 1);
			if (prefix != null && prefix.string.length > 0 && uri != null
					&& uri.string.length > 0) {
				uriString = uri.string;
				if (uriString.startsWith("<"))
					uriString = uriString.substring(1);
				if (uriString.endsWith(">"))
					uriString = uriString.substring(0, uriString.length - 1);
				queryPrefixes[prefix.string] = uriString;
			}
		}
	}
	return queryPrefixes;
}

/**
 * Append prefix declaration to list of prefixes in query window.
 * 
 * @param cm
 * @param prefix
 */
function appendToPrefixes(cm, prefix) {
	var lastPrefix = null;
	var lastPrefixLine = 0;
	var numLines = cm.lineCount();
	for ( var i = 0; i < numLines; i++) {
		var firstToken = getNextNonWsToken(cm, i);
		if (firstToken != null
				&& (firstToken.string == "PREFIX" || firstToken.string == "BASE")) {
			lastPrefix = firstToken;
			lastPrefixLine = i;
		}
	}

	if (lastPrefix == null) {
		cm.replaceRange("PREFIX " + prefix + "\n", {
			line : 0,
			ch : 0
		});
	} else {
		var previousIndent = getIndentFromLine(cm, lastPrefixLine);
		cm.replaceRange("\n" + previousIndent + "PREFIX " + prefix, {
			line : lastPrefixLine
		});
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
	if (charNumber == undefined)
		charNumber = 1;
	var token = cm.getTokenAt({
		line : line,
		ch : charNumber
	});
	if (token == null || token == undefined || token.className != "sp-ws") {
		return "";
	} else {
		return token.string + getIndentFromLine(cm, line, token.end + 1);
	}
	;
}

