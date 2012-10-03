package com.data2semantics.yasgui.client.tab;

import com.data2semantics.yasgui.client.View;
import com.smartgwt.client.widgets.menu.Menu;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.events.ClickHandler;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

public class ConfigMenu extends Menu {
	@SuppressWarnings("unused")
	private View view;
	public ConfigMenu(final View view) {
		this.view = view;
		
		MenuItem prefixUpdate = new MenuItem("Force prefixes update");
		prefixUpdate.setIcon("icons/diagona/reload.png");
		prefixUpdate.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(MenuItemClickEvent event) {
				view.setAutocompletePrefixes(true);
			}});
		MenuItem endpointsUpdate = new MenuItem("Force endpoints update");
		endpointsUpdate.setIcon("icons/diagona/reload.png");
		endpointsUpdate.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(MenuItemClickEvent event) {
				view.initEndpointDataSource(true);
			}});
		setItems(prefixUpdate, endpointsUpdate);
	}
}
