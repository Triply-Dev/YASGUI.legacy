package com.data2semantics.yasgui.client.tab.results;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.SparqlJsonHelper;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.data2semantics.yasgui.shared.Output;
import com.data2semantics.yasgui.shared.exceptions.SparqlEmptyException;
import com.data2semantics.yasgui.shared.exceptions.SparqlParseException;
import com.google.gwt.dom.client.Style.Unit;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
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
			String outputFormat = getView().getSelectedTabSettings().getOutputFormat();
			if (outputFormat.equals(Output.OUTPUT_TABLE)) {
				addMember(new ResultGrid(getView(), queryTab, queryResults));
			} else if (outputFormat.equals(Output.OUTPUT_TABLE_SIMPLE)) {
				addMember(new SimpleGrid(getView(), queryTab, queryResults));
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
}
