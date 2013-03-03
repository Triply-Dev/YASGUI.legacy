package com.data2semantics.yasgui.client;

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

import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

public class ConfigMenu extends Menu {
	private View view;
	public ConfigMenu(final View view) {
		this.view = view;
		ArrayList<MenuItem> items = new ArrayList<MenuItem>();
		MenuItem prefixUpdate = new MenuItem("Force prefixes update");
		prefixUpdate.setIcon("icons/diagona/reload.png");
		prefixUpdate.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(MenuItemClickEvent event) {
				view.setAutocompletePrefixes(true);
			}});
		items.add(prefixUpdate);
		if (!view.getSettings().inSingleEndpointMode()) {
			MenuItem endpointsUpdate = new MenuItem("Force endpoints update");
			endpointsUpdate.setIcon("icons/diagona/reload.png");
			endpointsUpdate.addClickHandler(new ClickHandler(){
				@Override
				public void onClick(MenuItemClickEvent event) {
					view.initEndpointDataSource(true);
				}});
			items.add(endpointsUpdate);
		}
		MenuItem compatability;
		Compatabilities compatabilities = new Compatabilities(view);
		if (!compatabilities.allSupported()) {
			compatability = getCompatabilityMenu("icons/fugue/exclamation.png");
		} else {
			compatability = getCompatabilityMenu("icons/fugue/information.png");
		}
		items.add(compatability);
		setItems(items.toArray(new MenuItem[items.size()]));
	}
	
	private MenuItem getCompatabilityMenu(String icon) {
		MenuItem compatability = new MenuItem("Show browser compatabilities");
		compatability.setIcon(icon);
		compatability.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(MenuItemClickEvent event) {
				Compatabilities compatabilities = new Compatabilities(view);
				compatabilities.drawContent();
			}});
		return compatability;
	}
}
