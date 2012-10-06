package com.data2semantics.yasgui.client;

import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.TooltipProperties;
import com.data2semantics.yasgui.shared.exceptions.ElementIdException;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;

public class Footer extends HLayout {
	private View view;
	private ImgButton tooltipButton;
	public Footer(View view) {
		this.view = view;
		setWidth100();
		setHeight(30);
		setStyleName("footer");
		setDefaultLayoutAlign(Alignment.CENTER);
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setWidth100();
		addMember(spacer);
		addGitHub();
		addTooltipToggler();
	}
	
	public void showTooltips() throws ElementIdException {
		TooltipProperties tProp = new TooltipProperties();
		tProp.setId(tooltipButton.getDOM().getId());
		tProp.setContent("Click this button to show these hints again");
		tProp.setMy(TooltipProperties.POS_BOTTOM_RIGHT);
		tProp.setAt(TooltipProperties.POS_TOP_CENTER);
		Helper.drawTooltip(tProp);
	}
	
	private void addTooltipToggler() {
		tooltipButton = new ImgButton();
		tooltipButton.setSrc("icons/fugue/question-white.png");
		tooltipButton.setMargin(3);
		tooltipButton.setHeight(20);
		tooltipButton.setShowRollOver(false);
		tooltipButton.setShowDown(false);
		tooltipButton.setWidth(20);
		tooltipButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.showTooltips();
			}
		});
		addMember(tooltipButton);
	}

	private void addGitHub() {
		Label link = new Label("View src on GitHub");
		link.setStyleName("footerlink");
		link.setAlign(Alignment.CENTER);
		link.setMargin(3);
//		link = new Label(message);
		link.setWrap(false);
		link.setCanSelectText(true);
		link.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open("http://github.com/LaurensRietveld/yasgui", "_blank", null);
			}
		});
		
		addMember(link);
	}

	
}
