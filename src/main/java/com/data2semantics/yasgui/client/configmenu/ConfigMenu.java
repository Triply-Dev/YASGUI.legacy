package com.data2semantics.yasgui.client.configmenu;

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
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.shared.StaticConfig;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

public class ConfigMenu extends Menu implements RpcElement {
	private View view;
	ArrayList<MenuItem> items = new ArrayList<MenuItem>();
	private MenuItem refreshMenuItem;
	private MenuItem logOutMenuItem;
	private MenuItem logInMenuItem;
	private MenuItem bugReportItem;
	public ConfigMenu(View view) {
		this.view = view;
		addOpenIdItem();
		addRefreshSubMenu();
		if (JsMethods.offlineSupported() && view.getSettings().getEnabledFeatures().offlineCachingEnabled()) {
			addOfflineAvailabilityItem();
		}
		addResetSettings();
		addCompatabilityItem();
		addRecentChangelogItem();
		addTooltipsItem();
		if (view.getSettings().bugReportsSupported()) {
			addBugReportItem();
		}
		addAboutItem();


		
		setItems(items.toArray(new MenuItem[items.size()]));
	}
	
	private void addBugReportItem() {
		bugReportItem = new MenuItem("Report a bug or request a new feature");
		bugReportItem.setIcon(Imgs.BUG.get());
		bugReportItem.addClickHandler(new ClickHandler(){
			public void onClick(MenuItemClickEvent event) {
				ReportIssue.report(view);
			}});
		items.add(bugReportItem);
	}

	private void addOfflineAvailabilityItem() {
		MenuItem offlineAvailabilityItem = new MenuItem("Configure offline availability");
		offlineAvailabilityItem.setIcon(Imgs.DISCONNECTED.get());
//		if (view.getSettings().useOfflineCaching()) {
//			offlineAvailabilityItem = new MenuItem("Disable offline caching");
//			offlineAvailabilityItem.setIcon(Imgs.CHECKMARK.get());
//		} else {
//			offlineAvailabilityItem = new MenuItem("Enable offline caching");
//			offlineAvailabilityItem.setIcon(Imgs.CROSS.get());
//		}
		offlineAvailabilityItem.addClickHandler(new ClickHandler(){
			public void onClick(MenuItemClickEvent event) {
				new OfflineAvailabilityConfig(view);
//				view.getSettings().enableOfflineCaching(!view.getSettings().useOfflineCaching());
//				LocalStorageHelper.storeSettingsInCookie(view.getSettings());
//				view.getElements().redrawConfigMenu();
//				if (view.getSettings().useOfflineCaching()) {
//					Helper.includeOfflineManifest();
//				}
			}});
		items.add(offlineAvailabilityItem);
	}

	private void addRecentChangelogItem() {
		MenuItem changes = new MenuItem("Show recent changes");
		changes.setIcon(Imgs.TOOLTIP.get());
		changes.addClickHandler(new ClickHandler(){
			public void onClick(MenuItemClickEvent event) {
				view.getChangelogHelper().draw(); //show from version 0 onwards
				view.showTooltips(StaticConfig.VERSION_ID-1);
			}});
		items.add(changes);
		
	}

	private void addResetSettings() {
		MenuItem reset = new MenuItem("Reset my settings");
		reset.setIcon(Imgs.CROSS.get());
		reset.addClickHandler(new ClickHandler(){
			public void onClick(MenuItemClickEvent event) {
				LocalStorageHelper.clearSettings();
				Window.Location.reload();
			}});
		items.add(reset);
		
	}

	private void addTooltipsItem() {
		MenuItem about = new MenuItem("Show help bubbles");
		about.setIcon(Imgs.TOOLTIP.get());
		about.addClickHandler(new ClickHandler(){
			public void onClick(MenuItemClickEvent event) {
				view.showTooltips(0); //show from version 0 onwards
			}});
		items.add(about);
	}
	
	private void addOpenIdItem() {
		if (view.getSettings().isDbSet()) {
			if (view.getOpenId() != null && view.getOpenId().isLoggedIn()) {
				logOutMenuItem = new MenuItem("Log out");
				logOutMenuItem.setIcon(Imgs.LOG_OUT.get());
				logOutMenuItem.addClickHandler(new ClickHandler(){
					public void onClick(MenuItemClickEvent event) {
						view.getOpenId().logOut();
					}});
				items.add(logOutMenuItem);
			} else {
				logInMenuItem = new MenuItem("Log in");
				logInMenuItem.setIcon(Imgs.LOG_IN.get());
				logInMenuItem.addClickHandler(new ClickHandler(){
					public void onClick(MenuItemClickEvent event) {
						view.getOpenId().showOpenIdProviders();
					}});
				items.add(logInMenuItem);
			}
		}
	}
	
	private void addAboutItem() {
		MenuItem about = new MenuItem("About (version " + StaticConfig.VERSION + ")");
		about.setIcon(Imgs.QUESTION_MARK.get());
		about.addClickHandler(new ClickHandler(){
			public void onClick(MenuItemClickEvent event) {
				new About(view);
			}});
		items.add(about);
	}
	
	private void addCompatabilityItem() {
		MenuItem compatability = new MenuItem("Show browser compatabilities");
		final Compatabilities compatabilities = new Compatabilities(view);
		if (!compatabilities.allSupported()) {
			compatability.setIcon(Imgs.WARNING.get());
		} else {
			compatability.setIcon(Imgs.COMPATIBLE.get());
		}
		compatability.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(MenuItemClickEvent event) {
				compatabilities.drawContent();
			}});
		items.add(compatability);
	}
	
	private void addRefreshSubMenu() {
		
		Menu refreshSubMenu = new Menu();  
        
		MenuItem prefixUpdate = new MenuItem("Force prefixes update");
		prefixUpdate.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(MenuItemClickEvent event) {
				view.setAutocompletePrefixes(true);
			}});
		MenuItem endpointsUpdate = null;
		if (view.getEnabledFeatures().endpointSelectionEnabled()) {
			endpointsUpdate = new MenuItem("Force endpoints update");
			endpointsUpdate.addClickHandler(new ClickHandler(){
				@Override
				public void onClick(MenuItemClickEvent event) {
					view.initEndpointDataSource(true);
				}});
		}
		if (endpointsUpdate == null) {
			refreshSubMenu.setItems(prefixUpdate);
		} else {
			refreshSubMenu.setItems(prefixUpdate, endpointsUpdate);
		}
		
		
		refreshMenuItem = new MenuItem("Refresh Data", Imgs.REFRESH.get()); 
		refreshMenuItem.setSubmenu(refreshSubMenu);
		items.add(refreshMenuItem);
	}

	public void disableRpcElements() {
		if (refreshMenuItem != null) {
			refreshMenuItem.setEnabled(false);
		}
		if (logInMenuItem != null) {
			logInMenuItem.setEnabled(false);
		}
		if (logOutMenuItem != null) {
			logOutMenuItem.setEnabled(false);
		}
		if (bugReportItem != null) {
			bugReportItem.setEnabled(false);
		}
	}

	public void enableRpcElements() {
		if (refreshMenuItem != null) {
			refreshMenuItem.setEnabled(true);
		}
		if (logInMenuItem != null) {
			logInMenuItem.setEnabled(true);
		}
		if (logOutMenuItem != null) {
			logOutMenuItem.setEnabled(true);
		}
		if (refreshMenuItem != null) {
			refreshMenuItem.setEnabled(true);
		}
		if (bugReportItem != null) {
			bugReportItem.setEnabled(true);
		}
	}
}
