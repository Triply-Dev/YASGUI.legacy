package com.data2semantics.yasgui.client.helpers;

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
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;

public class AppcacheHelper {
	public enum Status{
		CACHED("YASGUI is cached and available offline"),
		DOWNLOADING("Currently downloading YASGUI for offline availability"),
		MANIFEST_404("Error: Unable to find manifest file"),
		CHECKING("Checking whether we need to update our local YASGUI cache"),
		DOWNLOAD_FAIL("Unable to download all files needed for offline availability"),
		UNKNOWN("YASGUI is not cached locally.");
		
		private String statusMsg;
		private Status(String statusMsg) {
			this.statusMsg = statusMsg;
		}
		public String get() {
			return this.statusMsg;
		}
	}
	
	private boolean appcacheFetched = false;
	private int downloadCount = 0;
	private int appcacheSize = 0;
	private static String PROGRESS_BAR_ID = "appcacheProgressBar";
	private View view;
	private Status status = Status.UNKNOWN;
	
	public AppcacheHelper(View view) {
		this.view = view;
		declareCallableJsMethods(this);
		if (view.getSettings().useOfflineCaching()) {
			Helper.includeOfflineManifest();
		}
	}

	public void setAppcacheComplete(boolean fetched) {
		this.appcacheFetched = fetched;
	}


	public void getAppcacheSize() {
		String url = GWT.getModuleBaseURL() + "manifest.appcache?count";
		if (JsMethods.isDevPageLoaded()) url += "&type=dev";
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
		try {
			builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable e) {
					view.getLogger().severe("Unable to fetch appcache count (does browser support appcache??)");
				}

				@Override
				public void onResponseReceived(Request request, Response response) {
					if (response.getStatusCode() == 200) {
						setAppcacheSize(response.getText());
					} else {
						view.getLogger().severe("Unable to fetch appcache count (does browser support appcache??)");
					}

				}
			});
		} catch (RequestException e) {
			//couldnt connect to server
			view.getLogger().severe("Unable to fetch appcache count (does browser support appcache??)");
		}
	}

	public void setAppcacheSize(int appcacheSize) {
		this.appcacheSize = appcacheSize;
	}
	public void setAppcacheSize(String appcacheSize) {
		try {
			this.appcacheSize = Integer.parseInt(appcacheSize.trim());
		} catch (Exception e) {
			view.getLogger().severe("Unable to parse appcache count to integer (val is '" + appcacheSize.trim() + "')");
		}
		
	}

	// Fired after the first cache of the manifest.
	public void appcacheEventCached() {
		setStatus(Status.CACHED);
//		JsMethods.logConsole("cached");
	}

	// Checking for an update. Always the first event fired in the sequence.
	public void appcacheEventChecking() {
		setStatus(Status.CHECKING);
		JsMethods.logConsole("checking");
	}

	// An update was found. The browser is fetching resources.
	public void appcacheEventDownloading() {
		setStatus(Status.DOWNLOADING);
		getAppcacheSize();
		notifyAppcacheDownload("<span>Downloading YASGUI for offline use</span><br><div style=\"margin-top:5px;\" id=\"" + PROGRESS_BAR_ID + "\" class=\"progressBarDefault\"><div></div>", true);
	}

	// The manifest returns 404 or 410, the download failed,
	public void appcacheEventError() {
//		view.getErrorHelper().onError(
//				"Failed fetching YASGUI for offline use. To avoid this error in the feature, disable 'Offline Caching' in the 'Configure YASGUI' menu");
		setStatus(Status.DOWNLOAD_FAIL);
		setAppcacheComplete(false);
	}

	// Fired after the first download of the manifest.
	public void appcacheEventNoupdate() {
		setAppcacheComplete(true);
	}

	// Fired if the manifest file returns a 404 or 410.
	public void apcacheEventObsolete() {
		setStatus(Status.MANIFEST_404);
		setAppcacheComplete(false);
		// do nothing
	}

	// Fired for each resource listed in the manifest as it is being fetched.
	public void appcacheEventProgress() {
		this.downloadCount++;
		updateDownloadProgressBar();
		setStatus(Status.DOWNLOADING);
	}

	private void updateDownloadProgressBar() {
		int percentage = 0;
		if (appcacheSize > 0) {
			percentage = (int)((downloadCount * 100.0f) / appcacheSize);
		}
		percentage = Math.min(100, percentage);
		updateProgressBar(PROGRESS_BAR_ID, percentage);
	}
	private void setStatus(Status status) {
		this.status = status;
	}
	// Fired when the manifest resources have been newly redownloaded.
	public void appcacheEventUpdateReady() {
		setAppcacheComplete(true);
		JsMethods.logConsole("updatereadye");
		setStatus(Status.CACHED);
	}

	public static native void declareCallableJsMethods(AppcacheHelper cacheHelper) /*-{
		$wnd.appcacheEventCached = function() {
			cacheHelper.@com.data2semantics.yasgui.client.helpers.AppcacheHelper::appcacheEventCached()();
		}
		$wnd.appcacheEventChecking = function() {
			cacheHelper.@com.data2semantics.yasgui.client.helpers.AppcacheHelper::appcacheEventChecking()();
		}
		$wnd.appcacheEventDownloading = function() {
			cacheHelper.@com.data2semantics.yasgui.client.helpers.AppcacheHelper::appcacheEventDownloading()();
		}
		$wnd.appcacheEventError = function() {
			cacheHelper.@com.data2semantics.yasgui.client.helpers.AppcacheHelper::appcacheEventError()();
		}
		$wnd.appcacheEventNoupdate = function() {
			cacheHelper.@com.data2semantics.yasgui.client.helpers.AppcacheHelper::appcacheEventNoupdate()();
		}
		$wnd.apcacheEventObsolete = function() {
			cacheHelper.@com.data2semantics.yasgui.client.helpers.AppcacheHelper::apcacheEventObsolete()();
		}
		$wnd.appcacheEventProgress = function() {
			cacheHelper.@com.data2semantics.yasgui.client.helpers.AppcacheHelper::appcacheEventProgress()();
		}
		$wnd.appcacheEventUpdateReady = function() {
			cacheHelper.@com.data2semantics.yasgui.client.helpers.AppcacheHelper::appcacheEventUpdateReady()();
		}
	}-*/;
	private native void notifyAppcacheDownload(String notificationMsg, boolean allowInit) /*-{
		var notificationId = "appcacheDownload";
		if ($wnd.$.noty.get(notificationId) == false) {
			if (allowInit) {
				$wnd.noty({
					text: notificationMsg,
					layout: 'bottomRight',
					type: 'alert',
					id: notificationId,
					closeWith: ['button'], // ['click', 'button', 'hover']
					buttons: [
					    {addClass: 'btn', text: 'Go to settings', onClick: function($noty) {
					        $noty.close();
					        $wnd.showOfflineAvailabilitySettings();
					      }
					    }
					  ]
				});
			}
		} else {
			$wnd.$.noty.setText(notificationId, notificationMsg);
		}
	}-*/;
	private native void updateProgressBar(String id, int percentage) /*-{
		if ($wnd.$('#' + id).length > 0) {
			$wnd.progressbarUpdate(percentage, $wnd.$('#' + id));
		}
	}-*/;

	public String getStatus() {
		return this.status.get();
	}
}
