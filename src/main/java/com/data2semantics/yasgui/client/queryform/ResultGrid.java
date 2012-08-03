package com.data2semantics.yasgui.client.queryform;

import java.util.ArrayList;
import java.util.List;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.shared.RdfNodeContainer;
import com.data2semantics.yasgui.shared.ResultSetContainer;
import com.data2semantics.yasgui.shared.SolutionContainer;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TextArea;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;

public class ResultGrid extends ListGrid {
	private View view;
	public ResultGrid(View view, ResultSetContainer resultSet) {
		setWidth(500);
		setHeight(400);
		this.view = view;
		List<ListGridField> listGridFields = getVarsAsListGridFields(resultSet.getResultVars());
		List<ListGridRecord> listGridRecords = getSolutionsAsGridRecords(resultSet.getQuerySolutions());
		
		setFields(listGridFields.toArray(new ListGridField[listGridFields.size()]));
		setData(listGridRecords.toArray(new ListGridRecord[listGridRecords.size()]));
		
	}
	
	private List<ListGridRecord> getSolutionsAsGridRecords(List<SolutionContainer> querySolutions) {
		List<ListGridRecord> listGridRecords = new ArrayList<ListGridRecord>();
		for (SolutionContainer solution: querySolutions) {
			ListGridRecord listGridRecord = new ListGridRecord();
			List<RdfNodeContainer> nodes = solution.getRdfNodes();
			for (RdfNodeContainer node: nodes) {
				listGridRecord.setAttribute(node.getVarName(), node.getValue());
			}
			listGridRecords.add(listGridRecord);
		}
		return listGridRecords;
	}

	private List<ListGridField> getVarsAsListGridFields(List<String> resultVars) {
		List<ListGridField> listGridFields = new ArrayList<ListGridField>();
		for (String resultVar: resultVars) {
			listGridFields.add(new ListGridField(resultVar, resultVar));
		}
		return listGridFields;
	}
	
	private View getView() {
		return this.view;
	}
}
