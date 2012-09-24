package com.data2semantics.yasgui.client.tab.results;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.HTMLPane;

public class JsonOutput extends HTMLPane {
	private static String APPEND_INPUT_ID = "_jsonResults";
	private View view;
	private String jsonString;
	private String inputId;
	public JsonOutput(View view, QueryTab tab, String jsonString) {
		this.view = view;
		//Do this because otherwise the vertical scrollbar of codemirror causes an horizontal gwt scrollbar.
//		setWidth(Window.getClientWidth() - 10);
		setWidth100();
		setHeight100();
		setOverflow(Overflow.AUTO);
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
	
	@SuppressWarnings("unused")
	private View getView() {
		return this.view;
	}

}
