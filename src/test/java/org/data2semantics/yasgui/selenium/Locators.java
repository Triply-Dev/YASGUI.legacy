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

public enum Locators {
	ERROR_WINDOW_MESSAGE("scLocator=//Window[ID=\"errorWindow\"]/item[0][Class=\"Label\"]/"),
	ERROR_WINDOW("scLocator=//Window[ID=\"errorWindow\"]"),
	MENU_BUTTON_TEXT("//span[contains(text(), \"#$#$\")]"),
	DIV_TEXT("//div[contains(text(), \"#$#$\")]"),
	MENU_ITEM("//nobr[contains(text(), \"#$#$\")]"),
	ICON("//img[contains(@src,'#$#$')]"),
	TOOLTIPS_VISIBLE("//div[contains(@class,'qtip') and contains(@style, 'display: block')]");
	
	private String locator;
	private static String REPLACE = "#$#$";
	private Locators(String locator) {
		this.locator = locator;
	}
	
	public String get() {
		return this.locator;
	}
	public String get(String replacement) {
		return this.locator.replace(REPLACE, replacement);
	}
}
