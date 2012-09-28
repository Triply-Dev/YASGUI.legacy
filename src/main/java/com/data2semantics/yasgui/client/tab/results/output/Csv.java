package com.data2semantics.yasgui.client.tab.results.output;

import java.util.ArrayList;
import java.util.HashMap;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.tab.results.input.SparqlResults;
import com.smartgwt.client.widgets.HTMLPane;

public class Csv extends HTMLPane {
	private static String QUOTE = "\"";
	private static String DELIMITER = ",";
	private static String LINE_BREAK = "\n";
	private View view;
	private ArrayList<String> variables;
	private ArrayList<HashMap<String, HashMap<String, String>>> querySolutions;
	private String csvString;
	public Csv(View view, SparqlResults queryResults) {
		this.view = view;
		setWidth100();
		setHeight100();
		variables = queryResults.getVariables();
		querySolutions = queryResults.getBindings();
	}
	
	public String getCsvString() {
		createCsvHeader();
		createCsvBody();
		return csvString;
	}
	
	private void createCsvHeader() {
		for (String variable: variables) {
			addValueToString(variable);
		}
		csvString += LINE_BREAK;
	}
	
	private void createCsvBody() {
		for (HashMap<String, HashMap<String, String>> querySolution: querySolutions) {
			addQuerySolutionToString(querySolution);
			csvString += LINE_BREAK;
		}
	}
	
	private void addQuerySolutionToString(HashMap<String, HashMap<String, String>> querySolution) {
		for (int variableKey = 0; variableKey < variables.size(); variableKey++) {
			String variable = variables.get(variableKey);
			if (querySolution.containsKey(variable)) {
				addValueToString(querySolution.get(variable).get("value"));
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
