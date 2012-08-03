package com.data2semantics.yasgui.client;

import com.google.gwt.core.client.EntryPoint;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;

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
	}
}
