package com.data2semantics.yasgui.server;

import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.RDFVisitor;
import com.hp.hpl.jena.rdf.model.Resource;

public class CustomRdfVisitor implements RDFVisitor {

	public String visitBlank(Resource resource, AnonId anonId) {
		return anonId.getLabelString();
	}

	public String visitLiteral(Literal literal) {
		return literal.toString();
	}

	public String visitURI(Resource resource, String uri) {
		return uri;
	}

}
