package com.data2semantics.yasgui.shared.autocompletions;

/*
 * #%L
 * YASGUI
 * %%
 * Copyright (C) 2013 - 2014 Laurens Rietveld
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

public enum AutocompletionConfigCols implements Serializable {
	ENDPOINT("endpoint", null),
	TYPE("type", "type of completions", false, 120),
	METHOD_QUERY("query", "# fetched from analyzing queries", false),
	METHOD_QUERY_RESULTS("queryResults", "# fetched from querying endpoint", false);
	
	private String key;
	private String label;
	private int width = -1;
	private boolean allowWrap = false;

	private AutocompletionConfigCols(String key, String label) {
		this.key = key;
		this.label = label;
	}
	private AutocompletionConfigCols(String key, String label, boolean allowWrap) {
		this.key = key;
		this.label = label;
		this.allowWrap = allowWrap;
	}
	private AutocompletionConfigCols(String key, String label, boolean allowWrap, int width) {
		this.key = key;
		this.label = label;
		this.width = width;
		this.allowWrap = allowWrap;
	}
	
	public String getKey() {
		return this.key;
	}
	public String getLabel() {
		return this.label;
	}
	public int getWidth() {
		return this.width;
	}
	public boolean useWrap() {
		return this.allowWrap;
	}
	
	
};