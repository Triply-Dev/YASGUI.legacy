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
package com.data2semantics.yasgui.client.tab.optionbar;

import java.util.Iterator;
import java.util.TreeMap;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.TooltipProperties;
import com.data2semantics.yasgui.client.helpers.properties.TooltipText;
import com.data2semantics.yasgui.client.helpers.properties.ZIndexes;
import com.data2semantics.yasgui.client.settings.TabSettings;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.http.client.URL;

public class LinkCreator extends ImgButton {
	private static final int TOOLTIP_VERSION_LINK = 2;
	private View view;
	private Window window;
	private static int WINDOW_WIDTH = 200;
	private static int WINDOW_HEIGHT = 170;
	private static int ICON_WIDTH = 25;
	private static int ICON_HEIGHT = 25;
	private static int ANIMATE_SPEED = 100;
	private CheckboxItem query;
	private CheckboxItem endpoint;
	private CheckboxItem tabTitle;
	private CheckboxItem outputFormat;
	private CheckboxItem requestOptions;
	private TextItem urlTextBox;
	private Canvas urlTextBoxAnim;
	
	public LinkCreator(View view) {
		this.view = view;
		setSrc("link.png");

		setWidth(ICON_WIDTH);
		setHeight(ICON_HEIGHT);
		setShowDown(false);
		setShowRollOver(false);

		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				window = new Window();
				window.setZIndex(ZIndexes.MODAL_WINDOWS);
				window.setTitle("Get link");
				window.setIsModal(true);
				window.setDismissOnOutsideClick(true);
				window.setShowHeader(false);
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
		LayoutSpacer spacer1 = new LayoutSpacer();
		spacer1.setHeight100();
		layout.addMember(spacer1);
		
		layout.addMember(getLinkText());

		layout.addMember(getLinkOptions());
		
		LayoutSpacer spacer2 = new LayoutSpacer();
		spacer2.setHeight100();
		layout.addMember(spacer2);
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand(){
			@Override
			public void execute() {
				updateLink();
				
			}});
		
		return layout;
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

	private DynamicForm getLinkOptions() {
		DynamicForm form = new DynamicForm();
		form.setWidth100();
		form.setTitleOrientation(TitleOrientation.RIGHT);

		query = new CheckboxItem("query");
		query.setValue(true);
		query.setDisabled(true);

		endpoint = new CheckboxItem("endpoint");
		endpoint.setValue(true);
		endpoint.setDisabled(true);

		tabTitle = new CheckboxItem("tabTitle", "tab title");
		tabTitle.addChangedHandler(new ChangedHandler() {
			public void onChanged(ChangedEvent event) {
				updateLink();
			}
		});
		outputFormat = new CheckboxItem("outputFormat", "output format");
		outputFormat.addChangedHandler(new ChangedHandler() {
			public void onChanged(ChangedEvent event) {
				updateLink();
			}
		});
		requestOptions = new CheckboxItem("requestOptions", "request options");
		requestOptions.addChangedHandler(new ChangedHandler() {
			public void onChanged(ChangedEvent event) {
				updateLink();
			}
		});


		form.setFields(query, endpoint, tabTitle, outputFormat, requestOptions);

		return form;
	}
	
	private void updateLink() {
		TreeMap<String, String> args = getQueryArgs();
		final String url = getLink(args);
		urlTextBoxAnim.animateFade(20, new AnimationCallback(){
			@Override
			public void execute(boolean earlyFinish) {
				urlTextBox.setValue(url);
				urlTextBoxAnim.animateFade(100, new AnimationCallback(){
					@Override
					public void execute(boolean earlyFinish) {
						//nothing
					}}, ANIMATE_SPEED);
			}}, ANIMATE_SPEED);
	}
	
	private TreeMap<String, String> getQueryArgs() {
		TreeMap<String, String> args = new TreeMap<String, String>();
		if ((Boolean)query.getValueAsBoolean()) args.put(TabSettings.QUERY_STRING, view.getSelectedTabSettings().getQueryString());
		if ((Boolean)endpoint.getValueAsBoolean()) args.put(TabSettings.ENDPOINT, view.getSelectedTabSettings().getEndpoint());
		if ((Boolean)outputFormat.getValueAsBoolean()) args.put(TabSettings.OUTPUT_FORMAT, view.getSelectedTabSettings().getOutputFormat());
		if ((Boolean)tabTitle.getValueAsBoolean()) args.put(TabSettings.TAB_TITLE, view.getSelectedTabSettings().getTabTitle());
		if ((Boolean)requestOptions.getValueAsBoolean()) {
			args.put(TabSettings.CONTENT_TYPE_SELECT, view.getSelectedTabSettings().getSelectContentType());
			args.put(TabSettings.CONTENT_TYPE_CONSTRUCT, view.getSelectedTabSettings().getConstructContentType());
			args.put(TabSettings.REQUEST_METHOD, view.getSelectedTabSettings().getRequestMethod());
			args.putAll(view.getSelectedTabSettings().getQueryArgs());
		}
		return args;
	}
	
	private String getLink(TreeMap<String, String> args) {
		String url = getBaseUrl();
		
		boolean firstItem = true;
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
	
	private String getBaseUrl() {
		String url = com.google.gwt.user.client.Window.Location.getProtocol() + "//";
		url += com.google.gwt.user.client.Window.Location.getHostName();
		
		String port = com.google.gwt.user.client.Window.Location.getPort();
		if (port != null && port.length() > 0) {
			url += ":" + port;
		}
		url += com.google.gwt.user.client.Window.Location.getPath();
		return url;
	}
	
	public void showToolTips(int fromVersionId) {
		if (fromVersionId <= TOOLTIP_VERSION_LINK) {
			TooltipProperties tProp = new TooltipProperties();
			tProp.setId(getDOM().getId());
			tProp.setContent(TooltipText.LINK_GENERATOR);
			tProp.setMy(TooltipProperties.POS_TOP_RIGHT);
			tProp.setAt(TooltipProperties.POS_BOTTOM_CENTER);
			Helper.drawTooltip(tProp);
		}
	}
}
