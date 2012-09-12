package com.data2semantics.yasgui.client.tab;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.smartgwt.client.types.TitleOrientation;
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
	private QueryTab queryTab;
	public EndpointInput(View view, QueryTab queryTab) {
		this.queryTab = queryTab;
		this.view = view;
		createTextInput();
	}
	
	private void createTextInput() {
		endpoint = new TextItem();
		setTitleOrientation(TitleOrientation.TOP);
		endpoint.setTitle("Endpoint");
		endpoint.setWidth(250);
		endpoint.setDefaultValue(getQueryTab().getTabSettings().getEndpoint());
		//For this default value, also retrieve CORS setting
		JsMethods.checkCorsEnabled(getView().getSelectedTabSettings().getEndpoint());
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
				if (!latestEndpointValue.equals(getEndpoint())) {
					JsMethods.checkCorsEnabled(getEndpoint());
					getView().getSettings().getSelectedTabSettings().setEndpoint(getEndpoint());
					LocalStorageHelper.storeSettingsInCookie(getView().getSettings());
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
	
	private QueryTab getQueryTab() {
		return this.queryTab;
	}
}
