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

// Warn if we're being loaded twice as things will be very likely to not work in this case
if (Selenium.prototype.sc_userExtensions_loaded) {
    LOG.warn("SmartClient user-extensions.js is being loaded more than once - " +
        "check for this file being included multiple times in the Selenium core extensions.");
}
Selenium.prototype.sc_userExtensions_loaded = true;

// provide control over whether to add URL query string defining sc_selenium as true
Selenium.prototype.use_url_query_sc_selenium = true;

PageBot.prototype.getAutWindow = function() {
    var autWindow = this.browserbot.getUserWindow();
    
    // if the user window is the dev console, redirect to the actual app window
    if (autWindow.targetWindow != null) autWindow = autWindow.targetWindow;
    // if SmartClient isn't loaded on the target window, just bail.
    if (autWindow.isc == null) return;

    if (autWindow.isc.AutoTest === undefined) {
        // this should never be the case with newer SC versions as AutoTest is part of core
        autWindow.isc.loadAutoTest();
    } else if (autWindow.isc.Canvas.getCanvasLocatorFallbackPath === undefined) {
        autWindow.isc.ApplyAutoTestMethods();
    }
    autWindow.isc.EventHandler.useNativeEventTime = false;
    return autWindow;
};

Selenium.prototype.getAutWindow = PageBot.prototype.getAutWindow;

