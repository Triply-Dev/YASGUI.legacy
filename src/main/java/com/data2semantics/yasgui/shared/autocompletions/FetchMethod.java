package com.data2semantics.yasgui.shared.autocompletions;

import java.io.Serializable;

public enum FetchMethod implements Serializable {
	QUERY_ANALYSIS("query"),
	QUERY_RESULTS("queryResults");
	private String method;
	private FetchMethod(){}
	private FetchMethod(String method) {
		this.method = method;
	}
	public String get() {
		return this.method;
	}
}