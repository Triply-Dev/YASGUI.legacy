package com.data2semantics.yasgui.shared.autocompletions;

import java.io.Serializable;

public enum AutocompletionConfigCols implements Serializable {
	ENDPOINT("endpoint", null),
	TYPE("type", "type of completions", false, 120),
	METHOD_QUERY("query", "# fetched from analyzing queries", false),
	METHOD_QUERY_RESULTS("queryResults", "# fetched from querying endpoint", false);
	
	private String key;
	private String label;
	private int width = -1;
	private boolean allowWrap = false;

	private AutocompletionConfigCols(String key, String label) {
		this.key = key;
		this.label = label;
	}
	private AutocompletionConfigCols(String key, String label, boolean allowWrap) {
		this.key = key;
		this.label = label;
		this.allowWrap = allowWrap;
	}
	private AutocompletionConfigCols(String key, String label, boolean allowWrap, int width) {
		this.key = key;
		this.label = label;
		this.width = width;
		this.allowWrap = allowWrap;
	}
	
	public String getKey() {
		return this.key;
	}
	public String getLabel() {
		return this.label;
	}
	public int getWidth() {
		return this.width;
	}
	public boolean useWrap() {
		return this.allowWrap;
	}
	
	
};