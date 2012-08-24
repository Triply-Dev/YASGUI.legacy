package com.data2semantics.yasgui.client.queryform;

import java.util.LinkedHashMap;

import com.data2semantics.yasgui.client.JsMethods;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.queryform.grid.ResultGrid;
import com.data2semantics.yasgui.shared.Output;
import com.data2semantics.yasgui.shared.Settings;
import com.data2semantics.yasgui.shared.rdf.ResultSetContainer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.hp.hpl.jena.reasoner.rulesys.builtins.AddOne;
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
		Button queryButton = new Button("Query via Jena");
		queryButton.setHeight100();
		queryButton.setWidth(130);
		queryButton.setAlign(Alignment.CENTER);
		queryButton.setID(QUERY_FORMAT_SELECTOR_ID);
		queryButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				getView().updateSettings();
				Helper.storeSettingsInCookie(getView().getSettings());
				getView().storePrefixes();
				Settings settings = getView().getSettings();
				if (getSelectedOutput().equals(Output.OUTPUT_TABLE)) {
					final ResultGrid queryTable = new ResultGrid(getView());
					getView().addQueryResult(queryTable);
					getView().getRemoteService().queryGetObject(settings,
							new AsyncCallback<ResultSetContainer>() {
								public void onFailure(Throwable caught) {
									getView().onError(caught.getMessage());
								}
								public void onSuccess(ResultSetContainer resultSet) {
									queryTable.drawQueryResults(resultSet);
								}
							});

				} else {
					//TODO: use request builder:
					//http://google-web-toolkit.googlecode.com/svn/javadoc/2.3/com/google/gwt/http/client/package-summary.html
					String endpoint = SafeHtmlUtils.htmlEscape(getView().getEndpoint());
					JsMethods.saveCodeMirror();
					String query = JsMethods.getValueUsingId(View.QUERY_INPUT_ID);
					String format = getSelectedOutput();
					String url = GWT.getModuleBaseURL() + "file?endpoint=" + endpoint + "&query=" + query + "&format=" + format;
					Window.open(url, "YASGUI - " + format, "");
				}
			}
		});
		addMember(queryButton);
		
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
		
		Button queryViaJs = new Button("Query via JS");
		queryViaJs.setHeight100();
		queryViaJs.setWidth(130);
		queryViaJs.setAlign(Alignment.CENTER);
		queryViaJs.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				JsMethods.queryJson(getView().getSettings().getQueryString(), getView().getSettings().getEndpoint());
				
			}
		});
		addMember(queryViaJs);
	}
	private View getView() {
		return this.view;
	}
	
	public String getSelectedOutput() {
		return this.outputSelection.getValueAsString();
	}
}
