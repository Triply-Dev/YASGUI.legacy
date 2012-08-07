package com.data2semantics.yasgui.shared;

import java.io.Serializable;

public class SparqlRuntimeException extends RuntimeException implements Serializable { 
	
	public SparqlRuntimeException(){}
	public SparqlRuntimeException(String message) {
		super(message);
	}

	public SparqlRuntimeException(String message, Throwable e) {
		super(message, e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
