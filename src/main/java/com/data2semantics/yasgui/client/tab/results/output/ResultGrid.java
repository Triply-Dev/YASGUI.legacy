package com.data2semantics.yasgui.client.tab.results.output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.tab.results.input.SparqlResults;
import com.data2semantics.yasgui.shared.Prefix;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class ResultGrid extends ListGrid {
	private static String SOLUTION_ATTRIBUTE = "yasgui___solution";
	private static String XSD_DATA_PREFIX = "http://www.w3.org/2001/XMLSchema#";
	private HashMap<Integer, HashMap<String, HashMap<String, String>>> solutions = new HashMap<Integer, HashMap<String, HashMap<String, String>>>();
	private View view;
	private SparqlResults sparqlResults;
	private HashMap<String, Prefix> queryPrefixes = new HashMap<String, Prefix>();
	public ResultGrid(View view, SparqlResults sparqlResults) {
		this.view = view;
		this.sparqlResults = sparqlResults;
		setWidth100();
		setHeight100();
		setShowRecordComponents(true);
		setShowRecordComponentsByCell(true);
		setShowRowNumbers(true);
		setFixedRecordHeights(false);
		setWrapCells(true);
		setCanResizeFields(true);
		queryPrefixes = Helper.getPrefixesFromQuery(getView().getSelectedTabSettings().getQueryString());
		drawQueryResults();
	}
	
	/**
	 * Take json string from query results, parse it, and draw in this table
	 * 
	 * @param jsonString
	 */
	public void drawQueryResults() {
		List<ListGridField> listGridFields = getVarsAsListGridFields(sparqlResults.getVariables());
		setFields(listGridFields.toArray(new ListGridField[listGridFields.size()]));
		List<ListGridRecord> rows = getSolutionsAsGridRecords(sparqlResults.getBindings());
		setData(rows.toArray(new ListGridRecord[rows.size()]));
	}
	
	/**
	 * Overridden method of ListGrid. Need to use this, because otherwise we cannot add different widgets to different cells
	 */
	protected Canvas createRecordComponent(ListGridRecord row, Integer colNum) {
		// fieldname is the identifier of the column, in our case the same as
		// the column header
		String colName = this.getFieldName(colNum);
		
		//the numbering field created by smartgwt has field name starting with $
		if (!colName.startsWith("$")) { 
			HashMap<String, HashMap<String, String>> bindings = solutions.get(row.getAttributeAsInt(SOLUTION_ATTRIBUTE));
			HashMap<String, String> binding = bindings.get(colName);
			String type = binding.get("type");
			if (type.equals("uri")) {
				final String uri = binding.get("value");
				Prefix prefix = getPrefixForUri(uri);
				String text = uri;
				if (prefix != null) {
					text = prefix.getPrefix() + ":" + uri.substring(prefix.getUri().length());
				}
				return Helper.getLinkNewWindow(text, uri);
			} else if (type.equals("literal")) {
				String literal = binding.get("value");
				Label label = new Label(literal);
				label.setOverflow(Overflow.VISIBLE);
				label.setWidth100();
				label.setAutoHeight();
				label.setCanSelectText(true);
				if (binding.containsKey("datatype") && binding.get("datatype") != null) {
					label.setPrompt("xsd:" + binding.get("datatype").substring(XSD_DATA_PREFIX.length()));
				}
				return label;
			} else {
				//is bnode
				String uri = binding.get("value");
				Label label = new Label(uri);
				label.setHeight100();
				label.setCanSelectText(true);
				label.setWidth100();
				return label;
			}
		}
		return null;
	}
	
	/**
	 * Get solutions from json object, and add as object to listgridrecords (i.e. table row)
	 * 
	 * @param solutions
	 * @return
	 */
	private ArrayList<ListGridRecord> getSolutionsAsGridRecords(ArrayList<HashMap<String, HashMap<String, String>>> solutions) {
		ArrayList<ListGridRecord> rows = new ArrayList<ListGridRecord>();
		for (HashMap<String, HashMap<String, String>> solution: solutions) {
			this.solutions.put(solution.hashCode(), solution);
			ListGridRecord row = new ListGridRecord();
			row.setAttribute(SOLUTION_ATTRIBUTE, solution.hashCode());
			rows.add(row);
			
		}
		return rows;
	}
	
	/**
	 * Get used vars from json object, and add them as variables (i.e. columns) to this listgrid 
	 * @param vars
	 * @return
	 */
	private ArrayList<ListGridField> getVarsAsListGridFields(ArrayList<String> vars) {
		ArrayList<ListGridField> listGridFields = new ArrayList<ListGridField>();
		for(String var: vars){
			ListGridField field = new ListGridField(var, var);
			field.setCellAlign(Alignment.LEFT);
			field.setAlign(Alignment.CENTER); //for header
			
			listGridFields.add(field);
		}
		return listGridFields;
	}
	
	/**
	 * Check for a uri whether there is a prefix defined in the query. Used to shorten uri's in the resultgrid
	 * @param uri
	 * @return null on no prefix found, or a string of the prefix
	 */
	private Prefix getPrefixForUri(String uri) {
		Prefix prefix = null;
		for (Map.Entry<String, Prefix> entry : queryPrefixes.entrySet()) {
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
