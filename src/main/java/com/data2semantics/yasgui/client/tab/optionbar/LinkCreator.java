package com.data2semantics.yasgui.client.tab.optionbar;

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

import java.util.Iterator;
import java.util.TreeMap;
import com.smartgwt.client.types.Positioning;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.TooltipProperties;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.settings.TooltipText;
import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.data2semantics.yasgui.shared.SettingKeys;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class LinkCreator extends ImgButton {
	private static final int TOOLTIP_VERSION_LINK = 2;
	private View view;
	private Window window;
	private static int WINDOW_WIDTH = 230;
	private static int WINDOW_HEIGHT = 85;
	private static int ICON_WIDTH = 25;
	private static int ICON_HEIGHT = 25;
	private static int ANIMATE_SPEED = 100;
	private static int OFFSET_TOP = 105;
	public static int OFFSET_RIGHT = 20;
	private TextItem urlTextBox;
	private Canvas urlTextBoxAnim;
	private Button shortenUrlButton;
	
	public LinkCreator(View view) {
		this.view = view;
		setSrc(Imgs.LINK.get());
		setWidth(ICON_WIDTH);
		setHeight(ICON_HEIGHT);
		setPosition(Positioning.ABSOLUTE);
		setTop(OFFSET_TOP);
		changeHorizontalOffset();
		setZIndex(ZIndexes.TAB_CONTROLS);
		setShowDown(false);
		setShowRollOver(false);

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				window = new Window();
				window.setShowHeader(false);
				window.setZIndex(ZIndexes.MODAL_WINDOWS);
				window.setTitle("Get link");
				window.setIsModal(true);
				window.setDismissOnOutsideClick(true);
				int left = (getAbsoluteLeft() + ICON_WIDTH) - WINDOW_WIDTH;
				int top = getAbsoluteTop() + ICON_HEIGHT;
				window.setRect(left, top, WINDOW_WIDTH, WINDOW_HEIGHT);

				window.setShowMinimizeButton(false);
				window.addItem(getWindowContent());
				window.draw();
			}
		});

	}

	private VLayout getWindowContent() {
		VLayout layout = new VLayout();
		layout.addMember(Helper.getVSpacer());
		
		layout.addMember(getLinkText());
		
		HLayout belowLink = new HLayout();
		belowLink.setMargin(4);
		
		if (view.getSettings().useBitly()) {
			belowLink.addMember(getShortenUrlButton());
		}
		
		layout.addMember(belowLink);
		
		layout.addMember(Helper.getVSpacer());
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand(){
			@Override
			public void execute() {
				updateLinkWithQueryArgs();
				
			}});
		
		return layout;
	}

	private Button getShortenUrlButton() {
		shortenUrlButton = new Button("Shorten url");
		shortenUrlButton.setWidth(75);
		shortenUrlButton.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				view.getElements().onLoadingStart("Fetching short url");
				view.getRemoteService().getShortUrl(urlTextBox.getValueAsString(), new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						view.getElements().onError(caught);
					}

					public void onSuccess(String shortUrl) {
						updateLink(shortUrl);
						shortenUrlButton.setDisabled(true);
						view.getElements().onLoadingFinish();
					}
				});
			}});
		return shortenUrlButton;
	}

	private Canvas getLinkText() {
		urlTextBoxAnim = new Canvas(); 
		urlTextBoxAnim.setWidth100();
		DynamicForm form = new DynamicForm();
		form.setWidth100();

		urlTextBox = new TextItem();
		urlTextBox.setCanEdit(false);
		urlTextBox.setHeight(27);
		urlTextBox.setShowTitle(false);
		urlTextBox.addClickHandler(new com.smartgwt.client.widgets.form.fields.events.ClickHandler(){
			@Override
			public void onClick(com.smartgwt.client.widgets.form.fields.events.ClickEvent event) {
				urlTextBox.selectValue();
			}});
		urlTextBox.setWidth(WINDOW_WIDTH - 15);
		form.setItems(urlTextBox);
		urlTextBoxAnim.addChild(form);
		return urlTextBoxAnim;
	}

	
	
	private void updateLinkWithQueryArgs() {
		TreeMap<String, String> args = getQueryArgs();
		updateLink(getLink(args));
	}

	private void updateLink(final String newUrl) {
		if (view.getSettings().useBitly()) {
			shortenUrlButton.setDisabled(false);
		}
		urlTextBoxAnim.animateFade(20, new AnimationCallback(){
			@Override
			public void execute(boolean earlyFinish) {
				urlTextBox.setValue(newUrl);
				urlTextBoxAnim.animateFade(100, new AnimationCallback(){
					@Override
					public void execute(boolean earlyFinish) {
						//nothing
					}}, ANIMATE_SPEED);
			}}, ANIMATE_SPEED);
	}
	
	private TreeMap<String, String> getQueryArgs() {
		TreeMap<String, String> args = new TreeMap<String, String>();
		args.put(SettingKeys.QUERY_STRING, view.getSelectedTabSettings().getQueryString());
		args.put(SettingKeys.ENDPOINT, view.getSelectedTabSettings().getEndpoint());
		args.put(SettingKeys.OUTPUT_FORMAT, view.getSelectedTabSettings().getOutputFormat());
		args.put(SettingKeys.TAB_TITLE, view.getSelectedTabSettings().getTabTitle());
		args.put(SettingKeys.CONTENT_TYPE_SELECT, view.getSelectedTabSettings().getSelectContentType());
		args.put(SettingKeys.CONTENT_TYPE_CONSTRUCT, view.getSelectedTabSettings().getConstructContentType());
		args.put(SettingKeys.REQUEST_METHOD, view.getSelectedTabSettings().getRequestMethod());
		args.putAll(view.getSelectedTabSettings().getCustomQueryArgs());
		return args;
	}
	
	private String getLink(TreeMap<String, String> args) {
		String url = JsMethods.getLocation();
		
		//remove these, as we will be adding these again
		url = Helper.removeArgumentsFromUrl(url, args.keySet());
		boolean firstItem = true;
		if (url.contains("?")) {
			firstItem = false;
		}
		Iterator<String> iterator = args.keySet().iterator();
		while (iterator.hasNext()) {
			if (firstItem) {
				url += "?";
				firstItem = false;
			} else {
				url += "&";
			}
			String key = iterator.next();
			String value = URL.encodeQueryString(args.get(key));
			url += key + "=" + value;
		}
		return url;
		
	}
	
	public void showToolTips(int fromVersionId) {
		if (fromVersionId <= TOOLTIP_VERSION_LINK) {
			TooltipProperties tProp = new TooltipProperties();
			tProp.setId(getDOM().getId());
			tProp.setContent(TooltipText.LINK_GENERATOR);
			tProp.setMy(TooltipProperties.POS_RIGHT_CENTER);
			tProp.setAt(TooltipProperties.POS_LEFT_CENTER);
			Helper.drawTooltip(tProp);
		}
	}
	
	/**
	 * called on initiation, as well as window resize
	 * @param offset
	 */
	public void changeHorizontalOffset() {
		int windowWidth = com.google.gwt.user.client.Window.getClientWidth();
		setLeft(windowWidth - ICON_WIDTH - OFFSET_RIGHT);//compensate for smargwt scrollbar, so subtract some more
	}
}
