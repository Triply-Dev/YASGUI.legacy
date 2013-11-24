package com.data2semantics.yasgui.server.queryanalysis;

/*
 * #%L
 * YASGUI
 * %%
 * Copyright (C) 2013 Laurens Rietveld
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementAssign;
import com.hp.hpl.jena.sparql.syntax.ElementBind;
import com.hp.hpl.jena.sparql.syntax.ElementDataset;
import com.hp.hpl.jena.sparql.syntax.ElementExists;
import com.hp.hpl.jena.sparql.syntax.ElementFetch;
import com.hp.hpl.jena.sparql.syntax.ElementFilter;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementMinus;
import com.hp.hpl.jena.sparql.syntax.ElementNamedGraph;
import com.hp.hpl.jena.sparql.syntax.ElementNotExists;
import com.hp.hpl.jena.sparql.syntax.ElementOptional;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementService;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.sparql.syntax.ElementUnion;
import com.hp.hpl.jena.sparql.syntax.ElementVisitor;


public class SparqlElementVisitor implements ElementVisitor {
	private static String TYPE_URI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
	private static String RANGE_URI = "http://www.w3.org/2000/01/rdf-schema#range";
	private static String DOMAIN_URI = "http://www.w3.org/2000/01/rdf-schema#domain";
	private static String SUBCLASS_URI = "http://www.w3.org/2000/01/rdf-schema#subClassOf";
	
	
	private int optionalDepth = 0;
	private int unionDepth = 0;
	private Set<String> properties = new HashSet<String>();
	private Set<String> possibleProperties = new HashSet<String>();
	private Set<String> classes = new HashSet<String>();
	private Set<String> possibleClasses = new HashSet<String>();
	
		
	public Set<String> getProperties() {
		return properties;
	}
	public Set<String> getPossibleProperties() {
		return possibleProperties;
	}
	public Set<String> getClasses() {
		return classes;
	}
	public Set<String> getPossibleClasses() {
		return possibleClasses;
	}
	public void visit(ElementTriplesBlock el) {
//		System.out.println("TRIPLESBLOCK");
	}

	public boolean isVariable(Node node) {
		return (node == null || node.isVariable() || node.isBlank());
	}
	public void visit(ElementPathBlock el) {
		Iterator<TriplePath> it = el.patternElts();
		while(it.hasNext()) {
			TriplePath t = it.next();
			getProperties(t);
			getClasses(t);
			
		}
	}
	
	
	public void visit(ElementFilter el) {
	}

	
	public void visit(ElementAssign el) {
	}

	
	public void visit(ElementBind el) {
	}

	
	public void visit(ElementUnion el) {
		//spaces(); System.out.println("UNION");
		
		
		unionDepth++;
		for(Element e : el.getElements()) {
			e.visit(this);
		}
		unionDepth--;
		
	}

	
	public void visit(ElementOptional el) {
		//spaces(); System.out.println("OPTIONAL");
		
		optionalDepth++;
		el.getOptionalElement().visit(this);
		optionalDepth--;
		
//		tripleExtractVisitor.clear();
//		el.getOptionalElement().visit(tripleExtractVisitor);
//		query.numOptionalTriples.add(tripleExtractVisitor.getNumTriples());
//		
//		query.numOptionalBlocks.increase();
	}

	
	public void visit(ElementGroup el) {
		
		for(Element e : el.getElements()) {
			e.visit(this);
		}
//		query.numBlocks.increase();
	}

	
	public void visit(ElementDataset el) {
	}

	
	public void visit(ElementNamedGraph el) {
	}

	
	public void visit(ElementExists el) {
	}

	
	public void visit(ElementNotExists el) {
	}

	
	public void visit(ElementMinus el) {
	}

	
	public void visit(ElementService el) {
		//spaces(); System.out.println("Service");
//		query.numServiceCalls.increase();
	}

	

	
	public void visit(ElementSubQuery el) {
//		System.out.println("Subquery"+ el.getQuery().toString());
//		query.numSubQueries.increase();
	}
	



	public void cleanPossibles() {
		for (String property: properties) {
			if (possibleProperties.contains(property)) {
				possibleProperties.remove(property);
			}
		}
		for (String classString: classes) {
			if (possibleClasses.contains(classString)) {
				possibleClasses.remove(classString);
			}
		}
	}
	
	private void getProperties(TriplePath t) {
		if (!isVariable(t.getPredicate())) {
			if (isAPossible()) {
				possibleProperties.add(t.getPredicate().getURI());
			} else {
				properties.add(t.getPredicate().getURI());
			}
		}
	}
	
	private boolean isAPossible() {
		return (optionalDepth > 0 || unionDepth > 0);
	}
	
	private boolean classInObjectPosition(TriplePath t) {
		return t.getPredicate().getURI().equals(TYPE_URI) || 
				t.getPredicate().getURI().equals(SUBCLASS_URI) ||
				t.getPredicate().getURI().equals(DOMAIN_URI) ||
				t.getPredicate().getURI().equals(RANGE_URI);
	}
	private boolean classInSubjectPosition(TriplePath t) {
		return t.getPredicate().getURI().equals(SUBCLASS_URI);
	}
	
	private void getClasses(TriplePath t) {
		System.out.println(t.toString());
		Set<String> classes = new HashSet<String>();
		if (!isVariable(t.getPredicate())) {
			//check if predicate indicates a class at object position
			if (!isVariable(t.getObject()) && classInObjectPosition(t)) {
				classes.add(t.getObject().getURI());
			}
			if (!isVariable(t.getSubject()) && classInSubjectPosition(t)) {
				classes.add(t.getSubject().getURI());
			}
		}
		if (isAPossible()) {
			possibleClasses.addAll(classes);
		} else {
			this.classes.addAll(classes);
		}
	}
	public void visit(ElementFetch el) {
	}

}
