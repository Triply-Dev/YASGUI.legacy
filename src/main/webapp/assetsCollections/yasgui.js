var corsEnabled = {};
var fetchingCors = {};
var proxy;
var sparqlHighlightHeight = {};
var sparqlHighlight = {};
var sparqlResponseHighlight = {};
var prefixes = [];
var properties = {};
$.fn.qtip.defaults.style.classes = "qtip-dark qtip-rounded qtip-shadow";
//These two variables are ugly workaround with which we can distinguish in our callback between a history state changed issued
//by the browser, and issued by our code
var historyCallbackEnabled = false;
var ignoreNextHistoryPush = false;

/**
 * Perform check to see if endpoint is cors enabled. Use this to decide where to send a query: via the proxy or directly
 * 
 * @param endpoint
 */
function checkCorsEnabled(endpoint) {
	//Only perform check if it hasnt been done already
	if (corsEnabled[endpoint] == null && (fetchingCors[endpoint] == null || fetchingCors[endpoint] == false)) {
		fetchingCors[endpoint] = true;
		$.ajax({
			url : endpoint,
			method : 'get',
			complete : function(xhr) {
				if (xhr.status != 0) { // CORS-enabled site
					corsEnabled[endpoint] = true;
				} else {
					corsEnabled[endpoint] = false;
				}
				fetchingCors[endpoint] = false;
			}
		});
	}
}
$(document).keydown(function(e) {
	var code = (e.keyCode ? e.keyCode : e.which);
	if (code == 27) {//escape key
		cancelQuery();
	}
	if (
			(code == 10 || code == 13) //enter
			&& (e.ctrlKey || e.metaKey) //ctrl or apple cmd key
		) {
		executeQuery();
		e.preventDefault();
	}

});

function updateBookmarkCmHeight(elementId) {
	cmHeight = sparqlHighlight[elementId].getWrapperElement().offsetHeight;
	if (sparqlHighlightHeight[elementId]) {
		if (cmHeight != sparqlHighlightHeight[elementId]) {
			sparqlHighlightHeight[elementId] = cmHeight;
			adjustBookmarkQueryInputForContent(cmHeight);
		}
	} else {
		sparqlHighlightHeight[elementId] = cmHeight;
	}
}

function initializeQueryCodemirror(elementId) {
	var qInput = document.getElementById(elementId);
	if (qInput) {
		if (sparqlHighlight[elementId] == null) { 
			//Only add if it hasnt been drawn yet
			sparqlHighlight[elementId] = CodeMirror.fromTextArea(qInput, {
				mode : "sparql11",
				theme: "yasgui",
				highlightSelectionMatches: {showToken: /\w/},
				tabMode : "indent",
				lineNumbers : true,
				gutters: ["gutterErrorBar","CodeMirror-linenumbers" ],
				matchBrackets : true,
				fixedGutter: true,
				extraKeys : {
					"Ctrl-Space" : "autoComplete",
					"Cmd-Space" : "autoComplete",
					"Ctrl-D" : "deleteLines",
					"Ctrl-K" : "deleteLines",
					"Cmd-D" : "deleteLines",
					"Cmd-K" : "deleteLines",
					"Ctrl-/" : "commentLines",
					"Cmd-/" : "commentLines",
					"Ctrl-Alt-Down" : "copyLineDown",
					"Ctrl-Alt-Up" : "copyLineUp",
					"Cmd-Alt-Down" : "copyLineDown",
					"Cmd-Alt-Up" : "copyLineUp",
					"Shift-Ctrl-F": "doAutoFormat",
					"Shift-Cmd-F": "doAutoFormat",
					"Tab" : "indentTab",
					"Shift-Tab": "unindentTab"
				}
			});
			
			sparqlHighlight[elementId].on("change", function(cm, change){
				checkSyntax(cm, true);
				setQueryType(cm.getStateAfter().queryType);
				height = sparqlHighlight[elementId].getWrapperElement().offsetHeight;
				if (sparqlHighlightHeight[elementId]) {
					if (height != sparqlHighlightHeight[elementId]) {
						sparqlHighlightHeight[elementId] = height;
						adjustQueryInputForContent();
					}
				} else {
					sparqlHighlightHeight[elementId] = height;
				}
				CodeMirror.showHint(cm, CodeMirror.prefixHint, {closeCharacters: /(?=a)b/});
				appendPrefixIfNeeded(cm);
			});
			sparqlHighlight[elementId].on("gutterClick", function(cm, change) {
				saveTabTitle();
			});
			sparqlHighlight[elementId].on("focus", function(cm, change) {
				saveTabTitle();
			});
			sparqlHighlight[elementId].on("blur", function(cm, change) {
				storeQueryInCookie();
			});
			//init query type
			setQueryType(sparqlHighlight[elementId].getStateAfter().queryType);
			
			
			//Append another classname to the codemirror div, so we can set width and height via css
			if (qInput.nextSibling != null && qInput.nextSibling.className == "CodeMirror") {
				qInput.nextSibling.className = "CodeMirror queryCm";
				scrollElement = qInput.nextSibling.getElementsByClassName("CodeMirror-scroll");
				//use jquery for this (a bit easier). for this element, find scroll class, and append another class
				$("#"+elementId).next().find($(".CodeMirror-scroll")).addClass("queryScrollCm");
			}
		}
	} else {
		onError("no text area for query input id: " + elementId);
	}
}

