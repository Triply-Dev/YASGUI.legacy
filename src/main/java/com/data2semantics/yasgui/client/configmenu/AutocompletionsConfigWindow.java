package com.data2semantics.yasgui.client.configmenu;

import com.data2semantics.yasgui.client.GwtCallbackWrapper;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IconButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
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
		setShowMinimizeButton(false);
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
		layout.addMember(getAddButton());
		return layout;
	}

	private IconButton getAddButton() {
		IconButton button = new IconButton("Add new private endpoint");
		button.setWidth(150);
		button.setShowRollOver(false);
		button.setShowDown(false);
		button.setIcon(Imgs.ADD.get());
		button.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				new GwtCallbackWrapper<Boolean>(view) {
					public void onCall(AsyncCallback<Boolean> callback) {
						view.getRemoteService().isEndpointAccessible("endpoint", callback);
					}

					protected void onFailure(Throwable throwable) {
						view.getErrorHelper().onError(throwable);
					}

					protected void onSuccess(Boolean accessible) {
						if (accessible) {
							
						} else {
							
						}
					}

				}.call();
				
			}});
		return button;
	}
}
