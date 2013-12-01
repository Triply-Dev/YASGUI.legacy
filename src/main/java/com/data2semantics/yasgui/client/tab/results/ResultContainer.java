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

import java.util.ArrayList;
import java.util.HashMap;

import com.data2semantics.yasgui.client.GwtCallbackWrapper;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.ContentTypes;
import com.data2semantics.yasgui.client.helpers.ContentTypes.Type;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.HistoryHelper.HistQueryResults;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.data2semantics.yasgui.client.tab.QueryTab;
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
import com.data2semantics.yasgui.shared.autocompletions.AccessibilityStatus;
import com.data2semantics.yasgui.shared.exceptions.SparqlEmptyException;
import com.data2semantics.yasgui.shared.exceptions.SparqlParseException;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	
	//use this setting to keep our memory use low. Only store query results in settings (for re-use) when the char lenght is below this value
	private static int MAX_CHARS_RESULTS= 300000;//approx resultset of a 1000
	//use this setting to keep our settings object small enough to fit in local storage. This value covers the aggregate resultset size of all tabs
	private static int MAX_CHARS_RESULTS_TOTAL= 600000;
	public enum ResultType {
		Table, Boolean, Insert;
	}
	
	private View view;
	private QueryTab queryTab;
	private RawResponse rawResponseOutput;
	private String queryString;
	HashMap<String, ResultType> queryTypes = new HashMap<String, ResultType>();

	private String endpoint;
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
	public void resetResultArea() {
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
	
	public void resetVariables() {
		queryString = null;
		endpoint = null;
	}
	
	public void drawIfPossible() {
		TabSettings tabSettings = view.getSelectedTabSettings();
		if (view.getSelectedTabSettings().getQueryResultsString() != null) {
			endpoint = view.getSelectedTabSettings().getEndpoint();
			doDraw(tabSettings.getQueryResultsString(), tabSettings.getQueryResultsContentType());
		} else if (view.getHistory().getHistQueryResults(tabSettings.getEndpoint(), tabSettings.getQueryString()) != null) {
			HistQueryResults queryResults = view.getHistory().getHistQueryResults(tabSettings.getEndpoint(), tabSettings.getQueryString());
			endpoint = tabSettings.getEndpoint();
			doDraw(queryResults.getString(), queryResults.getContentType());
		} else {
			resetResultArea();
			resetVariables();
		}
	}
	
	/**
	 * Process and draw results. Checks for content type. If it is missing or strange, try to detect by parsing to xml or json
	 * 
	 * @param queryString query string for which we got these results 
	 * (this is -not- necessarily the query from the current tab object, as query execute might have a big latency with time to edit the query string)
	 * @param resultString
	 * @param contentType
	 */
	public void drawResult(String endpoint, String queryString, String resultString, String contentType) {
		this.queryString = queryString;
		this.endpoint = endpoint;
		storeResult(endpoint, queryString, resultString, contentType);
		doDraw(resultString, contentType);	
	}
	
	private void doDraw(String resultString, String contentTypeString) {
		if (resultString != null && resultString.length() > 0) {
			resetResultArea();
			Type contentType = ContentTypes.detectContentType(contentTypeString);
			
			if (contentType == null) {
				//assuming select query here (no construct)
				contentType = detectContentTypeFromResultstring(resultString);
			}
			if (contentType == null) {
				view.getErrorHelper().onQueryError(queryTab.getID(), "Unable to detect content type and parse the query results<br><br>" + resultString);
				return;
			}
			
			
			if ((queryTab.getQueryType().equals("CONSTRUCT") || queryTab.getQueryType().equals("DESCRIBE")) && !ResultsHelper.tabularConstructContentType(contentType)) {
				drawGraphResult(resultString, contentType);
			} else {
				addQueryResult(resultString, contentType);
			}
			resetVariables();
		}
	}
	

	
	private void storeResult(String endpoint, String queryString, String resultString, String contentType) {
		/**
		 * Store in settings
		 */
		view.getSettings().clearQueryResults(MAX_CHARS_RESULTS_TOTAL);
		if (resultString.length() < MAX_CHARS_RESULTS) {
			view.getSelectedTabSettings().setQueryResultsString(resultString);
			view.getSelectedTabSettings().setQueryResultsContentType(contentType);
		} else {
			view.getSelectedTabSettings().clearQueryResultsString();;
			view.getSelectedTabSettings().clearQueryResultsContentType();
		}
		
		/**
		 * Store in history helper as well. We store the resultsets in the settings object (above) and store this in localstorage,
		 * but we remove this part when storing the history state. If we don't, then every resultset in our history gets stored,
		 * making the memory size (about) linearly increase each time we execute a query...
		 * Cleaning our history of our previous states is (for security reason) not possible.. (we can only edit our -current- history state)
		 * Therefore, keep our last three resultsets in memory manually. Is ugly though (would prefer to use a proper history object for this instead of building on manually), but it'll have to do
		 */
		view.getHistory().addQueryResults(endpoint, queryString, resultString, contentType);
	}

	private void drawGraphResult(String responseString, Type contentType) {
		drawRawResponse(responseString, contentType);
	}
	
	private void addQueryResult(String responseString, Type contentType) {
		try {
			String queryType = JsMethods.getQueryType(view.getSelectedTab().getQueryTextArea().getInputId());
			if (!queryTypes.containsKey(queryType)) {
				throw new SparqlParseException("No valid query type detected for this query");
			}
			ResultType queryMode = queryTypes.get(queryType);
			switch (queryTypes.get(queryType)) {
            case Insert:
            	addMember(getResultsLabel(Imgs.CHECKBOX.get(), "Done"));
                    break;
            case Boolean:
            case Table:
            	String outputFormat = view.getSelectedTabSettings().getOutputFormat();
				if (outputFormat.equals(Output.OUTPUT_RAW_RESPONSE)) {
					drawRawResponse(responseString, contentType);
				} else {
					SparqlResults results = getSelectResultsFromString(responseString, contentType, queryTypes.get(queryType));
					logQueryForAnalysis(results);
					if (queryMode == ResultType.Boolean){
						drawResultsAsBoolean(results);
					} else if (queryMode == ResultType.Table) {
						drawResultsInTable(results, outputFormat);
					}
				}
				break;
                  
    }
		
		} catch (SparqlEmptyException e) {
			VLayout vLayout = new VLayout();
			vLayout.setWidth100();
			vLayout.setHeight100();
			
			ArrayList<String> usedNamedGraphs = view.getSelectedTabSettings().getNamedGraphs();
			ArrayList<String> usedDefaultGraphs = view.getSelectedTabSettings().getDefaultGraphs();
			if (
					(view.getEnabledFeatures().defaultGraphsSpecificationEnabled() && usedDefaultGraphs.size() > 0) ||
					(view.getEnabledFeatures().namedGraphsSpecificationEnabled() && usedNamedGraphs.size() > 0)) {
				vLayout.addMember(getResultsLabelWithWarning(Imgs.CROSS.get(), e.getMessage(), "You have specified named and/or default graphs in your query request, which may explain this empty result set"));
			} else {
				vLayout.addMember(getResultsLabel(Imgs.CROSS.get(), e.getMessage()));
			}
			
			addMember(vLayout);
		} catch (Exception e) {
			view.getErrorHelper().onError(e);
			
		} 
	}
	
	private void logQueryForAnalysis(SparqlResults results) {
		//only log when we actually have results
		if (results.getBindings().size() > 0 && endpoint != null && (view.getAutocompletionsInfo() == null || view.getAutocompletionsInfo().queryAnalysisEnabled(endpoint))) {
			new GwtCallbackWrapper<AccessibilityStatus>(view) {
				public void onCall(AsyncCallback<AccessibilityStatus> callback) {
					view.getRemoteService().logLazyQuery(queryString, endpoint, callback);
				}

				protected void onFailure(Throwable throwable) {
					JsMethods.logConsole("exception: " + throwable.getMessage());
				}

				protected void onSuccess(AccessibilityStatus status) {
					if (status == AccessibilityStatus.INACCESSIBLE && JsMethods.corsEnabled(endpoint)) {
						//inaccessible from YASGUI, but accessible from client side!
						
					}
				}

			}.call();
		}
	}
	private SparqlResults getSelectResultsFromString(String responseString, Type contentType, ResultType resultType) {
		SparqlResults results = null;
		if (contentType == Type.SELECT_JSON) {
			results = new JsonResults(responseString, view, resultType);
		} else if (contentType == Type.SELECT_XML) {
			results = new XmlResults(responseString, view, resultType);
		} else if (contentType == Type.SELECT_CSV) {
			results = new DlvResults(responseString, view, resultType, ",");
		} else if (contentType == Type.SELECT_TSV) {
			results = new DlvResults(responseString, view, resultType, "\t");
		} else {
			throw new SparqlParseException("no valid content type found for this response");
		}
		return results;
	}
	public HLayout getResultsLabelWithWarning(String iconSrc, String message, String warningMessage) {
		HLayout resultLayout = new HLayout();
		resultLayout.setDefaultLayoutAlign(VerticalAlignment.CENTER);
		resultLayout.setHeight(60);
		resultLayout.setWidth100();
		
		Img cross = new Img();
		cross.setSrc(iconSrc);
		cross.setSize(20);
		
		Img warning = new Img();
		warning.setSrc(Imgs.WARNING.get());
		warning.setSize(13);
		warning.setTooltip(warningMessage);
		
		Label messageLabel = new Label("&nbsp;" + message);
		messageLabel.setAutoHeight();
		messageLabel.setStyleName("queryResultText");
		messageLabel.setWidth(70);
		resultLayout.addMembers(Helper.getHSpacer(), cross, messageLabel, warning, Helper.getHSpacer());
		return resultLayout;
	}
	
	public HLayout getResultsLabel(String iconSrc, String message) {
		HLayout resultLayout = new HLayout();
		resultLayout.setDefaultLayoutAlign(VerticalAlignment.CENTER);
		resultLayout.setHeight(60);
		resultLayout.setWidth100();
		
		Img cross = new Img();
		cross.setSrc(iconSrc);
		cross.setSize(16);
		
		
		Label messageLabel = new Label("&nbsp;" + message);
		messageLabel.setAutoHeight();
		messageLabel.setStyleName("queryResultText");
		messageLabel.setWidth(70);
		resultLayout.addMember(Helper.getHSpacer());
		resultLayout.addMember(cross);
		resultLayout.addMember(messageLabel);
		resultLayout.addMember(Helper.getHSpacer());
		
		return resultLayout;
	}
	private void drawResultsAsBoolean(SparqlResults sparqlResults) {
		if (sparqlResults.getBooleanResult()) {
			addMember(getResultsLabel(Imgs.CHECKBOX.get(), "true"));
		} else {
			addMember(getResultsLabel(Imgs.CROSS.get(), "false"));
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
	
	private void drawRawResponse(String responseString, Type contentType) {
		if (JsMethods.stringToDownloadSupported()) {
			String url = JsMethods.stringToUrl(responseString, contentType.getContentType());
			view.getSelectedTab().getDownloadLink().showDownloadIcon(url, contentType);
		}
		rawResponseOutput = new RawResponse(view, queryTab, responseString);
		addMember(rawResponseOutput);
		
		final String mode;
		if (contentType.getCmMode() != null) {
			mode = contentType.getCmMode();
		} else {
			mode = Type.SELECT_XML.getCmMode();
		}
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
	
	public Type detectContentTypeFromResultstring(String responseString) {
		Type contentType = null;
		try {
			JSONValue jsonValue = JSONParser.parseStrict(responseString);
			if (jsonValue != null) {
				JSONObject jsonObject = jsonValue.isObject();
				JSONValue head = jsonObject.get("head");
				if (head != null) {
					return Type.SELECT_JSON;
				}
			}
		} catch (Exception e) {}
		try {
			Document xmlDoc = XMLParser.parse(responseString);
			if (xmlDoc != null && xmlDoc.getElementsByTagName("sparql").getLength() > 0) {
				return Type.SELECT_XML;
			}
		} catch (Exception e) {}
		
		return contentType;
	}
}
