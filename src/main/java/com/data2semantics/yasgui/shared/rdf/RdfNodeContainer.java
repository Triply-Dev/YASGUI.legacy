/*******************************************************************************
 * Copyright (c)  2012 Laurens Rietveld
 * 
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.data2semantics.yasgui.shared.rdf;

import java.io.Serializable;

public class RdfNodeContainer implements Serializable {
	private static final long serialVersionUID = 1L;
	private String varName;
	private String value;
	private boolean isUri;
	private boolean isAnon;
	private boolean isLiteral;
	private String datatypeUri;
	
	public RdfNodeContainer() {
		
	}
	
	public String getVarName() {
		return varName;
	}
	public void setVarName(String varName) {
		this.varName = varName;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

	public boolean isUri() {
		return isUri;
	}

	public void setIsUri(boolean isUri) {
		this.isUri = isUri;
	}

	public boolean isAnon() {
		return isAnon;
	}

	public void setIsAnon(boolean isAnon) {
		this.isAnon = isAnon;
	}

	public boolean isLiteral() {
		return isLiteral;
	}

	public void setIsLiteral(boolean isLiteral) {
		this.isLiteral = isLiteral;
	}

	public void setDatatype(String datatypeUri) {
		this.datatypeUri = datatypeUri;
		
	}
	public String getDatatypeUri() {
		return this.datatypeUri;
	}
	
	
		
		
}
