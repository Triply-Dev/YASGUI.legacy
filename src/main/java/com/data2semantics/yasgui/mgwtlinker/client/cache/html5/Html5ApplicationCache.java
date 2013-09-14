package com.data2semantics.yasgui.mgwtlinker.client.cache.html5;

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

import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;
import com.data2semantics.yasgui.mgwtlinker.client.cache.ApplicationCache;
import com.data2semantics.yasgui.mgwtlinker.client.cache.ApplicationCacheStatus;
import com.data2semantics.yasgui.mgwtlinker.client.cache.event.CachedEvent;
import com.data2semantics.yasgui.mgwtlinker.client.cache.event.CheckingEvent;
import com.data2semantics.yasgui.mgwtlinker.client.cache.event.CheckingEvent.Handler;
import com.data2semantics.yasgui.mgwtlinker.client.cache.event.DownloadingEvent;
import com.data2semantics.yasgui.mgwtlinker.client.cache.event.ErrorEvent;
import com.data2semantics.yasgui.mgwtlinker.client.cache.event.NoUpdateEvent;
import com.data2semantics.yasgui.mgwtlinker.client.cache.event.ObsoleteEvent;
import com.data2semantics.yasgui.mgwtlinker.client.cache.event.ProgressEvent;
import com.data2semantics.yasgui.mgwtlinker.client.cache.event.UpdateReadyEvent;

public class Html5ApplicationCache implements ApplicationCache {

  private static final ApplicationCacheStatus[] STATUS_MAPPING = new ApplicationCacheStatus[] {
      ApplicationCacheStatus.UNCACHED, ApplicationCacheStatus.IDLE, ApplicationCacheStatus.CHECKING, ApplicationCacheStatus.DOWNLOADING, ApplicationCacheStatus.UPDATEREADY,
      ApplicationCacheStatus.OBSOLETE};

  public static Html5ApplicationCache createIfSupported() {
    if (!isSupported()) {
      return null;
    }
    return new Html5ApplicationCache();
  }

  protected static native boolean isSupported()/*-{
		return typeof ($wnd.applicationCache) == "object";
  }-*/;

  protected EventBus eventBus = new SimpleEventBus();

  protected Html5ApplicationCache() {
    initialize();
  }

  @Override
  public ApplicationCacheStatus getStatus() {
    int status0 = getStatus0();
    return STATUS_MAPPING[status0];
  }

  @Override
  public HandlerRegistration addCheckingHandler(Handler handler) {
    return eventBus.addHandler(CheckingEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addCachedHandler(com.data2semantics.yasgui.mgwtlinker.client.cache.event.CachedEvent.Handler handler) {
    return eventBus.addHandler(CachedEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addDownloadingHandler(com.data2semantics.yasgui.mgwtlinker.client.cache.event.DownloadingEvent.Handler handler) {
    return eventBus.addHandler(DownloadingEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addErrorHandler(com.data2semantics.yasgui.mgwtlinker.client.cache.event.ErrorEvent.Handler handler) {
    return eventBus.addHandler(ErrorEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addNoUpdateHandler(com.data2semantics.yasgui.mgwtlinker.client.cache.event.NoUpdateEvent.Handler handler) {
    return eventBus.addHandler(NoUpdateEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addObsoleteHandler(com.data2semantics.yasgui.mgwtlinker.client.cache.event.ObsoleteEvent.Handler handler) {
    return eventBus.addHandler(ObsoleteEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addProgressHandler(com.data2semantics.yasgui.mgwtlinker.client.cache.event.ProgressEvent.Handler handler) {
    return eventBus.addHandler(ProgressEvent.getType(), handler);
  }

  @Override
  public HandlerRegistration addUpdateReadyHandler(com.data2semantics.yasgui.mgwtlinker.client.cache.event.UpdateReadyEvent.Handler handler) {
    return eventBus.addHandler(UpdateReadyEvent.getType(), handler);
  }

  protected native int getStatus0()/*-{
		return $wnd.applicationCache.status;
  }-*/;

  protected void onChecking() {
    eventBus.fireEventFromSource(new CheckingEvent(), this);
  }

  protected void onError() {
    eventBus.fireEventFromSource(new ErrorEvent(), this);
  }

  protected void onNoUpdate() {
    eventBus.fireEventFromSource(new NoUpdateEvent(), this);
  }

  protected void onDownloading() {
    eventBus.fireEventFromSource(new DownloadingEvent(), this);
  }

  protected void onProgress() {
    eventBus.fireEventFromSource(new ProgressEvent(), this);
  }

  protected void onUpdateReady() {
    eventBus.fireEventFromSource(new UpdateReadyEvent(), this);
  }

  protected void onCached() {
    eventBus.fireEventFromSource(new CachedEvent(), this);
  }

  protected void onObsolete() {
    eventBus.fireEventFromSource(new ObsoleteEvent(), this);
  }

  protected native void initialize() /*-{
		var that = this;

		var check = $entry(function() {
			that.@com.data2semantics.yasgui.mgwtlinker.client.cache.html5.Html5ApplicationCache::onChecking()();
		});
		$wnd.applicationCache.addEventListener("checking", check);

		var onError = $entry(function() {
			that.@com.data2semantics.yasgui.mgwtlinker.client.cache.html5.Html5ApplicationCache::onError()();

		});
		$wnd.applicationCache.addEventListener("error", onError);

		var onUpdate = $entry(function() {
			that.@com.data2semantics.yasgui.mgwtlinker.client.cache.html5.Html5ApplicationCache::onNoUpdate()();

		});
		$wnd.applicationCache.addEventListener("noupdate", onUpdate);

		var ondownloading = $entry(function() {
			that.@com.data2semantics.yasgui.mgwtlinker.client.cache.html5.Html5ApplicationCache::onDownloading()();
		});
		$wnd.applicationCache.addEventListener("downloading", ondownloading);

		var onprogress = $entry(function() {
			that.@com.data2semantics.yasgui.mgwtlinker.client.cache.html5.Html5ApplicationCache::onProgress()();
		});
		$wnd.applicationCache.addEventListener("progress", onprogress);

		var onupdateReady = $entry(function() {
			that.@com.data2semantics.yasgui.mgwtlinker.client.cache.html5.Html5ApplicationCache::onUpdateReady()();
		});
		$wnd.applicationCache.addEventListener("updateready", onupdateReady);

		var oncached = $entry(function() {
			that.@com.data2semantics.yasgui.mgwtlinker.client.cache.html5.Html5ApplicationCache::onCached()();
		});
		$wnd.applicationCache.addEventListener("cached", oncached);

		var onobsolete = $entry(function() {
			that.@com.data2semantics.yasgui.mgwtlinker.client.cache.html5.Html5ApplicationCache::onObsolete()();
		});
		$wnd.applicationCache.addEventListener("obsolete", onobsolete);

  }-*/;

  @Override
  public native void swapCache() /*-{
		$wnd.applicationCache.swapCache();
  }-*/;
  
  @Override
  public native void update() /*-{
		$wnd.applicationCache.update();
  }-*/;

}
