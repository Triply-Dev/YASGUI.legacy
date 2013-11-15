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
	private int optionalDepth = 0;
	private int unionDepth = 0;
	private Set<String> properties = new HashSet<String>();
	private Set<String> possibleProperties = new HashSet<String>();
		
	public Set<String> getProperties() {
		return properties;
	}
	public Set<String> getPossibleProperties() {
		return possibleProperties;
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
			if (!isVariable(t.getPredicate())) {
				if (optionalDepth > 0 || unionDepth > 0) {
					possibleProperties.add(t.getPredicate().getURI());
				} else {
					properties.add(t.getPredicate().getURI());
				}
			}
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
	



	public void cleanPossibleProperties() {
		for (String property: properties) {
			if (possibleProperties.contains(property)) {
				possibleProperties.remove(property);
			}
		}

		
	}
	public void visit(ElementFetch el) {
	}

}
