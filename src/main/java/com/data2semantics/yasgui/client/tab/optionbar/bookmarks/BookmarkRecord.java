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

import com.data2semantics.yasgui.shared.Bookmark;
import com.smartgwt.client.widgets.grid.ListGridRecord;

public class BookmarkRecord extends ListGridRecord {
	public static String KEY_ENDPOINT = "endpoint";
	public static String KEY_TITLE = "title";
	public static String KEY_QUERY = "query";
	public static String KEY_QUERY_TEXTAREA = "queryTextArea";
	public static String KEY_ID = "queryId";
	public static String APPEND_INPUT_ID = "_bookmarkQuery";
	
    public BookmarkRecord() {
    }
    public BookmarkRecord(Bookmark bookmark) {
       	setBookmarkId(bookmark.getBookmarkId());
        setTitle(bookmark.getTitle());
        setEndpoint(bookmark.getEndpoint());
        setQuery(bookmark.getQuery());
    }
    public BookmarkRecord(int bookmarkId, String title, String endpoint, String query) {
    	setBookmarkId(bookmarkId);
        setTitle(title);
        setEndpoint(endpoint);
        setQuery(query);
    }
    public void setEndpoint(String endpoint) {
        setAttribute(KEY_ENDPOINT, endpoint);
    }

    public String getEndpoint() {
        return getAttributeAsString(KEY_ENDPOINT);
    }

    public void setTitle(String title) {
        setAttribute(KEY_TITLE, title);
    }

    public String getTitle() {
        return getAttributeAsString(KEY_TITLE);
    }


    public void setQuery(String query) {
        setAttribute(KEY_QUERY, query);
        setQueryAsTextArea(query);
    }
    
    private void setQueryAsTextArea(String query) {
    	setAttribute(KEY_QUERY_TEXTAREA, getQueryAsTextArea(query));
    }

    public String getQuery() {
        return getAttributeAsString(KEY_QUERY);
    }
    public void setBookmarkId(int bookmarkId) {
    	setAttribute(KEY_ID, bookmarkId);
    }
    
    public int getBookmarkId() {
    	return getAttributeAsInt(KEY_ID);
    }
    
    public String getQueryAsTextArea(String query) {
    	return "<textarea " + "id=\"" + getInputId() + "\"" + ">" + query + "</textarea>";
    }
    
    public String getInputId() {
    	return getBookmarkId() + APPEND_INPUT_ID;
    }
    public Bookmark toBookmark() {
    	Bookmark bookmark = new Bookmark();
    	bookmark.setQuery(getQuery());
    	bookmark.setEndpoint(getEndpoint());
    	bookmark.setTitle(getTitle());
    	bookmark.setBookmarkId(getBookmarkId());
    	return bookmark;
    }
    
}
