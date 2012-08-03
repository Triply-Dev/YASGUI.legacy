package com.data2semantics.yasgui.shared;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class ResultSetContainer implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<String> resultVars;
	private List<SolutionContainer> querySolutions = new ArrayList<SolutionContainer>();
	
	public ResultSetContainer() {
		
	}
	public void setResultVars(List<String> resultVars) {
		this.resultVars = resultVars;
		
	}
	
	public List<String> getResultVars() {
		return this.resultVars;
	}
	
	public void addQuerySolution(SolutionContainer solution) {
		this.querySolutions.add(solution);
	}
	
	public List<SolutionContainer> getQuerySolutions() {
		return this.querySolutions;
	}
	


}
