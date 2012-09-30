package com.data2semantics.yasgui.client.tab.results.input;

import java.util.ArrayList;
import java.util.HashMap;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.shared.exceptions.SparqlEmptyException;
import com.data2semantics.yasgui.shared.exceptions.SparqlParseException;

/**
 * Interface for getting sparql or xml sparql result in a common object form
 */
public interface SparqlResults {
	//Solutions //array
	//solution //collection of bindings
	//binding //1 binding (variable)
	
	
	ArrayList<String> getVariables();
	ArrayList<HashMap<String, HashMap<String, String>>> getBindings();
	void processResults(String jsonString) throws SparqlParseException, SparqlEmptyException;
	boolean getBooleanResult();
	View getView();
}
