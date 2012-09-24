package com.data2semantics.yasgui.client.tab.results;

import java.util.HashMap;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.SparqlJsonHelper;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.data2semantics.yasgui.shared.Output;
import com.data2semantics.yasgui.shared.exceptions.SparqlEmptyException;
import com.data2semantics.yasgui.shared.exceptions.SparqlParseException;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

public class QueryResultContainer extends VLayout {
	public static int RESULT_TYPE_TABLE = 1;
	public static int RESULT_TYPE_BOOLEAN = 2;
	public static int RESULT_TYPE_INSERT = 3;
	private View view;
	private QueryTab queryTab;
	private JsonOutput jsonOutput;
	SparqlJsonHelper queryResults;
	HashMap<String, Integer> queryTypes = new HashMap<String, Integer>();
	public QueryResultContainer(View view, QueryTab queryTab) {
		setPossibleQueryTypes();
		
		this.view = view;
		this.queryTab = queryTab;
	}
	
	private View getView() {
		return this.view;
	}
	
	
	private void setPossibleQueryTypes() {
		queryTypes.put("SELECT", RESULT_TYPE_TABLE);
		queryTypes.put("ASK", RESULT_TYPE_BOOLEAN);
		queryTypes.put("CONSTRUCT", RESULT_TYPE_TABLE);
		queryTypes.put("INSERT", RESULT_TYPE_INSERT);
		queryTypes.put("DESCRIBE", RESULT_TYPE_TABLE);
		queryTypes.put("LOAD", RESULT_TYPE_INSERT);
		queryTypes.put("CLEAR", RESULT_TYPE_INSERT);
		queryTypes.put("DROP", RESULT_TYPE_INSERT);
		queryTypes.put("ADD", RESULT_TYPE_INSERT);
		queryTypes.put("MOVE", RESULT_TYPE_INSERT);
		queryTypes.put("COPY", RESULT_TYPE_INSERT);
		queryTypes.put("CREATE", RESULT_TYPE_INSERT);
		queryTypes.put("INSERT", RESULT_TYPE_INSERT);
		queryTypes.put("DELETE", RESULT_TYPE_INSERT);
		queryTypes.put("WITH", RESULT_TYPE_INSERT); //used in MODIFY
	}
	
	/**
	 * Empty query result area
	 */
	public void reset() {
		if (jsonOutput != null) {
			//We have an old codemirror object used for showing json results. Clean this up
			JsMethods.destroyCodeMirrorJsonResult(jsonOutput.getInputId());
			jsonOutput = null;
		}
		Canvas[] members = getMembers();
		for (Canvas member : members) {
			removeMember(member);
		}
	}
	
	public void addQueryResult(String jsonString) {
		reset();
		String queryType = JsMethods.getQueryType(getView().getSelectedTab().getQueryTextArea().getInputId());
		if (!queryTypes.containsKey(queryType)) {
			getView().onError("No valid query type detected for this query");
			return;
		}
		int queryMode = queryTypes.get(queryType);
		
		try {
			if (queryMode == RESULT_TYPE_INSERT) {
				setOkMessage("Done");
			} else if (queryMode == RESULT_TYPE_BOOLEAN || queryMode == RESULT_TYPE_TABLE) {
				String outputFormat = getView().getSelectedTabSettings().getOutputFormat();
				if (outputFormat.equals(Output.OUTPUT_JSON)) {
					jsonOutput = new JsonOutput(getView(), queryTab, jsonString);
					addMember(jsonOutput);
					Scheduler.get().scheduleDeferred(new Command() {
						public void execute() {
							JsMethods.attachCodeMirrorToJsonResult(jsonOutput.getInputId(), Window.getClientWidth()-10);
						}
					});
				} else if (queryMode == RESULT_TYPE_BOOLEAN){
					drawResultsAsBoolean(new SparqlJsonHelper(jsonString, getView(), queryMode));
				} else if (queryMode == RESULT_TYPE_TABLE) {
					
					drawResultsInTable(new SparqlJsonHelper(jsonString, getView(), queryMode), outputFormat);
				}
			}
		} catch (SparqlParseException e) {
			getView().onError(e);
		} catch (SparqlEmptyException e) {
			setErrorResultMessage(e.getMessage());
		}
		
		
	}
	
	public void setErrorResultMessage(String message) {
		HLayout empty = new HLayout();
		empty.setHeight(50);
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setWidth100();
		empty.setWidth100();
		
		Img cross = new Img();
		cross.setSrc("icons/fugue/cross.png");
		cross.setSize(16);
		
		
		Label errorMessage = new Label("&nbsp;" + message);
		errorMessage.setAutoHeight();
		errorMessage.setWidth(70);
		empty.addMember(spacer);
		empty.addMember(cross);
		empty.addMember(errorMessage);
		empty.addMember(spacer);
		
		addMember(empty);
	}
	
	public void setOkMessage(String message) {
		HLayout empty = new HLayout();
		empty.setHeight(50);
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setWidth100();
		empty.setWidth100();
		
		Img cross = new Img();
		cross.setSrc("icons/fugue/tick.png");
		cross.setSize(16);
		
		
		Label emptyMessage = new Label("&nbsp;" + message);
		emptyMessage.setAutoHeight();
		emptyMessage.setWidth(70);
		empty.addMember(spacer);
		empty.addMember(cross);
		empty.addMember(emptyMessage);
		empty.addMember(spacer);
		
		addMember(empty);
	}
	private void drawResultsAsBoolean(SparqlJsonHelper queryResults) {
		if (queryResults.getBooleanResult()) {
			setOkMessage("true");
		} else {
			setErrorResultMessage("false");
		}
	}
	
	private void drawResultsInTable(SparqlJsonHelper queryResults, String outputFormat) {
		if (outputFormat.equals(Output.OUTPUT_TABLE)) {
			addMember(new ResultGrid(getView(), queryResults));
		} else if (outputFormat.equals(Output.OUTPUT_TABLE_SIMPLE)) {
			addMember(new SimpleGrid(getView(), queryResults));
		} else if (outputFormat.equals(Output.OUTPUT_CSV)) {
			CsvOutput output = new CsvOutput(getView(), queryResults);
			JsMethods.openDownDialogForCsv(output.getCsvString());
			getView().getLogger().severe(output.getCsvString());
		}
	}
	public JsonOutput getJsonOutput() {
		return this.jsonOutput;
	}
	
}
