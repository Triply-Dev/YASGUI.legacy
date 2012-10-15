/**
 * Copyright 2011 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.data2semantics.yasgui.client.helpers;

import java.util.ArrayList;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.user.client.Window;

/**
 * Default {@link GoogleAnalytics} implementation that uses JSNI to expose Google Analytics javascript methods.
 * 
 * @author Christian Goudreau
 */
public class GoogleAnalytics {
	
	public static String UID = "UA-35569470-1";
	public static void init(String userAccount) {

		Element firstScript = Document.get().getElementsByTagName("script").getItem(0);

		ScriptElement config = Document.get().createScriptElement(
				"var _gaq = _gaq || [];_gaq.push(['_setAccount', '" + userAccount + "']);_gaq.push(['_trackPageview']);");

		firstScript.getParentNode().insertBefore(config, firstScript);

		ScriptElement script = Document.get().createScriptElement();

		// Add the google analytics script.
		script.setSrc(("https:".equals(Window.Location.getProtocol()) ? "https://ssl" : "http://www") + ".google-analytics.com/ga.js");
		script.setType("text/javascript");
		script.setAttribute("async", "true");

		firstScript.getParentNode().insertBefore(script, firstScript);
	}

	public static native void addAccount(String trackerName, String userAccount) /*-{
		$wnd._gaq.push([ '" + trackerName + "._setAccount',
				'" + userAccount + "' ]);
	}-*/;

	public static native void trackPageview(String pageName) /*-{
		if (!pageName.match("^/") == "/") {
			pageName = "/" + pageName;
		}

		$wnd._gaq.push([ '_trackPageview', pageName ]);
	}-*/;

	public static native void trackPageview() /*-{
		$wnd._gaq.push([ '_trackPageview' ]);
	}-*/;

	public static native void trackPageview(String trackerName, String pageName) /*-{
		if (!pageName.match("^/") == "/") {
			pageName = "/" + pageName;
		}

		$wnd._gaq.push([ '" + trackerName + "._trackPageview', pageName ]);
	}-*/;
	
	public static void trackEvents(GoogleAnalyticsEvent... events) throws IllegalArgumentException {
		if (events.length > 0) {
			ArrayList<String> categories = new ArrayList<String>();
			ArrayList<String> actions = new ArrayList<String>();
			ArrayList<String> optLabels = new ArrayList<String>();
			ArrayList<Integer> optValues = new ArrayList<Integer>();
			for (GoogleAnalyticsEvent event: events) {
				if (event.getCategory() == null || event.getAction() == null) {
					//at least require this
					throw new IllegalArgumentException("No category or action passed for google analytics event");
				}
				categories.add(event.getCategory());
				actions.add(event.getAction());
				optLabels.add(event.getOptLabel());
				optValues.add(event.getOptValue());
			}
			trackEvents(categories.toArray(new String[categories.size()]), actions.toArray(new String[actions.size()]), optLabels.toArray(new String[optLabels.size()]), optValues.toArray(new Integer[optValues.size()]));
		}
	}

	private static native void trackEvent(String category, String action) /*-{
		$wnd._gaq.push([ '_trackEvent', category, action ]);
	}-*/;

	private static native void trackEvent(String category, String action, String optLabel) /*-{
		$wnd._gaq.push([ '_trackEvent', category, action, optLabel ]);
	}-*/;

	private static native void trackEvent(String category, String action, String optLabel, int optValue) /*-{
		
		$wnd._gaq.push([ '_trackEvent', category, action, optLabel, optValue ]);
	}-*/;

	private static native void trackEvent(String category, String action, String optLabel, int optValue, boolean optNonInteraction) /*-{
		$wnd._gaq.push([ '_trackEvent', category, action, optLabel, optValue,
				optNonInteraction ]);
	}-*/;
	
	
	private static native void trackEvents(String[] category, String[] action, String[] optLabel, Integer[] optValue) /*-{
		if (category.length == action.length && action.length == optLabel.length && optLabel.length == optValue.length) {
			commands = [];
			for (i = 0; i < category.length; i++) {
				if (optValue[i] != null && optValue[i] != 0) {
					commands.push([ '_trackEvent', category[i], action[i], optLabel[i], optValue[i] ]);
				} else if (optLabel[i] != null && optLabel[i] != "") {
					commands.push([ '_trackEvent', category[i], action[i], optLabel[i] ]);
				} else {
					commands.push([ '_trackEvent', category[i], action[i] ]);
				}
				commands.push([ '_trackEvent', category[i], action[i], optLabel[i], optValue[i] ]);
			}
			$wnd._gaq.push.apply($wnd._gaq.push, commands);
		} else {
			throw new Error("Unequal set of arguments for tracking events: " + category.length + " - " + action.length + " - " + optLabel.length + " - " + optValue.length);
		}
	}-*/;
	

	private static native void trackEventWithTracker(String trackerName, String category, String action) /*-{
		$wnd._gaq.push([ '" + trackerName + "._trackEvent', category, action ]);
	}-*/;

	private static native void trackEventWithTracker(String trackerName, String category, String action, String optLabel) /*-{
		$wnd._gaq.push([ '" + trackerName + "._trackEvent', category, action,
				optLabel ]);
	}-*/;

	private static native void trackEventWithTracker(String trackerName, String category, String action, String optLabel, int optValue) /*-{
		$wnd._gaq.push([ '" + trackerName + "._trackEvent', category, action,
				optLabel, optValue ]);
	}-*/;

	private static native void trackEventWithTracker(String trackerName, String category, String action, String optLabel, int optValue,
			boolean optNonInteraction) /*-{
										$wnd._gaq.push([ '" + trackerName + "._trackEvent', category, action, optLabel, optValue, optNonInteraction ]);
										}-*/;
}
