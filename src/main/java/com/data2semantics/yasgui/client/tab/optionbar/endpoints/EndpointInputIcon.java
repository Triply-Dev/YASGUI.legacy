package com.data2semantics.yasgui.client.tab.optionbar.endpoints;

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

import com.data2semantics.yasgui.client.GwtCallbackWrapper;
import com.data2semantics.yasgui.client.RpcElement;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.JsMethods;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.MovedEvent;
import com.smartgwt.client.widgets.events.MovedHandler;
import com.smartgwt.client.widgets.layout.HLayout;

public class EndpointInputIcon extends HLayout implements RpcElement {
	private View view;
	private static int OFFSET_Y = 18;
	private static int OFFSET_X =  375;//offset from left of endpoint input
	private ImgButton canvasImg;
	public EndpointInputIcon(View view) {
		this.view = view;
		setAutoWidth();
		setZIndex(ZIndexes.DOWNLOAD_ICON);
		Scheduler.get().scheduleDeferred(new Command() {
			public void execute() {
				positionElement();
			}
		});
		addMovedHandler(new MovedHandler(){
			@Override
			public void onMoved(MovedEvent event) {
				positionElement();
			}});
		setAutoHeight();
		setAutoWidth();
		
		updateFetchIcon();
	}

	private void positionElement() {
		setSnapTo("TL");
		setSnapOffsetLeft(view.getSelectedTab().getEndpointInput().getAbsoluteLeft() + OFFSET_X);
		setSnapOffsetTop(OFFSET_Y);
	}
	
	private void addFetchAutocompletionsButton() {
		resetButtonSpace();
		canvasImg = new ImgButton();
		canvasImg.setSrc(Imgs.DOWNLOAD_ROUND.get());
		canvasImg.setHeight(16);
		canvasImg.setWidth(16);
		canvasImg.setShowRollOverIcon(false);
		canvasImg.setShowOverCanvas(false);
		canvasImg.setShowDownIcon(false);
		canvasImg.setTooltip("Fetch predicate autocompletion information for this endpoint");
		canvasImg.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				getProperties();
			}});
		canvasImg.setZIndex(ZIndexes.DOWNLOAD_ICON);
		addMember(canvasImg);
		
	}
	private void addFetchingAutocompletionsIcon() {
		resetButtonSpace();
		Img img = new Img(Imgs.LOADING.get());
		img.setHeight(16);
		img.setWidth(16);
		img.setTooltip("Fetching predicate autocompletion information for this endpoint");
		addMember(img);
	}
	
	public void updateFetchIcon() {
		final String endpoint = view.getSelectedTabSettings().getEndpoint();
		if (JsMethods.propertiesRetrieved(endpoint)) {
			addAutocompletionsFetchedIcon();
		} else {
			addFetchAutocompletionsButton();
		}
	}
	
	private void addAutocompletionsFetchedIcon() {
		resetButtonSpace();
		canvasImg = new ImgButton();
		canvasImg.setSrc(Imgs.CHECKMARK.get());
		canvasImg.setHeight(16);
		canvasImg.setWidth(16);
		canvasImg.setShowOverCanvas(false);
		canvasImg.setShowRollOverIcon(false);
		canvasImg.setShowDownIcon(false);
		canvasImg.setTooltip("Autocompletion information fetched for this endpoint");
		addMember(canvasImg);
	}
	private void addFetchingFailedIcon() {
		resetButtonSpace();
		canvasImg = new ImgButton();
		canvasImg.setSrc(Imgs.CROSS.get());
		canvasImg.setHeight(16);
		canvasImg.setWidth(16);
		canvasImg.setShowOverCanvas(false);
		canvasImg.setShowRollOverIcon(false);
		canvasImg.setShowDownIcon(false);
		canvasImg.setTooltip("Unable to fetch predicate autocompletion information for this endpoint. Click to retry");
		canvasImg.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				getProperties();
			}});
		addMember(canvasImg);
	}
	
	private void resetButtonSpace() {
		Canvas[] contents = getMembers();
		for (Canvas content: contents) {
			content.destroy();
		}
	}

	public void getProperties() {
		final String endpoint = view.getSelectedTabSettings().getEndpoint();
		if (JsMethods.propertiesRetrieved(endpoint)) {
			addAutocompletionsFetchedIcon();
		} else {
			addFetchingAutocompletionsIcon();
			new GwtCallbackWrapper<String>(view) {
				public void onCall(AsyncCallback<String> callback) {
					view.getRemoteService().fetchProperties(endpoint, false, callback);
				}

				protected void onFailure(Throwable throwable) {
					view.getErrorHelper().onError(throwable);
					addFetchingFailedIcon();
				}

				protected void onSuccess(String properties) {
					if (properties.length() > 0) {
						JsMethods.setAutocompleteProperties(endpoint, properties);
						addAutocompletionsFetchedIcon();
					} else {
						addFetchingFailedIcon();
					}
				}

			}.call();
		}
	}

	public void disableRpcElements() {
		if (canvasImg != null) {
			canvasImg.setDisabled(true);
		}
		
	}

	public void enableRpcElements() {
		if (canvasImg != null) {
			canvasImg.setDisabled(false);
		}
	}
}
