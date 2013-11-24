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

public class EndpointInfo implements Serializable {
	private static final long serialVersionUID = 905442860112391094L;
	private String endpoint;
	
	private AutocompletionTypeInfo propertyInfo = new AutocompletionTypeInfo();
	private AutocompletionTypeInfo classInfo = new AutocompletionTypeInfo();
	public String getEndpoint() {
		return endpoint;
	}
	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	public AutocompletionTypeInfo getPropertyInfo() {
		return this.propertyInfo;
	}
	public AutocompletionTypeInfo getClassInfo() {
		return this.classInfo;
	}
	
	public String toString() {
		String result = "EndpointInfo ("+ endpoint + "):\n";
		result += propertyInfo.toString() + "\n";
		result += classInfo.toString() + "\n";
		return result;
	}
}