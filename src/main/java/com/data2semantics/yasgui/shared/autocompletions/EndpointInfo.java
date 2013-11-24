package com.data2semantics.yasgui.shared.autocompletions;

import java.io.Serializable;

public class EndpointInfo implements Serializable {
	private static final long serialVersionUID = 905442860112391094L;
	private String endpoint;
	
	private AutocompletionTypeInfo propertyInfo = new AutocompletionTypeInfo();
	private AutocompletionTypeInfo classInfo = new AutocompletionTypeInfo();
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public AutocompletionTypeInfo getPropertyInfo() {
		return this.propertyInfo;
	}
	public AutocompletionTypeInfo getClassInfo() {
		return this.classInfo;
	}
	
	public String toString() {
		String result = "EndpointInfo ("+ endpoint + "):\n";
		result += propertyInfo.toString() + "\n";
		result += classInfo.toString() + "\n";
		return result;
	}
}