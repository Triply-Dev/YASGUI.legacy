package com.data2semantics.yasgui.shared.exceptions;

import java.io.Serializable;

public class SparqlEmptyException extends RuntimeException implements Serializable { 
	
	public SparqlEmptyException(){}
	public SparqlEmptyException(String message) {
		super(message);
	}

	public SparqlEmptyException(String message, Throwable e) {
		super(message, e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
