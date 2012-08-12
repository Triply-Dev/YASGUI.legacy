package com.data2semantics.yasgui.client.queryform.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.shared.Prefix;
import com.data2semantics.yasgui.shared.rdf.RdfNodeContainer;
import com.data2semantics.yasgui.shared.rdf.ResultSetContainer;
import com.data2semantics.yasgui.shared.rdf.SolutionContainer;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class ResultGrid extends ListGrid {
	private static String SOLUTION_PREFIX = "yasgui___solution";
	private static String VARIABLE_PREFIX = "yasgui___var";
	private static String XSD_DATA_PREFIX = "http://www.w3.org/2001/XMLSchema#";
	private View view;
	private HashMap<String, Prefix> prefixes;
	public ResultGrid(View view) {
		this.view = view;
		this.prefixes = getView().getPrefixes();
		setWidth100();
		setHeight(350);
		setShowRecordComponents(true);
		setShowRecordComponentsByCell(true);
		setShowRowNumbers(true);
//		setFixedRecordHeights(false);
		setAutoFitData(Autofit.VERTICAL);
		setCanResizeFields(true);
		setEmptyMessage("Executing query");
	}

	public ResultGrid(View view, ResultSetContainer resultSet) {
		this(view);
		drawQueryResults(resultSet);
	}

	public void drawQueryResults(ResultSetContainer resultSet) {
		List<ListGridField> listGridFields = getVarsAsListGridFields(resultSet.getResultVars());
		setFields(listGridFields.toArray(new ListGridField[listGridFields.size()]));
		if (resultSet.getQuerySolutions().size() > 0) {
			List<ListGridRecord> rows = getSolutionsAsGridRecords(resultSet.getQuerySolutions());
			setData(rows.toArray(new ListGridRecord[rows.size()]));
		} else {
			setEmptyMessage("No results");
			redraw();
		}
	}

	
	protected Canvas createRecordComponent(ListGridRecord row, Integer colNum) {
		// fieldname is the identifier of the column, in our case the same as
		// the column header
		String colName = this.getFieldName(colNum);
		
		//the numbering field created by smartgwt has field name starting with $
		if (colName.startsWith(VARIABLE_PREFIX)) { 
			String varName = colName.substring(VARIABLE_PREFIX.length());
			SolutionContainer solution = (SolutionContainer) row.getAttributeAsObject(SOLUTION_PREFIX);
			RdfNodeContainer node = solution.get(varName);
			if (node.isUri()) {
				String uri = node.getValue();
				Prefix prefix = getPrefixForUri(uri);
				String text = uri;
				if (prefix != null) {
					text = prefix.getPrefix() + ":" + uri.substring(prefix.getUri().length());
				}
				HTMLPane html = new HTMLPane();
				html.setContents("<a href=\"" + uri + "\" target=\"_blank\">" + text + "</a>");
				html.setHeight100();
				html.setWidth100();
				return html;
			} else if (node.isLiteral()) {
				String literal = node.getValue();
				Label label = new Label(literal);
				if (node.getDatatypeUri() != null) {
					label.setPrompt("xsd:" + node.getDatatypeUri().substring(XSD_DATA_PREFIX.length()));
				}
				return label;
			} else {
				//is bnode
				String uri = node.getValue();
				Label label = new Label(uri);
				return label;
			}
		}
		return null;
	}

	private ArrayList<ListGridRecord> getSolutionsAsGridRecords(List<SolutionContainer> querySolutions) {
		ArrayList<ListGridRecord> rows = new ArrayList<ListGridRecord>();
		for (SolutionContainer solution : querySolutions) {
			ListGridRecord row = new ListGridRecord();
			row.setAttribute(SOLUTION_PREFIX, solution); 
			HashMap<String, RdfNodeContainer> nodes = solution.getRdfNodes();
			for (RdfNodeContainer node : nodes.values()) {
				row.setAttribute(node.getVarName(), node.getValue());
			}
			rows.add(row);
		}
		return rows;
	}

	

	private ArrayList<ListGridField> getVarsAsListGridFields(List<String> resultVars) {
		ArrayList<ListGridField> listGridFields = new ArrayList<ListGridField>();
		for (String resultVar : resultVars) {
			ListGridField field = new ListGridField(VARIABLE_PREFIX + resultVar, resultVar);
			listGridFields.add(field);
		}
		return listGridFields;
	}
	
	private Prefix getPrefixForUri(String uri) {
		Prefix prefix = null;
		for (Map.Entry<String, Prefix> entry : prefixes.entrySet()) {
		    String prefixUri = entry.getKey();
		    if (uri.startsWith(prefixUri)) {
		    	prefix = entry.getValue();
		    	break;
		    }
		}
		return prefix;
	}

	private View getView() {
		return this.view;
	}
}
