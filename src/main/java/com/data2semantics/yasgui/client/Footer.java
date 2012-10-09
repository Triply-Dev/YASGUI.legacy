/*******************************************************************************
 * Copyright (c)  2012 Laurens Rietveld
 * 
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 * 
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 * 
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.data2semantics.yasgui.client;

import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.TooltipProperties;
import com.data2semantics.yasgui.client.helpers.properties.TooltipText;
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
	
	private static String GITHUB_LINK = "http://github.com/LaurensRietveld/yasgui";
	private View view;
	private ImgButton tooltipButton;
	public Footer(View view) {
		this.view = view;
		setWidth100();
		setHeight(30);
		setStyleName("footer");
		setDefaultLayoutAlign(Alignment.CENTER);
		addYasguiVersion();
		LayoutSpacer spacer = new LayoutSpacer();
		spacer.setWidth100();
		addMember(spacer);
		addGitHub();
		LayoutSpacer smallSpacer = new LayoutSpacer();
		smallSpacer.setWidth(15);
		addMember(smallSpacer);
		addTooltipToggler();
		LayoutSpacer extraSmallSpacer = new LayoutSpacer();
		extraSmallSpacer.setWidth(5);
		addMember(extraSmallSpacer);
	}
	
	public void showTooltips() throws ElementIdException {
		TooltipProperties tProp = new TooltipProperties();
		tProp.setId(tooltipButton.getDOM().getId());
		tProp.setContent(TooltipText.TOOLTIP_BUTTON);
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
	
	private void addYasguiVersion() {
		Label yasguiVersion = new Label("Yasgui " + View.VERSION);
		yasguiVersion.setAlign(Alignment.CENTER);
		yasguiVersion.setMargin(3);
		yasguiVersion.setStyleName("footerText");
		yasguiVersion.setWrap(false);
		yasguiVersion.setCanSelectText(true);
		yasguiVersion.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Window.open(GITHUB_LINK, "_blank", null);
				
			}});
		addMember(yasguiVersion);
	}

	private void addGitHub() {

		ImgButton githubButton = new ImgButton();
		githubButton.setSrc("github.png");
		githubButton.setMargin(1);
		githubButton.setHeight(20);
		githubButton.setShowRollOver(false);
		githubButton.setShowDown(false);
		githubButton.setWidth(45);
		githubButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open(GITHUB_LINK, "_blank", null);
			}
		});
		addMember(githubButton);
		
	}

	
}
