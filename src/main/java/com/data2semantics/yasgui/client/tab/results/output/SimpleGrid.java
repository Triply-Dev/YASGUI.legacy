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
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.tab.results.input.ResultsHelper;
import com.data2semantics.yasgui.client.tab.results.input.SparqlResults;
import com.data2semantics.yasgui.shared.Prefix;
import com.smartgwt.client.util.StringUtil;
import com.smartgwt.client.widgets.HTMLPane;

public class SimpleGrid extends HTMLPane {
	@SuppressWarnings("unused")
	private View view;
	private HashMap<String, Prefix> queryPrefixes = new HashMap<String, Prefix>();
	private ArrayList<String> variables;
	private ArrayList<HashMap<String, HashMap<String, String>>> solutions;
	private String html = "";

	public SimpleGrid(View view, SparqlResults sparqlResults) {
		this.view = view;
		setWidth100();
		setHeight100();
		queryPrefixes = Helper.getPrefixHashMapFromQuery(view.getSettings().getSelectedTabSettings().getQueryString());
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
				html += "<td>";
				if (bindings.containsKey(variable)) {
					HashMap<String, String> binding = bindings.get(variable);
					if (binding.get("type").equals("uri")) {
						html += ResultsHelper.getOpenAsResourceLinkForUri(binding, queryPrefixes);
						html += "&nbsp;";
						html += ResultsHelper.getImageLink(Imgs.OTHER_IMAGES_DIR + Imgs.get(Imgs.EXTERNAL_LINK), binding.get("value"));
					} else if (binding.get("type").equals("literal") || binding.get("type").equals("typed-literal")) {
						html += ResultsHelper.getLiteralFromBinding(binding);
					} else {
						html += StringUtil.asHTML(binding.get("value"));
					}
				} else {
					html += "&nbsp;";
				}
				html += "</td>";
			}
			html += "</tr>";
			rowId++;
		}
		html += "</tbody>";
	}
}
