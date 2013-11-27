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

import java.util.ArrayList;

import com.data2semantics.yasgui.client.RpcElement;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.helpers.TooltipProperties;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.settings.Settings;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.data2semantics.yasgui.client.settings.TooltipText;
import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.data2semantics.yasgui.client.tab.results.output.RawResponse;
import com.data2semantics.yasgui.shared.exceptions.ElementIdException;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.types.TabTitleEditEvent;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.CloseClickHandler;
import com.smartgwt.client.widgets.tab.events.TabCloseClickEvent;
import com.smartgwt.client.widgets.tab.events.TabDeselectedEvent;
import com.smartgwt.client.widgets.tab.events.TabDeselectedHandler;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.smartgwt.client.widgets.tab.events.TabTitleChangedEvent;
import com.smartgwt.client.widgets.tab.events.TabTitleChangedHandler;
import com.smartgwt.client.widgets.tab.events.TabsReorderedEvent;
import com.smartgwt.client.widgets.tab.events.TabsReorderedHandler;

public class QueryTabs extends TabSet implements RpcElement {
	private View view;
	private static boolean STORE_SETTINGS_ON_CLOSE_DEFAULT = true;
	private ImgButton addTabButton;
	
	public static int INDENT_TABBAR_START = 41; //space reserved for buttons on lhs
	public static int INDENT_TABBAR_END = 300; //space reserved for exec query on rhs
	private HLayout controls;
	private LayoutSpacer controlsSpacer;
	private static int TOOLTIP_VERSION_TAB_SELECTION = 1;
	
	public QueryTabs(final View view) {
		setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER, Helper.getHSpacer(53));
		this.view = view;
		setTabBarPosition(Side.TOP);
		setTabBarAlign(Side.LEFT);
		setOverflow(Overflow.HIDDEN);
		setWidth100();
		setHeight100();
		setCanEditTabTitles(true);
		setTitleEditorTopOffset(5);
		setCanReorderTabs(true);
		setTitleEditorLeftOffset(5);
		addTabDeselectedHandler(new TabDeselectedHandler(){
			//this works for within smartgwt. on codemirror focus needs separate event though
			@Override
			public void onTabDeselected(TabDeselectedEvent event) {
				saveTabTitle();
			}});
		addTabsReorderedHandler(new TabsReorderedHandler() {
			public void onTabsReordered(TabsReorderedEvent event) {
				
				
			}
		});
		setTitleEditEvent(TabTitleEditEvent.DOUBLECLICK);
		setTabsFromSettings();
		
