package com.data2semantics.yasgui.client;

import java.util.logging.Level;

import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.UmbrellaException;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Positioning;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

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
//		ImgButton query = new ImgButton();
//		query.setSrc("icons/custom/start.png");
////		query.setTooltip("Query");
//		query.setHeight(48);
//		query.setShowRollOver(false);
//		query.setShowDown(false);
//		query.setWidth(48);
//		query.setAlign(Alignment.CENTER);
//		query.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				JsMethods.queryJson(view.getSelectedTabSettings().getQueryString(), view.getSelectedTabSettings().getEndpoint());
//				
//			}
//		});
//		query.setPosition(Positioning.ABSOLUTE);
//		query.setTop(0);
//		query.setLeft(0);
//		query.draw();
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
