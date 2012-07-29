package com.data2semantics.yasgui.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Label;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Yasgui implements EntryPoint {

  /**
   * Create a remote service proxy to talk to the server-side Greeting service.
   */
  private final GreetingServiceAsync greetingService = GWT.create(GreetingService.class);

  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
	        Label label = new Label();  
	        label.setHeight(30);  
	        label.setPadding(10);  
	        label.setAlign(Alignment.CENTER);  
	        label.setValign(VerticalAlignment.CENTER);  
	        label.setWrap(false);  
	        label.setShowEdges(true);  
	        label.setContents("<i>Approved</i> for release");  
	        label.draw();  

  }
}
