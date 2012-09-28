package com.data2semantics.yasgui.client.tab;

import com.data2semantics.yasgui.client.QueryTabs;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.data2semantics.yasgui.client.tab.results.ResultContainer;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.layout.HLayout;
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
	private ResultContainer queryResultContainer;
	private TabSettings tabSettings;
	private OutputSelection outputSelection;
	
	public QueryTab(View view, TabSettings tabSettings) {
		super(tabSettings.getTabTitle());
		this.tabSettings = tabSettings;
		this.view = view;
		this.queryResultContainer = new ResultContainer(getView(), this);
		setCanClose(true);
		HLayout queryOptions = new HLayout();
		queryOptions.setDefaultLayoutAlign(VerticalAlignment.BOTTOM);
		queryOptions.setHeight(35);
		endpointInput = new EndpointInput(getView(), this);
		queryOptions.addMember(endpointInput);
		
		EndpointSearch searchIcon = new EndpointSearch(getView());
		
		queryOptions.addMember(searchIcon);

		outputSelection = new OutputSelection(getView(), this);
		queryOptions.addMember(outputSelection);
		
		vLayout.addMember(queryOptions);

		queryTextArea = new QueryTextArea(getView(), this);
		vLayout.addMember(queryTextArea);

		vLayout.addMember(queryResultContainer);
		setPane(vLayout);
		setContextMenu();
	}

	/**
	 * Create context menu used for the tab bar.
	 */
	private void setContextMenu() {
		Menu menu = new Menu();
		MenuItem copy = new MenuItem();
		copy.setTitle("Create copy");
		copy.setIcon("icons/fugue/document-copy.png");
		copy.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				int tabNumber = ((QueryTabs) getTabSet()).getTabNumber(getTabObject().getID());
				TabSettings settings = (TabSettings) getView().getSettings().getTabArray().get(tabNumber).clone();
				settings.setTabTitle("Copy of " + settings.getTabTitle());
				((QueryTabs) getTabSet()).addTab(settings, true);
			}
		});

		MenuItem renameTab = new MenuItem();
		renameTab.setTitle("Rename Tab");
		renameTab.setIcon("icons/fugue/edit.png");
		renameTab.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(MenuItemClickEvent event) {
				((QueryTabs) getTabSet()).editTabTitle(getTabObject());
			}
		});
		MenuItem closeTab = new MenuItem();
		closeTab.setTitle("Close");
		closeTab.setIcon("icons/custom/close-one.png");
		closeTab.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(MenuItemClickEvent event) {
				((QueryTabs) getTabSet()).removeAndPostProcessTab(getTabObject());
			}
		});
		MenuItem closeOtherTabs = new MenuItem();
		closeOtherTabs.setTitle("Close others");
		closeOtherTabs.setIcon("icons/custom/close-others.png");
		closeOtherTabs.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(MenuItemClickEvent event) {
				((QueryTabs) getTabSet()).removeAllExcept(getTabObject());

			}
		});

		MenuItem closeAll = new MenuItem();
		closeAll.setTitle("Close all");
		closeAll.setIcon("icons/custom/close-all.png");
		closeAll.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(MenuItemClickEvent event) {
				((QueryTabs) getTabSet()).removeAllTabs();

			}
		});

		MenuItemSeparator separator = new MenuItemSeparator();
		menu.setItems(renameTab, copy, separator, closeTab, closeOtherTabs, closeAll);
		setContextMenu(menu);

	}

	private QueryTab getTabObject() {
		return this;
	}

	public ResultContainer getResultContainer() {
		return this.queryResultContainer;
	}

	public QueryTextArea getQueryTextArea() {
		return this.queryTextArea;
	}

	private View getView() {
		return this.view;
	}

	public TabSettings getTabSettings() {
		return this.tabSettings;
	}
	
	public EndpointInput getEndpointInput() {
		return this.endpointInput;
	}
}
