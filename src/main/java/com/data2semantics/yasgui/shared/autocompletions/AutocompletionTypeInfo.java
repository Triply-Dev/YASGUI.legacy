package com.data2semantics.yasgui.shared.autocompletions;

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
	public boolean retryAllowed() {
		return fetchFailCount < AutocompletionsInfo.MAX_RETRIES;
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