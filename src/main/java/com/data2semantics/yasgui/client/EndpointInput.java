package com.data2semantics.yasgui.client;

import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.BlurEvent;
import com.smartgwt.client.widgets.form.fields.events.BlurHandler;
import com.smartgwt.client.widgets.form.fields.events.FocusEvent;
import com.smartgwt.client.widgets.form.fields.events.FocusHandler;

public class EndpointInput extends DynamicForm {
	private View view;
	private TextItem endpoint;
	public static String ENDPOINT_INPUT_NAME = "EndpointInput";
	private String latestEndpointValue; //used to know when to check for cors enabled. Not just on blur, but only on blur and when value has changed
	public EndpointInput(View view) {
		this.view = view;
		endpoint = new TextItem();
		endpoint.setTitle("Endpoint");
		endpoint.setWidth(250);
		endpoint.setDefaultValue(getView().getSettings().getEndpoint());
		//For this default value, also retrieve CORS setting
		JsMethods.checkCorsEnabled(getView().getSettings().getEndpoint());
		endpoint.setName(ENDPOINT_INPUT_NAME);
		endpoint.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				latestEndpointValue = getEndpoint();
				
			}
		});
		endpoint.addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				getView().updateSettings();
				if (!latestEndpointValue.equals(getEndpoint())) {
					JsMethods.checkCorsEnabled(getEndpoint());
				}
			}

		});
		setFields(endpoint);
	}
	
	public String getEndpoint() {
		return endpoint.getValueAsString();
	}
	
	private View getView() {
		return this.view;
	}
}
