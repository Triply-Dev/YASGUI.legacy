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
