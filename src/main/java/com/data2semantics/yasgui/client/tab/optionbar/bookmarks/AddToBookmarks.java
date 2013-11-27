package com.data2semantics.yasgui.client.tab.optionbar.bookmarks;

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
import com.smartgwt.client.widgets.ImgButton;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.data2semantics.yasgui.client.GwtCallbackWrapper;
import com.data2semantics.yasgui.client.RpcElement;
import com.data2semantics.yasgui.client.View;
import com.data2semantics.yasgui.client.helpers.Helper;
import com.data2semantics.yasgui.client.helpers.TooltipProperties;
import com.data2semantics.yasgui.client.settings.Imgs;
import com.data2semantics.yasgui.client.settings.TooltipText;
import com.data2semantics.yasgui.client.settings.ZIndexes;
import com.data2semantics.yasgui.shared.Bookmark;
import com.data2semantics.yasgui.shared.exceptions.OpenIdException;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AddToBookmarks extends ImgButton implements RpcElement {
	private View view;
	private Window window;
	private CheckboxItem includeEndpoint;
	private TextItem bookmarkTitle;
	private static int TOOLTIP_VERSION_BOOKMARKS = 7;
	private static int WINDOW_WIDTH = 340;
	private static int WINDOW_HEIGHT = 70;
	private static int ICON_WIDTH = 25;
	private static int ICON_HEIGHT = 25;
	private boolean enabled = false;

	public AddToBookmarks(View view) {
		this.view = view;
		setEnabled(view.getOpenId() != null && view.getOpenId().isLoggedIn());
		setWidth(ICON_WIDTH);
		setHeight(ICON_HEIGHT);
		setShowDown(false);
		setShowRollOver(false);
		setHandlers();

	}
	
	/**
	 * Set widget enabled/disabled
	 * @param enabled
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		if (enabled) {
			setSrc(Imgs.BOOKMARK_QUERY.get());
			setTooltip("add query to bookmarks");
			setCursor(Cursor.POINTER);
		} else {
			setSrc(Imgs.BOOKMARK_QUERY.getDisabled());
			setTooltip("log in to use your bookmarks");
			setCursor(Cursor.DEFAULT);
		}
	}

	/**
	 * Attach icon handlers
	 */
	private void setHandlers() {
		addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (enabled) {
					window = new Window();
					window.setShowHeader(false);
					window.setZIndex(ZIndexes.MODAL_WINDOWS);
					window.setIsModal(true);
					window.setDismissOnOutsideClick(true);
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
	
	/**
	 * Get content for bookmark popup
	 * @return
	 */
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
		bookmarkTitle.addKeyPressHandler(new KeyPressHandler(){
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getKeyName().equalsIgnoreCase("enter")) {
					bookmark();
				}
			}});
		if (view.getEnabledFeatures().endpointSelectionEnabled()) {
			includeEndpoint = new CheckboxItem();
			includeEndpoint.setValue(true);
			includeEndpoint.setTitle("Include endpoint");
			includeEndpoint.setLabelAsTitle(true);
			form.setItems(bookmarkTitle, includeEndpoint);
		} else {
			form.setItems(bookmarkTitle);
		}

		Button bookmarkButton = new Button("Bookmark");
		bookmarkButton.setHeight100();
		bookmarkButton.setWidth(60);
		bookmarkButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				bookmark();
			}
		});
		hlayout.addMembers(form, Helper.getHSpacer(), bookmarkButton);
		return hlayout;
	}
	
	protected void bookmark() {
		final Bookmark bookmark = new Bookmark();
		if (includeEndpoint != null && includeEndpoint.getValueAsBoolean()) {
			bookmark.setEndpoint(view.getSelectedTabSettings().getEndpoint());
		}
		bookmark.setQuery(view.getSelectedTabSettings().getQueryString());
		bookmark.setTitle(bookmarkTitle.getValueAsString());

		window.clear();
		setSrc(Imgs.LOADING.get());
		new GwtCallbackWrapper<Void>(view) {
			public void onCall(AsyncCallback<Void> callback) {
				view.getRemoteService().addBookmark(bookmark, callback);
			}

			protected void onFailure(Throwable throwable) {
				setSrc(Imgs.BOOKMARK_QUERY.get());
				if (throwable instanceof OpenIdException) {
					view.getErrorHelper().onError(throwable.getMessage() + ". Logging out");
					view.getOpenId().logOut();
				} else {
					view.getErrorHelper().onError(throwable);
				}
			}

			protected void onSuccess(Void t) {
				setSrc(Imgs.BOOKMARK_QUERY.get());
			}

		}.call();
	}
	/**
	 * Show tooltips
	 * @param fromVersionId
	 */
	public void showToolTips(int fromVersionId) {
		if (fromVersionId < TOOLTIP_VERSION_BOOKMARKS) {
			TooltipProperties tProp = new TooltipProperties();
			tProp.setId(getDOM().getId());
			tProp.set(TooltipText.ADD_TO_BOOKMARKS);
			tProp.setMy(TooltipProperties.POS_TOP_RIGHT);
			tProp.setAt(TooltipProperties.POS_BOTTOM_CENTER);
			tProp.setYOffset(2);
			Helper.drawTooltip(tProp);
		}
	}

	public void disableRpcElements() {
		setEnabled(false);
		
	}

	public void enableRpcElements() {
		setEnabled(true);
	}

}
