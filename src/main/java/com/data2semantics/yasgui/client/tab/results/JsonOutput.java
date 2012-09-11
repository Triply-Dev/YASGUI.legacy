package com.data2semantics.yasgui.client.tab.results;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.google.gwt.dom.client.Style.Overflow;
import com.smartgwt.client.widgets.HTMLPane;

public class JsonOutput extends HTMLPane {
	private static String APPEND_INPUT_ID = "_jsonResults";
	private View view;
	private QueryTab tab;
	private String jsonString;
	private String inputId;
	public JsonOutput(View view, QueryTab tab, String jsonString) {
		jsonString = jsonString.replace("\n", "");
		this.tab = tab;
		this.view = view;
		setWidth100();
		setHeight100();
		this.jsonString = jsonString;
		this.inputId = tab.getID() + APPEND_INPUT_ID;
		drawTextArea();
	}
	
	private void drawTextArea() {
		setContents("<textarea " + "id=\"" + getInputId() + "\"" + ">" + jsonString + "</textarea>");
	}
	
	public String getInputId() {
		return this.inputId;
	}

}
