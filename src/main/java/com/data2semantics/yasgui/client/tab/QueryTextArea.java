package com.data2semantics.yasgui.client.tab;

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

import java.util.HashMap;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.helpers.TooltipProperties;
import com.data2semantics.yasgui.client.settings.TooltipText;
import com.data2semantics.yasgui.shared.Prefix;
import com.data2semantics.yasgui.shared.exceptions.ElementIdException;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;

public class QueryTextArea extends HTMLPane {
	private static String PREFIX_PATTERN = "\\s*PREFIX\\s*(\\w*):\\s*<(.*)>\\s*$";
	private static final int TOOLTIP_VERSION_KEYBOARD_SHORTCUTS = 1;
	private static final int TOOLTIP_VERSION_PREFIX = 1;
	private View view;
	private static String APPEND_INPUT_ID = "_queryInput";
	private String inputId;
	private QueryTab tab;
	private static int HEIGHT = 306; //just a little bit higher than the min-length property of codemirror in css (300)
	
	public QueryTextArea(final View view, QueryTab tab) {
		this.tab = tab;
		this.view = view;
		this.inputId = tab.getID() + APPEND_INPUT_ID;
		setHeight(Integer.toString(HEIGHT) + "px");
		setCanDragResize(true);
		setDragAppearance(DragAppearance.OUTLINE);
		setStyleName("queryInputContainer");
		addResizedHandler(new ResizedHandler(){
			@Override
			public void onResized(ResizedEvent event) {
				try {
					adjustForContent(true);
				} catch (Exception e) {
					//on first load, event.getY is null. Just ignore
				}
				
		}});
		setTextArea();
	}
	public void showTooltips(int fromVersionId) throws ElementIdException {
		showPrefixTooltip(fromVersionId);
		showKeyShortcutsTooltip(fromVersionId);
	}
	
	private void showPrefixTooltip(int fromVersionId) throws ElementIdException {
		if (fromVersionId < TOOLTIP_VERSION_PREFIX) {
			TooltipProperties tProp = new TooltipProperties();
			tProp.setId(getDOM().getId());
			tProp.setContent(TooltipText.QUERY_PREFIXES_AUTOCOMPLETE);
			tProp.setMy(TooltipProperties.POS_TOP_LEFT);
			tProp.setAt(TooltipProperties.POS_TOP_LEFT);
			tProp.setXOffset(240);
			tProp.setYOffset(50);
			Helper.drawTooltip(tProp);
		}
	}
	
	private void showKeyShortcutsTooltip(int fromVersionId) throws ElementIdException {
		if (fromVersionId < TOOLTIP_VERSION_KEYBOARD_SHORTCUTS) {
			TooltipProperties tProp = new TooltipProperties();
			tProp.setId(getDOM().getId());
			tProp.setContent(TooltipText.QUERY_KEYBOARD_SHORTCUTS);
			tProp.setMy(TooltipProperties.POS_CENTER);
			tProp.setAt(TooltipProperties.POS_RIGHT_BOTTOM);
			tProp.setXOffset(-150);
			tProp.setYOffset(-110);
			Helper.drawTooltip(tProp);
		}
	}
	
	public void setTextArea() {
		setContents("<textarea " + "id=\"" + getInputId() + "\"" + ">" + tab.getTabSettings().getQueryString() + "</textarea>");
	}
	
	public String getQuery() {
		JsMethods.saveCodeMirror(getInputId());
		return JsMethods.getValueUsingId(inputId);
	}
	
	public String getInputId() {
		return this.inputId;
	}

	public void setQuery(String queryString) {
		JsMethods.setCodemirrorContent(this.inputId, queryString);
	}
	public void queryForResource(String resource) {
		String query = "";
		HashMap<String, Prefix> prefixes = getPrefixHashMap();
		for (Prefix prefix: prefixes.values()) {
			query += prefix.toString() + "\n";
		}
		if (prefixes.size() > 0) query += "\n";
		query += "SELECT ?property ?hasValue ?isValueOf\n" +
				"WHERE {\n" + 
				"	{ <" + resource + "> ?property ?hasValue }\n" +
				"	UNION\n" + 
				"	{ ?isValueOf ?property <" + resource + "> }\n" +
				"}";
		setQuery(query);
		view.getSelectedTabSettings().setQueryString(query);
		LocalStorageHelper.storeSettingsInCookie(view.getSettings());
		view.getElements().executeQuery();
	}
	
	/**
	 * Checks to query string and retrieves/stores all defined prefixes in an object variable
	 */
	public HashMap<String, Prefix> getPrefixHashMap() {
		HashMap<String, Prefix> queryPrefixes = new HashMap<String, Prefix>();
		RegExp regExp = RegExp.compile(PREFIX_PATTERN, "gm");
		while (true) {
			MatchResult matcher = regExp.exec(getQuery());
			if (matcher == null)
				break;
			queryPrefixes.put(matcher.getGroup(2), new Prefix(matcher.getGroup(1), matcher.getGroup(2)));
		}
		return queryPrefixes;
	}
}
