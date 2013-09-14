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

import com.data2semantics.yasgui.client.ConnectivityHelper.ConnCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class GwtCallbackWrapper<T> {
	protected View view;
	private AsyncCallback<T> asyncCallback = new AsyncCallback<T>() {
		public void onSuccess(T result) {
			GwtCallbackWrapper.this.onSuccess(result);
		}

		public void onFailure(final Throwable t) {
			String message = t.getMessage().trim();
			if (message.equals("0")) {
				//we cannot reach the server..
				//Check whether we are online
				view.getConnHelper().checkOnlineStatus(new ConnCallback() {
					@Override
					public void connectedCallback() {
						//we are online, and still our other request failed. Show error!
						view.getLogger().severe("error thrown before connectivity check returned 'connected'");
						GwtCallbackWrapper.this.onFailure(t);
					}
				});
			} else {
				GwtCallbackWrapper.this.onFailure(t);
			}
		}
	};

	protected final AsyncCallback<T> getAsyncCallback() {
		return asyncCallback;
	}
	public GwtCallbackWrapper(View view) {
		this.view = view;
	}
	
	protected abstract void onSuccess(T t);

	protected abstract void onFailure(Throwable throwable);

	protected abstract void onCall(AsyncCallback<T> callback);

	public final void call() {
		onCall(getAsyncCallback());
	}
}