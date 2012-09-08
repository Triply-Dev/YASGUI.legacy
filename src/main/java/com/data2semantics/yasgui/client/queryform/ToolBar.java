package com.data2semantics.yasgui.client.queryform;

import com.data2semantics.yasgui.client.View;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.menu.IconMenuButton;

public class ToolBar extends HLayout {
	private View view;
	public static String QUERY_FORMAT_SELECTOR_ID = "queryFormat";
	private ConfigMenu configMenu;
//	private SelectItem outputSelection;
	public ToolBar(View view) {
		this.view = view;
		this.configMenu = new ConfigMenu(getView());
        setWidth100();
        drawLhs();
        LayoutSpacer spacer = new LayoutSpacer();
        spacer.setWidth100();
        addMember(spacer);
        drawRhs();
	}
	
	private void drawLhs() {
		Button queryViaJs = new Button("Query");
		queryViaJs.setHeight(30);
		queryViaJs.setWidth(130);
		queryViaJs.setAlign(Alignment.CENTER);
//		queryViaJs.addClickHandler(new ClickHandler() {
//			public void onClick(ClickEvent event) {
//				JsMethods.queryJson(getView().getSelectedTabSettings().getQueryString(), getView().getSelectedTabSettings().getEndpoint());
//			}
//		});
		addMember(queryViaJs);
	}
	
	private void drawRhs() {
		drawConfigMenu();
		
	}
	
	private void drawConfigMenu() {
		IconMenuButton config = new IconMenuButton("");
		config.setIcon("icons/diagona/bolt.png");
		config.setMenu(configMenu);
		addMember(config);
	}
	
	private View getView() {
		return this.view;
	}
	
}
