package com.data2semantics.yasgui.client.queryform;

import com.data2semantics.yasgui.client.EndpointInput;
import com.data2semantics.yasgui.client.QueryTextArea;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.queryform.grid.ResultGrid;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemSeparator;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.Tab;

public class QueryTab extends Tab {
	private View view;
	private QueryTextArea queryTextArea;
	private EndpointInput endpointInput;
	private VLayout vLayout = new VLayout();
	private VLayout queryResultContainer = new VLayout();
	private ResultGrid resultGrid;
	private TabSettings tabSettings;
	public QueryTab(View view, TabSettings tabSettings) {
		super(tabSettings.getTabTitle());
		this.tabSettings = tabSettings;
		this.view = view;
		setCanClose(true);
		
		endpointInput = new EndpointInput(getView(), this);
		vLayout.addMember(endpointInput);
		
		queryTextArea = new QueryTextArea(getView(), this);
		vLayout.addMember(queryTextArea);
		//queryResultContainer.addMember(testGrid);

		vLayout.addMember(queryResultContainer);

		setPane(vLayout);

		setContextMenu();
	}

	private void setContextMenu() {
		Menu menu = new Menu();
		MenuItem copy = new MenuItem();
		copy.setTitle("Create copy");
		copy.setIcon("icons/fugue/document-copy.png");
		copy.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				int tabNumber = ((QueryTabs)getTabSet()).getTabNumber(getTabObject().getID());
				TabSettings settings = (TabSettings)getView().getSettings().getTabArray().get(tabNumber).clone();
				settings.setTabTitle("Copy of " + settings.getTabTitle());
				((QueryTabs)getTabSet()).addTab(settings, true);
			}
		});
		
		MenuItem renameTab = new MenuItem();
		renameTab.setTitle("Rename Tab");
		renameTab.setIcon("icons/fugue/edit.png");
		renameTab.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(MenuItemClickEvent event) {
				((QueryTabs)getTabSet()).editTabTitle(getTabObject());
			}
		});
		MenuItem closeTab = new MenuItem();
		closeTab.setTitle("Close");
		closeTab.setIcon("icons/custom/close-one.png");
		closeTab.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(MenuItemClickEvent event) {
				((QueryTabs)getTabSet()).removeAndPostProcessTab(getTabObject());
			}
		});
		MenuItem closeOtherTabs = new MenuItem();
		closeOtherTabs.setTitle("Close others");
		closeOtherTabs.setIcon("icons/custom/close-others.png");
		closeOtherTabs.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(MenuItemClickEvent event) {
				((QueryTabs)getTabSet()).removeAllExcept(getTabObject());
				
			}
		});
		
		MenuItem closeAll = new MenuItem();
		closeAll.setTitle("Close all");
		closeAll.setIcon("icons/custom/close-all.png");
		closeAll.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(MenuItemClickEvent event) {
				((QueryTabs)getTabSet()).removeAllTabs();
				
			}
		});
		
		MenuItemSeparator separator = new MenuItemSeparator();
		menu.setItems(renameTab, copy, separator, closeTab, closeOtherTabs, closeAll);
		setContextMenu(menu);

	}
	
	private QueryTab getTabObject() {
		return this;
	}

	public void resetQueryResult() {
		Canvas[] members = queryResultContainer.getMembers();
		for (Canvas member : members) {
			queryResultContainer.removeMember(member);
		}
	}

	public void addQueryResult(ResultGrid resultGrid) {
		resetQueryResult();
		this.resultGrid = resultGrid;
		queryResultContainer.addMember(resultGrid);
	}

	public QueryTextArea getQueryTextArea() {
		return this.queryTextArea;
	}

	private View getView() {
		return this.view;
	}

	public void drawResultsInTable(String jsonResult) {
		resultGrid.drawQueryResultsFromJson(jsonResult);
	}
	
	public TabSettings getTabSettings() {
		return this.tabSettings;
	}
}
