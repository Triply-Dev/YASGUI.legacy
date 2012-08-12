package com.data2semantics.yasgui.shared;

import java.io.Serializable;

public class Settings implements Serializable {
	private static final long serialVersionUID = 1L;
	private String endpoint;
	private String queryString;
	private String outputFormat;

	public Settings(){}
	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String query) {
		this.queryString = query;
	}

	public String getOutputFormat() {
		return outputFormat;
	}

	public void setOutputFormat(String outputFormat) {
		this.outputFormat = outputFormat;
	}
	
	public String toString() {
		return endpoint;
	}
}
