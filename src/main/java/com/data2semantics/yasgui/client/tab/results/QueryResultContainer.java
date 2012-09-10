package com.data2semantics.yasgui.client.tab.results;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.SparqlJsonHelper;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.data2semantics.yasgui.shared.Output;
import com.data2semantics.yasgui.shared.exceptions.SparqlEmptyException;
import com.data2semantics.yasgui.shared.exceptions.SparqlParseException;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;

public class QueryResultContainer extends VLayout {
	private View view;
	private QueryTab queryTab;
	SparqlJsonHelper queryResults;
	public QueryResultContainer(View view, QueryTab queryTab) {
		this.view = view;
		this.queryTab = queryTab;
	}
	
	private View getView() {
		return this.view;
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
		try {
			queryResults = new SparqlJsonHelper(jsonString, getView());
			String outputFormat = getView().getSettings().getOutputFormat();
			getView().getLogger().severe(outputFormat);
			if (outputFormat.equals(Output.OUTPUT_TABLE)) {
				getView().getLogger().severe("before1");
				addMember(new ResultGrid(getView(), queryTab, queryResults));
			} else if (outputFormat.equals(Output.OUTPUT_TABLE_SIMPLE)) {
				getView().getLogger().severe("before2");
				addMember(new SimpleGrid(getView(), queryTab, queryResults));
			}
		} catch (SparqlParseException e) {
			getView().onError(e);
		} catch (SparqlEmptyException e) {
			setEmptyMessage(e.getMessage());
		}
		
		
	}
	
	public void setEmptyMessage(String message) {
		getView().onError(message);//todo improve
	}
}
