package com.data2semantics.yasgui.shared.autocompletions;

import java.io.Serializable;

public enum FetchType implements Serializable {
	CLASSES("class", "classes"),
	PROPERTIES("property", "properties");
	private String singular;
	private String plural;

	private FetchType(){}
	private FetchType(String singular, String plural) {
		this.singular = singular;
		this.plural = plural;
	}
	public String getSingular() {
		return this.singular;
	}
	public String getSingularCamelCase() {
		return this.singular.substring(0, 1).toUpperCase() + this.singular.substring(1);
	}
	public String getPlural() {
		return this.plural;
	}
	public String getPluralCamelCase() {
		return this.plural.substring(0, 1).toUpperCase() + this.plural.substring(1);
	}
}