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

public class QueryLayout extends VLayout {
	public static String QUERY_INPUT_ID = "queryInput";
	private static String DEFAULT_QUERY = "SELECT * {?x ?f ?g} LIMIT 10";
	private static String DEFAULT_ENDPOINT = "http://eculture2.cs.vu.nl:5020/sparql/";
	private View view;
	private Label queryResultText = new Label();
	private TextItem endpoint;
	private TextArea queryInput;
	private ToolBar toolBar;
	private ResultGrid queryTable;

	public QueryLayout(View view) {
		setMargin(10);
		setWidth(800);
		this.view = view;
		this.toolBar = new ToolBar(getView());
		addMember(this.toolBar);
		// Img img = new Img("xml.png");
		// addMember(img);
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

		Button queryButton = new Button("Query");
		queryButton.setHeight(18);
		queryButton.setWidth(110);
		queryButton.setAlign(Alignment.CENTER);
		queryButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (getToolBar().getSelectedOutput().equals(ToolBar.OUTPUT_TABLE)) {
					if (queryTable != null && hasMember(queryTable)) {
						removeMember(queryTable);
					}
					queryTable = new ResultGrid(getView());
					addMember(queryTable);
					getView().getRemoteService().queryGetObject(endpoint.getValueAsString(), getQuery(QUERY_INPUT_ID),
							new AsyncCallback<ResultSetContainer>() {
								public void onFailure(Throwable caught) {
									getView().onError(caught);
								}

								public void onSuccess(ResultSetContainer resultSet) {
									queryTable.drawQueryResults(resultSet);
								}
							});

				} else {
					getView().onError("Other output formats not supported yet");
				}
			}
		});
		addMember(queryButton);

		Button queryAsText = new Button("Get Text");
		queryAsText.setHeight(18);
		queryAsText.setWidth(110);
		queryAsText.setAlign(Alignment.CENTER);
		queryAsText.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				getView().getRemoteService().queryGetJson(endpoint.getValueAsString(), getQuery(QUERY_INPUT_ID),
						new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
								getView().onError(caught);
							}

							public void onSuccess(String queryResultString) {
								queryResultText.setContents(SafeHtmlUtils.htmlEscape(queryResultString));
							}
						});
			}
		});
		addMember(queryAsText);

		addMember(queryResultText);
	}

	private String getTextArea() {
		String textArea = "" + "<textarea " + "id=\"" + QUERY_INPUT_ID + "\"" + ">" + DEFAULT_QUERY + "</textarea>";

		return textArea;

	}

	private ToolBar getToolBar() {
		return this.toolBar;
	}

	private View getView() {
		return this.view;
	}

	public static native void attachCodeMirror(String queryInputId) /*-{
		if ($doc.getElementById(queryInputId)) {
			$wnd.sparqlHighlight = $wnd.CodeMirror.fromTextArea($doc
					.getElementById(queryInputId), {
				mode : "application/x-sparql-query",
				tabMode : "indent",
				matchBrackets : true
			});
		}
	}-*/;

	public static native String getQuery(String queryInputId) /*-{
		query = "";
		$wnd.sparqlHighlight.save();
		if ($doc.getElementById(queryInputId)) {
			if ($doc.getElementById(queryInputId).value) {
				query = $doc.getElementById(queryInputId).value;
			}
		}
		return query;
	}-*/;
}
