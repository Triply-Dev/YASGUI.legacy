package com.data2semantics.yasgui.client;

import java.util.logging.Level;

import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.UmbrellaException;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Yasgui implements EntryPoint {
	private View view;

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		view = new View();
		GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {
			public void onUncaughtException(Throwable e) {
				Throwable unwrapped = unwrap(e);  
				view.getLogger().log(Level.SEVERE, "Exception caught", e);  
				view.onError(unwrapped);
			}
		});
		view.draw();
	}

	private Throwable unwrap(Throwable e) {   
	    if(e instanceof UmbrellaException) {   
	      UmbrellaException ue = (UmbrellaException) e;  
	      if(ue.getCauses().size() == 1) {   
	        return unwrap(ue.getCauses().iterator().next());  
	      }  
	    }  
	    return e;  
	  }
	
	
}
