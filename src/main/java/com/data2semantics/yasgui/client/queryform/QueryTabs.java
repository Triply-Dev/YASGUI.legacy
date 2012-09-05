package com.data2semantics.yasgui.client.queryform;

import java.util.ArrayList;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.TabTitleEditEvent;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

public class QueryTabs extends TabSet {
	private View view;

	public QueryTabs(View view) {
		this.view = view;
		setTabBarPosition(Side.TOP);
		setTabBarAlign(Side.LEFT);
		setWidth100();
		setHeight100();
		setCanEditTabTitles(true);
		setTitleEditEvent(TabTitleEditEvent.DOUBLECLICK);
		addNewTab();

	}

	private View getView() {
		return this.view;
	}

	public void addNewTab() {
		QueryTab tab = new QueryTab(getView(), getTabTitle("Query"));
		addTab(tab);
		selectTab(tab);
		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				QueryTab tab = (QueryTab) getSelectedTab();
				JsMethods.attachCodeMirror(tab.getQueryTextArea().getInputId());
			}
		});
	}
	
	private String getTabTitle(String title) {
		Tab[] tabs = getTabs();
		ArrayList<String> tabTitles = new ArrayList<String>();
		for (Tab tab: tabs) {
			tabTitles.add(tab.getTitle());
		}
		if (tabTitles.contains(title)) {
			return createTitle(tabTitles, title, 1);
		} else {
			return title;
		}
	}
	
	private String createTitle(ArrayList<String> tabTitles, String title, int i) {
		String newTitle = title + "(" + Integer.toString(i) + ")";
		if (tabTitles.contains(newTitle)) {
			return createTitle(tabTitles, title, i + 1);
		} else {
			return newTitle;
		}
	}
}
