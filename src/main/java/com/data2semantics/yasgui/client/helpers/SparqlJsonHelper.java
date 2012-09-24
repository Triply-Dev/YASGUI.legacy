package com.data2semantics.yasgui.client.helpers;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.tab.results.QueryResultContainer;
import com.data2semantics.yasgui.shared.exceptions.SparqlEmptyException;
import com.data2semantics.yasgui.shared.exceptions.SparqlParseException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

/**
 * Object to parse (into JSONObject) and validate a sparql json string
 */
public class SparqlJsonHelper {
	public JSONObject queryResult;
	private View view;
	private int queryMode;
	public SparqlJsonHelper(String jsonString, View view, int queryMode) throws SparqlParseException, SparqlEmptyException {
		this.view = view;
		this.queryMode = queryMode;
		view.getLogger().severe("24");
		getAndValidateJsonObject(jsonString);
		
	}
	
	public JSONArray getVariables() throws SparqlParseException {
		JSONObject head = getAsObject(queryResult, "head");
		return getAsArray(head, "vars");
	}
	
	public JSONArray getQuerySolutions() throws SparqlParseException {
		JSONObject results = getAsObject(queryResult, "results");
		return getAsArray(results, "bindings");
	}
	
	
	/**
	 * Main parser method
	 * @param jsonString Json string to parse
	 * @throws SparqlParseException When json string is not valid
	 * @throws SparqlEmptyException When json string is valid, but contains no results
	 */
	private void getAndValidateJsonObject(String jsonString) throws SparqlParseException, SparqlEmptyException {
		if (jsonString == null || jsonString.length() == 0) {
			throw new SparqlParseException("Unable to parse empty JSON string");
		}
		JSONValue jsonValue = JSONParser.parseStrict(jsonString);
		if (jsonValue == null) {
			throw new SparqlParseException("Unable to parse query json string");
		}
		queryResult = jsonValue.isObject();
		if (queryResult == null) throw new SparqlParseException("Unable to parse query json string");
		
		JSONObject head = getAsObject(queryResult, "head");
		
		if (queryMode == QueryResultContainer.RESULT_TYPE_TABLE) {
			JSONObject resultsObject = getAsObject(queryResult, "results");
			JSONArray bindingsArray = getAsArray(resultsObject, "bindings");
			if (bindingsArray.size() == 0) {
				throw new SparqlEmptyException("No results");
			}
			JSONArray vars = getAsArray(head, "vars");
			if (vars.size() == 0) {
				throw new SparqlEmptyException("Vars missing from json object");
			}
		}
		
		
	}
	
	/**
	 * Gets JSON value as object, and throws exception when value is null
	 * 
	 * @param jsonValue
	 * @param message
	 * @return
	 * @throws SparqlParseException
	 */
	public JSONObject getAsObject(JSONObject jsonObject, String key) throws SparqlParseException {
		JSONValue jsonValue = jsonObject.get(key);
		if (jsonValue == null) {
			throw new SparqlParseException("Unable to get " + key + " as object");
		}
		JSONObject result = jsonValue.isObject();
		if (result == null) {
			throw new SparqlParseException("Unable to get " + key + " as object");
		}
		return result;
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
		JSONBoolean jsonBoolean = queryResult.get("boolean").isBoolean();
		if (jsonBoolean == null) {
			throw new SparqlParseException("Cannot format value as boolean");
		}
		return jsonBoolean.booleanValue();
	}
	
	@SuppressWarnings("unused")
	private View getView() {
		return this.view;
	}
}
