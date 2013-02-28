package com.data2semantics.yasgui.client.tab.results.output;

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
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.properties.ZIndexes;
import com.data2semantics.yasgui.client.tab.QueryTab;
import com.smartgwt.client.types.Positioning;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.events.ContentLoadedEvent;
import com.smartgwt.client.widgets.events.ContentLoadedHandler;

public class RawResponse extends HTMLPane {
	private static String APPEND_INPUT_ID = "_rawResponse";
	@SuppressWarnings("unused")
	private View view;
	private String responseString;
	private String inputId;
	private String contentType;
	public RawResponse(View view, QueryTab tab, String responseString, String contentType) {
		this.contentType = contentType;
		this.view = view;
		this.responseString = responseString;
		this.inputId = tab.getID() + APPEND_INPUT_ID;
		String htmlContent = "";
		if (JsMethods.stringToDownloadSupported()) {
			htmlContent += getDownloadLink();
		}
		htmlContent += getTextArea();
		setContents(htmlContent);
	}
	
	private String getTextArea() {
		return "<textarea style=\"overflow:scroll;\" " + "id=\"" + getInputId() + "\"" + ">" + responseString + "</textarea>";
	}
	
	public String getInputId() {
		return this.inputId;
	}
	
	public String getDownloadLink() {
		String url = JsMethods.stringToUrl(responseString, contentType);
		String style = "style='z-index:" + Integer.toString(ZIndexes.DOWNLOAD_ICON) + ";position:absolute;right:0px;top:0px;'";
		String downloadLink = "<a " + style + " href='" + url + "' ";
		if (JsMethods.downloadAttributeSupported()) {
			downloadLink += "download='" + getDownloadFilename() + "'";
		} else {
			downloadLink += "target='_blank'";
		}
		downloadLink += "><img src='images/icons/custom/download.png'></img></a>";
		view.getLogger().severe(downloadLink);
		return downloadLink;
	}
	
	public String getDownloadFilename() {
		String filename = view.getSelectedTabSettings().getTabTitle();
		return filename;
	}
}
