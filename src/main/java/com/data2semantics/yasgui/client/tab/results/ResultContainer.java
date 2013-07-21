package com.data2semantics.yasgui.client.tab.results;

/*
 * #%L
 * YASGUI
 * %%
 * Copyright (C) 2013 Laurens Rietveld
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import java.util.HashMap;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.data2semantics.yasgui.client.tab.optionbar.QueryConfigMenu;
import com.data2semantics.yasgui.client.tab.results.input.DlvResults;
import com.data2semantics.yasgui.client.tab.results.input.JsonResults;
import com.data2semantics.yasgui.client.tab.results.input.ResultsHelper;
import com.data2semantics.yasgui.client.tab.results.input.SparqlResults;
import com.data2semantics.yasgui.client.tab.results.input.XmlResults;
import com.data2semantics.yasgui.client.tab.results.output.Csv;
import com.data2semantics.yasgui.client.tab.results.output.RawResponse;
import com.data2semantics.yasgui.client.tab.results.output.ResultGrid;
import com.data2semantics.yasgui.client.tab.results.output.SimpleGrid;
import com.data2semantics.yasgui.shared.Output;
import com.data2semantics.yasgui.shared.exceptions.SparqlEmptyException;
import com.data2semantics.yasgui.shared.exceptions.SparqlParseException;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.XMLParser;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class ResultContainer extends VLayout {
	public static String XSD_DATA_PREFIX = "http://www.w3.org/2001/XMLSchema#";
	
	public enum ResultType {
		Table, Boolean, Insert;
	}
	
	public static int CONTENT_TYPE_JSON = 1;
	public static int CONTENT_TYPE_XML = 2;
	public static int CONTENT_TYPE_TURTLE = 3;
	public static int CONTENT_TYPE_CSV = 4;
	public static int CONTENT_TYPE_TSV = 5;
	
	private String contentType;
	private View view;
	private QueryTab queryTab;
	private RawResponse rawResponseOutput;
	HashMap<String, ResultType> queryTypes = new HashMap<String, ResultType>();
	public ResultContainer(View view, QueryTab queryTab) {
		setPossibleQueryTypes();
		this.view = view;
		this.queryTab = queryTab;
	}
	
	private void setPossibleQueryTypes() {
		queryTypes.put("SELECT", ResultType.Table);
		queryTypes.put("ASK", ResultType.Boolean);
		queryTypes.put("CONSTRUCT", ResultType.Table);
		queryTypes.put("INSERT", ResultType.Insert);
		queryTypes.put("DESCRIBE", ResultType.Table);
		queryTypes.put("LOAD", ResultType.Insert);
		queryTypes.put("CLEAR", ResultType.Insert);
		queryTypes.put("DROP", ResultType.Insert);
		queryTypes.put("ADD", ResultType.Insert);
		queryTypes.put("MOVE", ResultType.Insert);
		queryTypes.put("COPY", ResultType.Insert);
		queryTypes.put("CREATE", ResultType.Insert);
		queryTypes.put("INSERT", ResultType.Insert);
		queryTypes.put("DELETE", ResultType.Insert);
		queryTypes.put("WITH", ResultType.Insert); //used in MODIFY
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
		reset();
		int resultFormat;
		this.contentType = contentType;
		
		
		if ((queryTab.getQueryType().equals("CONSTRUCT") || queryTab.getQueryType().equals("DESCRIBE")) && !ResultsHelper.tabularContentType(contentType)) {
			drawGraphResult(resultString);
			return;
		}
		
		if (contentType == null) {
			//assuming select query here (no construct)
			resultFormat = detectContentType(resultString);
			if (resultFormat == 0) {
				view.getElements().onQueryError(queryTab.getID(), "Unable to detect content type<br><br>" + resultString);
				return;
			}
		} else if (contentType.contains("sparql-results+json")) {
			resultFormat = ResultContainer.CONTENT_TYPE_JSON;
		} else if (contentType.contains("sparql-results+xml")) {
			resultFormat = ResultContainer.CONTENT_TYPE_XML;
		} else if (contentType.contains(QueryConfigMenu.CONTENT_TYPE_SELECT_CSV)) {
			resultFormat = ResultContainer.CONTENT_TYPE_CSV;
		} else if (contentType.contains(QueryConfigMenu.CONTENT_TYPE_SELECT_TSV)) {
			resultFormat = ResultContainer.CONTENT_TYPE_TSV;
		} else {
			//assuming select query here (no construct)
			resultFormat = detectContentType(resultString);
			if (resultFormat == 0) {
				view.getElements().onQueryError(queryTab.getID(), "Unable to parse results with content type " + contentType + ".<br><br>" + resultString);
				return;
			}
		}
		addQueryResult(resultString, resultFormat);
	}
	

	
	private void drawGraphResult(String responseString) {
		int mode = 0;
		if (contentType.contains(QueryConfigMenu.CONTENT_TYPE_CONSTRUCT_TURTLE)) {
			mode = CONTENT_TYPE_TURTLE;
		} else {
			mode = CONTENT_TYPE_XML;
		}
		drawRawResponse(responseString, mode);
	}
	
	public void addQueryResult(String responseString, int resultFormat) {
		reset();
		try {
			String queryType = JsMethods.getQueryType(view.getSelectedTab().getQueryTextArea().getInputId());
			if (!queryTypes.containsKey(queryType)) {
				throw new SparqlParseException("No valid query type detected for this query");
			}
			ResultType queryMode = queryTypes.get(queryType);
			switch (queryTypes.get(queryType)) {
            case Insert:
            	setResultMessage(Imgs.get(Imgs.CHECKBOX), "Done");
                    break;
            case Boolean:
            case Table:
            	String outputFormat = view.getSelectedTabSettings().getOutputFormat();
				if (outputFormat.equals(Output.OUTPUT_RAW_RESPONSE)) {
					drawRawResponse(responseString, resultFormat);
				} else {
					SparqlResults results = getResultsFromString(responseString, resultFormat, queryMode);
					if (queryMode == ResultType.Boolean){
						drawResultsAsBoolean(results);
					} else if (queryMode == ResultType.Table) {
						drawResultsInTable(results, outputFormat);
					}
				}
				break;
                  
    }
		
		} catch (SparqlEmptyException e) {
			setResultMessage(Imgs.get(Imgs.CROSS), e.getMessage());
		} catch (Exception e) {
			view.getElements().onError(e);
			
		} 
	}
	
	private SparqlResults getResultsFromString(String responseString, int resultFormat, ResultType queryMode) {
		SparqlResults results = null;
		if (resultFormat == CONTENT_TYPE_JSON) {
			results = new JsonResults(responseString, view, queryMode);
		} else if (resultFormat == CONTENT_TYPE_XML) {
			results = new XmlResults(responseString, view, queryMode);
		} else if (resultFormat == CONTENT_TYPE_CSV) {
			results = new DlvResults(responseString, view, queryMode, ",");
		} else if (resultFormat == CONTENT_TYPE_TSV) {
			results = new DlvResults(responseString, view, queryMode, "\t");
		} else {
			throw new SparqlParseException("no valid content type found for this response");
		}
		return results;
	}
	public void setResultMessage(String iconSrc, String message) {
		HLayout empty = new HLayout();
		empty.setDefaultLayoutAlign(VerticalAlignment.CENTER);
		empty.setHeight(50);
		empty.setWidth100();
		
		Img cross = new Img();
		cross.setSrc(iconSrc);
		cross.setSize(16);
		
		
		Label emptyMessage = new Label("&nbsp;" + message);
		emptyMessage.setAutoHeight();
		emptyMessage.setStyleName("queryResultText");
		emptyMessage.setWidth(70);
		empty.addMember(Helper.getHSpacer());
		empty.addMember(cross);
		empty.addMember(emptyMessage);
		empty.addMember(Helper.getHSpacer());
		
		addMember(empty);
	}
	private void drawResultsAsBoolean(SparqlResults sparqlResults) {
		if (sparqlResults.getBooleanResult()) {
			setResultMessage(Imgs.get(Imgs.CHECKBOX), "true");
		} else {
			setResultMessage(Imgs.get(Imgs.CROSS), "false");
		}
	}
	
	private void drawResultsInTable(SparqlResults sparqlResults, String outputFormat) {
		Csv csvParser = new Csv(view, sparqlResults);
		if (JsMethods.stringToDownloadSupported()) {
			String url = JsMethods.stringToUrl(csvParser.getCsvString(), "text/csv");
			view.getSelectedTab().getDownloadLink().showCsvIcon(url);
		}
		if (outputFormat.equals(Output.OUTPUT_TABLE)) {
			HTMLPane html = new HTMLPane();
			//html.setHeight(27);
			addMember(html);
			addMember(new ResultGrid(view, sparqlResults, html));
		} else if (outputFormat.equals(Output.OUTPUT_TABLE_SIMPLE)) {
			addMember(new SimpleGrid(view, sparqlResults));
		}
	}
	
	private void drawRawResponse(String responseString, int resultFormat) {
		if (JsMethods.stringToDownloadSupported()) {
			String url = JsMethods.stringToUrl(responseString, contentType);
			view.getSelectedTab().getDownloadLink().showDownloadIcon(url, resultFormat);
		}
		rawResponseOutput = new RawResponse(view, queryTab, responseString);
		addMember(rawResponseOutput);

		final String mode;
		if (resultFormat == CONTENT_TYPE_JSON) {
			mode = "json";
		} else if (resultFormat == CONTENT_TYPE_TURTLE) {
			mode = "text/turtle";
		} else {
			mode = "xml";
		}
		view.getLogger().severe(mode);
		//on window resize, part of the page get redrawn. This means we have to attach to codemirror again
		//this is also called on first load
		rawResponseOutput.addResizedHandler(new ResizedHandler(){
			@Override
			public void onResized(ResizedEvent event) {
				Scheduler.get().scheduleDeferred(new Command() {
					public void execute() {
						JsMethods.initializeQueryResponseCodemirror(rawResponseOutput.getInputId(), mode);
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
				JSONObject jsonObject = jsonValue.isObject();
				JSONValue head = jsonObject.get("head");
				if (head != null) {
					return CONTENT_TYPE_JSON;
				}
			}
		} catch (Exception e) {}
		try {
			Document xmlDoc = XMLParser.parse(responseString);
			if (xmlDoc != null && xmlDoc.getElementsByTagName("sparql").getLength() > 0) {
				return CONTENT_TYPE_XML;
			}
		} catch (Exception e) {}
		
		return contentType;
	}
	

	
}
