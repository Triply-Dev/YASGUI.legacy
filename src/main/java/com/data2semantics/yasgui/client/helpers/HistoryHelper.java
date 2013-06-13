package com.data2semantics.yasgui.client.helpers;

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

import java.io.IOException;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.settings.Settings;

public class HistoryHelper {
	private View view;
	private String previousCheckpointSettings = "";
	public HistoryHelper(View view) {
		this.view = view;
		JsMethods.setHistoryStateChangeCallback();
	}
	
	/**
	 * Set history checkpoint, normally called -after- executing a change / operation (e.g. after adding a new tab)
	 */
	public void setHistoryCheckpoint() {
		String currentSettingsString = view.getSettings().toString();
		if (currentSettingsString.equals(previousCheckpointSettings) == false) {
			//only add new checkpoint when the settings are different than the last one
			previousCheckpointSettings = currentSettingsString;
			JsMethods.pushHistoryState(currentSettingsString, view.getSettings().getBrowserTitle(), "");
		}
	}
	
	/**
	 * Set history checkpoint, normally called -after- executing a change / operation (e.g. after adding a new tab)
	 */
	public void replaceHistoryState() {
		String currentSettingsString = view.getSettings().toString();
		previousCheckpointSettings = currentSettingsString;
		JsMethods.replaceHistoryState(currentSettingsString, view.getSettings().getBrowserTitle(), "");
	}
	
	public void onHistoryStateChange() {
		try {
			updateView(JsMethods.getHistoryState().getData());
		} catch (Exception e) {
			view.getElements().onError(e);
		}
	}
	
	private void updateView(String settingsString) throws IOException {
		//for backwards compatability, retrieve default methods again 
		//(olders cached versions of the settings did not store these defaults)
		Settings settings = new Settings(JsMethods.getDefaultSettings());
		settings.addToSettings(settingsString);
		
		//check whether the tab settings stayed the same. if they did, don't redraw the tabs, only the selected tab setting
		boolean tabSetChanged = !settings.getTabArrayAsJson().toString().equals(view.getSettings().getTabArrayAsJson().toString());
		view.setSettings(settings);
		LocalStorageHelper.storeSettingsInCookie(view.getSettings());
		if (tabSetChanged) {
			view.getTabs().redrawTabs();
		} else {
			view.getTabs().selectTab(view.getSettings().getSelectedTabNumber());
		}
	}
	
}
