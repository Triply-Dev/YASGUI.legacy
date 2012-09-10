package com.data2semantics.yasgui.client.tab.results;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.SparqlJsonHelper;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.data2semantics.yasgui.shared.Output;
import com.data2semantics.yasgui.shared.Prefix;
import com.data2semantics.yasgui.shared.exceptions.SparqlEmptyException;
import com.data2semantics.yasgui.shared.exceptions.SparqlParseException;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

public class QueryResultContainer extends VLayout {
	private static int RESULT_TYPE_TABLE = 1;
	private static int RESULT_TYPE_BOOLEAN = 2;
	private static int RESULT_TYPE_INSERT = 3;
	private View view;
	private QueryTab queryTab;
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
			Canvas[] members = getMembers();
			for (Canvas member : members) {
				removeMember(member);
			}
	}
	
	public void addQueryResult(String jsonString) {
		reset();
		String queryType = JsMethods.getQueryType(getView().getSelectedTab().getQueryTextArea().getInputId());
		int queryMode = queryTypes.get(queryType);
		try {
			
			if (queryMode == RESULT_TYPE_INSERT) {
				setDoneMessage();
			} else if (queryMode == RESULT_TYPE_BOOLEAN) {
				queryResults = new SparqlJsonHelper(jsonString, getView());
			} else if (queryMode == RESULT_TYPE_TABLE) {
				queryResults = new SparqlJsonHelper(jsonString, getView());
				String outputFormat = getView().getSelectedTabSettings().getOutputFormat();
				if (outputFormat.equals(Output.OUTPUT_TABLE)) {
					addMember(new ResultGrid(getView(), queryTab, queryResults));
				} else if (outputFormat.equals(Output.OUTPUT_TABLE_SIMPLE)) {
					addMember(new SimpleGrid(getView(), queryTab, queryResults));
				}
			}
		} catch (SparqlParseException e) {
			getView().onError(e);
		} catch (SparqlEmptyException e) {
			setEmptyMessage(e.getMessage());
		}
		
		
	}
	
	public void setEmptyMessage(String message) {
		HLayout empty = new HLayout();
		empty.setHeight(50);
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setWidth100();
		empty.setWidth100();
		
		Img cross = new Img();
		cross.setSrc("icons/fugue/cross.png");
		cross.setSize(16);
		
		
		Label emptyMessage = new Label("No results");
		emptyMessage.setAutoHeight();
		emptyMessage.setWidth(70);
		empty.addMember(spacer);
		empty.addMember(cross);
		empty.addMember(emptyMessage);
		empty.addMember(spacer);
		
		addMember(empty);
	}
	
	public void setDoneMessage() {
		HLayout empty = new HLayout();
		empty.setHeight(50);
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setWidth100();
		empty.setWidth100();
		
		Img cross = new Img();
		cross.setSrc("icons/fugue/tick.png");
		cross.setSize(16);
		
		
		Label emptyMessage = new Label("Done");
		emptyMessage.setAutoHeight();
		emptyMessage.setWidth(50);
		empty.addMember(spacer);
		empty.addMember(cross);
		empty.addMember(emptyMessage);
		empty.addMember(spacer);
		
		addMember(empty);
	}
}
