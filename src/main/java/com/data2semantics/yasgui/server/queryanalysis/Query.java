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

import java.util.Set;

import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.sparql.core.Prologue;
import com.hp.hpl.jena.sparql.syntax.Element;

public class Query extends com.hp.hpl.jena.query.Query {
	
	private SparqlElementVisitor visitor = null;
	public Query() {};
	public Query(Prologue prologue) {
		super(prologue);
	}
	
	public static Query create(String queryString) {
		Query query = new Query();
		query = (Query)(QueryFactory.parse(query, queryString, null, Syntax.defaultQuerySyntax));
		query.generateQueryStats();
		return query;
	}
	
	private void generateQueryStats() {
		Element queryElement = getQueryPattern();
		visitor = new SparqlElementVisitor();
		if (queryElement == null) return;
		queryElement.visit(visitor);
		visitor.cleanPossibles();
	}
	
	public Set<String> getProperties() {
		if (visitor == null) generateQueryStats();
		return visitor.getProperties();
	}
	public Set<String> getPossibleProperties() {
		if (visitor == null) generateQueryStats();
		return visitor.getPossibleProperties();
	}
	public Set<String> getClasses() {
		if (visitor == null) generateQueryStats();
		return visitor.getClasses();
	}
	public Set<String> getPossibleClasses() {
		if (visitor == null) generateQueryStats();
		return visitor.getPossibleClasses();
	}
	public static void main(String[] args) {
		Query query = Query.create("PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> SELECT * WHERE {<http://bfsdfdfdfdfdf> rdfs:subClassOf <http://bfsd>}");
		for (String classString:query.getClasses()) {
			System.out.println("asd" + classString);
		}
		
	}
	
}