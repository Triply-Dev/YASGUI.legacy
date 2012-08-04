package com.data2semantics.yasgui.client.queryform;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.shared.ResultSetContainer;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuButton;

public class QueryLayout extends VLayout {
	public static String QUERY_INPUT_ID = "queryInput";
	private static String DEFAULT_QUERY = "SELECT * {?x ?f ?g} LIMIT 10";
	private static String DEFAULT_ENDPOINT = "http://eculture2.cs.vu.nl:5020/sparql/";
	private View view;
	private Label queryResultText = new Label();
	private TextItem endpoint;
	private TextArea queryInput;

	public QueryLayout(View view) {
		setMargin(10);
		setWidth(800);
		this.view = view;
//		RibbonBar ribbonBar = new RibbonBar();  
//        ribbonBar.setLeft(0);  
//        ribbonBar.setTop(75);  
//        ribbonBar.setWidth100();
//        Menu menu = new Menu();  
//        
//        RibbonGroup fileGroup = new RibbonGroup();  
//        fileGroup.setTitle("File");  
//        fileGroup.setTitleAlign(Alignment.LEFT);  
//        fileGroup.setNumRows(1);  
//        fileGroup.setRowHeight(76);  
//        fileGroup.addControl(new MenuButton("bla1", menu));
//
//        ribbonBar.addMember(fileGroup);
//        addMember(ribbonBar);
        
		HTMLPane queryInput = new HTMLPane();
		queryInput.setHeight("350px");
		queryInput.setContents(getTextArea());
		addMember(queryInput);
		DynamicForm endpointForm = new DynamicForm();
		endpoint = new TextItem();
		endpoint.setTitle("Endpoint");
		endpoint.setWidth(250);
		endpoint.setDefaultValue(DEFAULT_ENDPOINT);
		endpointForm.setFields(endpoint);
		addMember(endpointForm);

		Button buttonText = new Button("Get Text");
		buttonText.setHeight(18);
		buttonText.setWidth(110);
		buttonText.setAlign(Alignment.CENTER);
		buttonText.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				getView().getRemoteService().queryGetJson(endpoint.getValueAsString(), getQuery(QUERY_INPUT_ID), new AsyncCallback<String>() {
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
				getView().getRemoteService().queryGetObject(endpoint.getValueAsString(), getQuery(QUERY_INPUT_ID),
						new AsyncCallback<ResultSetContainer>() {
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
	}

	private String getTextArea() {
		String textArea = "" +
			"<textarea " +
				"id=\"" + QUERY_INPUT_ID + "\"" +
				">" +
					DEFAULT_QUERY +
			"</textarea>";
		
		return textArea;
		
	}

	private View getView() {
		return this.view;
	}

	public static native void attachCodeMirror(String queryInputId) /*-{
		if ($doc.getElementById(queryInputId)) {
			var editor = $wnd.CodeMirror.fromTextArea($doc.getElementById(queryInputId), {
				mode : "application/x-sparql-query",
				tabMode : "indent",
				matchBrackets : true
			});
		}
	}-*/;
	
	public static native String getQuery(String queryInputId) /*-{
		query = "";
		if ($doc.getElementById(queryInputId)) {
			if ($doc.getElementById(queryInputId).value) {
				query = $doc.getElementById(queryInputId).value;
			}
		}
		return query;
	}-*/;
}
