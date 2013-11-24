package com.data2semantics.yasgui.shared.autocompletions;

import java.io.Serializable;

public class AutocompletionTypeInfo implements Serializable {
	private static final long serialVersionUID = 7983956584768879376L;
	private boolean hasAutocompletions = false;
	private int fetchFailCount = 0;
	private boolean queryAnalysisEnabled = true;
	private boolean queryResultsFetching = true;

	public boolean hasAutocompletions() {
		return hasAutocompletions;
	}

	public void setHasAutocompletions(boolean hasAutocompletions) {
		this.hasAutocompletions = hasAutocompletions;
	}

	public int getFetchFailCount() {
		return fetchFailCount;
	}

	public void setFetchFailCount(int fetchFailCount) {
		this.fetchFailCount = fetchFailCount;
	}

	public boolean isQueryAnalysisEnabled() {
		return queryAnalysisEnabled;
	}

	public void setQueryAnalysisEnabled(boolean queryAnalysisEnabled) {
		this.queryAnalysisEnabled = queryAnalysisEnabled;
	}
	public boolean isQueryResultsFetchingEnabled() {
		return queryResultsFetching;
	}
	
	public void setQueryResultsFetchingEnabled(boolean queryResultsFetching) {
		this.queryResultsFetching = queryResultsFetching;
	}
	
	public String toString() {
		String result = "";
		result += "has autocompletions: " + (hasAutocompletions? "yes": "no") + "\n";
		result += "fetchFailCount: " + fetchFailCount + "\n";
		result += "queryAnalysisEnabled: " + (queryAnalysisEnabled? "yes": "no") + "\n";
		result += "queryResultsFetchingEnabled: " + (queryResultsFetching? "yes": "no") + "\n";
		return result;
	}
	
}