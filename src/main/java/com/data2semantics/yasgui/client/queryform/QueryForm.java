package com.data2semantics.yasgui.client.queryform;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.YasguiServiceAsync;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.VLayout;

public class QueryForm extends VLayout {
	private View view;
	public QueryForm(View view) {
		this.view = view;
		TextArea queryInput = new TextArea();
		
		addMember(queryInput);
		
		Button button = new Button("Molecule");
        button.setHeight(18);  
        button.setWidth(110);
        button.setAlign(Alignment.CENTER);
        button.addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
            	getView().getRemoteService().query(new AsyncCallback<String>() {
            		public void onFailure(Throwable caught) {
            			getView().onError(caught.getMessage());
            		}
					public void onSuccess(String queryResult) {
						Label label = new Label();
						addMember(label);
					}
            	});
        		
        	}
        });
        addMember(button);
        
//		try {
//			getView().getServerSideApi().getInfo(patientId, new AsyncCallback<Patient>() {
//				public void onFailure(Throwable caught) {
//					getView().onError("Failed retrieving patient details:<br/>" + caught.getMessage());
//				}
//				public void onSuccess(Patient patient) {
//					patientInfo = patient;
//					drawInfoIntoTable(patientInfo);
//					getView().onLoadingFinish();
//					groupBy(Row.KEY);
//					
//				}
//			});
//		} catch (Exception e) {
//			getView().onError("Failed retrieving patient details:<br/>" + e.getMessage());
//		}
	}
	
	private View getView() {
		return this.view;
	}
}
