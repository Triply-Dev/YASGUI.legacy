package com.data2semantics.yasgui.server.autocompletions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.data2semantics.yasgui.server.db.DbHelper;
import com.data2semantics.yasgui.server.fetchers.AutocompletionFetcher;
import com.data2semantics.yasgui.server.fetchers.ClassesFetcher;
import com.data2semantics.yasgui.server.fetchers.PropertiesFetcher;
import com.data2semantics.yasgui.shared.autocompletions.AutocompleteKeys;
import com.data2semantics.yasgui.shared.autocompletions.AutocompletionStatusLevel;
import com.data2semantics.yasgui.shared.autocompletions.AutocompletionsInfo;
import com.data2semantics.yasgui.shared.autocompletions.EndpointPrivateFlag;
import com.data2semantics.yasgui.shared.autocompletions.FetchMethod;
import com.data2semantics.yasgui.shared.autocompletions.FetchType;
import com.data2semantics.yasgui.shared.exceptions.EndpointIdException;
import com.google.common.collect.HashMultimap;

public class AutocompleteResponseCreator {
	
	private FetchType type;
	private FetchMethod method;
	private String endpoint;
	private int maxResults;
	private String partialCompletion;
	private DbHelper dbHelper;
	private int endpointId = -1;
	private boolean endpointFoundOrAccessible = true;
	private JSONArray queryResultsResults = new JSONArray();
	private JSONArray queryAnalysisResults = new JSONArray();
	

	private AutocompleteResponseCreator(HttpServletRequest request, HttpServletResponse response, String configDir, FetchType type, FetchMethod method, String endpoint, String partialCompletion, int maxResults) throws SQLException, ClassNotFoundException, FileNotFoundException, JSONException, IOException, ParseException {
		this.type = type;
		this.method = method;
		this.endpoint = endpoint;
		this.partialCompletion = partialCompletion;
		this.maxResults = maxResults;
		
		this.dbHelper = new DbHelper(new File(configDir), request);
		
		try {
			endpointId = getEndpointId(dbHelper, endpoint);
		} catch (EndpointIdException e) {
			//endpoint not reachable, AND user not logged in
			endpointFoundOrAccessible = false;
		}
		
	}
	private int getEndpointId(DbHelper dbHelper, String endpoint) throws EndpointIdException, SQLException {
		try {
			return dbHelper.getEndpointId(endpoint, EndpointPrivateFlag.OWN_AND_PUBLIC);
		} catch (EndpointIdException e) {
			//endpoint id does not exists probably
			return dbHelper.generateIdForEndpoint(endpoint);
		}
	}
	
	private void fetchCompletions(FetchMethod method) throws SQLException {
		HashMultimap<String, String> completionsMap = HashMultimap.create();
		if (endpointFoundOrAccessible) {
			completionsMap = dbHelper.getAutocompletions(endpointId, partialCompletion, maxResults, type);
		}
		for (String uri: completionsMap.keySet()) {
			for (String methodString: completionsMap.get(uri)) {
				if (methodString.equals(FetchMethod.QUERY_ANALYSIS.get())) {
					queryAnalysisResults.put(uri);
				} else {
					queryResultsResults.put(uri);
				}
			}
		}
	}
	
	private JSONObject createJson() throws JSONException, SQLException, ClassNotFoundException, FileNotFoundException, IOException, ParseException {
		JSONObject mainResultObject = new JSONObject();
		
		fetchCompletions(method);
		
		
		if (method == null || method == FetchMethod.QUERY_RESULTS) {
			mainResultObject.put(AutocompleteKeys.RESPONSE_METHOD_QUERY_RESULTS, getQueryResultsJson());
		}
		
		if (method == null || method == FetchMethod.QUERY_ANALYSIS) {
			mainResultObject.put(AutocompleteKeys.RESPONSE_METHOD_QUERY_ANALYSIS, getQueryAnalysisJson());
		}
		return mainResultObject;
	}
	
