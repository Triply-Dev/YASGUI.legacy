package com.data2semantics.yasgui.shared;

import java.io.Serializable;

public class Prefix implements Serializable {
	private String prefix;
	private String uri;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Prefix(){}
	public Prefix(String prefix, String uri) {
		this.prefix = prefix;
		this.uri = uri;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getUri() {
		return uri;
	}
	public void setUri(String uri) {
		this.uri = uri;
	}
	
	
}
