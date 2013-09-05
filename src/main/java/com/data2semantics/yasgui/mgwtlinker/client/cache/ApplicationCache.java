package com.data2semantics.yasgui.mgwtlinker.client.cache;

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

import com.google.web.bindery.event.shared.HandlerRegistration;

import com.data2semantics.yasgui.mgwtlinker.client.cache.event.*;


public interface ApplicationCache {
	public ApplicationCacheStatus getStatus();

	public void swapCache();

	public void update();

	public HandlerRegistration addCheckingHandler(CheckingEvent.Handler handler);

	public HandlerRegistration addCachedHandler(CachedEvent.Handler handler);

	public HandlerRegistration addDownloadingHandler(DownloadingEvent.Handler handler);

	public HandlerRegistration addErrorHandler(ErrorEvent.Handler handler);

	public HandlerRegistration addNoUpdateHandler(NoUpdateEvent.Handler handler);

	public HandlerRegistration addObsoleteHandler(ObsoleteEvent.Handler handler);

	public HandlerRegistration addProgressHandler(ProgressEvent.Handler handler);

	public HandlerRegistration addUpdateReadyHandler(UpdateReadyEvent.Handler handler);
}
