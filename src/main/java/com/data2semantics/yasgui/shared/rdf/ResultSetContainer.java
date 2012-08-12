package com.data2semantics.yasgui.shared.rdf;

import java.util.ArrayList;
import java.io.Serializable;


public class ResultSetContainer implements Serializable {

	private static final long serialVersionUID = 1L;
	private ArrayList<String> resultVars;
	private ArrayList<SolutionContainer> querySolutions = new ArrayList<SolutionContainer>();
	
	public ResultSetContainer() {
		
	}
	public void setResultVars(ArrayList<String> resultVars) {
		this.resultVars = resultVars;
		
	}
	
	public ArrayList<String> getResultVars() {
		return this.resultVars;
	}
	
	public void addQuerySolution(SolutionContainer solution) {
		this.querySolutions.add(solution);
	}
	
	public ArrayList<SolutionContainer> getQuerySolutions() {
		return this.querySolutions;
	}
	


}
