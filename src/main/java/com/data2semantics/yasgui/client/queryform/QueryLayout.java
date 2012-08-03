package com.data2semantics.yasgui.client.queryform;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.shared.ResultSetContainer;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;

public class QueryLayout extends VLayout {
	private View view;
	private Label queryResultText = new Label();
	private TextItem endpoint;
	private TextArea queryInput;
	public QueryLayout(View view) {
//		setAlign(Alignment.CENTER);
		setMargin(10);
		setWidth(800);
		this.view = view;
		queryInput = new TextArea();
		queryInput.setHeight("400px");
		queryInput.setWidth("100%");
		queryInput.setText("SELECT * {?x ?f ?g} LIMIT 10");
		addMember(queryInput);
		
		DynamicForm endpointForm = new DynamicForm();
	    endpoint = new TextItem();
	    endpoint.setTitle("Endpoint");
	    endpoint.setWidth(250);
	    endpoint.setDefaultValue("http://eculture2.cs.vu.nl:5020/sparql/");
	    endpointForm.setFields(endpoint);
	    addMember(endpointForm);
	    
		
		Button buttonText = new Button("Get Text");
        buttonText.setHeight(18);  
        buttonText.setWidth(110);
        buttonText.setAlign(Alignment.CENTER);
        buttonText.addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
            	getView().getRemoteService().queryGetJson(endpoint.getValueAsString(), queryInput.getText(), new AsyncCallback<String>() {
            		public void onFailure(Throwable caught) {
            			getView().onError(caught);
            		}
					public void onSuccess(String queryResultString) {
						queryResultText.setContents(SafeHtmlUtils.htmlEscape(queryResultString));
					}
            	});
        	}
        });
        addMember(buttonText);
        
        Button buttonTable = new Button("Get Table");
        buttonTable.setHeight(18);  
        buttonTable.setWidth(110);
        buttonTable.setAlign(Alignment.CENTER);
        buttonTable.addClickHandler(new ClickHandler() {
        	public void onClick(ClickEvent event) {
            	getView().getRemoteService().queryGetObject(endpoint.getValueAsString(), queryInput.getText(), new AsyncCallback<ResultSetContainer>() {
            		public void onFailure(Throwable caught) {
            			getView().onError(caught);
            		}
					public void onSuccess(ResultSetContainer resultSet) {
						addMember(new ResultGrid(getView(), resultSet));
					}
            	});
        	}
        });
        addMember(buttonTable);
        
        
        addMember(queryResultText);
        
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