PageBot.prototype.locateByScID = function (idLocator, getter, description) {

    var undef, autWindow = this.getAutWindow();

    idLocator = idLocator.replace(/'/g, "");
    idLocator = idLocator.replace(/"/g, "");

    var scObj = autWindow[idLocator];
    if(scObj == null) {
        LOG.info("Unable to locate SC object with ID " + idLocator);
        return description == "element" ? null : undef;
    } else {
        LOG.debug('Found SC object ' + scObj);
    }

    var scLocator = "//" + scObj.getClassName() + "[ID=\"" + idLocator + "\"]";
    LOG.debug("Using SC Locator " + scLocator);
    var target = autWindow.isc.AutoTest[getter](scLocator);
    LOG.info("Returning " + description + " :: " + target + " for SC locator " + scLocator);
    return target;
},

PageBot.prototype.locateByScLocator = function (scLocator, getter, description) {

    // support scLocators with the direct ID of the widget specified
    if(scLocator.indexOf("/") == -1) {
        LOG.debug("Using ID locator");
        return this.locateByScID(scLocator, getter, description);
    }
    var autWindow = this.getAutWindow();

    var target = autWindow.isc.AutoTest[getter](scLocator);
    LOG.debug("Returning " + description + " :: " + target + " for SC locator " + scLocator);
    return target;
},

// All locateElementBy* methods are added as locator-strategies.
PageBot.prototype.locateElementByScID = function(idLocator, inDocument, inWindow) {
    LOG.debug("Locate Element with SC ID=" + idLocator + ", inDocument=" + inDocument + 
              ", inWindow=" + inWindow.location.href);
    return this.locateByScID(idLocator, "getElement", "element");
},
PageBot.prototype.locateElementByScLocator = function(scLocator, inDocument, inWindow) {
    LOG.debug("Locate Element with SC Locator=" + scLocator + ", inDocument=" + inDocument + 
              ", inWindow=" + inWindow.location.href);
    return this.locateByScLocator(scLocator, "getElement", "element");
},

// We must do our own locator strategy resolution for locateValueBy*
PageBot.prototype.locateValueByScLocatorOrScID = function(locator) {
    LOG.debug("Locate Value with SC Locator/ScID=" + locator);

    var locatorObject = parse_locator(locator),
        locatorType = locatorObject.type;

    // install trimmed locator
    locator = locatorObject.string;

    if (locatorType == "scid") return this.locateByScID(locator, "getValue", "value");
    else                  return this.locateByScLocator(locator, "getValue", "value");
},

Selenium.prototype.orig_doType = Selenium.prototype.doType;

Selenium.prototype.doType = function(locator, value) {
    /**
   * Sets the value of an input field, as though you typed it in.
   *
   * <p>Can also be used to set the value of combo boxes, check boxes, etc. In these cases,
   * value should be the value of the option selected, not the visible text.</p>
   *
   * @param locator an <a href="#locators">element locator</a>
   * @param value the value to type
   */
   this.orig_doType(locator, value);

    // Selenium doesn't actually simulate a user typing into an input box so
    // for SmartClient FormItem's manually register the change.
    if(this.hasSC()) {
    
        var element = this.page().findElement(locator);
        if (element != null) {
    
            var autWindow = this.getAutWindow(),
                canvas = autWindow.isc.AutoTest.locateCanvasFromDOMElement(element);
            if (canvas != null && autWindow.isc.DynamicForm && autWindow.isc.isA.DynamicForm(canvas)) {
                var itemInfo = autWindow.isc.DynamicForm._getItemInfoFromElement(element, canvas);
                if (itemInfo && itemInfo.item) {
                    itemInfo.item.updateValue();
                }
            }
        }
    }
};

Selenium.prototype.orig_doClick = Selenium.prototype.doClick;

Selenium.prototype.doClick = function(locator, eventParams)
{
    LOG.info("Located in doScClick : " + locator);
    var element = this.page().findElement(locator);

    if(this.isSCLocator(locator)) {
        var autWindow = this.getAutWindow();
      
        var canvas = autWindow.isc.AutoTest.locateCanvasFromDOMElement(element);
        // if the clicked element does not correspond to a SmartClient widget,
        // then perform the default SmartClient click operation
        if(canvas == null) {
            this.orig_doClick(locator, eventParams);
            return;
        }
        LOG.debug("Located canvas " + canvas + " for locator " + locator);

        var coords = this.getSCLocatorCoords(autWindow, locator);
        if (coords == null) return;
        var clientX = coords[0];
        var clientY = coords[1];
  
        // Ensure we explicitly indicate whether this is a second click within double-click delay
        // This makes SC logic fire double click on the second click, regardless of the
        // playback timing
        if (autWindow.isc.EH._isSecondClick == null) {
            autWindow.isc.EH._isSecondClick = false;
        }
      
        LOG.debug("clientX = " + clientX + ", clientY=" + clientY);

        // fire a sequence of mousedown, mouseup and click operation to
        // trigger a SmartClient click event
        this.browserbot.triggerMouseEvent(element, "mouseover", true, clientX, clientY);
        this.browserbot.triggerMouseEvent(element, "mousedown", true, clientX, clientY);
        this.browserbot.triggerMouseEvent(element, "mouseup", true, clientX, clientY);
        this.browserbot.clickElement(element);
        
        autWindow.isc.EH._isSecondClick = null;
    } else {
        this.orig_doClick(locator, eventParams);
    }
};

// Special secondClick event - second half of a double-click
Selenium.prototype.doSecondClick = function (locator, eventParams) 
{
    if (!this.hasSC()) return this.doClick(locator, eventParams);
    
    var autWindow = this.getAutWindow();
    autWindow.isc.EH._isSecondClick = true;
    this.doClick(locator, eventParams);
    autWindow.isc.EH._isSecondClick = null;
}

// ensure playback of mouseDown / mouseUp on SmartClient locators behaves as expected.
Selenium.prototype.orig_doMouseDown = Selenium.prototype.doMouseDown;

Selenium.prototype.doMouseDown = function(locator, eventParams)
{
    LOG.info("Located in doMouseDown : " + locator);
    var element = this.page().findElement(locator);
    if(this.isSCLocator(locator)) {
        var autWindow = this.getAutWindow();
      
        var canvas = autWindow.isc.AutoTest.locateCanvasFromDOMElement(element);
        // if the clicked element does not correspond to a SmartClient widget,
        // then perform the default SmartClient click operation
        if(canvas == null) {
            this.orig_doMouseDown(locator, eventParams);
            return;
        }
        LOG.debug("Located canvas " + canvas + " for locator " + locator);

        var coords = this.getSCLocatorCoords(autWindow, locator);
        if (coords == null) return;
        var clientX = coords[0];
        var clientY = coords[1];
  
      
        LOG.debug("clientX = " + clientX + ", clientY=" + clientY);

        // fire mouseover / mouseDown
        // This will set up for SmartClient click, doubleclick or drag event as appropriate
        this.browserbot.triggerMouseEvent(element, "mouseover", true, clientX, clientY);
        this.browserbot.triggerMouseEvent(element, "mousedown", true, clientX, clientY);
    } else {
        this.orig_doMouseDown(locator, eventParams);
    }
};


Selenium.prototype.orig_doMouseUp = Selenium.prototype.doMouseUp;

Selenium.prototype.doMouseUp = function(locator, eventParams)
{
    LOG.info("Located in doMouseUp : " + locator);
    var element = this.page().findElement(locator);
    if(this.isSCLocator(locator)) {
        var autWindow = this.getAutWindow();
      
        var canvas = autWindow.isc.AutoTest.locateCanvasFromDOMElement(element);
        // if the clicked element does not correspond to a SmartClient widget,
        // then perform the default SmartClient click operation
        if(canvas == null) {
            this.orig_doMouseUp(locator, eventParams);
            return;
        }
        LOG.debug("Located canvas " + canvas + " for locator " + locator);

        var coords = this.getSCLocatorCoords(autWindow, locator);
        if (coords == null) return;
        
        var clientX = coords[0];
        var clientY = coords[1];
  
        LOG.debug("clientX = " + clientX + ", clientY=" + clientY);

        // fire mouseUp and click to trigger a SmartClient click event
        // We should have already fired mouseDown
        this.browserbot.triggerMouseEvent(element, "mouseup", true, clientX, clientY);
        this.browserbot.clickElement(element);
        
    } else {
        this.orig_doMouseUp(locator, eventParams);
    }
};


Selenium.prototype.orig_doDoubleClick = Selenium.prototype.doDoubleClick;

Selenium.prototype.doDoubleClick = function(locator, eventParams)
{
    LOG.info("Locator in doDoubleClick : " + locator);
    var element = this.page().findElement(locator);
    
    if(this.hasSC()) {
        var autWindow = this.getAutWindow();
        var canvas = autWindow.isc.AutoTest.locateCanvasFromDOMElement(element);
        // if the clicked element does not correspond to a SmartClient widget,
        // then perform the default SmartClient doubleclick operation
        if(canvas == null) {
            this.orig_doDoubleClick(locator, eventParams);
            return;
        }
        LOG.debug("Located canvas " + canvas + " for locator " + locator);
        var coords = this.getSCLocatorCoords(autWindow, locator);
        if (coords == null) return;
        var clientX = coords[0];
        var clientY = coords[1];

        LOG.debug("clientX = " + clientX + ", clientY=" + clientY);

        //fire a sequence of events to trigger a SmartClient doubleclick event
        this.browserbot.triggerMouseEvent(element, "mouseover", true, clientX, clientY);
        this.browserbot.triggerMouseEvent(element, "mousedown", true, clientX, clientY);
        this.browserbot.triggerMouseEvent(element, "mouseup", true, clientX, clientY);
        this.browserbot.clickElement(element);
        this.browserbot.triggerMouseEvent(element, "mousedown", true, clientX, clientY);
        this.browserbot.triggerMouseEvent(element, "mouseup", true, clientX, clientY);
        this.browserbot.clickElement(element);

    } else {
        this.orig_doDoubleClick(locator, eventParams);
    }
};

Selenium.prototype.orig_doContextMenu = Selenium.prototype.doContextMenu;

Selenium.prototype.doContextMenu = function(locator, eventParams)
{
    LOG.info("Locator in doContextMenu : " + locator);
    var element = this.page().findElement(locator);
    if(this.hasSC()) {
        var autWindow = this.getAutWindow();
        var canvas = autWindow.isc.AutoTest.locateCanvasFromDOMElement(element);
        if(canvas == null) {
            this.orig_doContextMenu(locator, eventParams);
            return;
        }
        LOG.debug("Located canvas " + canvas + " for locator " + locator);

        var coords = this.getSCLocatorCoords(autWindow, locator);
        var clientX = coords[0];
        var clientY = coords[1];

        LOG.debug("clientX = " + clientX + ", clientY=" + clientY);
        this.browserbot.triggerMouseEvent(element, "contextmenu", true, clientX, clientY);
    } else {
        this.orig_doContextMenu(locator, eventParams);
    }
};


Selenium.prototype.hasSC = function() {
    var autWindow = this.browserbot.getUserWindow();
    if (autWindow.targetWindow != null) autWindow = autWindow.targetWindow;
    return autWindow.isc != null;
};

Selenium.prototype.isSCLocator = function(locator) {
    if (!this.hasSC()) return false;
    return locator && (locator.substring(0, "scLocator".length) == "scLocator" ||
                       locator.substring(0, "scID".length)      == "scID");
};

// append the query string to the URL; set sc_selenium to true
Selenium.prototype.appendScSeleniumQueryToURL = function (url) {
    var index, baseUrl = url, fragment = "";

    if ((index = url.indexOf("#")) >= 0) {
        fragment = url.substring(index);
        baseUrl = url.substring(0,index);
    }

    if ((index = baseUrl.indexOf("?")) >= 0) baseUrl += "&sc_selenium=true";
    else                                     baseUrl += "?sc_selenium=true";

    return baseUrl + fragment;
};

Selenium.prototype.orig_getTable = Selenium.prototype.getTable;

Selenium.prototype.getTable = function(tableCellAddress) {
/**
 * Gets the text from a cell of a table. The cellAddress syntax
 * tableLocator.row.column, where row and column start at 0.
 *
 * @param tableCellAddress a cell address, e.g. "foo.1.4"
 * @return string the text from the specified cell
 */

    if(this.hasSC()) {
        // This regular expression matches "tableName.row.column"
        // For example, "mytable.3.4"
        var pattern = /(.*)\.(\d+)\.(\d+)/;
        if (!pattern.test(tableCellAddress)) {
            throw new SeleniumError("Invalid target format. Correct format is " +
                                    "tableLocator.rowNum.columnNum");
        }

        var pieces = tableCellAddress.match(pattern),
            tableName = pieces[1],
            row = pieces[2],
            col = pieces[3];

        var autWindow = this.getAutWindow(),
            element = this.browserbot.findElement(tableName),
            listGrid = autWindow.isc.AutoTest.locateCanvasFromDOMElement(element);

        if (listGrid != null) {
            //the locator can return a GridBody
            if(listGrid.grid) listGrid = listGrid.grid;

            if (autWindow.isc.isA.Function(listGrid.getCellValue)) {
                LOG.debug("Found ListGrid " + listGrid.getClassName());

                var record = listGrid.getRecord(Number(row));
                LOG.debug("Record for row " + row + " is " + record);
                return listGrid.getCellValue(record, row, col);
            }
        }
    }
    return this.orig_getTable(tableCellAddress);
};

Selenium.prototype.orig_doMouseOver = Selenium.prototype.doMouseOver;

Selenium.prototype.doMouseOver = function(locator, eventParams) {
    /**
   * Simulates a user hovering a mouse over the specified element.
   *
   * @param locator an <a href="#locators">element locator</a>
   */

    LOG.info("Locator in doMouseOver : " + locator);
    var element = this.page().findElement(locator);
    if(this.hasSC()) {
        var autWindow = this.getAutWindow();
        var canvas = autWindow.isc.AutoTest.locateCanvasFromDOMElement(element);
        if(canvas == null) {
            this.orig_doMouseOver(locator);
            return;
        }
        LOG.debug("Located canvas " + canvas + " for locator " + locator);

        var coords = this.getSCLocatorCoords(autWindow, locator);
        var clientX = coords[0];
        var clientY = coords[1];

        LOG.debug("clientX = " + clientX + ", clientY=" + clientY);
        this.browserbot.triggerMouseEvent(element, "mouseover", true, clientX, clientY);
    } else {
        this.orig_doMouseOver(locator);
    }

};


Selenium.prototype.orig_doMouseMove = Selenium.prototype.doMouseMove;

Selenium.prototype.doMouseMove = function(locator, eventParams) {
    /**
   * Simulates a user hovering a mouse over the specified element.
   *
   * @param locator an <a href="#locators">element locator</a>
   */

    LOG.info("Locator in doMouseMove : " + locator);
    var element = this.page().findElement(locator);
    if(this.hasSC()) {
        var autWindow = this.getAutWindow();
        var canvas = autWindow.isc.AutoTest.locateCanvasFromDOMElement(element);
        if(canvas == null) {
            this.orig_doMouseMove(locator);
            return;
        }
        LOG.debug("Located canvas " + canvas + " for locator " + locator);

        var coords = this.getSCLocatorCoords(autWindow, locator);
        var clientX = coords[0];
        var clientY = coords[1];

        LOG.debug("clientX = " + clientX + ", clientY=" + clientY);
        autWindow.isc.EH.immediateMouseMove = true;
        this.browserbot.triggerMouseEvent(element, "mousemove", true, clientX, clientY);
        autWindow.isc.EH.immediateMouseMove = null;
    } else {
        this.orig_doMouseMove(locator);
    }

};

// Override drag and drop for SC components
Selenium.prototype.orig_doDragAndDrop = Selenium.prototype.doDragAndDrop;
Selenium.prototype.doDragAndDrop = function (locator, eventParams) {
    var element = this.page().findElement(locator);
    if (this.isSCLocator(locator)) {
        var autWindow = this.getAutWindow();
      
        var canvas = autWindow.isc.AutoTest.locateCanvasFromDOMElement(element);
        // if the clicked element does not correspond to a SmartClient widget,
        // then perform the default SmartClient click operation
        if(canvas == null) {
            this.orig_doDragAndDrop(locator, eventParams);
            return;
        }
        LOG.debug("Located canvas " + canvas + " for locator " + locator);

        var coords = this.getSCLocatorCoords(autWindow, locator);
        if (coords == null) return;
        var clientX = coords[0];
        var clientY = coords[1];
  
      
        LOG.debug("clientX = " + clientX + ", clientY=" + clientY);

        autWindow.isc.EH.immediateMouseMove = true;

        // fire mouseover / mouseDown / mousemove at original coordinates
        this.browserbot.triggerMouseEvent(element, "mouseover", true, clientX, clientY);
        this.browserbot.triggerMouseEvent(element, "mousedown", true, clientX, clientY);
        this.browserbot.triggerMouseEvent(element, "mousemove", true, clientX, clientY);
        // now trigger mousemove and mouseup at drop coordinates
        // eventParams should contain offset as string like "+100,-25"
        var delta = eventParams.split(",");
        clientX += parseInt(delta[0]);
        clientY += parseInt(delta[1]);
        
        this.browserbot.triggerMouseEvent(element, "mousemove", true, clientX, clientY);
        this.browserbot.triggerMouseEvent(element, "mouseup", true, clientX, clientY);
        
        autWindow.isc.EH.immediateMouseMove = null;

    } else {
        this.orig_doDragAndDrop(locator, eventParams);
    }
};

Selenium.prototype.orig_doDragAndDropToObject = Selenium.prototype.dragAndDropToObject;
Selenium.prototype.doDragAndDropToObject = function (locator, targetLocator) {
    var element = this.page().findElement(locator);
    if (this.isSCLocator(locator)) {
        var autWindow = this.getAutWindow(),
            isc = autWindow.isc,
            canvas = isc.AutoTest.locateCanvasFromDOMElement(element);
        // if the clicked element does not correspond to a SmartClient widget,
        // then perform the default SmartClient click operation
        if(canvas == null) {
            this.orig_doDragAndDropToObject(locator, targetLocator);
            return;
        }
        LOG.debug("Located canvas " + canvas + " for locator " + locator);
        var coords = this.getSCLocatorCoords(autWindow, locator);
        if (coords == null) return;

        var clientX = coords[0];
        var clientY = coords[1];
  
        LOG.debug("clientX = " + clientX + ", clientY=" + clientY);

        isc.EH.immediateMouseMove = true;

        // fire mouseover / mouseDown / mousemove at original coordinates
        this.browserbot.triggerMouseEvent(element, "mouseover", true, clientX, clientY);
        this.browserbot.triggerMouseEvent(element, "mousedown", true, clientX, clientY);
        this.browserbot.triggerMouseEvent(element, "mousemove", true, clientX, clientY);
        // now trigger mousemove and mouseup at drop coordinates
        
        var dropElement = this.page().findElement(targetLocator);
        var isSCTarget = targetLocator.indexOf("scLocator") != -1;
        if (isSCTarget) {
            var targetCoords = this.getSCLocatorCoords(autWindow, targetLocator);
            if (targetCoords != null) coords = targetCoords;
            // if target is GridRenderer, bias drop coordinates downward within the
            // DOM element to ensure deterministic behavior when dropping on row
            var canvas = isc.AutoTest.locateCanvasFromDOMElement(dropElement);
            if (isc.isA.GridRenderer(canvas)) coords[1] += dropElement.offsetHeight/4;
        } else {
            // In this case we've got a drag from a SmartClient component to
            // some arbitrary element on the page.
            var targetLeft = isc.Element.getLeftOffset(dropElement);
            var targetTop = isc.Element.getTopOffset(dropElement);
            coords = [targetLeft,targetTop];
        }

        this.browserbot.triggerMouseEvent(dropElement, "mouseover", true, coords[0], coords[1]);
        this.browserbot.triggerMouseEvent(dropElement, "mousemove", true, coords[0], coords[1]);
        this.browserbot.triggerMouseEvent(dropElement, "mouseup", true, coords[0], coords[1]);
        
        isc.EH.immediateMouseMove = null;

    } else {
        this.orig_doDragAndDropToObject(locator, targetLocator);
    }
    
};

Selenium.prototype.getSCLocatorCoords = function (autWindow, scLocator) {
    if (scLocator.indexOf("scLocator=") != -1) {
        scLocator = scLocator.substring("scLocator=".length);
        var coords = autWindow.isc.AutoTest.getPageCoords(scLocator);
        LOG.debug("Determining page coordinates for SC Locator:" + scLocator + ": " + coords);
        return coords;
    } else if (scLocator.indexOf("scID=") != -1) {
        var ID = scLocator.substring("scID=".length);
        var canvas = autWindow[ID];
        if (canvas != null && autWindow.isc.isA.Canvas(canvas) &&
            canvas.isDrawn() && canvas.isVisible()) 
        {
            var left = canvas.getPageLeft() + parseInt(canvas.getVisibleWidth()/2),
                top = canvas.getPageTop() + parseInt(canvas.getVisibleHeight()/2);
            LOG.debug("Determining page coordinates for SC canvas:" + ID + ": " + [left,top]);
            return [left,top];
        }
    }
    LOG.debug("Unable to determine page coordinates for SC Locator:" + scLocator);
    return null;
};

Selenium.prototype.isElementClickable = function (locator) {
    LOG.info("Located in isScElementClickable : " + locator);

    var isElementPresent = this.isElementPresent(locator);

    // if not present at all, report null
    if (!isElementPresent) return null;

    // not an SmartClient Locator, report null
    if (!this.isSCLocator(locator)) return null;

    // otherwise, run SmartClient verifications
    var autWindow = this.getAutWindow(),
        element = this.page().findElement(locator);
    
    return autWindow.isc.AutoTest.isElementClickable(element);
};

Selenium.prototype.doSetImplicitNetworkWait = function (waitArg) {
    var autWindow = this.getAutWindow();
    waitArg = waitArg != null ? waitArg.toLowerCase().replace(/^\s+|\s+$/g, "") : "";
    autWindow.isc.AutoTest.implicitNetworkWait = waitArg == "" || waitArg == "true";
};

Selenium.prototype.isGridDone = function (locator) {

    LOG.info("Located in isScGridDone : " + locator);

    // not an SmartClient Locator, report null
    if (!this.isSCLocator(locator)) return null;

    // otherwise, run SmartClient verifications
    var autWindow = this.getAutWindow(),
        element = this.page().findElement(locator);

    return autWindow.isc.AutoTest.isGridDone(element);
};

Selenium.prototype.orig_doOpen = Selenium.prototype.doOpen;

Selenium.prototype.doOpen = function(url, ignoreResponseCode) {
    if (this.use_url_query_sc_selenium) url = this.appendScSeleniumQueryToURL(url);
    return this.orig_doOpen(url, ignoreResponseCode);
};

Selenium.prototype.orig_doOpenWindow = Selenium.prototype.doOpenWindow;

Selenium.prototype.doOpenWindow = function(url, windowID) {
    if (this.use_url_query_sc_selenium) url = this.appendScSeleniumQueryToURL(url);
    return this.orig_doOpenWindow(url, windowID);
};

// We override Selenium.prototype.getValue() to provide a meaningful JS value that's
// based on the SC widget containing the locator.  We don't provide an override for 
// Selenium.prototype.getText() because the native version works just as well on
// DOM elements in SC widgets as on any other DOM element, returning any contained text.

Selenium.prototype.orig_getValue = Selenium.prototype.getValue;

Selenium.prototype.getValue = function (locator) {

    LOG.info("Located in getSCValue : " + locator); 

    if (!this.isSCLocator(locator)) return this.orig_getValue(locator);

    // use SmartClient custom locator strategies at this point
    return this.browserbot.locateValueByScLocatorOrScID(locator);
};

// The following wrappers are required for Selenium IDE to work properly with
// modifier key commands such as controlKeyDown() while prototyping Selenium
// scripts.  In Selenium IDE, a new browserbot can be created each time a
// command is executed, so that simply retrieving modifier key state from the
// browserbot won't work.  A workaround is to retrieve it from core.events

Selenium.prototype.orig_doKeyDown = Selenium.prototype.doKeyDown;

Selenium.prototype.doKeyDown = function(locator, keySequence) {
    this.browserbot.shiftKeyDown = core.events.shiftKeyDown_;
    this.browserbot.controlKeyDown = core.events.controlKeyDown_;
    this.orig_doKeyDown(locator, keySequence);
};

Selenium.prototype.orig_doKeyPress = Selenium.prototype.doKeyPress;

Selenium.prototype.doKeyPress = function(locator, keySequence) {
    this.browserbot.shiftKeyDown = core.events.shiftKeyDown_;
    this.browserbot.controlKeyDown = core.events.controlKeyDown_;
    this.orig_doKeyPress(locator, keySequence);
};

Selenium.prototype.orig_doKeyUp = Selenium.prototype.doKeyUp;

Selenium.prototype.doKeyUp = function(locator, keySequence) {
    this.browserbot.shiftKeyDown = core.events.shiftKeyDown_;
    this.browserbot.controlKeyDown = core.events.controlKeyDown_;
    this.orig_doKeyUp(locator, keySequence);
};

// When Selenium is launched using the HTMLLauncher class, override HtmlTestSuite to 
// collect and return the ISC Developer Console messages along with the test results.
// HtmlTestSuite will not be present when this file is loaded into Selenium IDE.

if (typeof HtmlTestSuite !== 'undefined') {

    HtmlTestSuite.prototype.orig_onTestSuiteComplete = 
        HtmlTestSuite.prototype._onTestSuiteComplete;

    HtmlTestSuite.prototype._onTestSuiteComplete = function () {
        var location = frames[0] && frames[0].location ? frames[0].location : "";
        if (location.toString().match(/[?&]addMessages\=true/)) {
            var messages = selenium.browserbot.getCurrentWindow().isc.Log.getMessages();
            LOG.pendingMessages.push({type: "ISC_DEVELOPER_MESSAGES", 
                                      msg: messages.join('\n')});
        }
        this.orig_onTestSuiteComplete();
    };

}

// Override HtmlTestRunner class so that the test is run in a maximized browser
// if requested from the TestRunner Java Framework.  This may enhance the usefulness
// of the capture screenshot capability.

if (typeof HtmlTestRunner !== 'undefined') {

    HtmlTestRunner.prototype.orig_startTestSuite = 
        HtmlTestRunner.prototype.startTestSuite;

    HtmlTestRunner.prototype.startTestSuite = function () {
        var location = frames[0] && frames[0].location ? frames[0].location : "";
        if (location.toString().match(/[?&]maximize\=true/)) {
            // the AUT (Application under Test) window contains the SmartClient code
            var autWindow = selenium.browserbot.getUserWindow();
            if (autWindow != null) {
                autWindow.moveTo(0,0);
                autWindow.resizeTo(screen.width, screen.height);
            }
        }
        this.orig_startTestSuite();
    };

}
