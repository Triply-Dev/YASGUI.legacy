package com.data2semantics.yasgui.client.queryform;

import java.util.LinkedHashMap;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.shared.Output;
import com.data2semantics.yasgui.shared.ResultSetContainer;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

public class ToolBar extends ToolStrip {
	private View view;

	private SelectItem outputSelection;
	public ToolBar(View view) {
		this.view = view;
		
        setWidth100();
        
        addOutputSelection();
        
        addButtons();
        
	}

	private void addOutputSelection() {
		outputSelection = new SelectItem();
        outputSelection.setTitleOrientation(TitleOrientation.TOP);
        outputSelection.setTitle("Output");  
        LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();  
        valueMap.put(Output.OUTPUT_TABLE, "Table"); 
        valueMap.put(Output.OUTPUT_CSV, "CSV"); 
        valueMap.put(Output.OUTPUT_JSON, "JSON");
        valueMap.put(Output.OUTPUT_XML, "XML");
        LinkedHashMap<String, String> valueIcons = new LinkedHashMap<String, String>();  
        valueIcons.put(Output.OUTPUT_TABLE, "table");  
        valueIcons.put(Output.OUTPUT_CSV, "csv");  
        valueIcons.put(Output.OUTPUT_JSON, "json");  
        valueIcons.put(Output.OUTPUT_XML, "xml");  
        outputSelection.setValueIcons(valueIcons);  
        
        outputSelection.setValueMap(valueMap);  
        outputSelection.setImageURLPrefix("logos/formats/");  
        outputSelection.setImageURLSuffix(".png");  
        outputSelection.setDefaultValue(Output.OUTPUT_TABLE);
        addFormItem(outputSelection);
	}
	
	
	private void addButtons() {
		Button queryButton = new Button("Query");
		queryButton.setHeight(18);
		queryButton.setWidth(110);
		queryButton.setAlign(Alignment.CENTER);
		queryButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (getSelectedOutput().equals(Output.OUTPUT_TABLE)) {
					final ResultGrid queryTable = new ResultGrid(getView());
					getView().getQueryLayout().addQueryResult(queryTable);
					getView().getRemoteService().queryGetObject(getView().getQueryLayout().getEndpoint(), QueryLayout.getQuery(QueryLayout.QUERY_INPUT_ID),
							new AsyncCallback<ResultSetContainer>() {
								public void onFailure(Throwable caught) {
									getView().onError(caught);
								}

								public void onSuccess(ResultSetContainer resultSet) {
									queryTable.drawQueryResults(resultSet);
								}
							});

				} else {
					final Label queryResultText = new Label();
					getView().getQueryLayout().addQueryResult(queryResultText);
					getView().getRemoteService().queryGetText(getView().getQueryLayout().getEndpoint(), QueryLayout.getQuery(QueryLayout.QUERY_INPUT_ID), getSelectedOutput(),
							new AsyncCallback<String>() {
								public void onFailure(Throwable caught) {
									getView().onError(caught);
								}

								public void onSuccess(String queryResultString) {
									queryResultText.setContents(SafeHtmlUtils.htmlEscape(queryResultString));
								}
							});
				}
			}
		});
		addMember(queryButton);

	}
	private View getView() {
		return this.view;
	}
	
	public String getSelectedOutput() {
		return this.outputSelection.getValueAsString();
	}
}
