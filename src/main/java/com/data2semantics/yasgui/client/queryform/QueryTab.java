package com.data2semantics.yasgui.client.queryform;

import com.data2semantics.yasgui.client.EndpointInput;
import com.data2semantics.yasgui.client.QueryTextArea;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.queryform.grid.ResultGrid;
import com.data2semantics.yasgui.shared.Prefix;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;

public class QueryTab extends Tab {
	private View view;
	private QueryTextArea queryTextArea;
	private EndpointInput endpointInput;
	private VLayout vLayout = new VLayout();
	private VLayout queryResultContainer = new VLayout();
	private ResultGrid resultGrid;
	public QueryTab(View view, String title) {
		super(title);
		this.view = view;
		setCanClose(true);
		
		queryTextArea = new QueryTextArea(getView(), getID()); 
		
		vLayout.addMember(queryTextArea);
		
		endpointInput = new EndpointInput(getView());
		vLayout.addMember(endpointInput);
		
		vLayout.addMember(queryResultContainer);
		setPane(vLayout);
	}
	
	public void resetQueryResult() {
		Canvas[] members = queryResultContainer.getMembers();
		for (Canvas member : members) {
			queryResultContainer.removeMember(member);
		}
	}

	public void addQueryResult(ResultGrid resultGrid) {
		resetQueryResult();
		this.resultGrid = resultGrid;
		queryResultContainer.addMember(resultGrid);
	}
	
	public QueryTextArea getQueryTextArea() {
		return this.queryTextArea;
	}
	
	private View getView() {
		return this.view;
	}
	public void drawResultsInTable(String jsonResult) {
		resultGrid.drawQueryResultsFromJson(jsonResult);
	}
	

}
