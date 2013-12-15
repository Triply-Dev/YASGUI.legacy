package com.data2semantics.yasgui.client.services;

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

import com.data2semantics.yasgui.shared.Bookmark;
import com.data2semantics.yasgui.shared.IssueReport;
import com.data2semantics.yasgui.shared.autocompletions.AutocompletionsInfo;
import com.data2semantics.yasgui.shared.autocompletions.FetchMethod;
import com.data2semantics.yasgui.shared.autocompletions.FetchType;
import com.data2semantics.yasgui.shared.exceptions.EndpointIdException;
import com.data2semantics.yasgui.shared.exceptions.FetchException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("YasguiService")
public interface YasguiService extends RemoteService {
	void logLazyQuery(String query, String endpoint) throws IllegalArgumentException, EndpointIdException;
	AutocompletionsInfo getAutocompletionsInfo() throws IllegalArgumentException, FetchException;
	String fetchPrefixes(boolean forceUpdate) throws IllegalArgumentException, FetchException;
	String fetchEndpoints(boolean forceUpdate) throws IllegalArgumentException, FetchException;
	String getShortUrl(String forceUpdate) throws IllegalArgumentException, FetchException;
	void addBookmark(Bookmark bookmark) throws IllegalArgumentException, FetchException;
	void updateBookmarks(Bookmark[] bookmarks) throws IllegalArgumentException, FetchException;
	void deleteBookmarks(int[] bookmarkIds) throws IllegalArgumentException, FetchException;
	Bookmark[] getBookmarks() throws IllegalArgumentException, FetchException;
	boolean isOnline() throws IllegalArgumentException;
	void logException(Throwable e);
	String reportIssue(IssueReport issueReport) throws IllegalArgumentException;
	boolean isEndpointAccessible(String endpoint) throws IllegalArgumentException;
	void clearPrivateCompletions(FetchType type, FetchMethod method, String endpoint) throws IllegalArgumentException;
}
