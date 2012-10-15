CodeMirror
		.defineMode(
				"sparql",
				function(config, parserConfig) {
					var tms = getTerminals();
					var indentUnit = config.indentUnit;

					var keywords = tms.keywords;
					var defaultQueryType="update";
					var punct = tms.punct;

					var acceptEmpty = true;

					function getPossibles(symbol) {
						var possibles = [], possiblesOb = ll1_table[symbol];
						if (possiblesOb != undefined)
							for ( var property in possiblesOb)
								possibles.push(property.toString());
						else
							possibles.push(symbol);
						return possibles;
					}

					
					var terminal = tms.terminal;

					function tokenBase(stream, state) {

						function nextToken() {

							var consumed = null;
							// Tokens defined by individual regular expressions
							for ( var i = 0; i < terminal.length; ++i) {
								consumed = stream.match(terminal[i].regex,
										true, false);
								if (consumed)
									return {
										cat : terminal[i].name,
										style : terminal[i].style,
										text : consumed[0]
									};
							}

							// Keywords
							consumed = stream.match(keywords, true, false);
							if (consumed)
								return {
									cat : stream.current().toUpperCase(),
									style : "sp-keyword",
									text : consumed[0]
								};

							// Punctuation
							consumed = stream.match(punct, true, false);
							if (consumed)
								return {
									cat : stream.current(),
									style : "sp-punc",
									text : consumed[0]
								};

							// Token is invalid
							// better consume something anyway, or else we're
							// stuck
							consumed = stream.match(/^.[A-Za-z0-9]*/, true,
									false);
							return {
								cat : "<invalid_token>",
								style : "error",
								text : consumed[0]
							};
						}

						function recordFailurePos() {
							// tokenOb.style= "sp-invalid";
							var col = stream.column();
							state.errorStartPos = col;
							state.errorEndPos = col + tokenOb.text.length;
						}
						;

						function setQueryType(s) {
							if (state.queryType == null) {
								if (s == "SELECT" || s == "CONSTRUCT"
										|| s == "ASK" || s == "DESCRIBE")
									state.queryType = s;
							}
						}

						// Some fake non-terminals are just there to have
						// side-effect on state
						// - i.e. allow or disallow variables and bnodes in
						// certain non-nesting
						// contexts
						function setSideConditions(topSymbol) {
							if (topSymbol == "disallowVars")
								state.allowVars = false;
							else if (topSymbol == "allowVars")
								state.allowVars = true;
							else if (topSymbol == "disallowBnodes")
								state.allowBnodes = false;
							else if (topSymbol == "allowBnodes")
								state.allowBnodes = true;
							else if (topSymbol == "storeProperty")
								state.storeProperty = true;
						}

						function checkSideConditions(topSymbol) {
							return ((state.allowVars || topSymbol != "var") && (state.allowBnodes || (topSymbol != "blankNode"
									&& topSymbol != "blankNodePropertyList" && topSymbol != "blankNodePropertyListPath")))
						}

						// CodeMirror works with one line at a time,
						// but newline should behave like whitespace
						// - i.e. a definite break between tokens (for
						// autocompleter)
						if (stream.pos == 0)
							state.possibleCurrent = state.possibleNext;

						var tokenOb = nextToken();

						if (tokenOb.cat == "<invalid_token>") {
							// set error state, and
							if (state.OK == true) {
								state.OK = false;
								recordFailurePos();
							}
							state.complete = false;
							// alert("Invalid:"+tokenOb.text);
							return tokenOb.style;
						}

						if (tokenOb.cat == "WS" || tokenOb.cat == "COMMENT") {
							state.possibleCurrent = state.possibleNext;
							return (tokenOb.style)
						}
						// Otherwise, run the parser until the token is digested
						// or failure
						var finished = false;
						var topSymbol;
						var token = tokenOb.cat;

						// Incremental LL1 parse
						while (state.stack.length > 0 && token && state.OK
								&& !finished) {
							topSymbol = state.stack.pop();
							if (!ll1_table[topSymbol]) {
								// Top symbol is a terminal
								if (topSymbol == token) {
									// Matching terminals
									// - consume token from input stream
									finished = true;
									setQueryType(topSymbol);
									// Check whether $ (end of input token) is
									// poss next
									// for everything on stack
									var allNillable = true;
									for ( var sp = state.stack.length; sp > 0; --sp) {
										var item = ll1_table[state.stack[sp - 1]];
										if (!item || !item["$"])
											allNillable = false;
									}
									state.complete = allNillable;
									if (state.storeProperty
											&& token.cat != "sp-punc") {
										state.lastProperty = tokenOb.text;
										state.storeProperty = false;
									}
								} else {
									state.OK = false;
									state.complete = false;
									recordFailurePos();
								}
							} else {
								// topSymbol is nonterminal
								// - see if there is an entry for topSymbol
								// and nextToken in table
								var nextSymbols = ll1_table[topSymbol][token];
								if (nextSymbols != undefined
										&& checkSideConditions(topSymbol)) {
									// Match - copy RHS of rule to stack
									for ( var i = nextSymbols.length - 1; i >= 0; --i)
										state.stack.push(nextSymbols[i]);
									// Peform any non-grammatical side-effects
									setSideConditions(topSymbol);
								} else {
									// No match in table - fail
									state.OK = false;
									state.complete = false;
									recordFailurePos();
									state.stack.push(topSymbol); // Shove
									// topSymbol
									// back on
									// stack
								}
							}
						}
						if (!finished && state.OK) {
							state.OK = false;
							state.complete = false;
							recordFailurePos();
						}

						state.possibleCurrent = state.possibleNext;
						state.possibleNext = getPossibles(state.stack[state.stack.length - 1]);

						// alert(token+"="+tokenOb.style+'\n'+state.stack);
						return tokenOb.style;
					}

					var indentTop = {
						"*[,, object]" : 3,
						"*[ (,), object]" : 3,
						"?[verb, objectList]" : 1,
						"object" : 2,
						"objectList" : 2,
						"objectListPath" : 2,
						"storeProperty" : 2,
						"propertyListNotEmpty" : 1,
						"propertyList" : 1,
						"propertyListPath" : 1,
						"propertyListPathNotEmpty" : 1
					};

					var indentTable = {
						"}" : 1,
						"]" : 0,
						")" : 1
					};

					function indent(state, textAfter) {
						var n = 0; // indent level
						var i = state.stack.length - 1;

						if (/^[\}\]\)]/.test(textAfter)) {
							// Skip stack items until after matching bracket
							var closeBracket = textAfter.substr(0, 1);
							for (; i >= 0; --i) {
								if (state.stack[i] == closeBracket) {
									--i;
									break
								}
								;
							}
						} else {
							// Consider nullable non-terminals if at top of stack
							var dn = indentTop[state.stack[i]];
							if (dn) {
								n += dn;
								--i
							}
						}
						for (; i >= 0; --i) {
							var dn = indentTable[state.stack[i]];
							if (dn)
								n += dn;
						}
						return n * config.indentUnit;
					}
					;
					
					return {
						token : tokenBase,
						startState : function(base) {
							return {
								tokenize : tokenBase,
								OK : true,
								complete : acceptEmpty,
								errorStartPos : null,
								errorEndPos : null,
								possibleCurrent : getPossibles("update"),
								possibleNext : getPossibles("update"),
//								possibleCurrent : getPossibles("update").concat(getPossibles("query")),
//								possibleNext : getPossibles("update").concat(getPossibles("query")),
								allowVars : true,
								allowBnodes : true,
								storeProperty : false,
								lastProperty : "",
								stack : ["update"]
							};
						},
						indent : indent,
						electricChars : "}])"
					};
				});
CodeMirror.defineMIME("application/x-sparql-query", "sparql");