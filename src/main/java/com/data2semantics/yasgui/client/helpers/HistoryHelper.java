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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.settings.Settings;

public class HistoryHelper {
	public class HistQueryResults{
		private String resultString;
		private String contentType;
		public HistQueryResults(String resultString, String contentType) {
			this.resultString = resultString;
			this.contentType = contentType;
		}
		public String getString() {
			return this.resultString;
		}
		public String getContentType() {
			return this.contentType;
		}
	}
	LinkedHashMap<String, HistQueryResults> histQueryResults = new LinkedHashMap<String, HistQueryResults>(MAX_HIST_RESULTS + 1) {
		private static final long serialVersionUID = -4596552223943368852L;
		protected boolean removeEldestEntry(Map.Entry<String, HistQueryResults> eldest) {
           return size() > MAX_HIST_RESULTS;
        }
     };
	private View view;
	private String previousCheckpointSettings = "";
	private boolean historyEnabled = false;
	private static int MAX_HIST_RESULTS = 3;
	public HistoryHelper(View view) {
		this.historyEnabled = JsMethods.historyApiSupported();
		this.view = view;
		if (historyEnabled) {
			JsMethods.setHistoryStateChangeCallback();
		}
	}
	
	/**
	 * Set history checkpoint, normally called -after- executing a change / operation (e.g. after adding a new tab)
	 * @throws IOException 
	 */
	public void setHistoryCheckpoint() {
		if (historyEnabled) {
			Settings currentSettings = view.getSettings().clone();
			
			//we don't want this stuff in our history: this might explode our memory when using query for lots of times during 1 session
			currentSettings.clearQueryResults();
			String currentSettingsString = currentSettings.toString();
			if (currentSettingsString.equals(previousCheckpointSettings) == false) {
				//only add new checkpoint when the settings are different than the last one
				previousCheckpointSettings = currentSettingsString;
				JsMethods.pushHistoryState(currentSettingsString, view.getSettings().getBrowserTitle(), "");
			}
		}
	}
	
	/**
	 * Set history checkpoint, normally called -after- executing a change / operation (e.g. after adding a new tab)
	 */
	public void replaceHistoryState() {
		if (historyEnabled) {
			String currentSettingsString = view.getSettings().toString();
			
			previousCheckpointSettings = currentSettingsString;
			JsMethods.replaceHistoryState(currentSettingsString, view.getSettings().getBrowserTitle(), "");
		}
	}
	
	public void onHistoryStateChange() {
		try {
			updateView(JsMethods.getHistoryState().getData());
		} catch (Exception e) {
			view.getErrorHelper().onError(e);
		}
	}
	
	private void updateView(String settingsString) throws IOException {
		//for backwards compatability, retrieve default methods again 
		//(olders cached versions of the settings did not store these defaults)
		Settings currentSettings = view.getSettings();
		Settings histSettings = new Settings(JsMethods.getDefaultSettings());
		histSettings.addToSettings(settingsString);
		view.setSettings(histSettings);
		LocalStorageHelper.storeSettings(view.getSettings());
		if (onlySelectedTabChanged(currentSettings, histSettings)) {
			view.getTabs().selectTab(view.getSettings().getSelectedTabNumber());
			view.getTabs().redrawTabs();
		} else if (onlyQueryChanged(currentSettings, histSettings)) {
			view.getSelectedTab().getQueryTextArea().setQuery(view.getSelectedTabSettings().getQueryString());
			view.getSelectedTab().getResultContainer().drawIfPossible();
		} else {
			view.getTabs().redrawTabs();
		}
		
	}
	
	private boolean onlySelectedTabChanged(Settings currentSettings, Settings histSettings) {
		int currentSettingsBak = currentSettings.getSelectedTabNumber();
		int histSettingsBak = histSettings.getSelectedTabNumber();
		currentSettings.setSelectedTabNumber(6666);
		histSettings.setSelectedTabNumber(6666);
		
		boolean onlySelectedTabChanged = currentSettings.toString().equals(histSettings.toString());
		
		currentSettings.setSelectedTabNumber(currentSettingsBak);
		histSettings.setSelectedTabNumber(histSettingsBak);
		
		return onlySelectedTabChanged;
	}
	
	private boolean onlyQueryChanged(Settings currentSettings, Settings histSettings) {
		String currentSettingsQuery = currentSettings.getSelectedTabSettings().getQueryString();
		String histSettingsQuery = histSettings.getSelectedTabSettings().getQueryString();
		
		currentSettings.getSelectedTabSettings().setQueryString("");
		histSettings.getSelectedTabSettings().setQueryString("");
		boolean onlyQueryChanged = currentSettings.toString().equals(histSettings.toString());
		
		currentSettings.getSelectedTabSettings().setQueryString(currentSettingsQuery);
		histSettings.getSelectedTabSettings().setQueryString(histSettingsQuery);
		return onlyQueryChanged;
		
	}

	public void addQueryResults(String endpoint, String queryString, String resultString, String contentType) {
		if (historyEnabled) {
			histQueryResults.put(endpoint + queryString, new HistQueryResults(resultString, contentType));
		}
	}
	
	public HistQueryResults getHistQueryResults(String endpoint, String queryString) {
		HistQueryResults foundQueryResults = null;
		List<String> keyList = new ArrayList<String>(this.histQueryResults.keySet());
		for (int i = keyList.size() - 1 ; i >= 0; i--) {
			if (keyList.get(i).equals(endpoint + queryString)) {
				foundQueryResults = histQueryResults.get(endpoint + queryString);
				break;
			}
		}
		return foundQueryResults;
	}


	
}
