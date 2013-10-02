package org.data2semantics.yasgui.selenium;

/*
 * #%L
 * YASGUI
 * %%
 * Copyright (C) 2013 Laurens Rietveld
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import static org.junit.Assert.assertFalse;

import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

public class SeleniumHelper {
	
	private Selenium selenium;
	private HttpCommandProcessor proc;

	public SeleniumHelper(Selenium selenium, HttpCommandProcessor proc) {
		this.selenium = selenium;
		this.proc = proc;
	}
	
    public String execCommand(String command, String... args) {
    	return proc.doCommand(command, args);
    }
    
    
    public void waitForElementClickable(String locator) {
    	execCommand("waitForElementClickable", locator);
    }
    public boolean isElementPresent(String locator) {
    	boolean present = true;
    	try {
    		execCommand("verifyElementPresent", locator);
    	} catch (SeleniumException e) {
    		present = false;
    	}
    	return present;
    }
    

    public void waitAndClickElementClickable(String locator) {
        waitForElementClickable(locator);
        selenium.click(locator);
        assertNoErrorWindow();
    }
    
    public void assertNoErrorWindow() {
    	assertFalse("Error window found", isElementPresent(Locators.ERROR_WINDOW.get()));
    }
    
    public String getErrorWindowMessage() {
    	return selenium.getText(Locators.ERROR_WINDOW_MESSAGE.get());
    }
    
    public void waitAndClickIcon(String contains) {
    	waitForElementClickable(Locators.ICON.get(contains));
    	clickIcon(contains);
        
    }
    
    
    public void clickIcon(String contains) {
    	execCommand("clickAt", Locators.ICON.get(contains), "10,10");
    	assertNoErrorWindow();
    }
    public void clickMenuItem(String text) {
    	String selector = Locators.MENU_ITEM.get(text);
    	execCommand("mouseOver", selector);
    	execCommand("mouseDownAt", selector, "10,10");
    	execCommand("mouseUpAt", selector, "10,10");
    }

}
