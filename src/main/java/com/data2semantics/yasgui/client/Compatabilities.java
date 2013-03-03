package com.data2semantics.yasgui.client;

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

import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.helpers.LocalStorageHelper;
import com.data2semantics.yasgui.client.helpers.properties.ZIndexes;
import com.data2semantics.yasgui.shared.StaticConfig;
import com.google.gwt.storage.client.Storage;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.HTMLFlow;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class Compatabilities extends Window {
	
	private static int HEIGHT = 200;
	private static int WIDTH = 700;
	private static int ROW_HEIGHT = 40;
	private static int BUTTON_WIDTH = 150;
	private static String URL_COMPATABILTIES_LOCAL_STORAGE = "http://caniuse.com/#feat=namevalue-storage";
	private static String URL_COMPATABILITIES_DOWNLOAD_ATTRIBUTE = "http://caniuse.com/#feat=download";
	private static String URL_COMPATABILITIES_DOWNLOAD_FILE = "http://caniuse.com/#feat=bloburls";
	
	
	public static int VERSION_NUMBER = 3; //used for determining whether we need warning icon (i.e. something not compatible, and not shown before)
	private boolean html5StorageSupported = false;
	private boolean downloadAttributeSupported = false;
	private boolean downloadFileSupported = false;
	private boolean allSupported = false;
	private View view;
	private VLayout layout = new VLayout();;
	public Compatabilities(View view) {
		this.view = view;
		setHeight(HEIGHT);
		setWidth(WIDTH);
		setZIndex(ZIndexes.MODAL_WINDOWS);
		setTitle("Features supported by your browser");
		setIsModal(true);
		setDismissOnOutsideClick(true);
		setAutoCenter(true);
		layout.setMargin(10);
		addItem(layout);
		checkCompatabilities();
	}
	
	private void checkCompatabilities() {
		html5StorageSupported = Storage.isLocalStorageSupported();
		downloadFileSupported = JsMethods.stringToDownloadSupported();
		downloadAttributeSupported = JsMethods.downloadAttributeSupported();
		
		allSupported = (html5StorageSupported && downloadFileSupported && downloadAttributeSupported);
	}
	
	public boolean allSupported() {
		return allSupported;
	}
	
	
	
	public void drawContent() {
		LocalStorageHelper.setCompatabilitiesShown(StaticConfig.VERSION_ID);
		
		String html = "<div style='width:100%;text-align:center;'>You are using <strong>" + JsMethods.getBrowserName() + " " + JsMethods.getBrowserVersionNumber() + "</strong> on <strong>" + JsMethods.GetBrowserOs() + "</strong>. ";
		if (allSupported) {
			html += "Congrats! Your browser supports all YASGUI functionality";
		} else {
			html += "We recommend the latest version of Chrome";
		}
		html += "</div>";
		HTMLFlow header = new HTMLFlow();
		header.setWidth100();
		header.setLayoutAlign(Alignment.CENTER);
		header.setContents(html);
		header.setMargin(10);
		layout.addMember(header);
		
		drawHtml5LocalStorage();
		drawDownloadFunctionality();
		drawDownloadFilenameFunctionality();
		draw();
		
		//ok, so we've shown the stuff. reload the menu button, as we might still have an exclamation mark there
		view.getTabs().redrawConfigButton("icons/diagona/bolt.png");
	}
	
	private void drawHtml5LocalStorage() {
		HLayout hlayout = new HLayout();
		hlayout.setHeight(ROW_HEIGHT);
		
		hlayout.addMember(getIcon(html5StorageSupported));
		hlayout.addMember(getRowName("HTML5 Local Storage"));
		if (html5StorageSupported) {
			hlayout.addMember(getExplanation("Supported by your browser. Allows for client-side caching, resulting in faster page loads"));
		} else {
			hlayout.addMember(getExplanation("Not supported by your browser. This results in slightly slower page loads, as client-side caching is not possible"));
		}
		hlayout.addMember(getLink(URL_COMPATABILTIES_LOCAL_STORAGE));
		
		layout.addMember(hlayout);
	}
	
	private void drawDownloadFunctionality() {
		HLayout hlayout = new HLayout();
		hlayout.setHeight(ROW_HEIGHT);
		
		hlayout.addMember(getIcon(downloadFileSupported));
		hlayout.addMember(getRowName("Clientside File Downloads"));
		if (downloadFileSupported) {
			hlayout.addMember(getExplanation("Supported by your browser. Allows you to download query results to file"));
		} else {
			hlayout.addMember(getExplanation("Not supported by your browser. You are not able to download query results to file"));
		}
		
		hlayout.addMember(getLink(URL_COMPATABILITIES_DOWNLOAD_FILE));
		
		layout.addMember(hlayout);
	}
	
	private void drawDownloadFilenameFunctionality() {
		HLayout hlayout = new HLayout();
		hlayout.setHeight(ROW_HEIGHT);
		
		hlayout.addMember(getIcon(downloadAttributeSupported));
		hlayout.addMember(getRowName("Download attributes"));
		if (downloadAttributeSupported) {
			hlayout.addMember(getExplanation("Supported by your browser. This allows the files you download to have 'sensible' names, instead of a long unrecognizable ID"));
		} else {
			hlayout.addMember(getExplanation("Not supported by your browser. Files you download will have a strange ID as filename, instead of a more sensible name"));
		}
		
		hlayout.addMember(getLink(URL_COMPATABILITIES_DOWNLOAD_ATTRIBUTE));
		
		layout.addMember(hlayout);
	}
	
	
	
	private Img getIcon(boolean supported) {
		Img img;
		if (supported) {
			img = new Img("icons/fugue/tick.png");
		} else {
			img = new Img("icons/fugue/cross.png");
		}
		img.setLayoutAlign(VerticalAlignment.CENTER);
		img.setValign(VerticalAlignment.CENTER);
		img.setHeight(32);
		img.setWidth(32);
		img.setMargin(5);
		return img;
	}
	
	private HTMLFlow getRowName(String string) {
		string = "<div style='height:" + Integer.toString(ROW_HEIGHT) + "px;'><img src='images/1px.png' alt=\"\" style=\"width:1px; height:100%; vertical-align:middle\" /><strong>" + string + "</strong></div>";
		HTMLFlow label = new HTMLFlow(string);
		label.setWidth(150);
		label.setHeight100();
		label.setLayoutAlign(VerticalAlignment.CENTER);
		return label;
	}
	
	private Button getLink(final String url) {
		Button moreInfo = new Button("Show Compatible Browsers");
		moreInfo.setWidth(BUTTON_WIDTH);
		moreInfo.setLayoutAlign(VerticalAlignment.CENTER);
		moreInfo.addClickHandler(new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				com.google.gwt.user.client.Window.open(url, "_blank", "");
			}});
		return moreInfo;
	}
	
	private Label getExplanation(String string){
		Label label = new Label(string);
		label.setWidth100();
		label.setMargin(5);
		return label;
	}
	
}
