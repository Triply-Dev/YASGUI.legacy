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

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

public class AutocompletionsMenu extends MenuItem {

	private View view;


	public AutocompletionsMenu(View view, String title, String icon) {
		super(title, icon);
		this.view = view;
		addSubmenuItems();
	}
	
	private void addSubmenuItems() {
		Menu autocompletionsSubMenu = new Menu();  
        
		MenuItem propMethods = new MenuItem("Change property autocompletion methods");
		propMethods.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(MenuItemClickEvent event) {
				JsMethods.showPropAutocompletionMethods();
			}});
		MenuItem classMethods = new MenuItem("Change class autocompletion methods");
		classMethods.addClickHandler(new ClickHandler(){
			
			@Override
			public void onClick(MenuItemClickEvent event) {
				JsMethods.showClassAutocompletionMethods();
			}});
		autocompletionsSubMenu.setItems(propMethods, classMethods);
		
		MenuItem localhostAutocompletion;
		if (view.getOpenId() != null && view.getOpenId().isLoggedIn()) {
			localhostAutocompletion = new MenuItem("Manage personal autocompletions");
			localhostAutocompletion.addClickHandler(new ClickHandler(){
				@Override
				public void onClick(MenuItemClickEvent event) {
					new AutocompletionsConfigWindow(view);
				}});
		} else {
			localhostAutocompletion = new MenuItem("Manage personal autocompletions (log in to change)");
			localhostAutocompletion.setEnabled(false);
		}
		autocompletionsSubMenu.setItems(propMethods, classMethods, localhostAutocompletion);
		setSubmenu(autocompletionsSubMenu);
		
	}
	
	
}
