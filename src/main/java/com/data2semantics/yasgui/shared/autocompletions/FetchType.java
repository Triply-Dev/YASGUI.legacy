package com.data2semantics.yasgui.shared.autocompletions;

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

import java.io.Serializable;

public enum FetchType implements Serializable {
	CLASSES("class", "classes"),
	PROPERTIES("property", "properties");
	private String singular;
	private String plural;

	private FetchType(){}
	private FetchType(String singular, String plural) {
		this.singular = singular;
		this.plural = plural;
	}
	public String getSingular() {
		return this.singular;
	}
	public String getSingularCamelCase() {
		return this.singular.substring(0, 1).toUpperCase() + this.singular.substring(1);
	}
	public String getPlural() {
		return this.plural;
	}
	public String getPluralCamelCase() {
		return this.plural.substring(0, 1).toUpperCase() + this.plural.substring(1);
	}
}