	private JSONObject getQueryAnalysisJson() throws JSONException, SQLException {
		JSONObject queryAnalysisMethodObject = new JSONObject();
		if (queryAnalysisResults.length() == 0) {
			queryAnalysisMethodObject.put(AutocompleteKeys.RESPONSE_STATUS, getStatusObject(FetchMethod.QUERY_ANALYSIS));
		}
		
		int size = queryAnalysisResults.length();
		if (size + queryResultsResults.length() == maxResults) {
			//there are probably more results than the maximum we have retrieved
			size = dbHelper.getAutcompletionCount(endpointId, partialCompletion, type, FetchMethod.QUERY_ANALYSIS);
		}
		queryAnalysisMethodObject.put(AutocompleteKeys.RESPONSE_RESULTS, queryAnalysisResults);
		queryAnalysisMethodObject.put(AutocompleteKeys.RESPONSE_RESULT_SIZE, size);
		return queryAnalysisMethodObject;
	}
	private JSONObject getQueryResultsJson() throws SQLException, ClassNotFoundException, FileNotFoundException, JSONException, IOException, ParseException {
		JSONObject resultsObject = new JSONObject();
		JSONObject statusObj = getStatusObject(FetchMethod.QUERY_RESULTS);
		if (statusObj == null && queryResultsResults.length() == 0 && !dbHelper.lastFetchSuccesful(endpointId, type)) {
			//no error or exception. just try fetching our stuff!
			AutocompletionFetcher fetcher = null;
			if (type == FetchType.PROPERTIES) {
				fetcher = new PropertiesFetcher(endpoint, dbHelper);
			} else {
				fetcher = new ClassesFetcher(endpoint, dbHelper);
			}
			try {
				fetcher.fetch();
			} catch (Exception e) {
				statusObj = generateStatusObject("failed", "Exception message: " + e.getMessage(), AutocompletionStatusLevel.ERROR);
			}
			fetchCompletions(FetchMethod.QUERY_RESULTS);
		}
		if (statusObj != null) {
			resultsObject.put(AutocompleteKeys.RESPONSE_STATUS, statusObj);
		}
		int size = queryResultsResults.length();
		if (size + queryAnalysisResults.length() == maxResults) {
			//there are probably more results than the maximum we have retrieved
			size = dbHelper.getAutcompletionCount(endpointId, partialCompletion, type, FetchMethod.QUERY_RESULTS);
		}
		resultsObject.put(AutocompleteKeys.RESPONSE_RESULTS, queryResultsResults);
		resultsObject.put(AutocompleteKeys.RESPONSE_RESULT_SIZE, size);
		return resultsObject;
	}
	
	private JSONObject generateStatusObject(String subject, String text, AutocompletionStatusLevel level) throws JSONException {
		JSONObject statusObj = new JSONObject();
		statusObj.put(AutocompleteKeys.RESPONSE_STATUS_SUBJECT, subject);
		statusObj.put(AutocompleteKeys.RESPONSE_STATUS_TEXT, text);
		statusObj.put(AutocompleteKeys.RESPONSE_STATUS_LEVEL, level);
		return statusObj;
	}
		

	private JSONObject getStatusObject(FetchMethod method) throws SQLException, JSONException {
		JSONObject statusObject = null;
		switch (method) {
			case QUERY_RESULTS: 
				int timeout = 5;
				if (dbHelper.lastFetchesFailed(endpointId, type, AutocompletionsInfo.MAX_RETRIES)) {
					statusObject = generateStatusObject(
							"failed",
							"the last " + AutocompletionsInfo.MAX_RETRIES + " attempts to query for " + type.getPlural() + " failed. YASGUI will not attempt to fetch any more " + type.getPlural() + " from the dataset in the future (to avoid unnecessary load on this endpoint)",
							AutocompletionStatusLevel.WARN
						);
					break;
				}
				if (dbHelper.stillFetching(endpointId, type, timeout)) {
					statusObject = generateStatusObject(
							"fetching", 
							"still fetching " + type.getPlural() + ". Try again in " + timeout + " minutes",
							AutocompletionStatusLevel.INFO
						);
					break;
				}
				if (!dbHelper.autocompletionFetchingEnabled(endpointId, type, method)) {
					statusObject = generateStatusObject(
							"disabled", 
							"YASGUI won't try to query for " + type.getPlural() + " for this endpoint. This setting is stored by the YASGUI manager.",
							AutocompletionStatusLevel.WARN
						);
					break;
				}
				if (!endpointFoundOrAccessible) {
					statusObject = generateStatusObject(
							"failed", 
							"YASGUI failed to reach this endpoint. This might be an endpoint only accessible from your computer. To use autocompletions for this endpoint, please log in",
							AutocompletionStatusLevel.ERROR
						);
					break;
				}
				break;
			case QUERY_ANALYSIS:
			default: 
				
				break;
		}
		return statusObject;
	}
	
	public static JSONObject create(HttpServletRequest request, HttpServletResponse response, String configDir, FetchType type, FetchMethod method, String endpoint, String partialCompletion, int maxResults) throws ClassNotFoundException, FileNotFoundException, SQLException, JSONException, IOException, ParseException {
		AutocompleteResponseCreator creator = new AutocompleteResponseCreator(request, response, configDir, type, method, endpoint, partialCompletion, maxResults);
		return creator.createJson();
	}

}
