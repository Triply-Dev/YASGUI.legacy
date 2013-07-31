/*
 * Isomorphic SmartClient
 * Version 8.0
 * Copyright(c) 1998 and beyond Isomorphic Software, Inc. All rights reserved.
 * "SmartClient" is a trademark of Isomorphic Software, Inc.
 *
 * licensing@smartclient.com
 *
 * http://smartclient.com/license
 */

function findScLocator(element, autWindow) {
    //the element selenium passes is a "safe" XPCNativeWrappers wrapper of the real element. XPCNativeWrappers are used to protect
    //the chrome code working with content objects and there's no way to access the real "underlying" element object.
    //example of an element passed here is [object XPCNativeWrapper [object HTMLInputElement]]

    //see https://developer.mozilla.org/en/wrappedJSObject
    //https://developer.mozilla.org/en/XPCNativeWrapper
    if (autWindow == null) autWindow = this.window;
    if (autWindow.wrappedJSObject) {
        autWindow = autWindow.wrappedJSObject;
    }

    if(hasSC(autWindow)) {
        var e;
        try {
            e = convertToLiveElement(element, autWindow);
            
            // Second parameter tells the autoTest subsystem *not* to return
            // a locator if the element relies on native event handling and we
            // dont' have a direct locator to pick it up. EG: Don't returns 
            // the locator for a canvas handle when a click occurred on a link embedded
            // in a canvas.
            var scLocator = autWindow.isc.AutoTest.getLocator(e, true);
            
            if(scLocator != null && scLocator != "") {
                return "scLocator=" + scLocator;
            } else {
                return null;
            }
        } catch(ex) {
            alert('caught error ' + ex + ' for element ' + e + ' with id' + e.id);
            return null;
        }
    } else {
        return null;
    }
}


function convertToLiveElement(element, autWindow) {
    var id = element.id,
        nullID;
    if (id == null || id === undefined || id == '') {
        //assign an id to the element if one does not exist so that it can be located by SC
        id = "sel_" + autWindow.isc.ClassFactory.getNextGlobalID();
        element.id = id;
        nullID = true;
    }

    //The sc classes are loaded in wrappedJSObject window, and not the window reference held by Locators.
    //see https://developer.mozilla.org/en/wrappedJSObject
    var e = autWindow.document.getElementById(id);
    
    // reset ID to null - this way if we *don't* get a SmartClient locator
    // normal page locator strategy will work
    if (nullID) {
        element.id = null;
    }
    return e;
}

LocatorBuilders.add('sc', findScLocator);
// add SC Locator to the head of the priority of builders.
LocatorBuilders.order = ['sc', 'id', 'link', 'name', 'dom:name', 'xpath:link', 'xpath:img', 'xpath:attributes', 'xpath:href', 'dom:index', 'xpath:position'];

//override the default clickLocator so that duplicate click events are not recorded
Recorder.removeEventHandler('clickLocator');
Recorder.addEventHandler('clickLocator', 'click', function(event) {
        if (event.button == 0) {

        // === start sc specific code ===
        var autWindow = this.window;
        if (autWindow.wrappedJSObject) {
            autWindow = autWindow.wrappedJSObject;
        }
        if(hasSC(autWindow)) {
            var element = this.clickedElement;
          
            var scLocator = findScLocator(element, autWindow);
        
            //if an scLocator is found, then this event will be captured by the scClickLocator mousedown event recorder
            // 'return' so that we don't get duplicate records
            if(scLocator != null) {
                return;
            }
        }
        // === end sc specific code ===
            
        var clickable = this.findClickableElement(event.target);
        if (clickable) {
            // prepend any required mouseovers. These are defined as
            // handlers that set the "mouseoverLocator" attribute of the
            // interacted element to the locator that is to be used for the
            // mouseover command. For example:
            //
            // Recorder.addEventHandler('mouseoverLocator', 'mouseover', function(event) {
            //     var target = event.target;
            //     if (target.id == 'mmlink0') {
            //         this.mouseoverLocator = 'img' + target._itemRef;
            //     }
            //     else if (target.id.match(/^mmlink\d+$/)) {
            //         this.mouseoverLocator = 'lnk' + target._itemRef;
            //     }
            // }, { alwaysRecord: true, capture: true });
            //
            if (this.mouseoverLocator) {
                this.record('mouseOver', this.mouseoverLocator, '');
                delete this.mouseoverLocator;
            }
            this.record("click", this.findLocators(event.target), '');
        } else {
            var target = event.target;
            this.callIfMeaningfulEvent(function() {
                    this.record("click", this.findLocators(target), '');
                });
        }
    }
	}, { capture: true });


