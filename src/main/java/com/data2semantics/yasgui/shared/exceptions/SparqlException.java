package com.data2semantics.yasgui.shared.exceptions;

import java.io.Serializable;

public class SparqlException extends RuntimeException implements Serializable { 
	
	public SparqlException(){}
	public SparqlException(String message) {
		super(message);
	}

	public SparqlException(String message, Throwable e) {
		super(message, e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
