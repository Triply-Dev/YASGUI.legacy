package com.data2semantics.yasgui.shared.rdf;

import java.io.Serializable;

public class RdfNodeContainer implements Serializable {
	private static final long serialVersionUID = 1L;
	private String varName;
	private String value;
	private boolean isUri;
	private boolean isAnon;
	private boolean isLiteral;
	private String datatypeUri;
	
	public RdfNodeContainer() {
		
	}
	
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public boolean isUri() {
		return isUri;
	}

	public void setIsUri(boolean isUri) {
		this.isUri = isUri;
	}

	public boolean isAnon() {
		return isAnon;
	}

	public void setIsAnon(boolean isAnon) {
		this.isAnon = isAnon;
	}

	public boolean isLiteral() {
		return isLiteral;
	}

	public void setIsLiteral(boolean isLiteral) {
		this.isLiteral = isLiteral;
	}

	public void setDatatype(String datatypeUri) {
		this.datatypeUri = datatypeUri;
		
	}
	public String getDatatypeUri() {
		return this.datatypeUri;
	}
	
	
		
		
}