Recorder.addEventHandler('scMouseDownLocator', 'mousedown', function(event) {
    if (event.button == 0) {
        var autWindow = this.window;
        if (autWindow.wrappedJSObject) {
            autWindow = autWindow.wrappedJSObject;
        }
        if(hasSC(autWindow)) {
            var element = this.clickedElement;

            // If the element clicked is a form text input based field or textArea do not
            // record it as a Selenium SC click as we want the default behaviour Selenium IDE
            // not registering click events on these to be used. This is particularly
            // important since the Selenium "type" command for text / textArea based input
            // fields is registered only on blur and recording clicks on these fields results
            // in out of order replays. The Selenium "type" command does not require the field
            // to be focused first.
            if(element.tagName && 
               ((element.tagName.toLowerCase() == "input" &&
                 (element.type == "text" || element.type == "password" || element.type == "file")) ||
                element.tagName.toLowerCase() == "textarea")) {
                return;
            }
            var canvas = autWindow.isc.AutoTest.locateCanvasFromDOMElement(element);
            var scLocator = findScLocator(element, autWindow);
            setSCContextValue(autWindow, "mouseDownTarget", canvas);
            setSCContextValue(autWindow, "mouseDownLocator", scLocator);
            setSCContextValue(autWindow, "mouseDownCoords", 
                              [autWindow.isc.EH.getX(), autWindow.isc.EH.getY()]);
        }
    }
}, { capture: true });