		selectTab(view.getSettings().getSelectedTabNumber());
		// Need to schedule attaching of codemirror. It uses doc.getElementById,
		// which doesnt work if element hasnt been drawn yet
		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				JsMethods.initializeQueryCodemirror(((QueryTab) getSelectedTab()).getQueryTextArea().getInputId());
				((QueryTab) getSelectedTab()).getQueryTextArea().adjustForContent(true);
				((QueryTab) getSelectedTab()).getResultContainer().drawIfPossible();
			}
		});
		addHandlers();
		addTabControls();
	}
	
	public void showTooltips(int fromVersionId) throws ElementIdException {
		showTabSelectionTooltip(fromVersionId);
	}
	
	private void showTabSelectionTooltip(int fromVersionId) throws ElementIdException {
		if (fromVersionId < TOOLTIP_VERSION_TAB_SELECTION) {
			TooltipProperties tProp = new TooltipProperties();
			tProp.setId(getDOM().getId());
			tProp.set(TooltipText.TAB_SELECTION);
			tProp.setMy(TooltipProperties.POS_LEFT_CENTER);
			tProp.setAt(TooltipProperties.POS_LEFT_TOP);
			tProp.setXOffset(126);
			tProp.setYOffset(12);
			Helper.drawTooltip(tProp);
		}
	}

	/**
	 * Load all tabs defined in our settings object, and load them
	 */
	private void setTabsFromSettings() {
		ArrayList<TabSettings> tabArray = view.getSettings().getTabArray();
		if (tabArray.size() == 0) {
			// Don't have anything yet. Just draw a new tab with default vals
			addTab(new TabSettings(view.getSettings()));
		} else {
			for (TabSettings tabSettings : tabArray) {
				addTab(tabSettings);
			}
		}
	}
	
	public void redrawTabs() {
		int selectedTabNumber = view.getSettings().getSelectedTabNumber();
		removeAllTabs(false, false);
		setTabsFromSettings();
		selectTab(selectedTabNumber);
	}

	/**
	 * Add controls (e.g. 'add new tab') to the tab bar
	 */
	private void addTabControls() {
		controls = new HLayout();
		controls.setBackgroundColor("white");
		controlsSpacer = new LayoutSpacer();
		controlsSpacer.setWidth100();
		controls.addMember(controlsSpacer);
		controls.setWidth(INDENT_TABBAR_START - 2);
		controls.setHeight(27);
		
		addTabButton = new ImgButton();
		addTabButton.setSrc(Imgs.ADD_TAB.get());
		addTabButton.setShowDown(false);
		addTabButton.setShowRollOver(false);
		addTabButton.setWidth(25);
		addTabButton.setTooltip("add new tab");
		
		addTabButton.setHeight(25);
		addTabButton.setZIndex(ZIndexes.TAB_CONTROLS);//Otherwise the onclick of the tab bar is used..
		addTabButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				TabSettings tabSettings = new TabSettings(view.getSettings());
				view.getSettings().addTabSettings(tabSettings);
				view.getTabs().addTab(tabSettings, true);
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
				view.getHistory().setHistoryCheckpoint();
			}
		});
		controls.setZIndex(ZIndexes.TAB_CONTROLS);
		controls.addMember(addTabButton);
		addChild(controls);
	}
	
	/**
	 * Define all handlers of the tabs on the tab bar
	 */
	private void addHandlers() {
		addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				view.getSettings().setSelectedTabNumber(event.getTabNum());
				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
				Scheduler.get().scheduleDeferred(new Command() {
					public void execute() {
						JsMethods.initializeQueryCodemirror(((QueryTab) getSelectedTab()).getQueryTextArea().getInputId());
						((QueryTab) getSelectedTab()).getQueryTextArea().adjustForContent(true);
						((QueryTab) getSelectedTab()).getResultContainer().drawIfPossible();
						view.getHistory().setHistoryCheckpoint();
					}
				});
			}
		});
		addTabTitleChangedHandler(new TabTitleChangedHandler() {
			@Override
			public void onTabTitleChanged(TabTitleChangedEvent event) {
				Settings settings = view.getSettings();
				int tabIndex = getTabNumber(event.getTab().getID());
				//Don't use selected one. Title may change by context menu, when other tab is selected
				settings.getTabArray().get(tabIndex).setTabTitle(event.getNewTitle());
				LocalStorageHelper.storeSettingsInCookie(settings);
			}
		});
		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(TabCloseClickEvent event) {
				closePreProcess((QueryTab)event.getTab(), true);
				closePostProcess((QueryTab)event.getTab());
				view.getHistory().setHistoryCheckpoint();
			}
		});
	}
	
	/**
	 * Remove all tabs except one
	 * @param except QueryTab to keep
	 */
	public void removeAllExcept(QueryTab except) {
		Tab[] tabs = getTabs();
		for (Tab tab: tabs) {
			if (!tab.equals(except)) {
				removeTab((QueryTab)tab, false, true);
			}
		}
		LocalStorageHelper.storeSettingsInCookie(view.getSettings());
	}
	
	/**
	 * Remove all tabs (and stores settings in local storage)
	 */
	public void removeAllTabs() {
		removeAllTabs(true, true);
	}
	
	public void removeAllTabs(boolean storeSettings, boolean updateSettings) {
		Tab[] tabs = getTabs();
		for (Tab tab: tabs) {
			removeTab((QueryTab)tab, false, updateSettings);
		}
		if (storeSettings) {
			LocalStorageHelper.storeSettingsInCookie(view.getSettings());
		}
	}
	
	
	/**
	 * Remove a single tab
	 * @param tab Tab to remove
	 * @param storeSettings Whether to save settings in cookie as well. 
	 * Use this settings if this methods is called often, we want to store once instead of multiple times for performance reasons 
	 */
	public void removeTab(QueryTab tab, boolean storeSettings, boolean updateSettings) {
		closePreProcess(tab, updateSettings);
		removeTab(tab);
		if (updateSettings) {
			closePostProcess(tab, storeSettings);
		}
	}
	
	/**
	 * Do code cleanup and updating of settings before tab is actually removed
	 * 
	 * @param queryTab
	 */
	public void closePreProcess(QueryTab queryTab, boolean updateSettings) {
		Settings settings = view.getSettings();
		if (updateSettings) {
			settings.removeTabSettings(getTabNumber(queryTab.getID()));
		}
		// To avoid codemirror js objects lying around, remove js objects
		// belonging to this tab
		JsMethods.destroyCodeMirrorQueryInput(queryTab.getQueryTextArea().getInputId());
		RawResponse jsonOutput = queryTab.getResultContainer().getRawResponseOutput();
		if (jsonOutput != null) {
			//We have outputted query results as json string using the codemirror highlighter. Also cleanup this object
			JsMethods.destroyCodeMirrorQueryResponse(jsonOutput.getInputId());
		}
		
	}
	
	public void removeAndPostProcessTab(QueryTab tab) {
		removeTab(tab, STORE_SETTINGS_ON_CLOSE_DEFAULT, true);
	}
	
	/**
	 * Postprocess for after removing of tab. Use this to update settings with the new selected tab number
	 * 
	 * @param queryTab
	 * @param storeSettings Whether to store settings in cookie. We might not always want to do this (when removing multiple tabs we want to do it only once)
	 */
	public void closePostProcess(QueryTab queryTab, boolean storeSettings) {
		Settings settings = view.getSettings();
		settings.setSelectedTabNumber(getSelectedTabNumber());
		if (storeSettings) {
			LocalStorageHelper.storeSettingsInCookie(settings);
		}
	}
	
	/**
	 * @see closePostProcess(QueryTab queryTab, boolean storeSettings)
	 * @param queryTab
	 */
	public void closePostProcess(QueryTab queryTab) {
		closePostProcess(queryTab, STORE_SETTINGS_ON_CLOSE_DEFAULT);
	}
	
	/**
	 * Caller for editing of tab title. Opens input area on tab bar
	 * 
	 * @param queryTab tab to edit title for
	 */
	public void editTabTitle(QueryTab queryTab) {
		int tabNumber = getTabNumber(queryTab.getID());
		editTabTitle(tabNumber);
	}

	/**
	 * Add a new tab to this tabset
	 * 
	 * @param tabSettings Settings to fill the new tab with
	 * @param select Select tab after loading
	 */
	public void addTab(TabSettings tabSettings, boolean select) {
		tabSettings.setTabTitle(createTabTitle(tabSettings.getTabTitle()));
		QueryTab tab = new QueryTab(view, tabSettings);
		addTab(tab);
		if (select) {
			selectTab(tab);
		}
	}
	
	/**
	 * @see addTab(TabSettings tabSettings, boolean select)
	 * @param tabSettings
	 */
	public void addTab(TabSettings tabSettings) {
		addTab(tabSettings, false);

	}

	/**
	 * Create tab title. Checks if it exists, and appends number if it does.
	 * 
	 * @param title
	 *            Title to create unique tab title for
	 * 
	 * @return unique tab title
	 */
	private String createTabTitle(String title) {
		Tab[] tabs = getTabs();
		ArrayList<String> tabTitles = new ArrayList<String>();
		for (Tab tab : tabs) {
			tabTitles.add(tab.getTitle());
		}
		if (tabTitles.contains(title)) {
			return createUniqueTitle(tabTitles, title, 1);
		} else {
			return title;
		}
	}

	/**
	 * Iteratively create new unique tab title. Checks if it exists, and ups the
	 * iterator which to append '(i)' to the string if string already exists
	 * 
	 * @param tabTitles
	 *            List of tab titles of current tabs
	 * @param title
	 *            Title which we want to add
	 * @param i
	 *            Iterator to append to title name
	 * 
	 * @return New unique tab title
	 */
	private String createUniqueTitle(ArrayList<String> tabTitles, String title, int i) {
		String newTitle = title + "(" + Integer.toString(i) + ")";
		if (tabTitles.contains(newTitle)) {
			return createUniqueTitle(tabTitles, title, i + 1);
		} else {
			return newTitle;
		}
	}

	public void loggedInCallback() {
		Tab[] tabs = getTabs();
		for (Tab tab: tabs) {
			QueryTab queryTab = (QueryTab) tab;
			queryTab.getBookmarkedQueries().setEnabled(true);
			queryTab.getAddToBookmarks().setEnabled(true);
		}
	}

	public void disableRpcElements() {
		Tab[] tabs = getTabs();
		for (Tab tab: tabs) {
			QueryTab queryTab = (QueryTab) tab;
			queryTab.disableRpcElements();
		}
	}
	public void enableRpcElements() {
		Tab[] tabs = getTabs();
		for (Tab tab: tabs) {
			QueryTab queryTab = (QueryTab) tab;
			queryTab.enableRpcElements();
		}
	}
	
}
