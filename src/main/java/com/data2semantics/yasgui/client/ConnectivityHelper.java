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

import com.data2semantics.yasgui.client.helpers.Helper;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ConnectivityHelper {
	public static interface ConnCallback {
		public void connectedCallback();
	}
	private View view;
	private boolean isOnline = true;

	public ConnectivityHelper(View view) {
		this.view = view;
	}
	
	public void checkOnlineStatus(final ConnCallback connCallback) {
		//do NOT use our GwtCallbackWrapper for this request!
		//otherwise we will get into infinite loops when disconnected
		view.getRemoteService().isOnline(new AsyncCallback<Boolean>() {
			public void onFailure(Throwable caught) {
				setIsOnline(false);
				Helper.onLoadingFinish();
			}
			public void onSuccess(Boolean isOnline) {
				setIsOnline(true);
				connCallback.connectedCallback();
			}
		});
	}
	
	public void checkOnlineStatus() {
		checkOnlineStatus(new ConnCallback() {
			
			@Override
			public void connectedCallback() {
				//do nothing
				
			}
		});
	}
	
	public void setIsOnline(boolean isOnline) {
		if (this.isOnline != isOnline) {
			this.isOnline = isOnline;
			if (isOnline) {
				view.enableRpcElements();
				view.getElements().showOfflineNotification(true);
				
			} else {
				view.disableRpcElements();
				view.getElements().showOfflineNotification(false);
			}
		}
	}
	
	public boolean isOnline() {
		return isOnline;
	}

	
}
