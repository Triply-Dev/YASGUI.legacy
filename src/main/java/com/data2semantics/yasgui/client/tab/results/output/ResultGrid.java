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
package com.data2semantics.yasgui.client.tab.results.output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.tab.results.input.ResultsHelper;
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
	private HashMap<Integer, HashMap<String, HashMap<String, String>>> solutions = new HashMap<Integer, HashMap<String, HashMap<String, String>>>();
	@SuppressWarnings("unused")
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
		queryPrefixes = Helper.getPrefixesFromQuery(view.getSelectedTabSettings().getQueryString());
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
			if (binding != null) {
				String type = binding.get("type");
				if (type.equals("uri")) {
					final String uri = binding.get("value");
					return Helper.getLinkNewWindow(ResultsHelper.getShortUri(uri, queryPrefixes), uri);
				} else if (type.equals("literal") || binding.get("type").equals("typed-literal")) {
					String literal = ResultsHelper.getLiteralFromBinding(binding);
					Label label = new Label(literal);
					label.setOverflow(Overflow.VISIBLE);
					label.setWidth100();
					label.setAutoHeight();
					label.setCanSelectText(true);
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
	
}
