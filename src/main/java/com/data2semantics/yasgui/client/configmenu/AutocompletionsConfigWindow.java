package com.data2semantics.yasgui.client.configmenu;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.VLayout;

public class AutocompletionsConfigWindow extends Window {
	private static int WINDOW_HEIGHT = 400;
	private static int WINDOW_WIDTH = 500;
	private View view;
	public AutocompletionsConfigWindow(View view) {
		this.view = view;
		setHeight(WINDOW_HEIGHT);
		setWidth(WINDOW_WIDTH);
		setZIndex(ZIndexes.MODAL_WINDOWS);
		setTitle("Manage personal autocompletions");
		setIsModal(true);
		setDismissOnOutsideClick(true);
		setAutoCenter(true);
		addItem(getLocalhostCompletionConfigContent());
		draw();
	}
	
	private Canvas getLocalhostCompletionConfigContent() {
		VLayout layout = new VLayout();
		layout.setWidth100();
		layout.setHeight100();
		layout.addMember(new AutocompletionConfigTable(view));
		return layout;
	}
}
