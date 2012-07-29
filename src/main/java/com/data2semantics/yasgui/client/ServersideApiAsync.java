package com.data2semantics.yasgui.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ServersideApiAsync {
	void greetServer(String name, AsyncCallback<String> callback) throws IllegalArgumentException;
	
}
