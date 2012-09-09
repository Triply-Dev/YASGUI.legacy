package com.data2semantics.yasgui.client.tab.results;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.SparqlJsonHelper;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.data2semantics.yasgui.shared.Prefix;
import com.data2semantics.yasgui.shared.exceptions.SparqlEmptyException;
import com.data2semantics.yasgui.shared.exceptions.SparqlParseException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
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
	JSONObject queryResult = new JSONObject();
	private View view;
	private SparqlJsonHelper results;
	private QueryTab tab;
	private HashMap<String, Prefix> queryPrefixes = new HashMap<String, Prefix>();
	public ResultGrid(View view, QueryTab tab, String jsonString) {
		this.tab = tab;
		this.view = view;
		setWidth100();
		setHeight(350);
		setShowRecordComponents(true);
		setShowRecordComponentsByCell(true);
		setShowRowNumbers(true);
		setFixedRecordHeights(false);
		setWrapCells(true);
		setCanResizeFields(true);
		
		getPrefixesFromQuery();
		drawQueryResultsFromJson(jsonString);
	}
	
	/**
	 * Take json string from query results, parse it, and draw in this table
	 * 
	 * @param jsonString
	 */
	public void drawQueryResultsFromJson(String jsonString) {
		try {
			results = new SparqlJsonHelper(jsonString, getView());
		} catch (SparqlParseException e) {
			getView().onError(e);
		} catch (SparqlEmptyException e) {
			setEmptyMessage(e.getMessage());
			redraw();
			return;
		}
		List<ListGridField> listGridFields = getVarsAsListGridFields(results.getVariables());
		setFields(listGridFields.toArray(new ListGridField[listGridFields.size()]));
		List<ListGridRecord> rows = getSolutionsAsGridRecords(results.getQuerySolutions());
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
			JSONObject solution = (JSONObject) row.getAttributeAsObject(SOLUTION_ATTRIBUTE);
			JSONObject node = solution.get(colName).isObject();
			String type = node.get("type").isString().stringValue();
			if (type.equals("uri")) {
				final String uri = node.get("value").isString().stringValue();
				Prefix prefix = getPrefixForUri(uri);
				String text = uri;
				if (prefix != null) {
					text = prefix.getPrefix() + ":" + uri.substring(prefix.getUri().length());
				}
				return Helper.getLinkNewWindow(text, uri);
			} else if (type.equals("literal")) {
				String literal = node.get("value").isString().stringValue();
				Label label = new Label(literal);
				label.setOverflow(Overflow.VISIBLE);
				label.setWidth100();
				label.setAutoHeight();
				label.setCanSelectText(true);
				if (node.get("datatype") != null) {
					label.setPrompt("xsd:" + node.get("datatype").isString().stringValue().substring(XSD_DATA_PREFIX.length()));
				}
				return label;
			} else {
				//is bnode
				String uri = node.get("value").isString().stringValue();
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
	 * @param querySolutions
	 * @return
	 */
	private ArrayList<ListGridRecord> getSolutionsAsGridRecords(JSONArray querySolutions) {
		ArrayList<ListGridRecord> rows = new ArrayList<ListGridRecord>();
		for (int i = 0; i < querySolutions.size(); i++) {
			JSONObject solution = results.getAsObject(querySolutions.get(i));
			ListGridRecord row = new ListGridRecord();
			row.setAttribute(SOLUTION_ATTRIBUTE, solution);
			rows.add(row);
		}
		return rows;
	}
	
	/**
	 * Get used vars from json object, and add them as variables (i.e. columns) to this listgrid 
	 * @param resultVars
	 * @return
	 */
	private ArrayList<ListGridField> getVarsAsListGridFields(JSONArray resultVars) {
		getView().getLogger().severe("in resultgrid2");
		ArrayList<ListGridField> listGridFields = new ArrayList<ListGridField>();
		getView().getLogger().severe("in resultgrid3");
		for(int i = 0; i < resultVars.size(); i++){
			getView().getLogger().severe("in resultgrid4");
			String resultVar = results.getAsString(resultVars.get(i));
			getView().getLogger().severe("in resultgrid5");
			ListGridField field = new ListGridField(resultVar, resultVar);
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
	
	/**
	 * Checks to query string and retrieves/stores all defined prefixes in an object variable
	 */
	private void getPrefixesFromQuery() {
		String query = JsMethods.getValueUsingId(tab.getQueryTextArea().getInputId());
		RegExp regExp = RegExp.compile("^\\s*PREFIX\\s*(\\w*):\\s*<(.*)>\\s*$", "gm");
		while (true) {
			MatchResult matcher = regExp.exec(query);
			if (matcher == null)
				break;
			queryPrefixes.put(matcher.getGroup(2), new Prefix(matcher.getGroup(1), matcher.getGroup(2)));
		}
	}
}
