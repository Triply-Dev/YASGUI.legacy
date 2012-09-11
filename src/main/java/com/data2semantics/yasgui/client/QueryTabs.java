package com.data2semantics.yasgui.client;

import java.util.ArrayList;

import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.ZIndexes;
import com.data2semantics.yasgui.client.settings.Settings;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.data2semantics.yasgui.client.tab.ConfigMenu;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.data2semantics.yasgui.client.tab.results.JsonOutput;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.TabTitleEditEvent;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.menu.IconMenuButton;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.CloseClickHandler;
import com.smartgwt.client.widgets.tab.events.TabCloseClickEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;
import com.smartgwt.client.widgets.tab.events.TabTitleChangedEvent;
import com.smartgwt.client.widgets.tab.events.TabTitleChangedHandler;

public class QueryTabs extends TabSet {
	private View view;
	private static boolean STORE_SETTINGS_ON_CLOSE_DEFAULT = true;
	public static int INDENT_TABS = 130;
	public QueryTabs(View view) {
		this.view = view;
		setTabBarThickness(28); //this way the icon menu button alligns well with the tabbar
		setTabBarPosition(Side.TOP);
		setTabBarAlign(Side.LEFT);
		setWidth100();
		setHeight100();
		setCanEditTabTitles(true);
		setTitleEditEvent(TabTitleEditEvent.DOUBLECLICK);
		setTabsFromSettings();
		selectTab(getView().getSettings().getSelectedTabNumber());
		setTabBarThickness(50);
		// Need to schedule attaching of codemirror. It uses doc.getElementById,
		// which doesnt work if element hasnt been drawn yet
		Scheduler.get().scheduleFinally(new Command() {
			public void execute() {
				JsMethods.attachCodeMirrorToQueryInput(((QueryTab) getSelectedTab()).getQueryTextArea().getInputId());
			}
		});
		addHandlers();
		addTabControls();
	}
	
	/**
	 * Load all tabs defined in our settings object, and load them
	 */
	private void setTabsFromSettings() {
		ArrayList<TabSettings> tabArray = getView().getSettings().getTabArray();
		if (tabArray.size() == 0) {
			// Don't have anything yet. Just draw a new tab with default vals
			addTab(new TabSettings());
		} else {
			for (TabSettings tabSettings : tabArray) {
				addTab(tabSettings);
			}
		}
	}

	/**
	 * Add controls (e.g. 'add new tab') to the tab bar
	 */
	private void addTabControls() {
		HLayout controls = new HLayout();
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setWidth100();
		controls.addMember(spacer);
		controls.setWidth(INDENT_TABS - 2);
		
		
		ImgButton button = new ImgButton();
		button.setSrc("icons/fugue/plus-button_modified.png");
		button.setShowDown(false);
		button.setShowRollOver(false);
		button.setWidth(20);
		button.setHeight(25);
		button.setZIndex(ZIndexes.TAB_CONTROLS);//Otherwise the onclick of the tab bar is used..
		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				TabSettings tabSettings = new TabSettings();
				getView().getSettings().addTabSettings(tabSettings);
				getView().getTabs().addTab(tabSettings, true);
				Helper.storeSettingsInCookie(getView().getSettings());
			}
		});
		
		IconMenuButton config = new IconMenuButton("");
		config.setIcon("icons/diagona/bolt.png");
		config.setMenu(new ConfigMenu(getView()));
		controls.setZIndex(ZIndexes.TAB_CONTROLS);
		controls.addMember(config);
		controls.addMember(button);
		addChild(controls);
	}
	
	private View getView() {
		return this.view;
	}

	/**
	 * Define all handlers of the tabs on the tab bar
	 */
	private void addHandlers() {
		addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
				Settings settings = getView().getSettings();
				settings.setSelectedTabNumber(event.getTabNum());
				Helper.storeSettingsInCookie(settings);
				Scheduler.get().scheduleDeferred(new Command() {
					public void execute() {
						JsMethods.attachCodeMirrorToQueryInput(((QueryTab) getSelectedTab()).getQueryTextArea().getInputId());
					}
				});
			}
		});
		addTabTitleChangedHandler(new TabTitleChangedHandler() {
			@Override
			public void onTabTitleChanged(TabTitleChangedEvent event) {
				Settings settings = getView().getSettings();
				int tabIndex = getTabNumber(event.getTab().getID());
				//Don't use selected one. Title may change by context menu, when other tab is selected
				settings.getTabArray().get(tabIndex).setTabTitle(event.getNewTitle());
				Helper.storeSettingsInCookie(settings);
			}
		});
		addCloseClickHandler(new CloseClickHandler() {
			@Override
			public void onCloseClick(TabCloseClickEvent event) {
				closePreProcess((QueryTab)event.getTab());
				closePostProcess((QueryTab)event.getTab());
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
				removeTab((QueryTab)tab, false);
			}
		}
		Helper.storeSettingsInCookie(getView().getSettings());
	}
	
	/**
	 * Remove all tabs
	 */
	public void removeAllTabs() {
		Tab[] tabs = getTabs();
		for (Tab tab: tabs) {
			removeTab((QueryTab)tab, false);
		}
		Helper.storeSettingsInCookie(getView().getSettings());
	}
	
	/**
	 * Remove a single tab
	 * @param tab Tab to remove
	 * @param storeSettings Whether to save settings in cookie as well. 
	 * Use this settings if this methods is called often, we want to store once instead of multiple times for performance reasons 
	 */
	public void removeTab(QueryTab tab, boolean storeSettings) {
		closePreProcess(tab);
		removeTab(tab);
		closePostProcess(tab, storeSettings);
	}
	
	/**
	 * Do code cleanup and updating of settings before tab is actually removed
	 * 
	 * @param queryTab
	 */
	public void closePreProcess(QueryTab queryTab) {
		Settings settings = getView().getSettings();
		settings.removeTabSettings(getTabNumber(queryTab.getID()));
		// To avoid codemirror js objects lying around, remove js objects
		// belonging to this tab
		JsMethods.destroyCodeMirrorQueryInput(queryTab.getQueryTextArea().getInputId());
		JsonOutput jsonOutput = queryTab.getResultContainer().getJsonOutput();
		if (jsonOutput != null) {
			//We have outputted query results as json string using the codemirror highlighter. Also cleanup this object
			JsMethods.destroyCodeMirrorJsonResult(jsonOutput.getInputId());
		}
		
	}
	
	public void removeAndPostProcessTab(QueryTab tab) {
		removeTab(tab, STORE_SETTINGS_ON_CLOSE_DEFAULT);
	}
	
	/**
	 * Postprocess for after removing of tab. Use this to update settings with the new selected tab number
	 * 
	 * @param queryTab
	 * @param storeSettings Whether to store settings in cookie. We might not always want to do this (when removing multiple tabs we want to do it only once)
	 */
	public void closePostProcess(QueryTab queryTab, boolean storeSettings) {
		Settings settings = getView().getSettings();
		settings.setSelectedTabNumber(getSelectedTabNumber());
		if (storeSettings) {
			Helper.storeSettingsInCookie(settings);
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
		QueryTab tab = new QueryTab(getView(), tabSettings);
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
}
