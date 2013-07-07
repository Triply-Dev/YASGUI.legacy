package com.data2semantics.yasgui.client.tab.results.input;

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

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.tab.results.ResultContainer.ResultType;
import com.data2semantics.yasgui.client.tab.results.input.dlv.DlvWrapper;
import com.data2semantics.yasgui.client.tab.results.input.dlv.Row;
import com.data2semantics.yasgui.shared.exceptions.SparqlEmptyException;
import com.data2semantics.yasgui.shared.exceptions.SparqlParseException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Object to parse and validate a sparql json string
 */
public class DlvResults implements SparqlResults {
	@SuppressWarnings("unused")
	private View view;
	private ResultType queryMode;
	private boolean booleanResult;
	private String separator;
	private ArrayList<String> variables = new ArrayList<String>();
	
	private ArrayList<HashMap<String, HashMap<String, String>>> bindings = new ArrayList<HashMap<String, HashMap<String, String>>>();
	public DlvResults(String dlvString, View view, ResultType queryMode, String separator) throws SparqlParseException, SparqlEmptyException {
		this.view = view;
		this.queryMode = queryMode;
		this.separator = separator;
		processResults(dlvString);
	}
	
	/**
	 * Main parser method
	 * @param jsonString Json string to parse
	 * @throws SparqlParseException When json string is not valid
	 * @throws SparqlEmptyException When json string is valid, but contains no results
	 */
	public void processResults(String jsonString) throws SparqlParseException, SparqlEmptyException {
		if (jsonString == null || jsonString.length() == 0) {
			throw new SparqlEmptyException("Unable to parse empty " + (separator.equals(",")? "CSV": "TSV") + " string");
		}
		DlvWrapper dlv;
		try {
			dlv = JsMethods.getDlv(jsonString, separator);
		} catch (Throwable e) {
			throw new SparqlParseException("Unable to parse " + (separator.equals(",")? "CSV": "TSV") + " string", e);
		}
		if (dlv.length() < 2) { //first row contains vars
			throw new SparqlEmptyException("No results");
		}
		if (queryMode == ResultType.Table) {
			storeTable(dlv);
		} else if (queryMode == ResultType.Boolean) {
			storeBooleanResult(dlv);
		}
	}	
	
	private void storeTable(DlvWrapper dlv) {
		storeVariables(dlv.getRow(0));
		for (int rowId = 1; rowId < dlv.length(); rowId++) {
			Row row = dlv.getRow(rowId);
			HashMap<String, HashMap<String, String>> bindingHashMap = new HashMap<String, HashMap<String, String>>();
			for (int colId = 0; colId < row.length(); colId++) {
				//only add this if we actually have an answer for this binding
				String value = row.getCol(colId);
				if (value != null && value.length() > 0) {
					HashMap<String,String> nodeHashMap = new HashMap<String, String>();
					nodeHashMap.put("value", value);
					bindingHashMap.put(this.variables.get(colId),nodeHashMap);
				}
			}
			this.bindings.add(bindingHashMap);
		}
	}
	
	public ArrayList<String> getVariables() {
		return this.variables;
	}
	
	private void storeVariables(Row variablesRow) throws SparqlParseException, SparqlEmptyException {
		for (int i = 0; i < variablesRow.length(); i++) {
			this.variables.add(variablesRow.getCol(i));
		}
	}
	
	public ArrayList<HashMap<String, HashMap<String, String>>> getBindings() {
		return this.bindings;
	}
	
	
	public boolean getBooleanResult() {
		return booleanResult;
	}
	
	
	private void storeBooleanResult(DlvWrapper dlv) throws SparqlParseException {
		Row row = dlv.getRow(1); //just 2 lines, second line contains the boolean val
		
		if (row.length() == 0 || row.getCol(0) == null || row.getCol(0).length() == 0) {
			throw new SparqlParseException("Invalid " + (separator.equals(",")? "CSV": "TSV") + ". Unable to detect boolean value");
		} else {
			String value = row.getCol(0);
			this.booleanResult = (value.equals("1") || value.equalsIgnoreCase("true"));
		}
	}
}
