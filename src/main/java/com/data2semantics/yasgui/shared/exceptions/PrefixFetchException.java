package com.data2semantics.yasgui.shared.exceptions;

import java.io.Serializable;

public class PrefixFetchException extends RuntimeException implements Serializable { 
	
	public PrefixFetchException(){}
	public PrefixFetchException(String message) {
		super(message);
	}

	public PrefixFetchException(String message, Throwable e) {
		super(message, e);
	}

	private static final long serialVersionUID = 1L;
	
}
