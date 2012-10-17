/*******************************************************************************
 * Copyright (c)  2012 Laurens Rietveld
 * 
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
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
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

public class ResultContainer extends VLayout {
	public static String XSD_DATA_PREFIX = "http://www.w3.org/2001/XMLSchema#";
	public static int RESULT_TYPE_TABLE = 1;
	public static int RESULT_TYPE_BOOLEAN = 2;
	public static int RESULT_TYPE_INSERT = 3;
	
	public static int CONTENT_TYPE_JSON = 1;
	public static int CONTENT_TYPE_XML = 2;
	private static String ICON_OK = "icons/fugue/tick.png";
	private static String ICON_WRONG = "icons/fugue/cross.png";
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
	
	/**
	 * Process and draw results. Checks for content type. If it is missing or strange, try to detect by parsing to xml or json
	 * 
	 * @param resultString
	 * @param contentType
	 */
	public void processResult(String resultString, String contentType) {
		int resultFormat;
		if (contentType == null) {
			resultFormat = detectContentType(resultString);
			if (resultFormat == 0) {
				view.getElements().onQueryError("Unable to detect content type<br><br>" + resultString);
				return;
			}
		} else if (contentType.contains("sparql-results+json")) {
			resultFormat = ResultContainer.CONTENT_TYPE_JSON;
		} else if (contentType.contains("sparql-results+xml")) {
			resultFormat = ResultContainer.CONTENT_TYPE_XML;
		} else {
			resultFormat = detectContentType(resultString);
			if (resultFormat == 0) {
				view.getElements().onQueryError("Unable to parse results with content type " + contentType + ".<br><br>" + resultString);
				return;
			}
		}
		addQueryResult(resultString, resultFormat);
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
				setResultMessage(ICON_OK, "Done");
			} else if (queryMode == RESULT_TYPE_BOOLEAN || queryMode == RESULT_TYPE_TABLE) {
				String outputFormat = view.getSelectedTabSettings().getOutputFormat();
				if (outputFormat.equals(Output.OUTPUT_RAW_RESPONSE)) {
					drawRawResponse(responseString, resultFormat);
				} else {
					SparqlResults results;
					view.getLogger().severe(Integer.toString(resultFormat));
					if (resultFormat == CONTENT_TYPE_JSON) {
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
			setResultMessage(ICON_WRONG, e.getMessage());
		} catch (Exception e) {
			view.onError(e);
			
		} 
	}
	
	public void setResultMessage(String iconSrc, String message) {
		HLayout empty = new HLayout();
		empty.setDefaultLayoutAlign(VerticalAlignment.CENTER);
		empty.setHeight(50);
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setWidth100();
		empty.setWidth100();
		
		Img cross = new Img();
		cross.setSrc(iconSrc);
		cross.setSize(16);
		
		
		Label emptyMessage = new Label("&nbsp;" + message);
		emptyMessage.setAutoHeight();
		emptyMessage.setStyleName("queryResultText");
		emptyMessage.setWidth(70);
		empty.addMember(spacer);
		empty.addMember(cross);
		empty.addMember(emptyMessage);
		empty.addMember(spacer);
		
		addMember(empty);
	}
	private void drawResultsAsBoolean(SparqlResults sparqlResults) {
		if (sparqlResults.getBooleanResult()) {
			setResultMessage(ICON_OK, "true");
		} else {
			setResultMessage(ICON_WRONG, "false");
		}
	}
	
	private void drawResultsInTable(SparqlResults sparqlResults, String outputFormat) {
		if (outputFormat.equals(Output.OUTPUT_TABLE)) {
			addMember(new ResultGrid(view, sparqlResults));
		} else if (outputFormat.equals(Output.OUTPUT_TABLE_SIMPLE)) {
			addMember(new SimpleGrid(view, sparqlResults));
		} else if (outputFormat.equals(Output.OUTPUT_CSV)) {
			Csv output = new Csv(view, sparqlResults);
			view.getLogger().severe(output.getCsvString());
		}
	}
	
	private void drawRawResponse(String responseString, int resultFormat) {
		rawResponseOutput = new RawResponse(view, queryTab, responseString);
		addMember(rawResponseOutput);
		final String mode;
		if (resultFormat == CONTENT_TYPE_JSON) {
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
						JsMethods.attachCodeMirrorToQueryResult(rawResponseOutput.getInputId(), mode);
						rawResponseOutput.adjustForContent(true);
					}
				});
		}});
	}
	public RawResponse getRawResponseOutput() {
		return this.rawResponseOutput;
	}
	
	public int detectContentType(String responseString) {
		int contentType = 0;
		try {
			JSONValue jsonValue = JSONParser.parseStrict(responseString);
			if (jsonValue != null) {
				return CONTENT_TYPE_JSON;
			}
		} catch (Exception e) {}
		try {
			Document xmlDoc = XMLParser.parse(responseString);
			if (xmlDoc != null) {
				return CONTENT_TYPE_XML;
			}
		} catch (Exception e) {}
		
		return contentType;
	}
	
}
