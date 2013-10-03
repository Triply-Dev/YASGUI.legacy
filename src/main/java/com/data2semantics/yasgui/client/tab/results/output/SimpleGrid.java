package com.data2semantics.yasgui.client.tab.results.output;

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

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.tab.results.input.ResultsHelper;
import com.data2semantics.yasgui.client.tab.results.input.SparqlResults;
import com.data2semantics.yasgui.shared.Prefix;
import com.smartgwt.client.util.StringUtil;
import com.smartgwt.client.widgets.HTMLPane;

public class SimpleGrid extends HTMLPane {
	private View view;
	private HashMap<String, Prefix> queryPrefixes = new HashMap<String, Prefix>();
	private ArrayList<String> variables;
	private ArrayList<HashMap<String, HashMap<String, String>>> solutions;
	private String html = "";
	private static int ROLL_OVER_ICON_SIZE = 12;

	public SimpleGrid(View view, SparqlResults sparqlResults) {
		this.view = view;
		setWidth100();
		setHeight100();
		queryPrefixes = view.getSelectedTab().getQueryTextArea().getPrefixHashMap();
		variables = sparqlResults.getVariables();
		solutions = sparqlResults.getBindings();
		drawTable();
		setContents(html);
		
	}

	private void drawTable() {
		html += "<table class=\"simpleTable\">";
		drawHeader();
		drawRows();

		html += "</table>";
	}

	private void drawHeader() {
		html += "<thead><tr class=\"simpleTable\">";
		html += "<th>#</th>";
		for (String variable: variables) {
			html += "<th>" + StringUtil.asHTML(variable) + "</th>";
		}
		html += "</tr></thead>";
	}

	private void drawRows() {
		html += "<tbody>";
		int rowId = 1;
		for (HashMap<String, HashMap<String, String>> bindings: solutions) {
			html += "<tr>";
			html += "<td>" + Integer.toString(rowId) + "</td>";
			for (String variable: variables) {
				if (bindings.containsKey(variable)) {
					HashMap<String, String> binding = bindings.get(variable);
					if (ResultsHelper.valueIsUri(binding)) {
						html += "<td class=\"snorqlTdWrapper\"><div>";
						if (view.getSettings().useUrlAsSnorql()) {
							html += ResultsHelper.getSnorqlHrefLink(binding, queryPrefixes);
							html += getImgLink(binding.get("value"), true);
						} else {
							html += ResultsHelper.getRegularHrefLink(binding, queryPrefixes);
							html += getImgLink(binding.get("value"), false);
						}
						html += "</div></td>";
					} else if (ResultsHelper.valueIsLiteral(binding)) {
						html += "<td>";
						html += ResultsHelper.getLiteralFromBinding(binding);
						html += "</td>";
					} else {
						html += "<td>";
						html += StringUtil.asHTML(binding.get("value"));
						html += "</td>";
					}
				} else {
					html += "<td>&nbsp;</td>";
				}
			}
			html += "</tr>";
			rowId++;
		}
		html += "</tbody>";
	}
	
	
	private String getImgLink(String url, boolean regularHref) {
		String html = "<div style=\"height:100%;position:absolute;top:0px;right:" + (ROLL_OVER_ICON_SIZE/2) + "px;\">";
		String divContentStyle = "position:absolute;top:50%;margin-top:-" + (ROLL_OVER_ICON_SIZE/2) + "px;";
		if (regularHref) {
			html += "<a style=\"" + divContentStyle + "\" href=\"" + url + "\" target=\"_blank\" class=\"extResourceLink\">";
			html += "<img src=\"" + Imgs.OTHER_IMAGES_DIR.getUnprocessed() + Imgs.EXTERNAL_LINK.get() +"\" height=\"" + ROLL_OVER_ICON_SIZE + "\" width=\"" + ROLL_OVER_ICON_SIZE + "\"/></a>";
		} else {
			html += "<img onclick=\"queryForResource('" + url +  "');\" class=\"extResourceLink\" style=\"cursor:pointer;" + divContentStyle + "\" src=\"" + Imgs.OTHER_IMAGES_DIR.getUnprocessed() + Imgs.INTERNAL_LINK.get() +"\" height=\"" + ROLL_OVER_ICON_SIZE + "\" width=\"" + ROLL_OVER_ICON_SIZE + "\"/>";
		}
		html += "</div>";
		return html;
	}
}
