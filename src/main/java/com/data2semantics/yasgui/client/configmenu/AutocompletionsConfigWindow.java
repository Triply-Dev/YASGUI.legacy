package com.data2semantics.yasgui.client.configmenu;

import java.util.HashMap;

import com.data2semantics.yasgui.client.GwtCallbackWrapper;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IconButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ButtonItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;

public class AutocompletionsConfigWindow extends Window {
	private static int WINDOW_HEIGHT = 400;
	private static int WINDOW_WIDTH = 500;
	private View view;
	VLayout layout = new VLayout();
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
		layout.setWidth100();
		layout.setHeight100();
		layout.addMember(new AutocompletionConfigTable(view));
		layout.addMember(getAddButton());
		return layout;
	}

	private IconButton getAddButton() {
		IconButton button = new IconButton("Fetch completions from my private endpoint");
		button.setWidth(250);
		button.setShowRollOver(false);
		button.setShowDown(false);
		button.setIcon(Imgs.ADD.get());
		button.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				drawEndpointAddDialogue();
			}});
		
		return button;
	}
	
	private void drawEndpointAddDialogue() {
		DynamicForm form = new DynamicForm();
		final TextItem endpointItem = new TextItem();  
        endpointItem.setTitle("Endpoint");  
        final SelectItem typesItem = new SelectItem();  
        typesItem.setTitle("Completion Types");  
        typesItem.setMultiple(true);  
        typesItem.setMultipleAppearance(MultipleAppearance.GRID);  
        final HashMap<String, String> types = new HashMap<String, String>();
        types.put("Properties",  "property");
        types.put("Classes",  "class");
        typesItem.setValueMap(types.keySet().toArray(new String[types.keySet().size()]));
        ButtonItem saveButton = new ButtonItem("Update");  
        saveButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				new GwtCallbackWrapper<Boolean>(view) {
					public void onCall(AsyncCallback<Boolean> callback) {
						view.getRemoteService().isEndpointAccessible(endpointItem.getValueAsString(), callback);
					}

					protected void onFailure(Throwable throwable) {
						view.getErrorHelper().onError(throwable);
					}

					protected void onSuccess(Boolean accessible) {
						destroy();
						if (accessible) {
							view.getErrorHelper().onError("The endpoint " + endpointItem.getValueAsString() + " is already accessible from the YASGUI server. The completions for this endpoint are managed by YASGUI by default. You are only able to manage your own private (e.g. localhost/intranet) endpoints");
						} else {
							if (typesItem.getValues().length > 0) {
								String type = null;
								if (typesItem.getValues().length == 1) {
									type = typesItem.getValues()[0];
								}
								JsMethods.fetchCompletions(endpointItem.getValueAsString(), type);
							}
						}
					}

				}.call();
				
				
			}  
        });
        form.setItems(endpointItem, typesItem, saveButton);
        layout.addMember(form);
	}
	
	
}
