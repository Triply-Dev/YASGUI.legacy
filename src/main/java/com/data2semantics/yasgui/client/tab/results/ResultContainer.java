package com.data2semantics.yasgui.client.tab.results;

import java.util.HashMap;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.data2semantics.yasgui.client.tab.results.input.JsonResults;
import com.data2semantics.yasgui.client.tab.results.input.SparqlResults;
import com.data2semantics.yasgui.client.tab.results.input.XmlResults;
import com.data2semantics.yasgui.client.tab.results.output.Csv;
import com.data2semantics.yasgui.client.tab.results.output.RawResponse;
import com.data2semantics.yasgui.client.tab.results.output.ResultGrid;
import com.data2semantics.yasgui.client.tab.results.output.SimpleGrid;
import com.data2semantics.yasgui.shared.Output;
import com.data2semantics.yasgui.shared.exceptions.SparqlEmptyException;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

public class ResultContainer extends VLayout {
	public static int RESULT_TYPE_TABLE = 1;
	public static int RESULT_TYPE_BOOLEAN = 2;
	public static int RESULT_TYPE_INSERT = 3;
	public static int RESULT_FORMAT_JSON = 1;
	public static int RESULT_FORMAT_XML = 2;
	
	private View view;
	private QueryTab queryTab;
	private RawResponse rawResponseOutput;
	HashMap<String, Integer> queryTypes = new HashMap<String, Integer>();
	public ResultContainer(View view, QueryTab queryTab) {
		setPossibleQueryTypes();
		this.view = view;
		this.queryTab = queryTab;
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
		if (rawResponseOutput != null) {
			//We have an old codemirror object used for showing json results. Clean this up
			JsMethods.destroyCodeMirrorQueryResponse(rawResponseOutput.getInputId());
			rawResponseOutput = null;
		}
		Canvas[] members = getMembers();
		for (Canvas member : members) {
			removeMember(member);
		}
	}
	
	public void addQueryResult(String responseString, int resultFormat) {
		reset();
		String queryType = JsMethods.getQueryType(view.getSelectedTab().getQueryTextArea().getInputId());
		if (!queryTypes.containsKey(queryType)) {
			view.onError("No valid query type detected for this query");
			return;
		}
		int queryMode = queryTypes.get(queryType);
		
		try {
			if (queryMode == RESULT_TYPE_INSERT) {
				setOkMessage("Done");
			} else if (queryMode == RESULT_TYPE_BOOLEAN || queryMode == RESULT_TYPE_TABLE) {
				String outputFormat = view.getSelectedTabSettings().getOutputFormat();
				if (outputFormat.equals(Output.OUTPUT_RAW_RESPONSE)) {
					drawRawResponse(responseString, resultFormat);
				} else {
					SparqlResults results;
					view.getLogger().severe(Integer.toString(resultFormat));
					if (resultFormat == RESULT_FORMAT_JSON) {
						results = new JsonResults(responseString, view, queryMode);
					} else {
						//xml
						results = new XmlResults(responseString, view, queryMode);
					}
					if (queryMode == RESULT_TYPE_BOOLEAN){
						drawResultsAsBoolean(results);
					} else if (queryMode == RESULT_TYPE_TABLE) {
						drawResultsInTable(results, outputFormat);
					}
				}
			}
		} catch (SparqlEmptyException e) {
			setErrorResultMessage(e.getMessage());
		} catch (Exception e) {
			view.onError(e);
			
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
	private void drawResultsAsBoolean(SparqlResults sparqlResults) {
		if (sparqlResults.getBooleanResult()) {
			setOkMessage("true");
		} else {
			setErrorResultMessage("false");
		}
	}
	
	private void drawResultsInTable(SparqlResults sparqlResults, String outputFormat) {
		if (outputFormat.equals(Output.OUTPUT_TABLE)) {
			addMember(new ResultGrid(view, sparqlResults));
		} else if (outputFormat.equals(Output.OUTPUT_TABLE_SIMPLE)) {
			addMember(new SimpleGrid(view, sparqlResults));
		} else if (outputFormat.equals(Output.OUTPUT_CSV)) {
			Csv output = new Csv(view, sparqlResults);
			JsMethods.openDownDialogForCsv(output.getCsvString());
//			view.getLogger().severe(output.getCsvString());
		}
	}
	
	private void drawRawResponse(String responseString, int resultFormat) {
		rawResponseOutput = new RawResponse(view, queryTab, responseString);
		addMember(rawResponseOutput);
		final String mode;
		if (resultFormat == RESULT_FORMAT_JSON) {
			mode = "json";
		} else {
			mode = "xml";
		}
		//on window resize, part of the page get redrawn. This means we have to attach to codemirror again
		//this is also called on first load
		rawResponseOutput.addResizedHandler(new ResizedHandler(){
			@Override
			public void onResized(ResizedEvent event) {
				Scheduler.get().scheduleDeferred(new Command() {
					public void execute() {
						JsMethods.attachCodeMirrorToQueryResult(rawResponseOutput.getInputId(), Window.getClientWidth()-10, mode);
					}
				});
		}});

	}
	public RawResponse getRawResponseOutput() {
		return this.rawResponseOutput;
	}
	
}
