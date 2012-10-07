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

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.tab.results.input.SparqlResults;
import com.smartgwt.client.widgets.HTMLPane;

public class Csv extends HTMLPane {
	private static String QUOTE = "\"";
	private static String DELIMITER = ",";
	private static String LINE_BREAK = "\n";
	@SuppressWarnings("unused")
	private View view;
	private ArrayList<String> variables;
	private ArrayList<HashMap<String, HashMap<String, String>>> querySolutions;
	private String csvString = "";
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
		csvString += " " + value + " " + DELIMITER;
	}
	
	private boolean needToQuoteString(String value) {
		//quote when it contains whitespace or the delimiter
		boolean quote = false;
		if (value.matches("[\\w|"+ DELIMITER + "|" + QUOTE + "]")) {
			quote = true;
		}
		return quote;
	}
}
