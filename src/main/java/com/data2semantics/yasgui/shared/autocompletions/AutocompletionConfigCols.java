package com.data2semantics.yasgui.shared.autocompletions;

import java.io.Serializable;

public enum AutocompletionConfigCols implements Serializable {
	ENDPOINT("endpoint", "endpoint"),
	TYPE("type", "type"),
	METHOD_QUERY("methodQuery", "query method"),
	METHOD_QUERY_RESULTS("methodQueryResults", "query results method");
	
	private String key;
	private String label;
	private int width = -1;

	private AutocompletionConfigCols(String key, String label) {
		this.key = key;
		this.label = label;
	}
	private AutocompletionConfigCols(String key, String label, int width) {
		this.key = key;
		this.label = label;
		this.width = width;
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
	
	
};