function initializeQueryResponseCodemirror(elementId, mode) {
	var qInput = document.getElementById(elementId);
	if (qInput) {
		var drawCodeMirror = false;
		if (sparqlResponseHighlight[elementId] == null) drawCodeMirror = true;
		
		//also check if it isnt drawn yet. Checking for just the javascript object in the sparqlResponseHighlight object is not enough
		//The object can be there, while visually you see the text area. a goof that happens between codemirror and smartgwt I believe (having to do with the way smartgwt loads pages icw resizing pages)
		if (qInput.nextSibling == null) drawCodeMirror = true;

		if (drawCodeMirror) {
			var cmMode = mode;
			if (mode == "json") {
				cmMode = {
					name: "javascript",
					json: true
				};
			}
			sparqlResponseHighlight[elementId] = CodeMirror.fromTextArea(document.getElementById(elementId), {
				mode : cmMode,
				theme: "yasgui",
				lineNumbers : true,
				highlightSelectionMatches: {showToken: /\w/},
				matchBrackets : true,
				readOnly: true,
				fixedGutter: true,
				hideVScroll: true
			});
			
			//Append another classname to the codemirror div, so we can set width and height via css
			if (qInput.nextSibling != null && qInput.nextSibling.className == "CodeMirror") {
				qInput.nextSibling.className = "CodeMirror resultCm";
				scrollElement = qInput.nextSibling.getElementsByClassName("CodeMirror-scroll");
				//use jquery for this (a bit easier). for this element, find scroll class, and append another class
				$("#"+elementId).next().find($(".CodeMirror-scroll")).addClass("resultScrollCm");
			}
		}
	} else {
		onError("no text area to create sparql response highlight for. Input id: " + elementId);
	}
}

function initializeQueryBookmarkCodemirror(elementId) {
	var qInput = document.getElementById(elementId);
	if (qInput) {
		sparqlHighlight[elementId] = CodeMirror.fromTextArea(qInput, {
			mode : "sparql11",
			tabMode : "indent",
			theme: "yasgui",
			highlightSelectionMatches: {showToken: /\w/},
			gutters: ["gutterErrorBar","CodeMirror-linenumbers" ],
			lineNumbers : true,
			matchBrackets : true,
			fixedGutter: true,
			viewportMargin: Infinity,
			extraKeys : {
				"Ctrl-Space" : "autoComplete",
				"Cmd-Space" : "autoComplete",
				"Ctrl-D" : "deleteLines",
				"Ctrl-K" : "deleteLines",
				"Cmd-D" : "deleteLines",
				"Cmd-K" : "deleteLines",
				"Ctrl-/" : "commentLines",
				"Cmd-/" : "commentLines",
				"Ctrl-Alt-Down" : "copyLineDown",
				"Ctrl-Alt-Up" : "copyLineUp",
				"Cmd-Alt-Down" : "copyLineDown",
				"Cmd-Alt-Up" : "copyLineUp",
				"Shift-Ctrl-F": "doAutoFormat",
				"Shift-Cmd-F": "doAutoFormat",
				"Tab" : "indentTab",
				"Shift-Tab": "unindentTab"
			}
		});
		
		sparqlHighlight[elementId].on("change", function(cm, change){
			checkSyntax(cm, false);
			setQueryType(cm.getStateAfter().queryType);
			updateBookmarkCmHeight(elementId);
			CodeMirror.showHint(cm, CodeMirror.prefixHint, {closeCharacters: /(?=a)b/});
			appendPrefixIfNeeded(cm);
		});
		sparqlHighlight[elementId].on("blur", function(cm, change) {
			updateBookmarkedQuery();
		});
			
		updateBookmarkCmHeight(elementId);
		//Append another classname to the codemirror div, so we can set width and height via css
		if (qInput.nextSibling != null && qInput.nextSibling.className == "CodeMirror") {
			qInput.nextSibling.className = "CodeMirror bookmarkCm";
			scrollElement = qInput.nextSibling.getElementsByClassName("CodeMirror-scroll");
			//use jquery for this (a bit easier). for this element, find scroll class, and append another class
			$("#"+elementId).next().find($(".CodeMirror-scroll")).addClass("bookmarkScrollCm");
		}
	} else {
		onError("no text area for bookmark query input id: " + elementId);
	}
	
}


var latestComplete = -1;
var easing = 100;
function progressbarUpdate(percent, $element) {
	var progressBarWidth = percent * $element.width() / 100;
	var newText = percent + "%&nbsp;";
	if ($element.find('div').html() != newText) {
		if (latestComplete != -1 && latestComplete < (percent-1)) {
			//the previous animation is still busy... Don't want a lag, so lower our easing factor
			easing = Math.round(easing / 2);
		}
		$element.find('div').animate(
			{width: progressBarWidth },
			easing,
			function(){latestComplete=percent;}).html(newText);
	}
}

