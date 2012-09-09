package com.data2semantics.yasgui.client;

import com.google.gwt.user.client.Window;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;

public class Footer extends HLayout {
	private View view;

	public Footer(View view) {
		this.view = view;
		setLayoutTopMargin(4);
		setWidth100();
		setHeight(30);
		setStyleName("footer");
		setDefaultLayoutAlign(Alignment.CENTER);
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setWidth100();
		addMember(spacer);
		addGitHub();
	}

	private void addGitHub() {
		Label label = getFooterLink("View src on GitHub", "http://github.com/LaurensRietveld/yasgui");
//		Label label = Helper.getLinkNewWindow("View src on GitHub", "http://github.com/LaurensRietveld/yasgui");
		
		addMember(label);
	}

	private View getView() {
		return this.view;
	}

	private Label getFooterLink(String message, final String url) {
		Label link = new Label(message);
		link.addStyleName("footerlink");
		link.setAlign(Alignment.CENTER);
//		link.setMargin(3);
//		link = new Label(message);
		link.setWrap(false);
		link.setCanSelectText(true);
		link.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open(url, "_blank", null);
			}
		});
		return link;
	}
}
