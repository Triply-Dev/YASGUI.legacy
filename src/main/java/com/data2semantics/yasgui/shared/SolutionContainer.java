package com.data2semantics.yasgui.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SolutionContainer implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<RdfNodeContainer> rdfNodes = new ArrayList<RdfNodeContainer>();
	
	public SolutionContainer() {
		
	}
	
	public void addRdfNodeContainer(RdfNodeContainer rdfNode) {
		rdfNodes.add(rdfNode);
	}
	
	public List<RdfNodeContainer> getRdfNodes() {
		return this.rdfNodes;
	}
		
}
