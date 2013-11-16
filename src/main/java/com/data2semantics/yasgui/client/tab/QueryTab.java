package com.data2semantics.yasgui.client.tab;

/*
 * #%L
 * YASGUI
 * %%
 * Copyright (C) 2013 Laurens Rietveld
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.data2semantics.yasgui.client.RpcElement;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.TooltipProperties;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.data2semantics.yasgui.client.settings.TooltipText;
import com.data2semantics.yasgui.client.tab.optionbar.DownloadLink;
import com.data2semantics.yasgui.client.tab.optionbar.LinkCreator;
import com.data2semantics.yasgui.client.tab.optionbar.OutputSelection;
import com.data2semantics.yasgui.client.tab.optionbar.QueryConfigMenu;
import com.data2semantics.yasgui.client.tab.optionbar.bookmarks.AddToBookmarks;
import com.data2semantics.yasgui.client.tab.optionbar.bookmarks.BookmarkedQueries;
import com.data2semantics.yasgui.client.tab.optionbar.endpoints.EndpointInput;
import com.data2semantics.yasgui.client.tab.optionbar.endpoints.EndpointSearch;
import com.data2semantics.yasgui.client.tab.results.ResultContainer;
import com.data2semantics.yasgui.shared.exceptions.ElementIdException;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemSeparator;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;
import com.smartgwt.client.widgets.tab.Tab;

public class QueryTab extends Tab implements RpcElement {
	private static final int TOOLTIP_VERSION_SEARCH_ICON = 1;
	private View view;
	private QueryTextArea queryTextArea;
	private EndpointInput endpointInput;
	private VLayout vLayout = new VLayout();
	private ResultContainer queryResultContainer;
	private TabSettings tabSettings;
	private OutputSelection outputSelection;
	private EndpointSearch searchIcon;
	private QueryConfigMenu queryConfigMenu;
	private DownloadLink downloadLink;
	private BookmarkedQueries bookmarkedQueries;
	private AddToBookmarks addToBookmarks;
	private String queryType;
	private LinkCreator linkCreator;
	public QueryTab(View view, TabSettings tabSettings) {
		super(tabSettings.getTabTitle());
		this.tabSettings = tabSettings;
		this.view = view;
		this.queryResultContainer = new ResultContainer(view, this);
		setCanClose(true);
		
		
		//For each tab we create, check the cors setting of the endpoint
		JsMethods.checkCorsEnabled(getTabSettings().getEndpoint());
		vLayout.addMember(getQueryOptionBar());
		
		queryTextArea = new QueryTextArea(view, this);
		vLayout.addMember(queryTextArea);
		
		vLayout.addMember(queryResultContainer);
		setPane(vLayout);
		setContextMenu();
	}
	
	/**
	 * Get option bar for this query, e.g. endpoint input, bookmarking funcationlity, permalink, etc etc
	 * @return
	 */
	private HLayout getQueryOptionBar() {
		HLayout queryOptions = new HLayout();
		queryOptions.setDefaultLayoutAlign(VerticalAlignment.BOTTOM);
		queryOptions.setHeight(25);
		if (view.getSettings().isDbSet()) {
			bookmarkedQueries = new BookmarkedQueries(view);
			queryOptions.addMember(bookmarkedQueries);
		}
		if (view.getEnabledFeatures().endpointSelectionEnabled()) {
			endpointInput = new EndpointInput(view, this);
			queryOptions.addMember(endpointInput);
		
			searchIcon = new EndpointSearch(view);
			queryOptions.addMember(searchIcon);

		}
		
		outputSelection = new OutputSelection(view, this);
		queryOptions.addMember(outputSelection);
		
		if (JsMethods.stringToDownloadSupported()) {
			downloadLink = new DownloadLink(view);
			queryOptions.addMember(downloadLink);
		}
		
		try {
			queryConfigMenu = new QueryConfigMenu(view);
			queryOptions.addMember(queryConfigMenu);
		} catch (IllegalStateException e) {
			//we don't have anything to add to this menu (everything is disabled)
			//just ignore
		}
		
		queryOptions.addMember(Helper.getHSpacer());
		
		linkCreator = new LinkCreator(view);
		queryOptions.addMember(linkCreator);
		
		if (view.getSettings().isDbSet()) {
			LayoutSpacer hSpacer = new LayoutSpacer();
			hSpacer.setWidth(6);
			queryOptions.addMember(hSpacer);
			addToBookmarks = new AddToBookmarks(view);
			queryOptions.addMember(addToBookmarks);
		}
		return queryOptions;
	}

	/**
	 * Create context menu used for the tab bar.
	 */
	private void setContextMenu() {
		Menu menu = new Menu();
		MenuItem copy = new MenuItem();
		copy.setTitle("Create copy");
		copy.setIcon(Imgs.COPY_TAB.get());
		copy.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(MenuItemClickEvent event) {
				int tabNumber = ((QueryTabs) getTabSet()).getTabNumber(getTabObject().getID());
				TabSettings tabSettings = (TabSettings) view.getSettings().getTabArray().get(tabNumber).clone();
				tabSettings.setTabTitle("Copy of " + tabSettings.getTabTitle());
				((QueryTabs) getTabSet()).addTab(tabSettings, true);
				view.getSettings().addTabSettings(tabSettings);
			}
		});

		MenuItem renameTab = new MenuItem();
		renameTab.setTitle("Rename Tab");
		renameTab.setIcon(Imgs.EDIT_TEXT.get());
		renameTab.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(MenuItemClickEvent event) {
				((QueryTabs) getTabSet()).editTabTitle(getTabObject());
			}
		});
		MenuItem closeTab = new MenuItem();
		closeTab.setTitle("Close");
		closeTab.setIcon(Imgs.CLOSE_TAB_SINGLE.get());
		closeTab.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(MenuItemClickEvent event) {
				((QueryTabs) getTabSet()).removeAndPostProcessTab(getTabObject());
				view.getHistory().setHistoryCheckpoint();
			}
		});
		MenuItem closeOtherTabs = new MenuItem();
		closeOtherTabs.setTitle("Close others");
		closeOtherTabs.setIcon(Imgs.CLOSE_TAB_OTHERS.get());
		closeOtherTabs.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(MenuItemClickEvent event) {
				((QueryTabs) getTabSet()).removeAllExcept(getTabObject());
				view.getHistory().setHistoryCheckpoint();
			}
		});

		MenuItem closeAll = new MenuItem();
		closeAll.setTitle("Close all");
		closeAll.setIcon(Imgs.CLOSE_TAB_ALL.get());
		closeAll.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(MenuItemClickEvent event) {
				((QueryTabs) getTabSet()).removeAllTabs();
				view.getHistory().setHistoryCheckpoint();
			}
		});

		MenuItemSeparator separator = new MenuItemSeparator();
		menu.setItems(renameTab, copy, separator, closeTab, closeOtherTabs, closeAll);
		setContextMenu(menu);

	}
	public void showTooltips(int fromVersionId) throws ElementIdException {
		queryTextArea.showTooltips(fromVersionId);
		showSearchIconTooltip(fromVersionId);
		if (queryConfigMenu != null) {
			queryConfigMenu.showTooltips(fromVersionId);
		}
		linkCreator.showToolTips(fromVersionId);
		if (addToBookmarks != null) {
			addToBookmarks.showToolTips(fromVersionId);
		}
		
	}
	private void showSearchIconTooltip(int fromVersionId) {
		if (searchIcon != null && fromVersionId < TOOLTIP_VERSION_SEARCH_ICON) {
			TooltipProperties tProp = new TooltipProperties();
			tProp.setId(searchIcon.getDOM().getId());
			tProp.setContent(TooltipText.ENDPOINT_SEARCH_ICON);
			tProp.setMy(TooltipProperties.POS_BOTTOM_LEFT);
			tProp.setAt(TooltipProperties.POS_TOP_CENTER);
			tProp.setYOffset(9);
//			tProp.setXOffset(-1);
			Helper.drawTooltip(tProp);
		}
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

	public TabSettings getTabSettings() {
		return this.tabSettings;
	}
	
	public EndpointInput getEndpointInput() {
		return this.endpointInput;
	}
	public void setQueryType(String queryType) {
		this.queryType = queryType;
		adaptInterfaceToQueryType();
	}
	
	public void adaptInterfaceToQueryType() {
		outputSelection.adaptToQueryType(queryType);
	}
	
	public String getQueryType() {
		return this.queryType;
	}
	public DownloadLink getDownloadLink() {
		return this.downloadLink;
	}
	public void setQueryString(String queryString) {
		queryTextArea.setQuery(queryString);
	}
	public void setEndpoint(String endpoint) {
		endpointInput.setEndpoint(endpoint);
	}
	public BookmarkedQueries getBookmarkedQueries() {
		return this.bookmarkedQueries;
	}
	public AddToBookmarks getAddToBookmarks() {
		return this.addToBookmarks;
	}


	public void disableRpcElements() {
		if (bookmarkedQueries != null) bookmarkedQueries.disableRpcElements();
		if (linkCreator != null) linkCreator.disableRpcElements();
		if (addToBookmarks != null) addToBookmarks.disableRpcElements();
	}

	@Override
	public void enableRpcElements() {
		if (bookmarkedQueries != null) bookmarkedQueries.enableRpcElements();
		if (linkCreator != null) linkCreator.enableRpcElements();
		if (addToBookmarks != null) addToBookmarks.enableRpcElements();
	}
	public QueryConfigMenu getQueryConfigMenu() {
		return queryConfigMenu;
	}
}
