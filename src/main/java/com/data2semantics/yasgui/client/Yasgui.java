package com.data2semantics.yasgui.client;

import com.data2semantics.yasgui.client.queryform.QueryLayout;
import com.google.gwt.core.client.EntryPoint;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Yasgui implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		View view = new View();
		view.draw();
		postProcess();
	}

	private void postProcess() {
		QueryLayout.attachCodeMirror(QueryLayout.QUERY_INPUT_ID);
	}
}
