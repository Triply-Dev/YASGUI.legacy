package com.data2semantics.yasgui.client;

import com.smartgwt.client.widgets.HTMLPane;

public class QueryTextArea extends HTMLPane {
	private View view;
	public static String QUERY_INPUT_ID = "queryInput";
	public static int WIDTH = 350;
	public QueryTextArea(View view) {
		this.view = view;
		setHeight(Integer.toString(WIDTH) + "px");
		setContents(getTextArea());
	}
	

	private String getTextArea() {
		String textArea = "" + "<textarea " + "id=\"" + QUERY_INPUT_ID + "\"" + ">" + getView().getSettings().getQueryString() + "</textarea>";
		return textArea;

	}
	
	private View getView() {
		return view;
	}

}
