package com.data2semantics.yasgui.client;

import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.smartgwt.client.widgets.HTMLPane;

public class QueryTextArea extends HTMLPane {
	private View view;
	private static String APPEND_INPUT_ID = "_queryInput";
	private String inputId;
	public static int WIDTH = 350;
	public QueryTextArea(View view, String tabId) {
		this.view = view;
		this.inputId = getID() + APPEND_INPUT_ID;
		setHeight(Integer.toString(WIDTH) + "px");
		setContents(getTextArea());
	}
	

	private String getTextArea() {
		String textArea = "" + "<textarea " + "id=\"" + getInputId() + "\"" + ">" + getView().getSelectedTabSettings().getQueryString() + "</textarea>";
		return textArea;

	}
	
	public String getQuery() {
		return JsMethods.getValueUsingId(inputId);
	}
	
	private View getView() {
		return view;
	}
	
	public String getInputId() {
		return this.inputId;
	}

}
