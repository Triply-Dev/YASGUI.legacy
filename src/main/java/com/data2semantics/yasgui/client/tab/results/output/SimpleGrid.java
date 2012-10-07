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
package com.data2semantics.yasgui.client.tab.results.output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
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
	private String html;

	public SimpleGrid(View view, SparqlResults sparqlResults) {
		this.view = view;
		setWidth100();
		setHeight100();
		queryPrefixes = Helper.getPrefixesFromQuery(view.getSettings().getSelectedTabSettings().getQueryString());
		variables = sparqlResults.getVariables();
		solutions = sparqlResults.getBindings();
		drawTable();
		setContents(html);
		
	}

	private void drawTable() {
		html = "<table class=\"simpleTable\">";
		drawHeader();
		drawRows();

		html += "</table>";
	}

	private void drawHeader() {
		html += "<thead><tr class=\"simpleTable\">";
		for (String variable: variables) {
			html += "<th>" + StringUtil.asHTML(variable) + "</th>";
		}
		html += "</tr></thead>";
	}

	private void drawRows() {
		html += "<tbody>";
		for (HashMap<String, HashMap<String, String>> bindings: solutions) {
			html += "<tr>";
			for (String variable: variables) {
				html += "<td>";
				if (bindings.containsKey(variable)) {
					HashMap<String, String> binding = bindings.get(variable);
					if (binding.get("type").equals("uri")) {
						String uri = binding.get("value");
						html += "<a href=\"" + uri + "\" target=\"_blank\">" + StringUtil.asHTML(getShortUri(uri)) + "</a>";
					} else {
						html += StringUtil.asHTML(binding.get("value"));
					}
				} else {
					html += "&nbsp;";
				}
				html += "</td>";
			}
			html += "</tr>";
		}
		html += "</tbody>";
	}

	/**
	 * Check for a uri whether there is a prefix defined in the query.
	 * 
	 * @param uri
	 * @return Short version of this uri if prefix is defined. Long version
	 *         otherwise
	 */
	private String getShortUri(String uri) {
		for (Map.Entry<String, Prefix> entry : queryPrefixes.entrySet()) {
			String prefixUri = entry.getKey();
			if (uri.startsWith(prefixUri)) {
				uri = uri.substring(prefixUri.length());
				uri = entry.getValue().getPrefix() + ":" + uri;
				break;
			}
		}
		return uri;
	}
}
