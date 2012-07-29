package com.data2semantics.yasgui.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Yasgui implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		ServerSideApiAsync serverSideApi = GWT.create(ServerSideApi.class);

		View view = new View(serverSideApi);
		view.draw();
	}
}