function dynamicSort(property) {
    var sortOrder = 1;
    if(property[0] === "-") {
        sortOrder = -1;
        property = property.substr(1);
    }
    return function (a,b) {
        var result = (a[property] < b[property]) ? -1 : (a[property] > b[property]) ? 1 : 0;
        return result * sortOrder;
    }
}

function dynamicSortMultiple() {
    /*
     * save the arguments object as it will be overwritten
     * note that arguments object is an array-like object
     * consisting of the names of the properties to sort by
     */
    var props = arguments;
    return function (obj1, obj2) {
        var i = 0, result = 0, numberOfProperties = props.length;
        /* try getting a different result from 0 (equal)
         * as long as we have extra properties to compare
         */
        while(result === 0 && i < numberOfProperties) {
            result = dynamicSort(props[i])(obj1, obj2);
            i++;
        }
        return result;
    }
}

Array.prototype.equals = function (array) {
    // if the other array is a falsy value, return
    if (!array)
        return false;

    // compare lengths - can save a lot of time
    if (this.length != array.length)
        return false;

    for (var i = 0; i < this.length; i++) {
        // Check if we have nested arrays
        if (this[i] instanceof Array && array[i] instanceof Array) {
            // recurse into the nested arrays
            if (!this[i].compare(array[i]))
                return false;
        }
        else if (this[i] != array[i]) {
            // Warning - two different object instances will never be equal: {x:20} != {x:20}
            return false;
        }
    }
    return true;
};
Array.prototype.unique = function() {
    var a = this.concat();
    for(var i=0; i<a.length; ++i) {
        for(var j=i+1; j<a.length; ++j) {
            if(a[i] === a[j])
                a.splice(j--, 1);
        }
    }

    return a;
};
if (typeof String.prototype.startsWith != 'function') {
	String.prototype.startsWith = function(str) {
		return this.slice(0, str.length) == str;
	};
}
if (typeof String.prototype.endsWith != 'function') {
	String.prototype.endsWith = function(str) {
		return this.slice(-str.length) == str;
	};
};
if (typeof String.prototype.contains != 'function') {
	String.prototype.contains = function(str) {
		return this.indexOf(":") >= 0;
	};
};

function compareObjects () {
  var leftChain, rightChain;

  function compare2Objects (x, y) {
    var p;

    // remember that NaN === NaN returns false
    // and isNaN(undefined) returns true
    if (isNaN(x) && isNaN(y) && typeof x === 'number' && typeof y === 'number') {
         return true;
    }

    // Compare primitives and functions.     
    // Check if both arguments link to the same object.
    // Especially useful on step when comparing prototypes
    if (x === y) {
        return true;
    }

    // Works in case when functions are created in constructor.
    // Comparing dates is a common scenario. Another built-ins?
    // We can even handle functions passed across iframes
    if ((typeof x === 'function' && typeof y === 'function') ||
       (x instanceof Date && y instanceof Date) ||
       (x instanceof RegExp && y instanceof RegExp) ||
       (x instanceof String && y instanceof String) ||
       (x instanceof Number && y instanceof Number)) {
        return x.toString() === y.toString();
    }

    // At last checking prototypes as good a we can
    if (!(x instanceof Object && y instanceof Object)) {
        return false;
    }

    if (x.isPrototypeOf(y) || y.isPrototypeOf(x)) {
        return false;
    }

    if (x.constructor !== y.constructor) {
        return false;
    }

    if (x.prototype !== y.prototype) {
        return false;
    }

    // check for infinitive linking loops
    if (leftChain.indexOf(x) > -1 || rightChain.indexOf(y) > -1) {
         return false;
    }

    // Quick checking of one object beeing a subset of another.
    // todo: cache the structure of arguments[0] for performance
    for (p in y) {
        if (y.hasOwnProperty(p) !== x.hasOwnProperty(p)) {
            return false;
        }
        else if (typeof y[p] !== typeof x[p]) {
            return false;
        }
    }

    for (p in x) {
        if (y.hasOwnProperty(p) !== x.hasOwnProperty(p)) {
            return false;
        }
        else if (typeof y[p] !== typeof x[p]) {
            return false;
        }

        switch (typeof (x[p])) {
            case 'object':
            case 'function':

                leftChain.push(x);
                rightChain.push(y);

                if (!compare2Objects (x[p], y[p])) {
                    return false;
                }

                leftChain.pop();
                rightChain.pop();
                break;

            default:
                if (x[p] !== y[p]) {
                    return false;
                }
                break;
        }
    }

    return true;
  }

  if (arguments.length < 1) {
    return true; //Die silently? Don't know how to handle such case, please help...
    // throw "Need two or more arguments to compare";
  }

  for (var i = 1, l = arguments.length; i < l; i++) {

      leftChain = []; //todo: this can be cached
      rightChain = [];

      if (!compare2Objects(arguments[0], arguments[i])) {
          return false;
      }
  }

  return true;
}
