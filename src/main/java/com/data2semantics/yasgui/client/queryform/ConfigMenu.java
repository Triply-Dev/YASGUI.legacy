package com.data2semantics.yasgui.client.queryform;

import com.data2semantics.yasgui.client.View;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

public class ConfigMenu extends Menu {
	private View view;
	public ConfigMenu(View view) {
		this.view = view;
		
		MenuItem prefixUpdate = new MenuItem("Force prefixes update");
		prefixUpdate.setIcon("icons/diagona/reload.png");
		prefixUpdate.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(MenuItemClickEvent event) {
				getView().setAutocompletePrefixes(true);
			}});
		
		setItems(prefixUpdate);
	}
	
	private View getView() {
		return this.view;
	}
}
