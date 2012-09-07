package com.data2semantics.yasgui.client.queryform.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.Overflow;
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
	JSONObject queryResult = new JSONObject();
	private View view;
	private SparqlJsonHelper results;
	private QueryTab tab;
	private HashMap<String, Prefix> queryPrefixes = new HashMap<String, Prefix>();
	public ResultGrid(View view, QueryTab tab) {
		this.tab = tab;
		this.view = view;
		setWidth100();
		setHeight(350);
		setShowRecordComponents(true);
		setShowRecordComponentsByCell(true);
		setShowRowNumbers(true);
		setFixedRecordHeights(false);
		setWrapCells(true);
//		setAutoFitData(Autofit.VERTICAL);
		setCanResizeFields(true);
		setEmptyMessage("Executing query");
		getPrefixesFromQuery();
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
		if (colName.startsWith(VARIABLE_PREFIX)) { 
			String varName = colName.substring(VARIABLE_PREFIX.length());
			JSONObject solution = (JSONObject) row.getAttributeAsObject(SOLUTION_PREFIX);
			JSONObject node = solution.get(varName).isObject();
			String type = node.get("type").isString().stringValue();
			if (type.equals("uri")) {
				final String uri = node.get("value").isString().stringValue();
				Prefix prefix = getPrefixForUri(uri);
				String text = uri;
				if (prefix != null) {
					text = prefix.getPrefix() + ":" + uri.substring(prefix.getUri().length());
				}
				
//				HTMLPane html = new HTMLPane();
//				html.setContents("<a href=\"" + uri + "\" target=\"_blank\">" + text + "</a>");
//				html.setHeight100();
//				html.setWidth100();
//				return html;
				return Helper.getLinkNewWindow(text, uri);
			} else if (type.equals("literal")) {
				String literal = node.get("value").isString().stringValue() + "fffffffffffffffffff ffffffffffffffffffffffffffffffffffffffffffffffff ffffffffffffffffffffffffffffffffffffffffffffffff ffffffffffffffffffffffffffffffffffffffffffffffff ffffffffffffffffffffffffffffffffffffffffffffffff ffffffffffffffffffffffffffffffffffffffffffffffff ffffffffffffffffffffffffffffffffffffffffffffffff ffffffffffffffffffffffffffffffffffffffffffffffff ffffffffffffffffffffffffffffffffffffffffffffffff ffffffffffffffffffffffffffffffffffffffffffffffff ffffffffffffffffffffffffffffffffffffffffffffffff ffffffffffffffffffffffffffffffffffffffffffffffff fffffffffffffffffffffffffffff";
				Label label = new Label(literal);
				label.setOverflow(Overflow.VISIBLE);
				label.setWidth100();
				label.setAutoHeight();
				
				if (node.get("datatype") != null) {
					label.setPrompt("xsd:" + node.get("datatype").isString().stringValue().substring(XSD_DATA_PREFIX.length()));
				}
				return label;
			} else {
				//is bnode
				String uri = node.get("value").isString().stringValue() + "dddddddddddddddddddddddddddd ddddddddddddddddddddddddddddddd";
				Label label = new Label(uri);
				label.setHeight100();
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
			row.setAttribute(SOLUTION_PREFIX, solution);
			
		    Set<String> myIter = solution.keySet();
		    Iterator<String> iterator = myIter.iterator();
		    while(iterator.hasNext()){
		    	String varName = iterator.next();
		    	JSONObject varAttributes = solution.get(varName).isObject();
		    	row.setAttribute(varName, varAttributes.get("value").isString().stringValue());
		    }
			rows.add(row);
		}
		return rows;
	}
	
	
	private ArrayList<ListGridField> getVarsAsListGridFields(JSONArray resultVars) {
		ArrayList<ListGridField> listGridFields = new ArrayList<ListGridField>();
		for(int i = 0; i < resultVars.size(); i++){
			String resultVar = results.getAsString(resultVars.get(i));
			ListGridField field = new ListGridField(VARIABLE_PREFIX + resultVar, resultVar);
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
