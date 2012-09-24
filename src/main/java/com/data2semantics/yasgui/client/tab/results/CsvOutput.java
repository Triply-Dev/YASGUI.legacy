package com.data2semantics.yasgui.client.tab.results;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.SparqlJsonHelper;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.smartgwt.client.widgets.HTMLPane;

public class CsvOutput extends HTMLPane {
	private static String QUOTE = "\"";
	private static String DELIMITER = ",";
	private static String LINE_BREAK = "\n";
	private View view;
	private JSONArray variables;
	private JSONArray querySolutions;
	private String csvString;
	public CsvOutput(View view, SparqlJsonHelper queryResults) {
		this.view = view;
		setWidth100();
		setHeight100();
		variables = queryResults.getVariables();
		querySolutions = queryResults.getQuerySolutions();
	}
	
	public String getCsvString() {
		createCsvHeader();
		createCsvBody();
		return csvString;
	}
	
	private void createCsvHeader() {
		for (int i = 0; i < variables.size(); i++) {
			addValueToString(variables.get(i).isString().stringValue());
		}
		csvString += LINE_BREAK;
	}
	
	private void createCsvBody() {
		for (int solutionKey = 0; solutionKey < querySolutions.size(); solutionKey++) {
			addQuerySolutionToString(querySolutions.get(solutionKey).isObject());
			csvString += LINE_BREAK;
		}
	}
	
	private void addQuerySolutionToString(JSONObject querySolution) {
		for (int bindingKey = 0; bindingKey < variables.size(); bindingKey++) {
			String variable = variables.get(bindingKey).isString().toString();
			if (querySolution.containsKey(variable)) {
				addValueToString(querySolution.get(variable).isObject().get("value").isString().stringValue());
			} else {
				addValueToString("");
			}
		}
	}
	private void addValueToString(String value) {
		//Quotes in the string need to be escaped
		value.replace(QUOTE, QUOTE+QUOTE);
		if (needToQuoteString(value)) {
			value = QUOTE + value + QUOTE;
		}
	}
	
	private boolean needToQuoteString(String value) {
		//quote when it contains whitespace or the delimiter
		boolean quote = false;
		if (value.matches("[\\w|"+ DELIMITER + "|" + QUOTE + "]")) {
			quote = true;
		}
		return quote;
	}

	@SuppressWarnings("unused")
	private View getView() {
		return this.view;
	}

}
