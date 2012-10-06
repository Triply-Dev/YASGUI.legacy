package com.data2semantics.yasgui.client.tab;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.TooltipProperties;
import com.data2semantics.yasgui.client.helpers.properties.TooltipText;
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
		showPrefixTooltip();
		showKeyShortcutsTooltip();
	}
	
	private void showPrefixTooltip() {
		TooltipProperties tProp = new TooltipProperties();
		tProp.setId(getDOM().getId());
		tProp.setContent(TooltipText.QUERY_PREFIXES_AUTOCOMPLETE);
		tProp.setMy(TooltipProperties.POS_LEFT_CENTER);
		tProp.setAt(TooltipProperties.POS_TOP_CENTER);
		tProp.setXOffset(-50);
		tProp.setYOffset(23);
		Helper.drawTooltip(tProp);
	}
	
	private void showKeyShortcutsTooltip() {
		TooltipProperties tProp = new TooltipProperties();
		tProp.setId(getDOM().getId());
		tProp.setContent(TooltipText.QUERY_KEYBOARD_SHORTCUTS);
		tProp.setMy(TooltipProperties.POS_CENTER);
		tProp.setAt(TooltipProperties.POS_LEFT_CENTER);
		tProp.setXOffset(200);
//		tProp.setYOffset(23);
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
