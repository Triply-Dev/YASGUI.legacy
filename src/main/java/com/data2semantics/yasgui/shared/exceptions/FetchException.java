package com.data2semantics.yasgui.shared.exceptions;

import java.io.Serializable;

public class FetchException extends RuntimeException implements Serializable { 
	
	public FetchException(){}
	public FetchException(String message) {
		super(message);
	}

	public FetchException(String message, Throwable e) {
		super(message, e);
	}

	private static final long serialVersionUID = 1L;
	
}
