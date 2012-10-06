package com.data2semantics.yasgui.client.tab;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.TooltipProperties;
import com.smartgwt.client.widgets.HTMLPane;

public class QueryTextArea extends HTMLPane {
	@SuppressWarnings("unused")
	private View view;
	private static String APPEND_INPUT_ID = "_queryInput";
	private String inputId;
	private QueryTab tab;
	public static int HEIGHT = 350;
	public QueryTextArea(View view, QueryTab tab) {
		this.tab = tab;
		this.view = view;
		this.inputId = tab.getID() + APPEND_INPUT_ID;
		setHeight(Integer.toString(HEIGHT) + "px");
		setTextArea();
	}
	public void showTooltips() {
		TooltipProperties tProp = new TooltipProperties();
		tProp.setId(getDOM().getId());
		tProp.setContent("Start typing the PREFIX definition to get an autocompletion list of prefixes.<br>Prefix missing? Add your prefix to <a href=\"http://prefix.cc/\" target=\"_blank\">prefix.cc</a>, and refresh autocompletion list (via config menu)");
		tProp.setMy(TooltipProperties.POS_LEFT_CENTER);
		tProp.setAt(TooltipProperties.POS_TOP_CENTER);
		tProp.setXOffset(-50);
		tProp.setYOffset(23);
		Helper.drawTooltip(tProp);
	}
	public void setTextArea() {
		setContents("<textarea " + "id=\"" + getInputId() + "\"" + ">" + tab.getTabSettings().getQueryString() + "</textarea>");
	}
	
	public String getQuery() {
		JsMethods.saveCodeMirror(getInputId());
		return JsMethods.getValueUsingId(inputId);
	}
	
	public String getInputId() {
		return this.inputId;
	}

}
