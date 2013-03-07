/*******************************************************************************
 * Copyright (c)  2012 Laurens Rietveld
 * 
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.data2semantics.yasgui.client.helpers;

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

import java.util.ArrayList;
import java.util.HashMap;

import com.data2semantics.yasgui.client.tab.optionbar.QueryConfigMenu;
import com.data2semantics.yasgui.shared.Prefix;
import com.data2semantics.yasgui.shared.exceptions.ElementIdException;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class Helper {
	private static String CRAWL_USER_AGENTS = "googlebot|msnbot|baidu|curl|wget|Mediapartners-Google|slurp|ia_archiver|Gigabot|libwww-perl|lwp-trivial|bingbot";
	private static String PREFIX_PATTERN = "\\s*PREFIX\\s*(\\w*):\\s*<(.*)>\\s*$";
	/**
	 * Implode arraylist into string
	 * 
	 * @param arrayList ArrayList to implode
	 * @param glue Glue (separator) for the seting
	 * @return concatenated arraylist
	 */
	public static String implode(ArrayList<String> arrayList, String glue) {
		String result = "";
		for (String stringItem : arrayList) {
			if (result.length() > 0) {
				result += glue;
			}
			result += stringItem;
		}
		return result;
	}
	
	/**
	 * SmartGWT does have a link element, but this requires a form. Use this object to mimic a link by creating a label object
	 * 
	 * @param message Text of link
	 * @param handler Clickhandler: what to do on onclick
	 * @return Label
	 */
	public static Label getLink(String message, ClickHandler handler) {
	   Label link = new Label();
	   link = new Label(message);
	   link.setStyleName("clickable");
	   link.setHeight100();
	   link.setWidth100();
	   link.setCanSelectText(true);
	   link.addClickHandler(handler);
	   return link;

	}
	
	/**
	 * Create a label element which opens a new window for a given url
	 * 
	 * @param message Text of link
	 * @param url Url to open page for
	 * @return Label
	 */
	public static Label getLinkNewWindow(String message, final String url) {
		return getLink(message, new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				Window.open(url, "_blank", null);
			}});
	}
	
	/**
	 * Checks to query string and retrieves/stores all defined prefixes in an object variable
	 */
	public static HashMap<String, Prefix> getPrefixHashMapFromQuery(String query) {
		HashMap<String, Prefix> queryPrefixes = new HashMap<String, Prefix>();
		RegExp regExp = RegExp.compile(PREFIX_PATTERN, "gm");
		while (true) {
			MatchResult matcher = regExp.exec(query);
			if (matcher == null)
				break;
			queryPrefixes.put(matcher.getGroup(2), new Prefix(matcher.getGroup(1), matcher.getGroup(2)));
		}
		return queryPrefixes;
	}
	
	public static String getStackTraceAsString(Throwable e) {
		String stackTraceString = e.getClass().getName() + ": " + e.getMessage();
		for (StackTraceElement ste : e.getStackTrace()) {
			stackTraceString += "\n" + ste.toString();
		}
		return stackTraceString;
	}
	
	public static String getCausesStackTraceAsString(Throwable e) {
		String stackTraceString = "";
		Throwable cause = e.getCause();
		if (cause != null) {
			stackTraceString += "\ncause: " + Helper.getStackTraceAsString(cause);
			stackTraceString += getCausesStackTraceAsString(e.getCause());
		}
		return stackTraceString;
	}
	public static boolean recordIsEmpty(ListGridRecord record) {
		boolean empty = true;
		String[] attributes = record.getAttributes();
		for (String attribute: attributes) {
			if (record.getAttribute(attribute).length() > 0) {
				empty = false;
				break;
			}
		}
		return empty;
	}
	
	public static void drawTooltip(TooltipProperties tProp) throws ElementIdException {
		if (tProp.getId() == null || tProp.getId().length() == 0) {
			throw new ElementIdException("No Id provided to draw tooltip for");
		}
		if (!JsMethods.elementExists(tProp.getId())) {
			throw new ElementIdException("id '" + tProp.getId() + "' not found on page. Unable to draw tooltip");
		}
		JsMethods.drawTooltip(tProp.getId(), tProp.getContent(), tProp.getMy(), tProp.getAt(), tProp.getXOffset(), tProp.getYOffset());
	}
	
	public static String getAcceptHeaders(String mainAccept) {
		String acceptString = mainAccept + "," +
						QueryConfigMenu.CONTENT_TYPE_CONSTRUCT_TURTLE + ";q=0.9," + 
						QueryConfigMenu.CONTENT_TYPE_CONSTRUCT_XML + ";q=0.9," + 
						QueryConfigMenu.CONTENT_TYPE_SELECT_JSON + ";q=0.9," +
						QueryConfigMenu.CONTENT_TYPE_SELECT_XML + ";q=0.9," +
						"*/*;q=0.8";
		return acceptString;
	}
	
	/**
	 * Check whether visitor is a crawler. This way we can avoid the google screenshot containing lots of popups
	 */
	public static boolean isCrawler() {
		String userAgent = JsMethods.getUserAgent();
		RegExp regExp = RegExp.compile(".*(" + CRAWL_USER_AGENTS + ").*");
		MatchResult matcher = regExp.exec(userAgent);
		boolean matchFound = (matcher != null);
		return matchFound;
	}
}