Recorder.addEventHandler('scMouseUpLocator', 'mouseup', function(event) {
    if (event.button == 0) {
        var autWindow = this.window;
        if (autWindow.wrappedJSObject) {
            autWindow = autWindow.wrappedJSObject;
        }
        if(hasSC(autWindow)) {
            var element = event.target;
            
            // If the element clicked is a form text input based field or textArea do not
            // record it as a Selenium SC click as we want the default behaviour Selenium IDE
            // not registering click events on these to be used. This is particularly
            // important since the Selenium "type" command for text / textArea based input
            // fields is registered only on blur and recording clicks on these fields results
            // in out of order replays. The Selenium "type" command does not require the field
            // to be focused first.
            if(element.tagName && 
               ((element.tagName.toLowerCase() == "input" && 
                 (element.type == "text" || element.type == "password" || element.type == "file")) || 
                element.tagName.toLowerCase() == "textarea")) {
                return;
            }
            var scLocator = findScLocator(element, autWindow);
            var mouseDownLocator = getSCContextValue(autWindow, "mouseDownLocator");
                
            // if mouseDown occurred over an sc-significant element, but mouseup didn't,
            // we may still need to fire drag stop handlers.
            if (scLocator == null && mouseDownLocator == null) return;
            
            // Are we finishing a drag operation?
            if (mouseDownLocator && autWindow.isc.EH.dragging) {
                // 2 possibilities: 
                // dragAndDropToObject - records a source and target object and at playback
                // coords will be determined from those elements
                // dragAndDrop - records a source and a px offset - at playback time we'll
                // move by that number of pixels.
                
                // If the current target matches the mouse-down target, or we have no SC element
                // at the current location, treat as absolute coordinates
                var dragToObject = (scLocator != null && scLocator != mouseDownLocator);
                
                // If the current target != the mouse down target and both are valid SC elements
                // its still worth discounting cases where we obviously aren't attempting to do
                // a drag and drop over the target object.
                // This is important to catch as in these cases absolute pixel offsets are likely
                // more appropriate than the center of whatever element we ended up over.
                if (dragToObject && 
                    (autWindow.isc.EH.dragTarget == null ||
                     autWindow.isc.EH.dragTarget.canDrop == null ||
                     autWindow.isc.EH.dragOperation == autWindow.isc.EH.DRAG_RESIZE)) 
                {
                    dragToObject = false;
                }
                
                // mouse over new target - record as "dragAndDropToObject". Coords will be
                // derived from the live DOM elements
                if (dragToObject) {
                    this.recordSC2(
                        "dragAndDropToObject", 
                        mouseDownLocator,
                        scLocator
                    );
                // mouse is over drag target (or no SC-significant target) - 
                // record as simple "dragAndDrop" and record offset based on mouse down vs
                // mouse up position.
                // This will handle drag reposition etc.
                } else {
                    var startCoords = getSCContextValue(autWindow, "mouseDownCoords");
                    var offsetX = autWindow.isc.EH.getX() - startCoords[0],
                        offsetY = autWindow.isc.EH.getY() - startCoords[1];
                        
                    this.recordSC1(
                        "dragAndDrop",
                        mouseDownLocator,
                        (offsetX > 0 ? "+" : "") + offsetX + "," +
                        (offsetY > 0 ? "+" : "") + offsetY
                    );
                }
            // Not a drag/drop interaction - perform click operation.
            } else {
                
                // don't fire click if mouseDown locator or mouse up locator are unset.
                if (mouseDownLocator != null && scLocator != null) {

                    // If this event is within the double click interval since the last SC click we
                    // recorded, record as a double-click event
                    var EH = autWindow.isc.EH,
                        time = autWindow.isc.timeStamp(),
                        withinDoubleClickInterval = false;
                    if (EH.lastClickTime != null) {
                        var completeTime = EH._lastClickCompleteTime || EH.$k9;
                        withinDoubleClickInterval = 
                         ((completeTime - EH.lastClickTime) < EH.DOUBLE_CLICK_DELAY) ?
                            time - EH.lastClickTime < EH.DOUBLE_CLICK_DELAY :
                            ((time - completeTime) < 100);
                    }

                    // If mouse coordinates haven't changed, use scLocator generated
                    // during the mouseDown event; this avoids generating a bad locator
                    // for situations in which the click itself moves the widget stack
                    var startCoords = getSCContextValue(autWindow, "mouseDownCoords");
                    if (autWindow.isc.EH.getX() == startCoords[0] &&
                        autWindow.isc.EH.getY() == startCoords[1]) scLocator = mouseDownLocator;
    
                    if (withinDoubleClickInterval) this.record("secondClick", scLocator, '');
                    else                           this.recordSC1("click",    scLocator, '');
                }
            }
            
            // clear out mouseDown context vars
            setSCContextValue(autWindow, "mouseDownTarget", null);
            setSCContextValue(autWindow, "mouseDownLocator", null);
            setSCContextValue(autWindow, "mouseDownCoords", null);
        }
    }
}, { capture: true });

Recorder.addEventHandler('scContextMenuLocator', 'mousedown', function(event) {
    if (event.button == 2) {
        var autWindow = this.window;
        if (autWindow.wrappedJSObject) {
            autWindow = autWindow.wrappedJSObject;
        }
        if(hasSC(autWindow)) {
            var element = this.clickedElement;
           
            var scLocator = findScLocator(element,autWindow);
            
            if(scLocator != null) {
                this.recordSC1("contextMenu", scLocator, '');
                delete this.click;
            }
        }
    }
}, { capture: true });


