package com.data2semantics.yasgui.client.queryform;

import java.util.LinkedHashMap;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.queryform.grid.ResultGrid;
import com.data2semantics.yasgui.shared.Output;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

public class ToolBar extends ToolStrip {
	private View view;
	public static String QUERY_FORMAT_SELECTOR_ID = "queryFormat";
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
        outputSelection.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				getView().updateSettings();
			}
        });
        addFormItem(outputSelection);
	}
	
	
	private void addButtons() {
		Button forcePrefixUpdate = new Button("Force prefixes update");
		forcePrefixUpdate.setHeight100();
		forcePrefixUpdate.setWidth(200);
		forcePrefixUpdate.setAlign(Alignment.CENTER);
		forcePrefixUpdate.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				getView().setAutocompletePrefixes(true);
				
			}
		});
		addMember(forcePrefixUpdate);
		
		Button addTabButton = new Button("Add tab");
		addTabButton.setHeight100();
		addTabButton.setWidth(200);
		addTabButton.setAlign(Alignment.CENTER);
		addTabButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				getView().getTabs().addNewTab();
				
			}
		});
		addMember(addTabButton);
		
		Button queryViaJs = new Button("Query via JS");
		queryViaJs.setHeight100();
		queryViaJs.setWidth(130);
		queryViaJs.setAlign(Alignment.CENTER);
		queryViaJs.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				getView().updateSettings();
				Helper.storeSettingsInCookie(getView().getSettings());
				QueryTab tab = getView().getSelectedTab();
				ResultGrid queryTable = new ResultGrid(getView(), tab);
				tab.addQueryResult(queryTable);
				JsMethods.queryJson(getView().getSettings().getQueryString(), getView().getSettings().getEndpoint());
				
			}
		});
		addMember(queryViaJs);
	}
	private View getView() {
		return this.view;
	}
	
}
