package com.data2semantics.yasgui.client.tab;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.smartgwt.client.widgets.HTMLPane;

public class QueryTextArea extends HTMLPane {
	private View view;
	private static String APPEND_INPUT_ID = "_queryInput";
	private String inputId;
	private QueryTab tab;
	public static int WIDTH = 350;
	public QueryTextArea(View view, QueryTab tab) {
		this.tab = tab;
		this.view = view;
		this.inputId = tab.getID() + APPEND_INPUT_ID;
		setHeight(Integer.toString(WIDTH) + "px");
		setTextArea();
	}
	
	public void setTextArea() {
		setContents("<textarea " + "id=\"" + getInputId() + "\"" + ">" + tab.getTabSettings().getQueryString() + "</textarea>");
	}
	
	public String getQuery() {
		JsMethods.saveCodeMirror(getInputId());
		return JsMethods.getValueUsingId(inputId);
	}
	
	private View getView() {
		return view;
	}
	
	public String getInputId() {
		return this.inputId;
	}

}
