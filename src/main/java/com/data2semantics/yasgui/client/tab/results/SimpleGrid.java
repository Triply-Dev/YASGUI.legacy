package com.data2semantics.yasgui.client.tab.results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.SparqlJsonHelper;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.data2semantics.yasgui.shared.Prefix;
import com.data2semantics.yasgui.shared.rdf.SolutionContainer;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.smartgwt.client.util.StringUtil;
import com.smartgwt.client.widgets.HTMLPane;

public class SimpleGrid extends HTMLPane {
	private View view;
	private QueryTab tab;
	private HashMap<String, Prefix> queryPrefixes = new HashMap<String, Prefix>();
	private JSONArray variables;
	private JSONArray querySolutions;
	private String html;

	public SimpleGrid(View view, QueryTab tab, SparqlJsonHelper queryResults) {
		this.tab = tab;
		this.view = view;
		setWidth100();
		setHeight100();
		queryPrefixes = Helper.getPrefixesFromQuery(view.getSettings().getSelectedTabSettings().getQueryString());
		variables = queryResults.getVariables();
		querySolutions = queryResults.getQuerySolutions();
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
		for (int i = 0; i < variables.size(); i++) {
			html += "<th>" + StringUtil.asHTML(variables.get(i).isString().stringValue()) + "</th>";
		}
		html += "</tr></thead>";
	}

	private void drawRows() {
		html += "<tbody>";
		for (int solutionKey = 0; solutionKey < querySolutions.size(); solutionKey++) {
			JSONObject querySolution = querySolutions.get(solutionKey).isObject();
			html += "<tr>";
			for (int variableKey = 0; variableKey < variables.size(); variableKey++) {
				html += "<td>";
				String variable = variables.get(variableKey).isString().stringValue();
				JSONObject binding = querySolution.get(variable).isObject();
				if (binding == null) {
					html += "&nbsp;";
				} else {
					if (binding.get("type").isString().stringValue().equals("uri")) {
						String uri = binding.get("value").isString().stringValue();
						html += "<a href=\"" + uri + "\" target=\"_blank\">" + StringUtil.asHTML(getShortUri(uri)) + "</a>";
					} else {
						html += StringUtil.asHTML(binding.get("value").isString().stringValue());
					}
				}
				html += "</td>";
			}
			html += "</tr>";
		}
		html += "</tbody>";
	}

	private View getView() {
		return this.view;
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
