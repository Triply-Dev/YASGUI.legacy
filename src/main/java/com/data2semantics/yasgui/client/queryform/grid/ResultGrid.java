package com.data2semantics.yasgui.client.queryform.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.SparqlJsonHelper;
import com.data2semantics.yasgui.client.queryform.QueryTab;
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
//		setCanSelectText(true);
		
		getPrefixesFromQuery();
		drawQueryResultsFromJson(jsonString);
	}

	public void drawQueryResultsFromJson(String jsonString) {
		try {
			results = new SparqlJsonHelper(getView(), jsonString);
		} catch (SparqlParseException e) {
			getView().onError(e);
		} catch (SparqlEmptyException e) {
			setEmptyMessage(e.getMessage());
			redraw();
		}
		List<ListGridField> listGridFields = getVarsAsListGridFields(results.getVariables());
		setFields(listGridFields.toArray(new ListGridField[listGridFields.size()]));
		List<ListGridRecord> rows = getSolutionsAsGridRecords(results.getQuerySolutions());
		setData(rows.toArray(new ListGridRecord[rows.size()]));
	}
	
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
	
	
	private ArrayList<ListGridField> getVarsAsListGridFields(JSONArray resultVars) {
		ArrayList<ListGridField> listGridFields = new ArrayList<ListGridField>();
		for(int i = 0; i < resultVars.size(); i++){
			String resultVar = results.getAsString(resultVars.get(i));
			ListGridField field = new ListGridField(resultVar, resultVar);
			field.setCellAlign(Alignment.LEFT);
			field.setAlign(Alignment.CENTER); //for header
			
			listGridFields.add(field);
		}
		return listGridFields;
	}
	
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
