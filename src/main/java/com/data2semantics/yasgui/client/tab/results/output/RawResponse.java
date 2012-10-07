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
package com.data2semantics.yasgui.client.tab.results.output;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.smartgwt.client.widgets.HTMLPane;

public class RawResponse extends HTMLPane {
	private static String APPEND_INPUT_ID = "_rawResponse";
	@SuppressWarnings("unused")
	private View view;
	private String responseString;
	private String inputId;
	public RawResponse(View view, QueryTab tab, String responseString) {
		this.view = view;
		this.responseString = responseString;
		this.inputId = tab.getID() + APPEND_INPUT_ID;
		drawTextArea();
	}
	
	private void drawTextArea() {
		setContents("<textarea style=\"overflow:scroll;\" " + "id=\"" + getInputId() + "\"" + ">" + responseString + "</textarea>");
	}
	
	public String getInputId() {
		return this.inputId;
	}
}
