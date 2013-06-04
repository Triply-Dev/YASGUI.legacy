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

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.shared.StaticConfig;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

public class ConfigMenu extends Menu {
	private View view;
	ArrayList<MenuItem> items = new ArrayList<MenuItem>();
	public ConfigMenu(View view) {
		this.view = view;
		addOpenIdItem();
		addRefreshSubMenu();
		
		addCompatabilityItem();
		addTooltips();
		addAboutItem();
		
		setItems(items.toArray(new MenuItem[items.size()]));
	}
	
	private void addTooltips() {
		MenuItem about = new MenuItem("Show help bubbles");
		about.setIcon(Imgs.get(Imgs.TOOLTIP));
		about.addClickHandler(new ClickHandler(){
			public void onClick(MenuItemClickEvent event) {
				view.showTooltips(0); //show from version 0 onwards
			}});
		items.add(about);
	}
	
	private void addOpenIdItem() {
		if (view.getSettings().isDbSet()) {
			if (view.getOpenId() != null && view.getOpenId().isLoggedIn()) {
				MenuItem logOut = new MenuItem("Log out");
				logOut.setIcon(Imgs.get(Imgs.LOG_OUT));
				logOut.addClickHandler(new ClickHandler(){
					public void onClick(MenuItemClickEvent event) {
						view.getOpenId().logOut();
					}});
				items.add(logOut);
			} else {
				MenuItem logIn = new MenuItem("Log in");
				logIn.setIcon(Imgs.get(Imgs.LOG_IN));
				logIn.addClickHandler(new ClickHandler(){
					public void onClick(MenuItemClickEvent event) {
						view.getOpenId().showOpenIdProviders();
					}});
				items.add(logIn);
			}
		}
	}
	
	private void addAboutItem() {
		MenuItem about = new MenuItem("About (version " + StaticConfig.VERSION + ")");
		about.setIcon(Imgs.get(Imgs.QUESTION_MARK));
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
			compatability.setIcon(Imgs.get(Imgs.WARNING));
		} else {
			compatability.setIcon(Imgs.get(Imgs.COMPATIBLE));
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
//		prefixUpdate.setIcon(Icons.REFRESH);
		prefixUpdate.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(MenuItemClickEvent event) {
				view.setAutocompletePrefixes(true);
			}});
//		items.add(prefixUpdate);
		MenuItem endpointsUpdate = null;
		if (!view.getSettings().inSingleEndpointMode()) {
			endpointsUpdate = new MenuItem("Force endpoints update");
//			endpointsUpdate.setIcon(Icons.REFRESH);
			endpointsUpdate.addClickHandler(new ClickHandler(){
				@Override
				public void onClick(MenuItemClickEvent event) {
					view.initEndpointDataSource(true);
				}});
//			items.add(endpointsUpdate);
		}
		if (endpointsUpdate == null) {
			refreshSubMenu.setItems(prefixUpdate);
		} else {
			refreshSubMenu.setItems(prefixUpdate, endpointsUpdate);
		}
		
		
		MenuItem refresh = new MenuItem("Refresh Data", Imgs.get(Imgs.REFRESH)); 
		refresh.setSubmenu(refreshSubMenu);
		items.add(refresh);
	}
}
