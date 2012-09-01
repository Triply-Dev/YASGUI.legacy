package com.data2semantics.yasgui.client;

import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
	
	public void setAutocompletePrefixes(boolean forceUpdate) {
		getView().onLoadingStart();
		// get prefixes from server
		getView().getRemoteService().fetchPrefixes(forceUpdate, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				getView().onError(caught.getMessage());
			}

			public void onSuccess(String prefixes) {
				JsMethods.setAutocompletePrefixes(prefixes);
				getView().onLoadingFinish();
			}
		});

	}

}
