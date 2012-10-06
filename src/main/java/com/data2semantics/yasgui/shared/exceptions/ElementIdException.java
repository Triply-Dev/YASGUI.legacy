package com.data2semantics.yasgui.shared.exceptions;

import java.io.Serializable;

public class ElementIdException extends RuntimeException implements Serializable { 
	
	public ElementIdException(){}
	public ElementIdException(String message) {
		super(message);
	}

	public ElementIdException(String message, Throwable e) {
		super(message, e);
	}

	private static final long serialVersionUID = 1L;
	
}
