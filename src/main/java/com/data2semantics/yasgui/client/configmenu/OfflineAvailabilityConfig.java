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
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.VisibilityChangedEvent;
import com.smartgwt.client.widgets.events.VisibilityChangedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;

public class OfflineAvailabilityConfig extends Window {
	private static int HEIGHT = 150;
	private static int WIDTH = 700;
	private View view;
	private DynamicForm form;
	private CheckboxItem useOfflineFunctionality;
	private StaticTextItem offlineStatus;
	private CheckboxItem downloadNotification;
	public OfflineAvailabilityConfig(View view) {
		this.view = view;
		setHeight(HEIGHT);
		setWidth(WIDTH);
		setZIndex(ZIndexes.MODAL_WINDOWS);
		setTitle("Configure Offline Availability");
		setIsModal(true);
		setDismissOnOutsideClick(true);
		setAutoCenter(true);
		addVisibilityChangedHandler(new VisibilityChangedHandler() {
			@Override
			public void onVisibilityChanged(VisibilityChangedEvent event) {
				storeContentInSettings();
			}
		});
		addContent();
		draw();
	}
	
	private void storeContentInSettings() {
		view.getSettings().setUseOfflineCaching(useOfflineFunctionality.getValueAsBoolean());
		view.getSettings().setShowAppcacheDownloadNotification(downloadNotification.getValueAsBoolean());
		LocalStorageHelper.storeSettingsInCookie(view.getSettings());
	}
	
	private void addContent() {
		form = new DynamicForm();
		form.setWidth100();
		form.setHeight100();
		form.setTitleWidth(300);
		form.setColWidths(0, 50);
		
		useOfflineFunctionality = new CheckboxItem();  
		
		useOfflineFunctionality.setLabelAsTitle(true);
		useOfflineFunctionality.setTitle("Use offline availability");
		useOfflineFunctionality.setHint("Enable to use YASGUI offline. Disable to avoid downloading YASUI locally (saves bandwidth)");
		useOfflineFunctionality.setValue(view.getSettings().useOfflineCaching());
		
		downloadNotification = new CheckboxItem();
		downloadNotification.setLabelAsTitle(true);
		downloadNotification.setTitle("Show download notification");
		downloadNotification.setHint("On every YASGUI update, your browser downloads a local version of YASGUI, and shows a download notification and progress bar");
		downloadNotification.setValue(view.getSettings().showAppcacheDownloadNotification());
		
		offlineStatus = new StaticTextItem();
		offlineStatus.setTitle("Offline availability status");
		offlineStatus.setValue(view.getAppcacheHelper().getStatus());
		
		form.setItems(useOfflineFunctionality, downloadNotification, offlineStatus);
		addItem(form);
	}
}
