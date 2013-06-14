package com.data2semantics.yasgui.client.configmenu;

/*
 * #%L
 * YASGUI
 * %%
 * Copyright (C) 2013 Laurens Rietveld
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.settings.ExternalLinks;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;

public class About extends Window {
	
	private static int HEIGHT = 100;
	private static int WIDTH = 800;
	private View view;
	public About(View view) {
		this.view = view;
		setHeight(HEIGHT);
		setWidth(WIDTH);
		setZIndex(ZIndexes.MODAL_WINDOWS);
		setTitle("About YASGUI");
		setIsModal(true);
		setDismissOnOutsideClick(true);
		setAutoCenter(true);
		
		addContent();
		
	}
	
	private void addContent() {
		HLayout hLayout = new HLayout();
		LayoutSpacer leftSpacer = new LayoutSpacer();
		leftSpacer.setWidth100();
		
		hLayout.addMember(leftSpacer);
		hLayout.addMember(getGithubCanvas());
		
		LayoutSpacer middleSpacer = new LayoutSpacer();
		middleSpacer.setWidth(40);
		hLayout.addMember(middleSpacer);
		
		hLayout.addMember(view.getElements().getYasguiLogo(45, "Show YASGUI page"));
		
		
		hLayout.addMember(middleSpacer);
		hLayout.addMember(getData2SemanticsCanvas());
		
		LayoutSpacer rightSpacer = new LayoutSpacer();
		rightSpacer.setWidth100();
		hLayout.addMember(rightSpacer);
		
		addItem(hLayout);
		draw();
	}
	
	private Img getGithubCanvas() {
		Img imgButton = new Img(Imgs.get(Imgs.LOGO_GITHUB));
		imgButton.setWidth(140);
		imgButton.setHeight(54);
		imgButton.setCursor(Cursor.POINTER);
		final String title = "View YASGUI source code";
		imgButton.setPrompt(title);
		imgButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				com.google.gwt.user.client.Window.open(ExternalLinks.GITHUB_PAGE, title, "_blank");
			}});
		
		return imgButton;
	}
	private Img getData2SemanticsCanvas() {
		Img imgButton = new Img(Imgs.get(Imgs.LOGO_DATA2SEMANTICS));
		imgButton.setWidth(300);
		imgButton.setHeight(61);
		imgButton.setCursor(Cursor.POINTER);
		final String title = "YASGUI is partially funded by Data2Semantics";
		imgButton.setPrompt(title);
		imgButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				com.google.gwt.user.client.Window.open(ExternalLinks.DATA2SEMANTICS, title, "_blank");
			}});
		
		return imgButton;
	}
	
	
}
