(function() {
	function extend(ChildClass, ParentClass) {
		ChildClass.prototype = new ParentClass();
		ChildClass.prototype.constructor = ChildClass;
	}
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
	var AutocompletionBase = function(cm, drawCallback) {
		this.maxResults = 50;
		this.cm = cm;
		this.drawCallback = drawCallback;
		this.statusMsgs = {};
		this.resultSizes = {};
		this.drawnResultSizes = {};
		this.fetched = {};
		this.cur = null;
		this.token = null;
		this.tokenPrefix = null;
		this.tokenPrefixUri = null;
		this.results = [];
		completionMethodChanged = function(type) {
			var button = $("#completionMethodButton");
			var checkboxElements=document.getElementsByName(type + "Completions");
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
	};
	AutocompletionBase.prototype = {
		drawIfNeeded: function(completion) {
			//only draw when responses from both calls are in!
			var done = true;
			for (request in completion.fetched) {
				if (!completion.fetched[request]) {
					done = false;
					break;
				}
			}
			if (done) completion.draw(completion);
		},
		prepareResultsForDrawing: function(completion) {
			//we want to:
			//- make results distinct (a uri might be fetched by several methods)
			//- limit results (make sure the ones with highest priority are included)
			//- sort results alphabetically
			
			//make results distinct. Don't want multiple URIs in there. Use 'priority' for retrieval methods.
			//if a uri is fetched using both the lazy method and the property method, add it as being retrieved as 'lazy'
			//do this by sorting by uri, and then priority. When encountering multiple uris, select the first one
			completion.results.sort(dynamicSortMultiple("uri", "priority"));
			//increment in reverse, as removing items from the array while looping through it causes problems
			var len = completion.results.length;
			while (len--) {
			    var result = completion.results[len];
			    if (completion.results[len-1] != undefined && completion.results[len-1].uri == result.uri) {
			    	//the result before this has same uri (and higher priority, as array is sorted). 
			    	//so remove current item
			    	completion.results.splice(len, 1);
			    }
			}
			
			//Only select x results. So first sort by priority / alphabetically, and then select x
			if (completion.maxResults < completion.results.length) {
				completion.results.sort(dynamicSortMultiple("priority", "uri"));
				completion.results.splice(completion.maxResults, completion.results.length - maxResults);
			}
			//now sort everything alphabetically again
			completion.results.sort(dynamicSortMultiple("uri"));
			
			//finally, get aggregated numbers
			for (var i = 0; i < completion.results.length; i++) {
				var type = completion.results[i].type;
				if (completion.drawnResultSizes[type] == undefined) {
		    		completion.drawnResultSizes[type] = 0;
		    	}
		    	completion.drawnResultSizes[type]++;
			}
		},
		preprocessToken: function(completion) {
			var token = getCompleteToken(completion.cm);
			if (token.string.contains(":") || token.string.startsWith("<")) {
				token = getCompleteToken(completion.cm);
				if (!token.string.startsWith("<")) {
					completion.tokenPrefix = token.string.substring(0,
							token.string.indexOf(":") + 1);
					var queryPrefixes = getPrefixesFromQuery(completion.cm);
					if (queryPrefixes[completion.tokenPrefix] != null) {
						completion.tokenPrefixUri = queryPrefixes[completion.tokenPrefix];
					}
				}
				// preprocess string for which to find the autocompletion
				completion.uriStart = getUriFromPrefix(completion.cm, token);
				if (completion.uriStart.startsWith("<"))
					completion.uriStart = completion.uriStart.substring(1);
				if (completion.uriStart.endsWith(">"))
					completion.uriStart = completion.uriStart.substring(0, completion.uriStart.length - 1);
			}
		},
		storeCompletionMethods: function(type) {
			var checkboxElements=document.getElementsByName(type + "Completions");
			var methods = {};
			for (var i = 0;i < checkboxElements.length; i++) {
				methods[checkboxElements[i].value] = checkboxElements[i].checked;
			}
			storeCompletionMethodsInSettings(JSON.stringify(methods));
		},
		
		legendDialogue: {
			legendId: "propertyLegend",
			legendHtml: "placeholder",
			generateHtml : function(completion, methods, dismissOnOutsideClick) {
				var methods = completion.methods;
				var sortedMethods = completion.legendDialogue.sortMethods(completion, methods);
				this.legendHtml = 
					"Methods used for fetching autocompletions: (<a href='" + getAutocompletionMoreInfoLink() + " ' target='_blank'>more info</a>):" +
					"<ul id='completionsLegend' class='completionsLegend'>";
				for (var i = 0; i < sortedMethods.length; i++) {
					var method = sortedMethods[i];
					var methodProps = completion.methodProperties[method];
					this.legendHtml += 
						"<li id='" + method + "Hint' class='" + method + "Hint completionLegend'>" +
						"<table style='min-height:25px;border-collapse:collapse'><tr>" + 
						"<td>" +
						"<span style='vertical-align: middle;display: inline;background-color:" + methodProps.color + "' class='completionTypeIcon completionTypeIconLegend'>" + methodProps.abbreviation + "</span>" +
						"</td>" +
						"<td>" + 
						"<input onclick=\"completionMethodChanged('" + completion.completionType + "');\" class='propertyCompletionMethodCheckbox' type='checkbox' name='propertyCompletions' value='" + method + "' " + (methods[method]? "checked":"") + ">" +
						"</td>" +
						"<td>" +
						methodProps.description + 
						" (";
					if (completion.resultSizes[method] != undefined) {
						if (completion.drawnResultSizes[method] == undefined || completion.drawnResultSizes[method] == completion.resultSizes[method]) {
							this.legendHtml += completion.resultSizes[method];
						} else {
							this.legendHtml += 
								"<span title='displayed suggestions vs total number of available suggestions'>" +
								completion.drawnResultSizes[method] + "/" + completion.resultSizes[method] +
								"</span>";
						}
					} else {
						if (completion.statusMsgs[method] != undefined) {
							this.legendHtml += completion.statusMsgs[method];
						} else {
							this.legendHtml += "unknown";
						}
					}
					this.legendHtml += ")" +
					"</td>" +
					"</tr></table>" +
					"</li>";
				}
				this.legendHtml += 
					"</ul>" +
					"<button id='completionMethodButton' style='display:none;float: right;' onclick=\"storeCompletionMethods('" + completion.completionType + "');$.noty.close('" + this.legendId + "');return false;\">Apply</button>";
			},
			sortMethods: function(completion, methods) {
				var sortedMethods = [];
				for (var method in methods) {
					//this is a -very- naive quick way to sort. 
					//it -only- makes sure our highest priority item is at the top of the array
					if (sortedMethods.length == 0 || completion.methodProperties[method].priority < completion.methodProperties[sortedMethods[0]].priority) {
						sortedMethods.unshift(method);
					} else {
						sortedMethods.push(method);
					}
				}
				return sortedMethods;
			},
			generateIds: function(completion) {
				this.legendId = completion.completionType + "Legend";
			},
			draw: function(completion, dismissOnOutsideClick) {
				this.generateIds(completion);
				this.generateHtml(completion, dismissOnOutsideClick);
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
					$.noty.setText(this.legendId, this.legendHtml);
				}
				this.addClickListener(completion);
			},
			addClickListener: function(completion) {
				$(document).on("click." + completion.completionType + "menu-outside", function(event) {
					if (document.getElementById(completion.completionType + "Legend") != undefined && $(".CodeMirror-hints")[0]) {
					    if(!$(event.target).parents().andSelf().is("#" + completion.completionType + "Legend") && !$(event.target).parents().andSelf().is(".CodeMirror-hints")) {
					    	$.noty.close(completion.completionType + "Legend");//remove legend popup
					    	$('.CodeMirror-hints').hide();//remove autocompletion
					    }
						//we have a hint item and property legend popup. proceed
					} else {
						//no property legend popup and hint item. We shouldnt check this listener anymore! Just remove this listener
						$(document).off("click." + completion.completionType + "menu-outside");
					}
				});
			},
			close: function() {
				$.noty.close(this.legendId);
			},
			update: function(completion) {
				//clear 'selected' class name from 
				$("#completionsLegend").find(".completionLegendSelected").each (function() {
				    $(this).removeClass("completionLegendSelected");
				});
				if (document.getElementById(completion.className) != undefined) {
					document.getElementById(completion.className).className += " " + "completionLegendSelected";
				}
				
			}
		},
		appendTypeIconsToAutocomplete: function(completion) {
			for (var method in completion.methodProperties) {
				var props = completion.methodProperties[method];
				$("." + method + "Hint.CodeMirror-hint").each(function(){
					$(this).prepend("<span style='background-color:" + props.color + "' class='completionTypeIcon'>" + props.abbreviation + "</span>");
				});
			}
		},
		draw:  function(completion) {
			completion.prepareResultsForDrawing(completion);
			results = completion.results;
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
					if (completion.tokenPrefix != null && completion.tokenPrefixUri != null) {
						// we need to get the suggested string back to prefixed form
						suggestedString = suggestedString
								.substring(completion.tokenPrefixUri.length);
						suggestedString = completion.tokenPrefix + suggestedString;
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
					var cur = completion.cm.getCursor();
					var token = getCompleteToken(completion.cm);
					var autocompleteObj =  {
						_handlers: {
							"close": [function(){
								completion.legendDialogue.close();
							}],
							"select": [function(completionSelect, element) {
								completion.legendDialogue.update(completionSelect);
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
					
					completion.drawCallback(autocompleteObj);
					if ($('.CodeMirror-hints')[0]) {
						//hmm, user might have request autocompletion, and moved to a new line in the meantime.
						//if this is the case, we won't get a suggestion popup. We don't want a legend dialogue then as well!
						completion.legendDialogue.draw(completion);
						completion.appendTypeIconsToAutocomplete(completion);
					}
				}
			} else {
				console.log("nothing to draw");
			}
		},
		requestServletAutocompletions: function(completion, methods) {
			if (location.href.indexOf("codemirror.html") !== -1) {
				var data = jQuery.parseJSON( '{"queryResults":{"results":["http://xmlns.com/foaf/0.1/prop","http://xmlns.com/foaf/0.1/prop3","http://xmlns.com/foaf/0.1/same", "http://xmlns.com/foaf/0.1/prop2"],"resultSize":4},"query":{"results":["http://xmlns.com/foaf/0.1/lazy2","http://xmlns.com/foaf/0.1/lazy1","http://xmlns.com/foaf/0.1/lazy3","http://xmlns.com/foaf/0.1/same","http://xmlns.com/foaf/0.1/lazy4"],"resultSize":5}}' );
				if (data.queryResults != undefined) {
					if (data.queryResults.status != undefined) {
						completion.statusMsgs['queryResults'] = data.queryResults.status;
					}
					completion.resultSizes['queryResults'] = data.queryResults.resultSize;
					for (var i = 0; i < data.queryResults.results.length; i++) {
						completion.results.push({
							type: "queryResults", 
							uri: data.queryResults.results[i],
							priority: completion.methodProperties.queryResults.priority
						});
					}
				}
				
				if (data.query != undefined) {
					if (data.query.status != undefined) {
						completion.statusMsgs['query'] = data.query.status;
					}
					completion.resultSizes['query'] = data.query.resultSize;
					for (var i = 0; i < data.query.results.length; i++) {
						completion.results.push({
							type: "query", 
							uri: data.query.results[i],
							priority: completion.methodProperties.query.priority
						});
					}
				}
				completion.fetched['servlet'] = true;
				completion.drawIfNeeded(completion);
				return;
			}
			
			completion.fetched['servlet'] = false;
			var args = {
					q:completion.uriStart, 
					max: completion.maxResults, 
					type: completion.completionType,
					endpoint: getCurrentEndpoint()
			};
			if (methods.length == 1) {
				args["method"] = methods[0];
			} else {
				//no need to add methods to args. We want all!
			}
			var url = "Yasgui/autocomplete?" + $.param(args);
			$.get(url, function(data) {
				if (data.queryResults != undefined) {
					if (data.queryResults.status != undefined) {
						completion.statusMsgs['queryResults'] = data.queryResults.status;
					}
					completion.resultSizes['queryResults'] = data.queryResults.resultSize;
					for (var i = 0; i < data.queryResults.results.length; i++) {
						completion.results.push({
							type: "queryResults", 
							uri: data.queryResults.results[i],
							priority: completion.methodProperties.queryResults.priority
						});
					}
				}
				
				if (data.query != undefined) {
					if (data.query.status != undefined) {
						completion.statusMsgs['query'] = data.query.status;
					}
					completion.resultSizes['query'] = data.query.resultSize;
					for (var i = 0; i < data.query.results.length; i++) {
						completion.results.push({
							type: "query", 
							uri: data.query.results[i],
							priority: completion.methodProperties.query.priority
						});
					}
				}
				completion.fetched['servlet'] = true;
				completion.drawIfNeeded(completion);
			}).fail(function(jqXHR, textStatus, errorThrown) {
				console.log(errorThrown);
			});
		},
	};
	var usePropertyAutocompletion = function(cm) {
		var token = getCompleteToken(cm);
		return ($.inArray("a", token.state.possibleCurrent) != -1 && token.type != "sp-var");
	};
	
	var PropertyAutocompletion = function(cm, drawCallback) {
		var completion = this;
		this.cm = cm;
		this.drawCallback = drawCallback;
		this.methodProperties = {
				"lov" : {
					"color":"#25547B;",
					"abbreviation": "L",
					"description": "Properties fetched from <a href='" + getLovApiLink() + "' target='_blank'>LOV</a>",
					"priority": 3,
				},
				"queryResults": {
					"color":"#502982;",
					"abbreviation": "P",
					"description": "Properties fetched from dataset (i.e. as rdf:Property)",
					"priority": 2,
				},
				"query": {
					"color":"#BF9C30;",
					"abbreviation": "C",
					"description": "Cached properties based on endpoint query logs",
					"priority": 1,
				}
			};
		this.completionType = "property";
		this.methods = getPropertyCompletionMethods();
		this.requestLovAutocompletions = function() {
			completion.fetched['lov'] = false;
		    var args = {q:completion.uriStart, page: 1, type: "queryResults"};
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
		    		completion.resultSizes['lov'] = data.total_results;
					for (var i = 0; i < data.results.length; i++) {
						completion.results.push({
							type: "lov", 
							uri: data.results[i].uri, 
							priority: completion.methodProperties.lov.priority
						});
				 	}
					var resultsSoFar = data.page_size * data.page;
					if (resultsSoFar < data.total_results && resultsSoFar < completion.maxResults) {
						increasePage();
						requestObj.doLovRequest();
					} else {
						//request done, draw!
						completion.fetched['lov'] = true;
						completion.drawIfNeeded(completion);
					}
				}).fail(function(jqXHR, textStatus, errorThrown) {
					console.log(errorThrown);
				  });
		    };
		    this.doLovRequest();
		};
		
		this.doRequests = function() {
			this.preprocessToken(completion);
			if (completion.uriStart == null || completion.uriStart.length == 0) {
				console.log("no uri to autocomplete");
				return;
			}
			var allDisabled = true;
			var methods = completion.methods;
			var servletMethods = [];
			for (var method in methods) {
				if (methods[method]) {
					allDisabled = false;
					if (method == "lov") {
						completion.requestLovAutocompletions();
					} else {
						//both other methods are executed as 1 single request
						servletMethods.push(method);
					}
				}
			}
			if (servletMethods.length > 0) {
				completion.requestServletAutocompletions(completion, servletMethods);
			}
			if (allDisabled) {
				completion.legendDialogue.draw(completion, true);
			}
		};
		this.doRequests();
	};
	extend(PropertyAutocompletion, AutocompletionBase);
	
	
	var useClassAutocompletion = function(cm) {
		var token = getCompleteToken(cm);
		var cur = cm.getCursor();
		var previousToken = getPreviousNonWsToken(cm, cur.line, token);
		return ($.inArray("a", previousToken.state.possibleCurrent) != -1 && token.type != "sp-var");
	};
	var ClassAutocompletion = function(cm, drawCallback) {
		var completion = this;
		this.cm = cm;
		this.drawCallback = drawCallback;
		this.methodProperties = {
				"queryResults": {
					"color":"#502982;",
					"abbreviation": "Cl",
					"description": "Classes fetched from dataset",
					"priority": 2,
				},
				"query": {
					"color":"#BF9C30;",
					"abbreviation": "Ca",
					"description": "Cached classes based on endpoint query logs",
					"priority": 1,
				}
		};
		this.completionType = "class";
		this.methods = getClassCompletionMethods();
		
		
		
		this.doRequests = function() {
			this.preprocessToken(completion);
			if (completion.uriStart == null || completion.uriStart.length == 0) {
				console.log("no uri to autocomplete");
				return;
			}
			var methods = completion.methods;
			var servletMethods = [];
			for (var method in methods) {
				if (methods[method]) {
					servletMethods.push(method);
				}
			}
			if (servletMethods.length > 0) {
				completion.requestServletAutocompletions(completion, servletMethods);
			} else {
				//no method is enabled. still draw dialogue (user might want to enable some methods again)
				completion.legendDialogue.draw(completion, true);
			}
		};
		this.doRequests();
	};
	extend(ClassAutocompletion, AutocompletionBase);
	
	CodeMirror.AutocompletionBase = function(cm, drawCallback) {
		if (usePropertyAutocompletion(cm)) {
			new PropertyAutocompletion(cm, drawCallback);
		} else if (useClassAutocompletion(cm)) {
			new ClassAutocompletion(cm, drawCallback);
		}
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
	};
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

