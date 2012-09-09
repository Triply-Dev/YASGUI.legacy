package com.data2semantics.yasgui.client;

import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;

public class Footer extends HLayout {
	private View view;
	
	public Footer(View view) {
		this.view = view;
		addMember(new Label("bla"));
	}
	
	private View getView() {
		return this.view;
	}
}
