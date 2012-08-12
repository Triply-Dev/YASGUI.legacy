package com.data2semantics.yasgui.shared;

import java.io.Serializable;
import java.util.HashMap;

public class SolutionContainer implements Serializable {
	private static final long serialVersionUID = 1L;
	private HashMap<String, RdfNodeContainer> rdfNodes = new HashMap<String, RdfNodeContainer>();
	
	public SolutionContainer() {
		
	}
	
	public void addRdfNodeContainer(RdfNodeContainer rdfNode) {
		rdfNodes.put(rdfNode.getVarName(), rdfNode);
	}
	
	public HashMap<String, RdfNodeContainer> getRdfNodes() {
		return this.rdfNodes;
	}
	
	public RdfNodeContainer get(String varName) {
		RdfNodeContainer node = null;
//		if (rdfNodes.containsKey(varName)) {
			node = rdfNodes.get(varName);
//		}
		return node;
	}
		
}
