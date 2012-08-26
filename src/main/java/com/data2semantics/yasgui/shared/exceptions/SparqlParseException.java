package com.data2semantics.yasgui.shared.exceptions;

import java.io.Serializable;

public class SparqlParseException extends RuntimeException implements Serializable { 
	
	public SparqlParseException(){}
	public SparqlParseException(String message) {
		super(message);
	}

	public SparqlParseException(String message, Throwable e) {
		super(message, e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
