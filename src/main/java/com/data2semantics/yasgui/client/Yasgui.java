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
		//YasguiServiceAsync serverSideApi = GWT.create(YasguiService.class);
		//YasguiServiceAsync serverSideApi = GWT.create(YasguiService.class);
		
		View view = new View();
		view.draw();
	}
}
