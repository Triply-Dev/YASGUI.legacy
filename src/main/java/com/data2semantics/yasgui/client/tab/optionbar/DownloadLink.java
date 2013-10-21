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

import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.ContentTypes.Type;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLFlow;

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
	
	public void showDownloadIcon(String url, Type contentType) {
		for (Canvas child: getChildren()) {
			child.markForDestroy();
		}
		addRegularDownload(url, contentType);
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
		String disabledIconHtml = "<img title='No results to download' " + IMAGE_STYLE + " src='" + Imgs.OTHER_IMAGES_DIR.getUnprocessed() + Imgs.DOWNLOAD.getDisabled() + "'></img>";
		html.setContents(disabledIconHtml);
		disabledIcon.addChild(html);
		addChild(disabledIcon);
	}
	private void addRegularDownload(String url, Type contentType) {
		downloadIcon = new Canvas();
		downloadIcon.setHeight(HEIGHT);
		downloadIcon.setWidth(WIDTH);
		HTMLFlow html = new HTMLFlow();
		html.setWidth(WIDTH);
		html.setHeight(HEIGHT+5);
		String downloadLink = "<a href='" + url + "' ";
		if (JsMethods.downloadAttributeSupported()) {
			downloadLink += "download='" + getDownloadFilename(contentType) + "'";
		} else {
			downloadLink += "target='_blank'";
		}
		downloadLink += "><img title='Download query response' " + IMAGE_STYLE + " src='" + Imgs.OTHER_IMAGES_DIR.getUnprocessed() + Imgs.DOWNLOAD.get() + "'></img></a>";
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
		downloadLink += "><img title='Download CSV table' " + IMAGE_STYLE + " src='" + Imgs.OTHER_IMAGES_DIR.getUnprocessed() + Imgs.TABLE.get() + "'></img></a>";
		html.setContents(downloadLink);
		csvIcon.addChild(html);
		addChild(csvIcon);
	}
	
	
	public String getDownloadFilename(Type contentType) {
		String filename = view.getSelectedTabSettings().getTabTitle();
		filename += contentType.getFileExtension();
		return filename;
	}
	
	public String getCsvDownloadFilename() {
		String filename = view.getSelectedTabSettings().getTabTitle() + ".csv";
		return filename;
	}

}
