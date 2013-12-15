package com.data2semantics.yasgui.client.configmenu;

import java.util.LinkedHashMap;

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
	private TextItem fetchEndpoint;
	private SelectItem fetchTypes;

	private LinkedHashMap<String, String> possibleTypes = new LinkedHashMap<String, String>();
	VLayout layout = new VLayout();
	private DynamicForm endpointAddForm;

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
		button.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				drawEndpointAddDialogue();
			}
		});

		return button;
	}

	private void drawEndpointAddDialogue() {
		endpointAddForm = new DynamicForm();
		fetchEndpoint = new TextItem();
		fetchEndpoint.setTitle("Endpoint");

		possibleTypes = new LinkedHashMap<String, String>();
		possibleTypes.put("class", "Classses");
		possibleTypes.put("property", "Propperties");
		// fetchTypes.setValueMap("Properties", "Classes");

		final SelectItem fetchTypes = new SelectItem();
		fetchTypes.setTitle("Completion Types");
		fetchTypes.setMultiple(true);
		fetchTypes.setMultipleAppearance(MultipleAppearance.PICKLIST);
//		fetchTypes.setValueMap("Properties", "Classes");

		fetchTypes.setValueMap(possibleTypes);
		ButtonItem saveButton = new ButtonItem("Fetch");
		saveButton.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler() {
			
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
//				JsMethods.logConsole(Integer.toString(fetchTypes.getSelectedRecords().length));
//				JsMethods.logConsole(Integer.toString(fetchTypes.getValues().length));
//				if ("sdf".equals("sdf")) return;
				new GwtCallbackWrapper<Boolean>(view) {
					public void onCall(AsyncCallback<Boolean> callback) {
						view.getRemoteService().isEndpointAccessible(fetchEndpoint.getValueAsString(), callback);
					}

					protected void onFailure(Throwable throwable) {
						view.getErrorHelper().onError(throwable);
					}

					protected void onSuccess(Boolean accessible) {
						if (accessible) {
							view.getErrorHelper()
									.onError(
											"The endpoint "
													+ fetchEndpoint.getValueAsString()
													+ " is already accessible from the YASGUI server. The completions for this endpoint are managed by YASGUI by default. You are only able to manage your own private (e.g. localhost/intranet) endpoints");
						} else {
							if (fetchTypes.getValues().length > 0) {
								String type = null;
								if (fetchTypes.getValues().length == 1) {
									type = fetchTypes.getValues()[0];
								}
								JsMethods.fetchCompletions(fetchEndpoint.getValueAsString(), type);
							}
							destroy();
						}
					}

				}.call();

			}
		});
		endpointAddForm.setItems(fetchEndpoint, fetchTypes, saveButton);
		layout.addMember(endpointAddForm);
	}

}
