package com.data2semantics.yasgui.client.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import com.data2semantics.yasgui.shared.Prefix;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class Helper {


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
	public static HashMap<String, Prefix> getPrefixesFromQuery(String query) {
		HashMap<String, Prefix> queryPrefixes = new HashMap<String, Prefix>();
		RegExp regExp = RegExp.compile("^\\s*PREFIX\\s*(\\w*):\\s*<(.*)>\\s*$", "gm");
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
}
