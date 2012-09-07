package com.data2semantics.yasgui.client.queryform;

import java.util.ArrayList;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.settings.Settings;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.types.TabTitleEditEvent;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
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
	private static boolean DEFAULT_STORE_SETTINGS_ON_CLOSE = true;
	public static int INDENT_TABS = 22;
	public QueryTabs(View view) {
		this.view = view;
		
		setTabBarPosition(Side.TOP);
		setTabBarAlign(Side.LEFT);
		setWidth100();
		setHeight100();
		setCanEditTabTitles(true);
		setTitleEditEvent(TabTitleEditEvent.DOUBLECLICK);
		setTabsFromSettings();
		selectTab(getView().getSettings().getSelectedTabNumber());

		// Need to schedule attaching of codemirror. It uses doc.getElementById,
		// which doesnt work if element hasnt been drawn yet
		Scheduler.get().scheduleFinally(new Command() {
			public void execute() {
				JsMethods.attachCodeMirror(((QueryTab) getSelectedTab()).getQueryTextArea().getInputId());
			}
		});
		addHandlers();
		addTabControls();
	}

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

	private void addTabControls() {
		ImgButton button = new ImgButton();
		//use modified logo. want a top/left margin, but gwt messes this up
		button.setSrc("icons/fugue/plus-button_modified.png");
		button.setWidth(20);
		
		//For now, dont do animations on hover or click: need to create a different icon for that
		button.setShowDown(false);
		button.setShowRollOver(false);
		button.setHeight(20);
		button.setZIndex(55555555);//Otherwise the onclick of the tab bar is used..
		button.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				TabSettings tabSettings = new TabSettings();
				getView().getSettings().addTabSettings(tabSettings);
				getView().getTabs().addTab(tabSettings, true);
				Helper.storeSettingsInCookie(getView().getSettings());
			}
		});
		addChild(button);
	}

	private View getView() {
		return this.view;
	}

	private void addHandlers() {
		addTabSelectedHandler(new TabSelectedHandler() {
			@Override
			public void onTabSelected(TabSelectedEvent event) {
//				getView().getLogger().severe("tabs in settings: " + Integer.toString(getView().getSettings().getTabArray().size()));
				Settings settings = getView().getSettings();
				settings.setSelectedTabNumber(event.getTabNum());
				Helper.storeSettingsInCookie(settings);
				Scheduler.get().scheduleDeferred(new Command() {
					public void execute() {
						JsMethods.attachCodeMirror(((QueryTab) getSelectedTab()).getQueryTextArea().getInputId());
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
	
	public void removeAllExcept(QueryTab except) {
		Tab[] tabs = getTabs();
		for (Tab tab: tabs) {
			if (!tab.equals(except)) {
				removeTab((QueryTab)tab, false);
			}
		}
		Helper.storeSettingsInCookie(getView().getSettings());
	}
	
	public void removeAllTabs() {
		Tab[] tabs = getTabs();
		for (Tab tab: tabs) {
			removeTab((QueryTab)tab, false);
		}
		Helper.storeSettingsInCookie(getView().getSettings());
	}
	
	public void removeTab(QueryTab tab, boolean storeSettings) {
		closePreProcess(tab);
		removeTab(tab);
		closePostProcess(tab);
	}
	
	public void closePreProcess(QueryTab queryTab) {
		Settings settings = getView().getSettings();
		settings.removeTabSettings(getTabNumber(queryTab.getID()));
		// To avoid codemirror js objects lying around, remove js object
		// belonging to this tab
		JsMethods.destroyCodeMirror(queryTab.getQueryTextArea().getInputId());
	}
	
	public void removeAndPostProcessTab(QueryTab tab) {
		removeTab(tab, DEFAULT_STORE_SETTINGS_ON_CLOSE);
	}
	public void closePostProcess(QueryTab queryTab, boolean storeSettings) {
		Settings settings = getView().getSettings();
		settings.setSelectedTabNumber(getSelectedTabNumber());
		if (storeSettings) {
			Helper.storeSettingsInCookie(settings);
		}
	}
	
	public void closePostProcess(QueryTab queryTab) {
		closePostProcess(queryTab, DEFAULT_STORE_SETTINGS_ON_CLOSE);
	}
	
	public void editTabTitle(QueryTab queryTab) {
		int tabNumber = getTabNumber(queryTab.getID());
		editTabTitle(tabNumber);
	}

	public void addTab(TabSettings tabSettings, boolean select) {
		tabSettings.setTabTitle(createTabTitle(tabSettings.getTabTitle()));
		final QueryTab tab = new QueryTab(getView(), tabSettings.getTabTitle());
		addTab(tab);
		if (select) {
			selectTab(tab);
		}
	}

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
