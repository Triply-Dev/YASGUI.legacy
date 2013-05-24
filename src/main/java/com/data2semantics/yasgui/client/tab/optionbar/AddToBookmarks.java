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

import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.properties.ZIndexes;
import com.data2semantics.yasgui.client.settings.Icons;
import com.data2semantics.yasgui.shared.Bookmark;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AddToBookmarks extends Img {
	private View view;
	private Window window;
	private CheckboxItem includeEndpoint;
	private TextItem bookmarkTitle;
	private static int WINDOW_WIDTH = 340;
	private static int WINDOW_HEIGHT = 70;
	private static int ICON_WIDTH = 25;
	private static int ICON_HEIGHT = 25;
	private boolean enabled = false;
	
	public AddToBookmarks(View view) {
		this.view = view;
		if (view.getOpenId() == null || !view.getOpenId().isLoggedIn()) {
			setDisabled();
		} else {
			setEnabled();
		}
		setWidth(ICON_WIDTH);
		setHeight(ICON_HEIGHT);
		setShowDown(false);
		setShowRollOver(false);
		setHandlers();
		
		

	}
	
	public void setEnabled() {
		enabled = true;
		setSrc(Icons.BOOKMARK_QUERY);
		setTooltip("add query to bookmarks");
		setCursor(Cursor.POINTER);
	}
	
	public void setDisabled() {
		enabled = false;
		setSrc(Icons.getDisabled(Icons.BOOKMARK_QUERY));
		setTooltip("log in to use your bookmarks");
		setCursor(Cursor.DEFAULT);
	}

	
	private void setHandlers() {
		addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (enabled) {
					window = new Window();
					window.setZIndex(ZIndexes.MODAL_WINDOWS);
					window.setIsModal(true);
					window.setDismissOnOutsideClick(true);
					window.setShowHeader(false);
					int left = (getAbsoluteLeft() + ICON_WIDTH) - WINDOW_WIDTH;
					int top = getAbsoluteTop() + ICON_HEIGHT;
					window.setRect(left, top, WINDOW_WIDTH, WINDOW_HEIGHT);
	
					window.setShowMinimizeButton(false);
					window.addItem(getPopupContent());
					window.draw();
				}
			}
		});
	}
	
	private HLayout getPopupContent() {
		HLayout hlayout = new HLayout();
		hlayout.setWidth100();
		hlayout.setHeight100();
		DynamicForm form = new DynamicForm();
		form.setHeight100();
		form.setWidth(260);
		form.setTitleWidth(100);
		bookmarkTitle = new TextItem();  
        bookmarkTitle.setTitle("Title");
        if (!view.getSettings().inSingleEndpointMode()) {
	        includeEndpoint = new CheckboxItem();  
	        includeEndpoint.setTitle("Include endpoint");
	        includeEndpoint.setLabelAsTitle(true);
	        form.setItems(bookmarkTitle, includeEndpoint);
        } else {
        	form.setItems(bookmarkTitle);
        }
        
        
        Button bookmarkButton = new Button("Bookmark");
        bookmarkButton.setHeight100();
        bookmarkButton.setWidth(60);
        bookmarkButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				Bookmark bookmark = new Bookmark();
				if (includeEndpoint != null  && includeEndpoint.getValueAsBoolean()) {
					bookmark.setEndpoint(view.getSelectedTabSettings().getEndpoint());
				}
				bookmark.setQuery(view.getSelectedTabSettings().getQueryString());
				bookmark.setTitle(bookmarkTitle.getValueAsString());
				
				window.clear();
				setSrc(Icons.LOADING);
				view.getRemoteService().addBookmark(bookmark, new AsyncCallback<Void>() {
					public void onFailure(Throwable caught) {
						view.getElements().onError(caught);
					}
		

					@Override
					public void onSuccess(Void result) {
						setSrc(Icons.BOOKMARK_QUERY);
					}
				});
			}});
        LayoutSpacer spacer = new LayoutSpacer();
        spacer.setWidth100();
        hlayout.addMembers(form, spacer, bookmarkButton);
		return hlayout;
	}
	

}
