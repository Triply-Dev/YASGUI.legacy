package com.data2semantics.yasgui.client.queryform;

import com.data2semantics.yasgui.client.View;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;

public class QueryLayout extends VLayout {
	private View view;
	private Label queryResult = new Label();
	private TextItem endpoint;
	private TextArea queryInput;
	public QueryLayout(View view) {
		this.view = view;
		queryInput = new TextArea();
		queryInput.setHeight("400px");
		queryInput.setWidth("600px");
		queryInput.setText("SELECT * {?x ?f ?g} LIMIT 10");
		addMember(queryInput);
		
		DynamicForm endpointForm = new DynamicForm();
	    endpoint = new TextItem();
	    endpoint.setTitle("Endpoint");
	    endpoint.setWidth(250);
	    endpoint.setDefaultValue("http://eculture2.cs.vu.nl:5020/sparql/");
	    endpointForm.setFields(endpoint);
	    addMember(endpointForm);
	    
		
		Button button = new Button("Query");
        button.setHeight(18);  
        button.setWidth(110);
        button.setAlign(Alignment.CENTER);
        button.addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
            	getView().getRemoteService().query(endpoint.getValueAsString(), queryInput.getText(), new AsyncCallback<String>() {
            		public void onFailure(Throwable caught) {
            			getView().onError(caught);
            		}
					public void onSuccess(String queryResultString) {
						queryResult.setContents(SafeHtmlUtils.htmlEscape(queryResultString));
					}
            	});
        	}
        });
        addMember(button);
        
        addMember(queryResult);
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
