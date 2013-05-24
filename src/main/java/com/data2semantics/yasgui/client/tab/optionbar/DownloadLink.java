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

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.settings.Icons;
import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.data2semantics.yasgui.client.tab.results.ResultContainer;

public class DownloadLink extends Canvas {
	private View view;
	private static int WIDTH = 20;
	private static int HEIGHT = 20;
	private static String IMAGE_STYLE = "style='padding-top:3px;padding-right:1px;padding-left:2px;height:" + HEIGHT + "px;width: " + WIDTH + "px;z-index:" + Integer.toString(ZIndexes.DOWNLOAD_ICON) + ";'";
	private Canvas disabledIcon;
	private Canvas downloadIcon;
	private Canvas csvIcon;
	
	public DownloadLink(View view) {
		this.view = view;
		setWidth(WIDTH);
		setHeight(HEIGHT);
		addDisabledDownload();
	}
	
	public void showDownloadIcon(String url, int contentTypeId) {
		for (Canvas child: getChildren()) {
			child.markForDestroy();
		}
		addRegularDownload(url, contentTypeId);
	}
	public void showDisabledIcon() {
		for (Canvas child: getChildren()) {
			child.markForDestroy();
		}
		addDisabledDownload();
	}
	public void showCsvIcon(String url) {
		for (Canvas child: getChildren()) {
			child.markForDestroy();
		}
		addCsvDownload(url);
	}
	
	private void addDisabledDownload() {
		disabledIcon = new Canvas();
		disabledIcon.setHeight(HEIGHT);
		disabledIcon.setWidth(WIDTH);
		HTMLFlow html = new HTMLFlow();
		html.setWidth(WIDTH);
		html.setHeight(HEIGHT+5);
		String disabledIconHtml = "<img title='No results to download' " + IMAGE_STYLE + " src='" + Icons.DIR_IMAGES + Icons.getDisabled(Icons.DOWNLOAD) + "'></img>";
		html.setContents(disabledIconHtml);
		disabledIcon.addChild(html);
		addChild(disabledIcon);
	}
	private void addRegularDownload(String url, int contentTypeId) {
		downloadIcon = new Canvas();
		downloadIcon.setHeight(HEIGHT);
		downloadIcon.setWidth(WIDTH);
		HTMLFlow html = new HTMLFlow();
		html.setWidth(WIDTH);
		html.setHeight(HEIGHT+5);
		String downloadLink = "<a href='" + url + "' ";
		if (JsMethods.downloadAttributeSupported()) {
			downloadLink += "download='" + getDownloadFilename(contentTypeId) + "'";
		} else {
			downloadLink += "target='_blank'";
		}
		downloadLink += "><img title='Download query response' " + IMAGE_STYLE + " src='" + Icons.DIR_IMAGES + Icons.DOWNLOAD + "'></img></a>";
		html.setContents(downloadLink);
		downloadIcon.addChild(html);
		addChild(downloadIcon);
	}
	
	private void addCsvDownload(String url) {
		csvIcon = new Canvas();
		csvIcon.setHeight(HEIGHT);
		csvIcon.setWidth(WIDTH);
		HTMLFlow html = new HTMLFlow();
		html.setWidth(WIDTH);
		html.setHeight(HEIGHT+5);
		String downloadLink = "<a href='" + url + "' ";
		if (JsMethods.downloadAttributeSupported()) {
			downloadLink += "download='" + getCsvDownloadFilename() + "'";
		} else {
			downloadLink += "target='_blank'";
		}
		downloadLink += "><img title='Download CSV table' " + IMAGE_STYLE + " src='" + Icons.DIR_IMAGES + Icons.TABLE + "'></img></a>";
		html.setContents(downloadLink);
		csvIcon.addChild(html);
		addChild(csvIcon);
	}
	
	
	
	public String getDownloadLink(String responseString, String contentType) {
		String url = JsMethods.stringToUrl(responseString, contentType);
		String style = "style='z-index:" + Integer.toString(ZIndexes.DOWNLOAD_ICON) + ";position:absolute;right:0px;top:0px;'";
		String downloadLink = "<a " + style + " href='" + url + "' ";
		if (JsMethods.downloadAttributeSupported()) {
			downloadLink += "download='" + getDownloadFilename(2) + "'";
		} else {
			downloadLink += "target='_blank'";
		}
		downloadLink += "><img src='" + Icons.DIR_IMAGES + Icons.DOWNLOAD + "'></img></a>";
		return downloadLink;
	}
	
	public String getDownloadFilename(int contentTypeId) {
		String filename = view.getSelectedTabSettings().getTabTitle();
		if (contentTypeId == ResultContainer.CONTENT_TYPE_JSON) {
			filename += ".json";
		} else if (contentTypeId == ResultContainer.CONTENT_TYPE_TURTLE) {
			filename += ".ttl";
		} else if (contentTypeId == ResultContainer.CONTENT_TYPE_XML) {
			filename += ".xml";
		}
		return filename;
	}
	
	public String getCsvDownloadFilename() {
		String filename = view.getSelectedTabSettings().getTabTitle() + ".csv";
		return filename;
	}

}