//override the default type locator to pick up typing within SC form items.
Recorder.removeEventHandler('type');
Recorder.addEventHandler('scType', 'change', function(event) {
        var tagName = event.target.tagName.toLowerCase();
        var type = event.target.type;
        if (('input' == tagName && ('text' == type || 'password' == type || 'file' == type)) ||
                'textarea' == tagName) {
    
            // === start sc specific code ===
            var autWindow = this.window;
            if (autWindow.wrappedJSObject) {
                autWindow = autWindow.wrappedJSObject;
            }
            if(hasSC(autWindow)) {
                var element = event.target;
              
                var scLocator = findScLocator(element, autWindow);
            
                if(scLocator != null) {
                    this.recordSC1("type", scLocator, event.target.value);
                }
            } else {
                this.record("type", this.findLocators(event.target), event.target.value);
            }
        }
    }, { capture: true });

CommandBuilders.add('action', function(window) {
    var autWindow = window;
    if (autWindow.wrappedJSObject) {
        autWindow = autWindow.wrappedJSObject;
    }

    if(hasSC(autWindow)) {
        var element = this.getRecorder(window).clickedElement;
      
        var scLocator = findScLocator(element,autWindow);
        
        if(scLocator != null) {
            return {
                command: "click",
                target: scLocator
            };
        } else {
            return {
                command: "click",
                disabled : true
            };
        }
    } else {
        return {
                command: "click",
                disabled : true
            };
    }
});


CommandBuilders.add('accessor', function(window) {
    var autWindow = window;
    if (autWindow.wrappedJSObject) {
        autWindow = autWindow.wrappedJSObject;
    }
    var result = { accessor: "table", disabled: true };
    if(hasSC(autWindow)) {
        var element = this.getRecorder(window).clickedElement;

        if (!element) return result;
        
        var e = convertToLiveElement(element, autWindow);
        
        var listGrid = autWindow.isc.AutoTest.locateCanvasFromDOMElement(e);

        if(listGrid == null || !listGrid.isA("GridRenderer")) return result;

        var cellXY = listGrid.getCellFromDomElement(e);
        if(cellXY == null) return result;
        var row = cellXY[0];
        var col = cellXY[1];
        //the locator can return a GridBody
        if(listGrid.grid) {
            listGrid = listGrid.grid;
        }

        var record = listGrid.getRecord(Number(row));

        var value = listGrid.getCellValue(record, row, col);

        result.target = 'scLocator=' + listGrid.getLocator() + '.' + row + '.' + col;
        result.value = value;
        result.disabled = false;
        return result;
    }

    return result;
});

Recorder.prototype.recordSC1 = function(actionName, argument1, argument2) {
    this.record("waitForElementClickable", argument1, '');
    this.record(actionName, argument1, argument2);
};

Recorder.prototype.recordSC2 = function(actionName, argument1, argument2) {
    this.record("waitForElementClickable", argument1, '');
    this.record("waitForElementClickable", argument2, '');
    this.record(actionName, argument1, argument2);
};

function hasSC(autWindow) {
    var hasSC = autWindow.isc != null;
    if(hasSC && autWindow.isc.AutoTest === undefined) {
        //this should never be the case with newer SC versions as AutoTest is part of core
        autWindow.isc.loadAutoTest();
    }
    if(hasSC && autWindow.isc.Canvas.getCanvasLocatorFallbackPath === undefined) {
        autWindow.isc.ApplyAutoTestMethods();
    }
    return hasSC;
}

function setSCContextValue(autWindow, fieldName, value) {
    if (!hasSC(autWindow)) return;
    if (autWindow.isc.SeleniumContext == null) autWindow.isc.SeleniumContext = {};
    autWindow.isc.SeleniumContext[fieldName] = value;
}

function getSCContextValue(autWindow, fieldName) {
    if (!hasSC(autWindow)) return;
    if (autWindow.isc.SeleniumContext == null) return null;
    return autWindow.isc.SeleniumContext[fieldName];
}