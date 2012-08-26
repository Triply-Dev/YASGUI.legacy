package com.data2semantics.yasgui.client.queryform.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.SparqlJsonHelper;
import com.data2semantics.yasgui.shared.Prefix;
import com.data2semantics.yasgui.shared.exceptions.SparqlEmptyException;
import com.data2semantics.yasgui.shared.exceptions.SparqlParseException;
import com.data2semantics.yasgui.shared.rdf.RdfNodeContainer;
import com.data2semantics.yasgui.shared.rdf.ResultSetContainer;
import com.data2semantics.yasgui.shared.rdf.SolutionContainer;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
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
	JSONObject queryResult = new JSONObject();
	private View view;
	private HashMap<String, Prefix> prefixes;
	private SparqlJsonHelper results;
	public ResultGrid(View view) {
		this.view = view;
		this.prefixes = getView().getQueryPrefixes();
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
			if (node.get("type").toString().equals("uri")) {
				final String uri = node.get("value").isString().stringValue();
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
			} else if (node.get("type").toString().equals("literal")) {
				String literal = node.get("value").isString().stringValue();
				Label label = new Label(literal);
				label.setHeight100();
				label.setWidth100();
				if (node.get("datatype") != null) {
					label.setPrompt("xsd:" + node.get("datatype").isString().stringValue().substring(XSD_DATA_PREFIX.length()));
				}
				return label;
			} else {
				//is bnode
				String uri = node.get("value").isString().stringValue();
				Label label = new Label(uri);
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
			getView().getLogger().severe("1");
			String resultVar = results.getAsString(resultVars.get(i));
			ListGridField field = new ListGridField(VARIABLE_PREFIX + resultVar, resultVar);
			listGridFields.add(field);
			getView().getLogger().severe("2");
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
