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

import com.data2semantics.yasgui.client.QueryTabs;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.TooltipProperties;
import com.data2semantics.yasgui.client.settings.Icons;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.data2semantics.yasgui.client.settings.TooltipText;
import com.data2semantics.yasgui.client.tab.optionbar.AddToBookmarks;
import com.data2semantics.yasgui.client.tab.optionbar.DownloadLink;
import com.data2semantics.yasgui.client.tab.optionbar.EndpointInput;
import com.data2semantics.yasgui.client.tab.optionbar.EndpointSearch;
import com.data2semantics.yasgui.client.tab.optionbar.LinkCreator;
import com.data2semantics.yasgui.client.tab.optionbar.OutputSelection;
import com.data2semantics.yasgui.client.tab.optionbar.QueryConfigMenu;
import com.data2semantics.yasgui.client.tab.optionbar.bookmarks.BookmarkedQueries;
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

public class QueryTab extends Tab {
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
		
		vLayout.addMember(getQueryOptionBar());
		
		queryTextArea = new QueryTextArea(view, this);
		vLayout.addMember(queryTextArea);
		
		vLayout.addMember(queryResultContainer);
		setPane(vLayout);
		setContextMenu();
	}
	
	private HLayout getQueryOptionBar() {
		HLayout queryOptions = new HLayout();
		queryOptions.setDefaultLayoutAlign(VerticalAlignment.BOTTOM);
		queryOptions.setHeight(35);
		if (view.getSettings().isDbSet()) {
			bookmarkedQueries = new BookmarkedQueries(view);
			queryOptions.addMember(bookmarkedQueries);
		}
		if (!view.getSettings().inSingleEndpointMode()) {
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
		
		queryConfigMenu = new QueryConfigMenu(view);
		queryOptions.addMember(queryConfigMenu);
		
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setWidth100();
		queryOptions.addMember(spacer);
		
		
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
		copy.setIcon(Icons.COPY_TAB);
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
		renameTab.setIcon(Icons.EDIT_TEXT);
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
	public void showTooltips(int fromVersionId) throws ElementIdException {
		queryTextArea.showTooltips(fromVersionId);
		showSearchIconTooltip(fromVersionId);
		queryConfigMenu.showTooltips(fromVersionId);
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
			tProp.setMy(TooltipProperties.POS_BOTTOM_CENTER);
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
}
