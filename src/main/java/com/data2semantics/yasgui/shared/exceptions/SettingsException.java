package com.data2semantics.yasgui.shared.exceptions;

import java.io.Serializable;

public class SettingsException extends RuntimeException implements Serializable { 
	
	public SettingsException(){}
	public SettingsException(String message) {
		super(message);
	}

	public SettingsException(String message, Throwable e) {
		super(message, e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
}
