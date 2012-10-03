package com.data2semantics.yasgui.client.tab.results.input;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.tab.results.ResultContainer;
import com.data2semantics.yasgui.shared.exceptions.SparqlEmptyException;
import com.data2semantics.yasgui.shared.exceptions.SparqlParseException;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Object to parse and validate a sparql json string
 */
public class XmlResults implements SparqlResults{
	@SuppressWarnings("unused")
	private View view;
	private int queryMode;
	private boolean booleanResult;
	private ArrayList<String> variables = new ArrayList<String>();
	
	private ArrayList<HashMap<String, HashMap<String, String>>> bindings = new ArrayList<HashMap<String, HashMap<String, String>>>();
	public XmlResults(String xmlString, View view, int queryMode) throws SparqlParseException, SparqlEmptyException {
		this.view = view;
		this.queryMode = queryMode;
		processResults(xmlString);
	}
	
	/**
	 * Main parser method
	 * @param xmlString xml string to parse
	 * @throws SparqlParseException When json string is not valid
	 * @throws SparqlEmptyException When json string is valid, but contains no results
	 */
	public void processResults(String xmlString) throws SparqlParseException, SparqlEmptyException {
		if (xmlString == null || xmlString.length() == 0) {
			throw new SparqlParseException("Unable to parse empty xml string");
		}
		Document xmlDoc = XMLParser.parse(xmlString);
		//no need for this anymore, and it can be quite big. Fingers crossed and hope garbage collector deals witht this properly
		xmlString = null; 
		
		if (queryMode == ResultContainer.RESULT_TYPE_TABLE) {
			storeVariables(xmlDoc);
			storeBindings(xmlDoc);
		} else if (queryMode == ResultContainer.RESULT_TYPE_BOOLEAN) {
			storeBooleanResult(xmlDoc);
		}
		
		
	}
	
	public ArrayList<String> getVariables() {
		return this.variables;
	}
	
	private void storeVariables(Document xmlDoc) throws SparqlParseException, SparqlEmptyException {
		NodeList variables = xmlDoc.getElementsByTagName("variable");
		if (variables.getLength() == 0) {
			throw new SparqlEmptyException("Variables missing from xml");
		}
		for (int i = 0; i < variables.getLength(); i++) {
			Node variable = variables.item(i);
			if (variable == null) {
				throw new SparqlParseException("Variable in head parsed as null");
			}
			String varName = ((Element)variable).getAttribute("name");
			if (varName == null) {
				throw new SparqlParseException("Variable in head has null value");
			}
			this.variables.add(varName);
		}
	}
	
	public ArrayList<HashMap<String, HashMap<String, String>>> getBindings() {
		return this.bindings;
	}
	
	private void storeBindings(Document xmlDoc) throws SparqlParseException, SparqlEmptyException {
		NodeList xmlSolutions = xmlDoc.getElementsByTagName("result");
		if (xmlSolutions.getLength() == 0) {
			throw new SparqlEmptyException("No solutions found in xml");
		}
		//Loop through results
		for (int i = 0; i < xmlSolutions.getLength(); i++) {
			bindings.add(getSolutionFromNode(xmlSolutions.item(i)));
		}
	}
	
	
	private HashMap<String, HashMap<String, String>> getSolutionFromNode(Node node) {
		HashMap<String, HashMap<String, String>> solution = new HashMap<String, HashMap<String, String>>();
		
		NodeList bindings = ((Element) node).getElementsByTagName("binding");
		for (int i = 0; i < bindings.getLength(); i++) {
			Node binding = bindings.item(i);
			solution.put(((Element) binding).getAttribute("name"), getBindingFromNode(binding));
		}
		return solution;
	}
	
	private HashMap<String, String> getBindingFromNode(Node binding) {
		HashMap<String, String> rdfNode = new HashMap<String, String>();
		NodeList literals = ((Element) binding).getElementsByTagName("literal");
		if (literals.getLength() > 0) {
			rdfNode = extractValuesFromXmlNode(literals.item(0), new String[]{"xml:lang", "datatype"});
			rdfNode.put("type", "literal");
		}
		NodeList bnodes = ((Element) binding).getElementsByTagName("bnode");
		if (bnodes.getLength() > 0) {
			Node bnode = bnodes.item(0);
			rdfNode.put("type", "bnode");
			rdfNode.put("value", bnode.getFirstChild().getNodeValue());
		}
		NodeList uris = ((Element) binding).getElementsByTagName("uri");
		if (uris.getLength() > 0) {
			Node uri = uris.item(0);
			rdfNode.put("type", "uri");
			rdfNode.put("value", uri.getFirstChild().getNodeValue());
		}
		return rdfNode;
	}
	
	private HashMap<String, String> extractValuesFromXmlNode(Node xmlNode, String[] attributes) {
		HashMap<String, String> rdfNode = new HashMap<String, String>();
		rdfNode.put("value", xmlNode.getFirstChild().getNodeValue());
		for (String attribute: attributes) {
			String attributeValue = ((Element)xmlNode).getAttribute(attribute);
			if (attributeValue != null) rdfNode.put(attribute, attributeValue);
		}
		return rdfNode;
	}
	
	public boolean getBooleanResult() {
		return booleanResult;
	}
	
	
	private void storeBooleanResult(Document xmlDoc) throws SparqlParseException {
		NodeList booleanNodeList = xmlDoc.getElementsByTagName("boolean");
		if (booleanNodeList.getLength() == 0) {
			throw new SparqlParseException("Missing boolean value in xml");
		}
		if (booleanNodeList.item(0).getFirstChild().getNodeValue().equals("true")) {
			booleanResult = true;
		} else {
			booleanResult = false;
		}
	}

}
