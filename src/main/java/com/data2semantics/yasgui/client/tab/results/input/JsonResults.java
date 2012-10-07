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
package com.data2semantics.yasgui.client.tab.results.input;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.tab.results.ResultContainer;
import com.data2semantics.yasgui.shared.exceptions.SparqlEmptyException;
import com.data2semantics.yasgui.shared.exceptions.SparqlParseException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Object to parse and validate a sparql json string
 */
public class JsonResults implements SparqlResults{
	@SuppressWarnings("unused")
	private View view;
	private int queryMode;
	private boolean booleanResult;
	private ArrayList<String> variables = new ArrayList<String>();
	
	/**Bindings are:
	"bindings" : [
	               {
	                 "a" : { ... } ,
	                 "b" : { ... } 
	               } ,
	               {
	                 "a" : { ... } ,
	                 "b" : { ... } 
	               }
	             ]**/
	private ArrayList<HashMap<String, HashMap<String, String>>> bindings = new ArrayList<HashMap<String, HashMap<String, String>>>();
	public JsonResults(String jsonString, View view, int queryMode) throws SparqlParseException, SparqlEmptyException {
		this.view = view;
		this.queryMode = queryMode;
		processResults(jsonString);
	}
	
	/**
	 * Main parser method
	 * @param jsonString Json string to parse
	 * @throws SparqlParseException When json string is not valid
	 * @throws SparqlEmptyException When json string is valid, but contains no results
	 */
	public void processResults(String jsonString) throws SparqlParseException, SparqlEmptyException {
		if (jsonString == null || jsonString.length() == 0) {
			throw new SparqlParseException("Unable to parse empty JSON string");
		}
		JSONValue jsonValue = JSONParser.parseStrict(jsonString);
		//no need for this anymore, and it can be quite big. Fingers crossed and hope garbage collector deals witht this properly
		jsonString = null; 
		if (jsonValue == null) {
			throw new SparqlParseException("Unable to parse query json string");
		}
		JSONObject queryResult = jsonValue.isObject();
		if (queryResult == null) throw new SparqlParseException("Unable to parse query json string");
		
		if (queryMode == ResultContainer.RESULT_TYPE_TABLE) {
			storeVariables(queryResult);
			storeBindings(queryResult);
		} else if (queryMode == ResultContainer.RESULT_TYPE_BOOLEAN) {
			storeBooleanResult(queryResult);
		}
	}	
	
	public ArrayList<String> getVariables() {
		return this.variables;
	}
	
	private void storeVariables(JSONObject queryResult) throws SparqlParseException, SparqlEmptyException {
		JSONObject head = getAsObject(queryResult.get("head"));
		JSONArray variables = getAsArray(head, "vars");
		if (variables.size() == 0) {
			throw new SparqlEmptyException("Vars missing from json object");
		}
		for (int i = 0; i < variables.size(); i++) {
			this.variables.add(variables.get(i).isString().stringValue());
		}
	}
	
	public ArrayList<HashMap<String, HashMap<String, String>>> getBindings() {
		return this.bindings;
	}
	
	private void storeBindings(JSONObject queryResult) throws SparqlParseException, SparqlEmptyException {
		JSONObject results = getAsObject(queryResult.get("results"));
		JSONArray bindingsArray = getAsArray(results, "bindings");
		if (bindingsArray.size() == 0) {
			throw new SparqlEmptyException("No results");
		}
		//Loop through binding array
		for (int i = 0; i < bindingsArray.size(); i++) {
			JSONObject bindingObject = getAsObject(bindingsArray.get(i));
			Set<String> keys = bindingObject.keySet();
			Iterator<String> keysIterator = keys.iterator();
			HashMap<String, HashMap<String, String>> bindingHashMap = new HashMap<String, HashMap<String, String>>();
			//get binding into hashmap (binding is: variabled => node-info json object
			while (keysIterator.hasNext()) {
				String variable = keysIterator.next();
				JSONObject node = getAsObject(bindingObject.get(variable));
				Set<String> nodeKeys = node.keySet();
				Iterator<String> nodeIterator = nodeKeys.iterator();
				HashMap<String, String> nodeHashMap = new HashMap<String, String>();
				while (nodeIterator.hasNext()) {
					String nodeKey = nodeIterator.next();
					String nodeValue = node.get(nodeKey).isString().stringValue();
					nodeHashMap.put(nodeKey, nodeValue);
				}
				bindingHashMap.put(variable, nodeHashMap);
			}
			this.bindings.add(this.bindings.size(), bindingHashMap);
		}
	}
	
	
	/**
	 * Gets JSON value as object, and throws exception when value is null
	 * 
	 * @param jsonValue
	 * @return
	 * @throws SparqlParseException
	 */
	public JSONObject getAsObject(JSONValue jsonValue) throws SparqlParseException {
		if (jsonValue == null) {
			throw new SparqlParseException("Unable to get as object");
		}
		JSONObject result = jsonValue.isObject();
		if (result == null) {
			throw new SparqlParseException("Unable to get as object");
		}
		return result;
	}
	
	/**
	 * Gets JSON value as array, and throws exception when value is null
	 * 
	 * @param jsonValue
	 * @param message
	 * @return
	 * @throws SparqlParseException
	 */
	public JSONArray getAsArray(JSONObject jsonObject, String key) throws SparqlParseException {
		JSONValue jsonValue = jsonObject.get(key);
		if (jsonValue == null) {
			throw new SparqlParseException("Unable to get " + key + " as array");
		}
		JSONArray result = jsonValue.isArray();
		if (result == null) {
			throw new SparqlParseException("Unable to get " + key + " as array");
		}
		return result;
	}
	
	/**
	 * Gets JSON value as string, and throws exception when value is null
	 * 
	 * @param jsonValue
	 * @param message
	 * @return
	 * @throws SparqlParseException
	 */
	public String getAsString(JSONValue jsonValue) throws SparqlParseException {
		JSONString jsonString = jsonValue.isString();
		if (jsonString == null) {
			throw new SparqlParseException("Cannot format value as string");
		}
		return jsonString.stringValue();
	}
	
	public boolean getBooleanResult() {
		return booleanResult;
	}
	
	
	private void storeBooleanResult(JSONObject queryResult) {
		JSONBoolean jsonBoolean = queryResult.get("boolean").isBoolean();
		if (jsonBoolean == null) {
			throw new SparqlParseException("Cannot format value as boolean");
		}
		booleanResult = jsonBoolean.booleanValue();
	}
}
