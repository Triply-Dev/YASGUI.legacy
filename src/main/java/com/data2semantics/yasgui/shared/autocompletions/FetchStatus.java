package com.data2semantics.yasgui.shared.autocompletions;

import java.io.Serializable;

public enum FetchStatus  implements Serializable {
	SUCCESSFUL("successful"),
	FETCHING("fetching"),
	FAILED("failed");
	private String statusString;

	private FetchStatus(){}
	private FetchStatus(String statusString) {
		this.statusString = statusString;
	}
	public String get() {
		return this.statusString;
	}
}