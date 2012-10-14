CodeMirror.defineMode("sparql", function(config, parserConfig) {

    var indentUnit = config.indentUnit;




    function getPossibles(symbol)
    {
	var possibles=[], possiblesOb=ll1_table[symbol];
	if (possiblesOb!=undefined)
	    for (var property in possiblesOb) 
		possibles.push(property.toString());
	else
	    possibles.push(symbol);
	return possibles;
    }

    var tms= getTerminals();
    var terminal=tms.terminal;
    var keywords=tms.keywords;
    var punct=tms.punct;

    function tokenBase(stream, state) {
	
	function nextToken() {

	    var consumed=null;
 	    // Tokens defined by individual regular expressions
	    for (var i=0; i<terminal.length; ++i) {
		consumed= stream.match(terminal[i].regex,true,false);
		if (consumed) 
		    return { cat: terminal[i].name, 
			     style: terminal[i].style,
			     text: consumed[0]
			   };
	    }

	    // Keywords
	    consumed= stream.match(keywords,true,false);
	    if (consumed)
		return { cat: stream.current().toUpperCase(),
			 style: "keyword",
			 text: consumed[0]
		       };
	
	    // Punctuation
	    consumed= stream.match(punct,true,false);
	    if (consumed) 
		return { cat: stream.current(),
			 style: "meta",
			 text: consumed[0]
		       };
	    
	    // Token is invalid
	    // better consume something anyway, or else we're stuck
	    consumed= stream.match(/^.[A-Za-z0-9]*/,true,false);
	    return { cat:"<invalid_token>", 
		     style: "error",
		     text: consumed[0]
		   };
	}

	function recordFailurePos() { 
	    // tokenOb.style= "sp-invalid";
	    var col= stream.column();
	    state.errorStartPos= col;
	    state.errorEndPos= col+tokenOb.text.length;
	};

	function setQueryType(s) {
	    if (s=="SELECT" || s=="CONSTRUCT" || s=="ASK" || s=="DESCRIBE")
		state.queryType=s;
	}

	// CodeMirror works with one line at a time,
	// but newline should behave like whitespace
	// - i.e. a definite break between tokens (for autocompleter)
	if (stream.pos==0) 
	    state.possibleCurrent= state.possibleNext;

	var tokenOb= nextToken();

	if (tokenOb.cat=="<invalid_token>") {
	    // set error state, and
	    if (state.OK==true) {
		state.OK=false;
		recordFailurePos();
	    }
	    //alert("Invalid:"+tokenOb.text);
	    return tokenOb.style;
	}

	if (tokenOb.cat == "WS" ||
	    tokenOb.cat == "COMMENT") {
	    state.possibleCurrent= state.possibleNext;
	    return(tokenOb.style);
	}
	// Otherwise, run the parser until the token is digested
	// or failure
	var finished= false;
	var topSymbol;
	var token= tokenOb.cat;

	// Incremental LL1 parse
	while(state.stack.length>0 && token && state.OK && !finished ) {
	    topSymbol= state.stack.pop();
	    
	    if (!ll1_table[topSymbol]) {
		// Top symbol is a terminal
		if (topSymbol==token) {
		    // Matching terminals
		    // - consume token from input stream
		    finished=true;
		    setQueryType(topSymbol);
		} else {
		    state.OK=false;
		    recordFailurePos();
		}
	    } else {
		// topSymbol is nonterminal
		//  - see if there is an entry for topSymbol 
		// and nextToken in table
		var nextSymbols= ll1_table[topSymbol][token];
		if (nextSymbols!=undefined) {
		    // Match - copy RHS of rule to stack
		    for (var i=nextSymbols.length-1; i>=0; --i)
			state.stack.push(nextSymbols[i]);
		} else {
		    // No match in table - fail
		    state.OK=false;
		    recordFailurePos();
		    state.stack.push(topSymbol);  // Shove topSymbol back on stack
		}
	    }
	} 

    	state.possibleCurrent= state.possibleNext;
	state.possibleNext= getPossibles(state.stack[state.stack.length-1]);

	//alert(tokenOb.style);
	return tokenOb.style;
    }

    
    var indentTop={
	"*[,, object]": 3,
	"?[verb, objectList]": 1,
	"object": 2,
	"objectList": 2,
	"propertyListNotEmpty": 1,
	"propertyList": 1,
    };

    var indentTable={
	"}":1,
	"]":0,
	")":1
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
									break;
								}
							}
						} else {
							// Consider nullable non-terminals if at top of
							// stack
							var dn = indentTop[state.stack[i]];
							if (dn) {
								n += dn;
								--i;
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
	token: tokenBase,
	startState: function(base) {
	    return {
		tokenize: tokenBase,
		OK: true, 
		errorStartPos: null,
		errorEndPos: null,
		queryType: null,
		possibleCurrent: getPossibles("query"),
		possibleNext: getPossibles("query"),
		stack: ["query"] }; },
	indent: indent,
	electricChars: "}])"
    };
});
CodeMirror.defineMIME("application/x-sparql-query", "sparql");
